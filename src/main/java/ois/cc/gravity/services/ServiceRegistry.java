/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services;

import code.ua.events.EventFailedCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.GReqType;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.services.ai.RequestAOPsAIPropertiesEditService;
import ois.cc.gravity.services.aops.*;
import ois.cc.gravity.services.aops.RequestSkillDeleteService;
import ois.cc.gravity.services.auth.RequestSUSigninService;
import ois.cc.gravity.services.auth.RequestUserLogoutService;
import ois.cc.gravity.services.auth.RequestUserRegisterService;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.common.RequestEntityDeleteService;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.common.RequestEntityFetchService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.oi.*;
import ois.cc.gravity.services.si.aopscsat.*;
import ois.cc.gravity.services.survey.*;
import ois.cc.gravity.services.sys.*;
import ois.cc.gravity.services.user.*;
import ois.cc.gravity.services.xalert.RequestXAlertIDAddService;
import ois.cc.gravity.services.xalert.RequestXAlertIDDeleteService;
import ois.cc.gravity.services.xalert.RequestXAlertIDEditService;
import ois.cc.gravity.services.xs.*;
import ois.cc.gravity.services.ai.RequestAOPsAIPropertiesAddService;
import ois.cc.gravity.ua.UAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import code.ua.requests.Request;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.AppConst;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;

/**
 * @author Deepak
 */
//@Service
public class ServiceRegistry
{

    private final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    private static ServiceRegistry _this;
    private static ServerContext _sctx;
    private final HashMap<String, Class<? extends IRequestService>> _hmClsNameProc;

    private static final HashMap<GReqCode, ArrayList<EN>> restrictentitylists = new HashMap<>();

    public ServiceRegistry()
    {
        this._hmClsNameProc = new HashMap<>();
        this._sctx = ServerContext.This();
    }

    static ServiceRegistry This()
    {
        if (_this == null)
        {
            _this = new ServiceRegistry();
        }
        return _this;
    }

    public IRequestService GetService(UAClient uac, Request request) throws Exception, GravityIllegalArgumentException, GravityRuntimeCheckFailedException
    {

        String reqKey = getRequestKey(request);
        if (!_hmClsNameProc.containsKey(reqKey))
        {
            Class procCls = FindServiceClass(request);
            if (procCls != null)
            {
                _hmClsNameProc.put(reqKey, procCls);
            }
            else
            {
                throw new Exception("No processor found for Request - " + reqKey);
            }
        }

        if (reqKey.equals(GReqType.User + "_" + GReqCode.Register.name()))
        {
            return new RequestUserRegisterService();
        }
        else if (reqKey.equals(GReqType.User + "_" + GReqCode.Logout.name()))
        {
            return new RequestUserLogoutService();
        }
        else if (reqKey.equals(GReqType.Control + "_" + GReqCode.TenantStart.name()))
        {
            return new RequestTenantStartService();
        }
        else if (reqKey.equals(GReqType.Control + "_" + GReqCode.TenantStop.name()))
        {
            return new RequestTenantStopService();
        }
        else if (reqKey.equals(GReqType.System + "_" + GReqCode.SUSignin.name()))
        {
            return new RequestSUSigninService(_sctx.GetTenantCtxByCode(AppConst.SYS_CLIENT_CODE));
        }
        else if (reqKey.equals(GReqType.Control + "_" + GReqCode.ClearTemporaryState.name()))
        {
            return new RequestClearTemporaryStateService();
        }
        else if (reqKey.equals(GReqType.Control + "_" + GReqCode.VersionInfoFetch.name()))
        {
            return new RequestVersionInfoFetchService();
        }
        return (IRequestService) _hmClsNameProc.get(reqKey).getConstructor(UAClient.class).newInstance(uac);
    }

    private Class FindServiceClass(Request request) throws Exception, GravityRuntimeCheckFailedException
    {
        Class reqCls = FindServiceClassInPkg(request);
        return reqCls;
    }

