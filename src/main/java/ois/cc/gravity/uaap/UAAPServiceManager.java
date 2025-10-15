//package ois.radius.core.gravity.uaap;
//
//import CrsCde.CODE.Common.Utils.LOGUtil;
//import code.db.jpa.JPAQuery;
//import code.ua.events.Event;
//import code.ua.events.EventUnAuthorizedRequest;
//import code.ua.requests.Request;
//import code.uaap.sdk.UAAPClient;
//import code.uaap.sdk.UAAPService;
//import code.uaap.service.common.entities.Effect;
//import code.uaap.service.common.entities.app.Policy;
//import code.uaap.service.event.*;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//import ois.radius.core.gravity.AppProps;
//import ois.radius.core.gravity.context.TenantContext;
//import ois.radius.core.gravity.entities.sys.UserType;
//import ois.radius.core.gravity.framework.requests.GReqCode;
//import ois.radius.core.gravity.framework.requests.GReqType;
//import ois.radius.core.gravity.services.RequestContext;
//import ois.radius.core.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
//import ois.radius.core.gravity.services.exceptions.GravityException;
//import ois.radius.core.gravity.services.exceptions.GravityRuntimeCheckFailedException;
//import ois.radius.core.gravity.services.exceptions.GravityUnhandledException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.vn.radius.cc.platform.exceptions.RADException;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//
//public class UAAPServiceManager
//{
//
//    private final Logger _logger = LoggerFactory.getLogger(getClass());
//
//    private final UAAPService _uaapSrvc;
//    /**
//     * CTClinet.Code-UAAPClient.
//     */
//    private final HashMap<String, UAAPClient> _hmUAAPClient;
//    private static UAAPServiceManager _this;
//
//    public static void Init()
//    {
//        if (_this != null)
//        {
//            return;
//        }
//        _this = new UAAPServiceManager();
//    }
//
//    private UAAPServiceManager()
//    {
//        _uaapSrvc = new UAAPService(AppProps.RAD_UAAP_Service_URL);
//        this._hmUAAPClient = new HashMap<>();
//    }
//
//    public static UAAPServiceManager This()
//    {
//        return _this;
//    }
//
//    private UAAPClient getUAAPClient(String clientcode) throws  GravityRuntimeCheckFailedException
//    {
//
//        if (_hmUAAPClient.containsKey(clientcode))
//        {
//            return _hmUAAPClient.get(clientcode);
//        }
//        else
//        {
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, "Tenant [" + clientcode + "] not registerd yet.");
//        }
//
//    }
//
//    public void AppLogin() throws Exception, GravityUnhandledException, GravityRuntimeCheckFailedException
//    {
//        code.uaap.service.event.Event ev = _uaapSrvc.AppLogin(AppProps.RAD_UAAP_Service_AppKey, AppProps.RAD_UAAP_Service_AppKeySecret);
//        if (ev.getEventCode().equals(EventCode.AppLogin))
//        {
//            EventAppLogin evlogin = (EventAppLogin) ev;
//            _uaapSrvc.setToken(evlogin.getToken());
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//    }
//
//    public void TenantRegister(String ctclient) throws GravityRuntimeCheckFailedException
//    {
//        try
//        {
//            UAAPClient uaapclnt = _uaapSrvc.TenantRegister(ctclient);
//            _hmUAAPClient.put(ctclient, uaapclnt);
//        }
//        catch (Exception ex)
//        {
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, ex.getMessage());
//        }
//    }
//
//    public Event EvaluateUaap(Request request, UAClient uac) throws Exception, RADException, GravityException
//    {
//        _logger.trace(LOGUtil.ArgString(request, uac));
//
//        if (request.getReqCode().equals(GReqCode.VersionInfoFetch))
//        {
//            return null;
//        }
//        if (request.getReqType().equals(GReqType.Auth))
//        {
//            return null;
//        }
//        if (uac.getUserType().equals(UserType.System))
//        {
//            return null;
//        }
//
//        if (!uac.getUserType().equals(UserType.Admin))
//        {
//            return null;
//        }
//
//        EN enName = RequestContext.GetENFromRequest(request);
//        if (enName == null)
//        {
//            //There are some request where we can't find EntityName in request. so ignore them.These requests will process in respective processor level.
//            return null;
//        }
//
//        if (request.getReqType().equals(GReqType.Config)
//                && !enName.equals(EN.Profile) && !enName.equals(EN.AdminGroup)
//                && !enName.equals(EN.Admin) && !enName.equals(EN.UserProfile))
//        {
//
//            String entityName = enName.name();
//            String action = request.getReqCode().name();
//
//            JPAQuery qry = new JPAQuery("Select a.Id From AdminGroup a Where a.Admins.Id =: aid");
//            qry.setParam("aid", uac.getUserId());
//            ArrayList<Long> adGpIds = uac.getCCtx().getDB().SelectList(qry);
//            adGpIds = adGpIds == null ? new ArrayList<>() : adGpIds;
//
//            code.uaap.service.event.Event ev = getUAAPClient(uac.getCtClient().getCode()).Evaluate(action, entityName, null, adGpIds);
//
//            if (ev.getEventCode().equals(EventCode.ActionEvalute))
//            {
//                EventActionEvalute actionEvalute = (EventActionEvalute) ev;
//                if (actionEvalute.getEffect().equals(Effect.Allow.name()))
//                {
//                    return null;
//                }
//                else
//                {
//                    return new EventUnAuthorizedRequest(request);
//                }
//            }
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, ((EventFailed) ev).getMessage());
//        }
//        return null;
//    }
//
//    public code.uaap.service.event.Event AddPolicy(TenantContext cctx, Policy policy) throws Exception, RADUnhandledException, GravityRuntimeCheckFailedException
//    {
//
//        code.uaap.service.event.Event ev = getUAAPClient(tctx.getClient().getCode()).AddPolicy(policy);
//
//        if (ev.getEventCode().equals(EventCode.Success))
//        {
//            EventSuccess success = (EventSuccess) ev;
//            return success;
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//
//    }
//
//    public code.uaap.service.event.Event EditPolicy(TenantContext cctx, Policy policy) throws Exception, RADUnhandledException, GravityRuntimeCheckFailedException
//    {
//        code.uaap.service.event.Event ev = getUAAPClient(cctx.getClient().getCode()).EditPolicy(policy);
//
//        if (ev.getEventCode().equals(EventCode.Success))
//        {
//            EventSuccess success = (EventSuccess) ev;
//            return success;
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//    }
//
//    public code.uaap.service.event.Event DeletePolicy(TenantContext cctx, String policy) throws Exception, RADUnhandledException, GravityRuntimeCheckFailedException
//    {
//        code.uaap.service.event.Event ev = getUAAPClient(cctx.getClient().getCode()).DeletePolicy(policy);
//
//        if (ev.getEventCode().equals(EventCode.Success))
//        {
//            EventSuccess success = (EventSuccess) ev;
//            return success;
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//
//    }
//
//    public code.uaap.service.event.Event MapPolicyUser(TenantContext cctx, String policycode, long userid) throws Exception, RADUnhandledException, GravityRuntimeCheckFailedException
//    {
//        code.uaap.service.event.Event ev = getUAAPClient(cctx.getClient().getCode()).MapPolicyUser(policycode, userid);
//
//        if (ev.getEventCode().equals(EventCode.Success))
//        {
//            EventSuccess success = (EventSuccess) ev;
//            return success;
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//
//    }
//
//    public code.uaap.service.event.Event UnmapPolicyUser(TenantContext cctx, String policycode, long userid) throws Exception, RADUnhandledException, GravityRuntimeCheckFailedException
//    {
//        code.uaap.service.event.Event ev = getUAAPClient(cctx.getClient().getCode()).UnmapPolicyUser(policycode, userid);
//
//        if (ev.getEventCode().equals(EventCode.Success))
//        {
//            EventSuccess success = (EventSuccess) ev;
//            return success;
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//    }
//
//    public code.uaap.service.event.Event MapPolicyUsergroup(TenantContext cctx, String policycode, long usergroupid) throws Exception, RADUnhandledException, GravityRuntimeCheckFailedException
//    {
//        code.uaap.service.event.Event ev = getUAAPClient(cctx.getClient().getCode()).MapPolicyUsergroup(policycode, usergroupid);
//
//        if (ev.getEventCode().equals(EventCode.Success))
//        {
//            EventSuccess success = (EventSuccess) ev;
//            return success;
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//    }
//
//    public code.uaap.service.event.Event UnmapPolicyUsergroup(TenantContext cctx, String policycode, long usergroupid) throws Exception, RADUnhandledException, GravityRuntimeCheckFailedException
//    {
//        code.uaap.service.event.Event ev = getUAAPClient(cctx.getClient().getCode()).UnmapPolicyUsergroup(policycode, usergroupid);
//
//        if (ev.getEventCode().equals(EventCode.Success))
//        {
//            EventSuccess success = (EventSuccess) ev;
//            return success;
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//    }
//
//    public code.uaap.service.event.Event FetchPolicy(TenantContext cctx, HashMap<String, ArrayList<String>> filter) throws Exception, RADUnhandledException, GravityRuntimeCheckFailedException
//    {
//        code.uaap.service.event.Event ev = getUAAPClient(cctx.getClient().getCode()).FetchPolicy(filter);
//        if (ev.getEventCode().equals(EventCode.FetchPolicy))
//        {
//            EventFetchPolicy fetched = (EventFetchPolicy) ev;
//            return fetched;
//        }
//        else
//        {
//            String msg = ((EventFailed) ev).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//
//    }
//
//    public ArrayList<Policy> FetchPoliciesByCode(TenantContext cctx, Profile profile) throws RADUnhandledException, Exception, GravityRuntimeCheckFailedException
//    {
//        HashMap<String, ArrayList<String>> filters = new HashMap<>();
//        filters.put("bycode", new ArrayList<>(Arrays.asList(profile.getCode())));
//        code.uaap.service.event.Event uaapEv = FetchPolicy(cctx, filters);
//        if (uaapEv.getEventCode().equals(EventCode.Failed))
//        {
//            String msg = ((EventFailed) uaapEv).getMessage();
//            _logger.debug(msg);
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UAAPRequestFailed, msg);
//        }
//        return ((EventFetchPolicy) uaapEv).getPolicy();
//    }
//
//}
//