    private static Class FindServiceClassInPkg(Request request) throws Exception, GravityRuntimeCheckFailedException
    {
        Class procCls = null;

        String[] arrPkgs =
                {
                        AppConst.UA_SERVICE_BASE_PKG + ".su", AppConst.UA_SERVICE_BASE_PKG + ".sys", AppConst.UA_SERVICE_BASE_PKG + ".auth", AppConst.UA_SERVICE_BASE_PKG + ".user", AppConst.UA_SERVICE_BASE_PKG + ".xserver", AppConst.UA_SERVICE_BASE_PKG + ".client", AppConst.UA_SERVICE_BASE_PKG + ".terminal", AppConst.UA_SERVICE_BASE_PKG + ".user.campaign", AppConst.UA_SERVICE_BASE_PKG + ".xs", AppConst.UA_SERVICE_BASE_PKG + ".common", AppConst.UA_REQUEST_BASE_PKG + ".aops", AppConst.UA_SERVICE_BASE_PKG + ".aops", AppConst.UA_SERVICE_BASE_PKG + ".si.aopscsat", AppConst.UA_SERVICE_BASE_PKG + ".survey",AppConst.UA_SERVICE_BASE_PKG + ".oi",AppConst.UA_SERVICE_BASE_PKG + ".ai",AppConst.UA_SERVICE_BASE_PKG + ".xalert"
                };

        String className = ServiceClassName(request);
        /**
         * Look for Processors specific for an entity.
         */
        GReqCode code = (GReqCode) request.getReqCode();
        addrestrictEntityintomap();
        switch (code)
        {
            case EntityAdd:
                EN en = ((RequestEntityAdd) request).getEntityName();
                if (restrictentitylists.get(GReqCode.EntityAdd).contains(en))
                {
                    throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.OperationNotAllowed, "ReqCode=" + GReqCode.EntityAdd.name() + ", Entity=" + en.name());
                }
                switch (en)
                {
                    case AgentStateReason:
                        procCls = RequestAgentStateReasonAddService.class;
                        break;
                    case AgentSkill:
                        procCls = RequestAgentSkillAddService.class;
                        break;
                    case Campaign:
                    case Process:
                        procCls = RequestAOPsAddService.class;
                        break;
                    case Skill:
                        procCls = RequestSkillAddService.class;
                        break;
                    case Queue:
                        procCls = RequestQueueAddService.class;
                        break;
                    case Disposition:
                        procCls = RequestDispositionAddService.class;
                        break;
                    case XServer:
                        procCls = RequestXServerAddService.class;
                        break;
                    case DialIDPlan:
                        procCls = RequestDialIDPlanAddService.class;
                        break;
                    case CallerIDPlan:
                        procCls = RequestCallerIDPlanAddService.class;
                        break;
                    case Terminal:
                        procCls = RequestTerminalAddService.class;
                        break;
                    case UserProfile:
                        procCls = RequestUserProfileAddService.class;
                        break;
                    case UserGroup:
                        procCls = RequestUserGroupAddService.class;
                        break;
                    case UserMedia:
                        procCls = RequestUserMediaAddService.class;
                        break;
                    case AgentMediaMap:
                        procCls = RequestAgentMediaMapAddService.class;
                        break;
                    case XServerEndpointProperties:
                        procCls = RequestXServerEndpointPropertiesAddService.class;
                        break;
                    case UserGroupAops:
                        procCls = RequestUserGroupAOPsAddService.class;
                        break;
                    case UserGroupUser:
                        procCls = RequestUserGroupUserAddService.class;
                        break;
                    case AOPsMedia:
                        procCls = RequestAOPsMediaAddService.class;
                        break;
                    case XSessionStatusRedial:
                        procCls = RequestXSessionStatusRedialAddService.class;
                        break;
                    case XPlatform:
                        procCls = RequestXPlatformAddService.class;
                        break;
                    case XPlatformUA:
                        procCls = RequestXPlatformUAAddService.class;
                        break;
                    case AOPsCDNAddress:
                        procCls = RequestAOPsCDNAddressAddService.class;
                        break;
                    case AOPsCallerIdAddress:
                        procCls = RequestAOPsCallerIdAddressAddService.class;
                        break;
                    case ContactBook:
                        procCls = RequestContactBookAddService.class;
                        break;
                    case ContactBookAddress:
                        procCls = RequestContactBookAddressAddService.class;
                        break;
                    case AOPsCSATConf:
                        procCls = RequestAOPsCSATConfAddService.class;
                        break;
                    case XAlertID:
                        procCls = RequestXAlertIDAddService.class;
                        break;
                    case AOPsBF:
                        procCls = RequestAOPsBFAddService.class;
                        break;
                    case AOPsAbandon:
                        procCls = RequestAOPsAbandonAddService.class;
                        break;
                    case CrossCXContactMap:
                        procCls = RequestCrossCXContactMapAddService.class;
                        break;
                    case Survey:
                        procCls = RequestSurveyAddService.class;
                        break;
                    case SurveyForm:
                        procCls = RequestSurveyFormConfigService.class;
                        break;
                    case SurveyData:
                        procCls = RequestSurveyDataConfigService.class;
                        break;
                    case OIRule:
                        procCls= RequestOIRuleAddService.class;
                        break;
                    case OIMetrics:
                        procCls= RequestOIMetricsAddService.class;
                        break;
                    case OIAlertConfig:
                        procCls= RequestOIAlertConfigAddServices.class;
                        break;
                    case AOPsAIProperties:
                        procCls= RequestAOPsAIPropertiesAddService.class;
                        break;
                    default:
                        procCls = RequestEntityAddService.class;
                }
                break;

            case EntityEdit:
                EN enE = ((RequestEntityEdit) request).getEntityName();
                switch (enE)
                {
                    case AgentStateReason:
                        procCls = RequestAgentStateReasonEditService.class;
                        break;

                    case Disposition:
                        procCls = RequestDispositionEditService.class;
                        break;
                    case Skill:
                        procCls = RequestSkillEditService.class;
                        break;
                    case CallerIDPlan:
                        procCls = RequestCallerIDPlanEditService.class;
                        break;
                    case DialIDPlan:
                        procCls = RequestDialIDPlanEditService.class;
                        break;
                    case XServer:
                        procCls = RequestXServerEditService.class;
                        break;
                    case Queue:
                        procCls = RequestQueueEditService.class;
                        break;
                    case Terminal:
                        procCls = RequestTerminalEditService.class;
                        break;
                    case UserProfile:
                        procCls = RequestUserProfileEditService.class;
                        break;
                    case Profile:
                        procCls = RequestProfileEditService.class;
                        break;
                    case UserProperties:
                        procCls = RequestUserPropertiesConfigService.class;
                        break;
                    case UserMedia:
                        procCls = RequestUserMediaEditService.class;
                        break;
                    case AgentMediaMap:
                        procCls = RequestAgentMediaMapEditService.class;
                        break;
                    case XServerEndpointProperties:
                        procCls = RequestXServerEndpointPropertiesEditService.class;
                        break;
                    case UserGroupAops:
                        procCls = RequestUserGroupUserEditService.class;
                        break;
                    case UserGroupUser:
                        procCls = RequestUserGroupAOPsEditService.class;
                        break;
                    case Campaign:
                    case Process:
                        procCls = RequestAOPsEditService.class;
                        break;
                    case AOPsProperties:
                        procCls = RequestAOPsPropertiesConfigService.class;
                        break;
                    case XSessionStatusRedial:
                        procCls = RequestXSessionStatusRedialEditService.class;
                        break;
                    case XPlatform:
                        procCls = RequestXPlatformEditService.class;
                        break;
                    case XPlatformUA:
                        procCls = RequestXPlatformUAEditService.class;
                        break;
                    case AOPsCDN:
                        procCls = RequestAOPsCDNEditService.class;
                        break;
                    case AOPsCallerId:
                        procCls = RequestAOPsCallerIdEditService.class;
                        break;
                    case AOPsCallerIdAddress:
                        procCls = RequestAOPsCallerIdAddressEditService.class;
                        break;
                    case ContactBook:
                        procCls = RequestContactBookEditService.class;
                        break;
                    case AOPsCSATConf:
                        procCls = RequestAOPsCSATConfEditService.class;
                        break;
                    case XAlertID:
                        procCls = RequestXAlertIDEditService.class;
                        break;
                    case AOPsBF:
                        procCls = RequestAOPsBFEditService.class;
                        break;
                    case AOPsAbandon:
                        procCls = RequestAOPsAbandonEditService.class;
                        break;
                    case AOPsBFProperties:
                        procCls = RequestAOPsBFPropertiesConfigService.class;
                        break;
                    case Survey:
                        procCls = RequestSurveyEditService.class;
                        break;
                    case AOPsAIProperties:
                        procCls= RequestAOPsAIPropertiesEditService.class;
                        break;
                    default:
                        procCls = RequestEntityEditService.class;

                }
                break;
            case EntityDelete:
                EN enD = ((RequestEntityDelete) request).getEntityName();
                switch (enD)
                {
                    case Campaign:
                    case Process:
                        procCls = RequestAOPsDeleteService.class;
                        break;
                    case Skill:
                        procCls = RequestSkillDeleteService.class;
                        break;
                    case Queue:
                        procCls = RequestQueueDeleteService.class;
                        break;
                    case UserGroup:
                        procCls = RequestUserGroupDeleteService.class;
                        break;
                    case CallerIDPlan:
                        procCls = RequestCallerIDPlanDeleteService.class;
                        break;
                    case DialIDPlan:
                        procCls = RequestDialIDPlanDeleteService.class;
                        break;
                    case Disposition:
                        procCls = RequestDispositionDeleteService.class;
                        break;
                    case Terminal:
                        procCls = RequestTerminalDeleteService.class;
                        break;
                    case XServer:
                        procCls = RequestXServerDeleteService.class;
                        break;
                    case UserProfile:
                        procCls = RequestUserProfileDeleteService.class;
                        break;
                    case UserMedia:
                        procCls = RequestUserMediaDeleteService.class;
                        break;
                    case AgentMediaMap:
                        procCls = RequestAgentMediaMapDeleteService.class;
                        break;
                    case UserGroupUser:
                        procCls = RequestUserGroupUserDeleteService.class;
                        break;
                    case UserGroupAops:
                        procCls = RequestUserGroupAopsDeleteService.class;
                        break;
                    case AOPsMedia:
                        procCls = RequestAOPsMediaDeleteService.class;
                        break;
                    case XSPIClient:
                        procCls = RequestXSPIClientDeleteService.class;
                        break;
                    case AOPsCDN:
                        procCls = RequestAOPsCDNDeleteService.class;
                        break;
                    case AOPsCallerId:
                        procCls = RequestAOPsCallerIdDeleteService.class;
                        break;
                    case XPlatform:
                        procCls = RequestXPlatformDeleteService.class;
                        break;
                    case XPlatformUA:
                        procCls = RequestXPlatformUADeleteService.class;
                        break;
                    case ContactBook:
                        procCls = RequestContactBookDeleteService.class;
                        break;
                    case ContactBookAddress:
                        procCls = RequestContactBookAddressDeleteService.class;
                        break;
                    case AOPsCSATConf:
                        procCls = RequestAOPsCSATConfDeleteService.class;
                        break;
                    case Profile:
                        procCls = RequestProfileDeleteService.class;
                        break;
                    case CrossCXContactMap:
                        procCls = RequestCrossCXContactMapDeleteService.class;
                        break;
                    case OIMetrics:
                        procCls= RequestOIMetricsDeleteService.class;
                        break;
                    case XAlertID:
                        procCls= RequestXAlertIDDeleteService.class;
                        break;
                    case OIAlertConfig:
                        procCls= RequestOIAlertConfigDeleteService.class;
                        break;
                    case Survey:
                        procCls=RequestSurveyDeleteService.class;
                        break;
                    default:
                        procCls = RequestEntityDeleteService.class;

                }
                break;
            case EntityFetch:
                EN enF = ((RequestEntityFetch) request).getEntityName();
                switch (enF)
                {
                    case Disposition:
                        return RequestDispositionFetchService.class;
                    case XSPIClient:
                        return RequestXSPIClientFetchService.class;
                    case XSPIConnect:
                        return RequestXSPIConnectFetchService.class;
                    case AOPsCDN:
                        return RequestAOPsCDNFetchService.class;
                    case AOPsCallerId:
                        return RequestAOPsCallerIdFetchService.class;
                    case ContactBook:
                        return RequestContactBookFetchService.class;
                    case AOPsCSATConf:
                        return RequestAOPsCSATConfFetchService.class;
                    case AOPsBFProperties:
                        return RequestAOPsBFPropertiesFetchService.class;
                    case SurveyForm:
                        return RequestSurveyFormFetchService.class;
                    case SurveyData:
                        return RequestSurveyDataFetchService.class;
                    case OIAlertConfig:
                        return RequestOIAlertConfigFetchService.class;
                    case OIMetrics:
                        return RequestOIMetricsFetchService.class;
                    case XServerEndpointProperties:
                        return RequestXServerEndpointPropertiesFetchService.class;
                  case Terminal:
                        return RequestTerminalFetchService.class;
                    case XServer:
                        return RequestXserverFetchService.class;
                    case AgentMediaMap:
                        return RequestAgentMediaMapFetchService.class;
                    case UserMedia:
                        return RequestUserMediaFetchService.class;
                    default:
                        return RequestEntityFetchService.class;
                }
            default:
                for (String pkg : arrPkgs)
                {
                    try
                    {
                        procCls = Class.forName(pkg + "." + className);
                        break;
                    }
                    catch (ClassNotFoundException ex)
                    {
                    }
                    catch (Exception ex)
                    {
                        throw ex;
                    }
                }

        }

        return procCls;
    }

    private static String ServiceClassName(Request request)
    {
        String className = request.getClass().getSimpleName() + "Service";
        return className;
    }

    private String getRequestKey(Request req) throws GravityIllegalArgumentException
    {
        GReqType GReqType = (GReqType) req.getReqType();
        GReqCode GReqCode = (GReqCode) req.getReqCode();
        String key = GReqType.name() + "_" + GReqCode.name();
        switch (GReqCode)
        {
            case EntityAdd:
            case EntitiesEdit:
            case EntityDelete:
            case EntityEdit:
            case EntityFetch:
            {
                //For the above requests 'EntityName' is a mandatory filed.
                EN en = RequestContext.GetENFromRequest(req);
                if (en == null)
                {
                    throw new GravityIllegalArgumentException("EntityName", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
                }
                key = key + "_" + en.name();
            }
            break;
        }

        return key;
    }

    private static void addrestrictEntityintomap()
    {
        ArrayList<EN> entityadd = new ArrayList<>(Arrays.asList(
                EN.AOPsProperties,
                EN.ContactScheduled,
                EN.AgentQueueStCh
        ));
        ArrayList<EN> entityedit = new ArrayList<>(Arrays.asList(
                EN.AOPsProperties,
                EN.ContactScheduled,
                EN.AgentQueueStCh
        ));
        ArrayList<EN> entityfetch = new ArrayList<>(Arrays.asList(
                EN.AOPsProperties,
                EN.ContactScheduled,
                EN.AgentQueueStCh
        ));

        ArrayList<EN> entitydelete = new ArrayList<>(Arrays.asList(
                EN.AOPsProperties,
                EN.ContactScheduled,
                EN.AgentQueueStCh
        ));

        restrictentitylists.put(GReqCode.EntityAdd, entityadd);
        restrictentitylists.put(GReqCode.EntityEdit, entityedit);
        restrictentitylists.put(GReqCode.EntityDelete, entitydelete);
        restrictentitylists.put(GReqCode.EntityFetch, entityfetch);
    }

}
