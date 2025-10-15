package ois.cc.gravity.ua.rest;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.requests.Request;
import code.ua.requests.RequestBuilder;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.cc.gravity.framework.requests.aops.*;
import ois.cc.gravity.framework.requests.auth.RequestClearTemporaryState;
import ois.cc.gravity.framework.requests.auth.RequestSUSignin;
import ois.cc.gravity.framework.requests.auth.RequestUserLogout;
import ois.cc.gravity.framework.requests.auth.RequestUserRegister;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.framework.requests.survey.*;
import ois.cc.gravity.framework.requests.survey.RequestSurveyInfoFetch;
import ois.cc.gravity.framework.requests.sys.*;
import ois.cc.gravity.framework.requests.user.RequestAgentSkillDelete;
import ois.cc.gravity.framework.requests.user.RequestProfileAdd;
import ois.cc.gravity.framework.requests.user.RequestProfileEdit;
import ois.cc.gravity.framework.requests.user.RequestUserPropertiesConfig;
import ois.cc.gravity.framework.requests.xs.RequestXPlatformDelete;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import ois.radius.cc.entities.tenant.cc.SurveyAttribute;
import org.json.JSONArray;

public class RESTRequestBuilder
{

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(RESTRequestBuilder.class);

    private static Request buildRequest(String reqid, EN en, Class cls, GReqType type, GReqCode code, JSONObject reqjson) throws GravityUnhandledException
    {
        try
        {

            reqjson.put("ReqId", reqid);
            reqjson.put("ReqType", type.name());
            reqjson.put("ReqCode", code.name());
            reqjson.put("EntityName", en);
            RequestBuilder reqBuildr = new RequestBuilder();
            Request req = null;
            req = reqBuildr.FromJSON(reqjson, cls, type, code);
            logger.trace("Request " + reqjson);

            return req;
        }
        catch (Exception ex)
        {
            throw new GravityUnhandledException(ex);
        }
    }

    private static Request buildSurveyRequest(String reqid, EN en, Class cls, GReqType type, GReqCode code, JSONObject reqjson) throws GravityUnhandledException, GravityRuntimeCheckFailedException, GravityIllegalArgumentException {
        try
        {

            reqjson.put("ReqId", reqid);
            reqjson.put("ReqType", type.name());
            reqjson.put("ReqCode", code.name());
            reqjson.put("EntityName", en);
            RequestBuilder reqBuildr = new RequestBuilder();

            Request req = BuildSurveyFormReq(reqid, reqjson);;

            logger.trace("Request " + reqjson);

            return req;
        }
        catch (Exception ex)
        {
            throw new GravityUnhandledException(ex);
        }
    }

    public static Request BuildReqAdd(String reqid, JSONObject reqjsnattrs, EN en, EN suben, String subenid) throws GravityUnhandledException, Exception, GravityRuntimeCheckFailedException, GravityIllegalArgumentException {
        JSONObject reqJson = new JSONObject();
        Class cls = null;
        GReqCode reqcode = null;
        switch (en)
        {
            case Disposition:
                cls = RequestDispositionAdd.class;
                reqcode = GReqCode.DispositionAdd;
                reqJson = reqjsnattrs;
                break;
            case Profile:
                cls = RequestProfileAdd.class;
                reqcode = GReqCode.ProfileAdd;
                reqJson = reqjsnattrs;
                break;
            case CrossCXContactMap:
                cls = RequestCrossCXContactMapAdd.class;
                reqcode = GReqCode.CrossCXContactMapAdd;
                reqJson = reqjsnattrs;
                break;
            case SurveyData:
                cls = RequestSurveyDataConfig.class;
                reqcode = GReqCode.SurveyDataConfig;
                reqJson = reqjsnattrs;
                break;
            case SurveyForm:
                cls = RequestSurveyFormConfig.class;
                reqcode = GReqCode.SurveyFormConfig;
                reqJson = reqjsnattrs;
                return buildSurveyRequest(reqid, en, cls, GReqType.Config, reqcode, reqJson);
            case OIMetrics:
                reqJson=reqjsnattrs;
                return BuildAOPsSLAConfigReq(reqid,reqJson);
            case AOPsAIProperties:
                reqJson=reqjsnattrs;
                return BuildAOPAIPropertiesReq(reqid,reqJson);
            case XAlertDR:
                reqJson=reqjsnattrs;
                return BuildXAlertDRAddReq(reqid,reqJson);
            default:
                cls = RequestEntityAdd.class;
                reqcode = GReqCode.EntityAdd;
                reqJson.put("Attributes", reqjsnattrs);
                break;
        }

        return buildRequest(reqid, en, cls, GReqType.Config, reqcode, reqJson);

    }

    public static Request BuildReqEdit(String reqid, JSONObject reqjsnattrs, EN en, Long id, EN suben, String subenid, RESTControllerEntity.ColAttrMapType mapType, HashMap<String, ArrayList<String>> colattr) throws GravityUnhandledException
    {

        JSONObject reqJson = new JSONObject();
        Class cls = null;
        GReqCode reqcode = null;
        switch (en)
        {
            case Disposition:
                cls = RequestDispositionEdit.class;
                reqcode = GReqCode.DispositionEdit;
                reqJson = reqjsnattrs;
                reqJson.put("DispositionId", id);
                break;
            case AOPsProperties:
                cls = RequestAOPsPropertiesConfig.class;
                reqcode = GReqCode.AOPsPropertiesConfig;
                reqJson.put("Campaign", id);
                reqJson.put("Attributes", reqjsnattrs);
                break;
            case UserProperties:
                cls = RequestUserPropertiesConfig.class;
                reqcode = GReqCode.UserPropertiesConfig;
                reqJson.put("UserId", id);
                reqJson.put("Attributes", reqjsnattrs);
                break;
            case Profile:
                cls = RequestProfileEdit.class;
                reqcode = GReqCode.ProfileEdit;
                reqJson = reqjsnattrs;
                reqJson.put("ProfileId", id);
                break;
            case XSPIConnect:
                cls = RequestXSPIConnectEdit.class;
                reqcode = GReqCode.XSPIConnectEdit;
                reqJson = reqjsnattrs;
                reqJson.put("XSPIConnectId", reqJson.getString("XSPIConnectId"));
                break;
            case AOPsBFProperties:
                cls = RequestAOPsBFPropertiesConfig.class;
                reqcode = GReqCode.AOPsBFPropertiesConfig;
                reqJson.put("AOPsBF", id);
                reqJson.put("Attributes", reqjsnattrs);
                break;
            case AOPsSchedule:
                cls=RequestAOPsScheduleConfig.class;
                reqcode=GReqCode.AOPsScheduleConfig;
                reqJson.put("Attributes", reqjsnattrs);
                break;
            case AOPsAIProperties:
                cls=RequestAOPsAIPropertiesEdit.class;
                reqcode=GReqCode.AOPsAIPropertiesEdit;
                reqJson.put("Id", id);
                reqjsnattrs.put("Id", id);
                reqJson = reqjsnattrs;
                break;
            default:
                cls = RequestEntityEdit.class;
                reqcode = GReqCode.EntityEdit;
                reqJson.put("EntityId", id);
                reqJson.put("Attributes", reqjsnattrs);
                break;
        }

        Request req = buildRequest(reqid, en, cls, GReqType.Config, reqcode, reqJson);

        if (mapType != null && req instanceof RequestEntityEdit)
        {
            RequestEntityEdit reqEdit = (RequestEntityEdit) req;
            switch (mapType)
            {
                case Append:
                    reqEdit.getAttributes().clear();
                    reqEdit.setAttributeCollectionAppend(colattr);
                    break;
                case Remove:
                    reqEdit.setAttributeCollectionRemove(colattr);
                    break;
            }
            req = reqEdit;
        }

        return req;
    }

    public static Request BuildReqDelete(String reqid, JSONObject reqjsnattrs, EN en, Long id, String ForceDelete) throws GravityUnhandledException
    {
        JSONObject reqJson = new JSONObject();
        Class cls = null;
        GReqCode reqcode = null;
        switch (en)
        {
            case AgentSkill:
                cls = RequestAgentSkillDelete.class;
                reqcode = GReqCode.AgentSkillDelete;
                reqJson.put("Id", id);
                break;
            case XPlatform:
                cls = RequestXPlatformDelete.class;
                reqcode = GReqCode.XPlatformDelete;
                reqJson.put("EntityId", id);
                reqJson.put("ForceDelete", ForceDelete);
                break;
            default:
                cls = RequestEntityDelete.class;
                reqcode = GReqCode.EntityDelete;
                reqJson.put("EntityId", id);
                break;
        }
        Request req = buildRequest(reqid, en, cls, GReqType.Config, reqcode, reqJson);
        return req;
    }

    public static synchronized Request BuildReqFtech(String reqid, EN en, Long uid, EN suben, String subenid, JSONObject reqbody) throws GravityUnhandledException
    {
        JSONObject reqJson = new JSONObject();
        Class cls = null;
        GReqCode reqcode = null;
        switch (en)
        {
            case AOPsProperties:
                cls = RequestAOPsPropertiesFetch.class;
                reqcode = GReqCode.AOPsPropertiesFetch;
                reqJson = reqbody;
                reqJson.put("AOPsId", subenid);
                break;
            case UserProperties:
                cls = RequestUserPropertiesFetch.class;
                reqcode = GReqCode.UserPropertiesFetch;
                reqJson = reqbody;
                reqJson.put("User", subenid);
                break;
            case AOPsBFProperties:
                cls = RequestAOPsBFPropertiesFetch.class;
                reqcode = GReqCode.AOPsBFPropertiesFetch;
                reqJson = reqbody;
                reqJson.put("AOPsBFId", Long.valueOf(subenid));
                break;
            case SurveyData:
                cls = RequestSurveyDataFetch.class;
                reqcode = GReqCode.SurveyDataFetch;
                reqJson = reqbody;
                reqJson.put("USUID", subenid);
                break;
            default:
                cls = RequestEntityFetch.class;
                reqcode = GReqCode.EntityFetch;
                reqJson = reqbody;
                logger.trace("ReqCode " + reqcode + " Class Is " + cls);
                break;
        }

        Request req = buildRequest(reqid, en, cls, GReqType.Config, reqcode, reqJson);
        return req;
    }

    public static Request BuildReqRegister(String reqid, String tntcode, String token, String role)
    {
        RequestUserRegister req = new RequestUserRegister(reqid);
        req.setTenantCode(tntcode);
        req.setAuthToken(token);
        req.setUserRole(role);

        return req;
    }

    public static Request BuildSuSignInRqeuest(String reqid, GReqType reqtype, GReqCode reqcode, String loginId, String password)
    {
        RequestSUSignin req = new RequestSUSignin(reqid, reqtype, reqcode);
        req.setLoginId(loginId);
        req.setPassword(password);
        return req;
    }

    public static Request BuildReqLogout(String reqid, String token, String role)
    {
        RequestUserLogout req = new RequestUserLogout(reqid);
        req.setToken(token);
        req.setUserRole(role);
        return req;
    }

    public static Request BuildReqClearTemporaryState(String reqid, String tntcode)
    {
        RequestClearTemporaryState req = new RequestClearTemporaryState(reqid);
        req.setTenantCode(tntcode);
        return req;
    }

    public static Request BuildVersionInfoFetch(String reqid)
    {
        RequestVersionInfoFetch req = new RequestVersionInfoFetch(reqid);
        return req;
    }

    public static Request BuildReqStartTenant(String reqid, JSONObject reqjson)
    {
        RequestTenantStart req = new RequestTenantStart(reqid);
        req.setTenantCode(reqjson.getString("Code"));
        return req;
    }

    public static Request BuildReqStopTenant(String reqid, JSONObject reqjson)
    {
        RequestTenantStop req = new RequestTenantStop(reqid);
        req.setTenantCode(reqjson.getString("Code"));
        return req;
    }

    public static Request BuildReqXSPIConDiscover(String reqid, JSONObject reqjson)
    {
        RequestXSPIClientDiscover req = new RequestXSPIClientDiscover(reqid);
        req.setTenantCode(reqjson.getString("TenantCode"));
        req.setXServerCode(reqjson.getString("XServerCode"));
        return req;
    }

    public static Request BuildReqXSPIClientDelete(String reqid, JSONObject reqjson)
    {
        RequestXSPIClientDelete req = new RequestXSPIClientDelete(reqid);
        req.setTenantCode(reqjson.getString("TenantCode"));
        req.setXServerCode(reqjson.getString("XServerCode"));
        return req;
    }

    public static Request BuildReqXSPIConConfig(String reqid, JSONObject reqjson) throws Exception
    {
        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Control.name());
        reqjson.put("ReqCode", GReqCode.XSPIConnectConfig.name());
        RequestBuilder reqBuildr = new RequestBuilder();
        RequestXSPIConnectEdit req = reqBuildr.FromJSON(reqjson, RequestXSPIConnectEdit.class, GReqType.Control, GReqCode.XSPIConnectConfig);
        return req;
    }

    public static Request BuildReqXSPIClientConfig(String reqid, JSONObject reqjson) throws Exception
    {

        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Control.name());
        reqjson.put("ReqCode", GReqCode.XSPIConnectConfig.name());
        RequestBuilder reqBuildr = new RequestBuilder();
        Request req = reqBuildr.FromJSON(reqjson, RequestXSPIClientConfig.class, GReqType.Control, GReqCode.XSPIClientConfig);
        return req;
    }

    public static Request BuildReqXSPIConnectAdd(String reqid, JSONObject reqjson) throws Exception
    {

        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Config.name());
        reqjson.put("ReqCode", GReqCode.XSPIConnectAdd.name());
        RequestBuilder reqBuildr = new RequestBuilder();
        Request req = reqBuildr.FromJSON(reqjson, RequestXSPIConnectAdd.class, GReqType.Config, GReqCode.XSPIConnectAdd);
        return req;
    }

    public static Request BuildAOPsCdnAddressEdit(String reqid, JSONObject reqjson, String code, Long id) throws Exception
    {
        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Config.name());
        reqjson.put("ReqCode", GReqCode.AOPsCDNAddressEdit.name());
        reqjson.put("AOPsId", id);
        reqjson.put("AOPsCDNCode", code);

        //check XPlatformId is exist or not if exist then set the value.
        if (reqjson.has("XPlatformID"))
        {
            reqjson.put("XPlatformID", XPlatformID.valueOf(reqjson.getString("XPlatformID")));
        }
        if (reqjson.has("XPlatformSID"))
        {
            reqjson.put("XPlatformSID", XPlatformSID.valueOf(reqjson.getString("XPlatformSID")));
        }
        if (reqjson.has("Channel"))
        {
            reqjson.put("Channel", Channel.valueOf(reqjson.getString("Channel")));
        }

        RequestBuilder reqBuilder = new RequestBuilder();
        Request req = reqBuilder.FromJSON(reqjson, RequestAOPsCDNAddressEdit.class, GReqType.Config, GReqCode.AOPsCDNAddressEdit);
        return req;
    }

    public static Request BuildReqCrossCXContactDelete(String reqid, String UCXConMapId)
    {
        RequestCrossCXContactMapDelete req = new RequestCrossCXContactMapDelete(reqid);
        req.setUCXConMapId(UCXConMapId);
        return req;
    }

    public static Request BuildReqSurveyDataFetch(String reqid, String USUId)
    {
        RequestSurveyDataFetch req = new RequestSurveyDataFetch(reqid);
        req.setUSUID(USUId);
        return req;
    }

    public static Request BuildAOPsCalleridEdit(String reqid, JSONObject reqjson, String code, Long id) throws Exception
    {
        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Config.name());
        reqjson.put("ReqCode", GReqCode.AOPsCallerIdAddressEdit.name());
        reqjson.put("AOPsId", id);
        reqjson.put("AOPsCallerIdCode", code);

        //check XPlatformId is exist or not if exist then set the value.
        if (reqjson.has("XPlatformID"))
        {
            reqjson.put("XPlatformID", XPlatformID.valueOf(reqjson.getString("XPlatformID")));
        }
        if (reqjson.has("XPlatformSID"))
        {
            reqjson.put("XPlatformSID", XPlatformSID.valueOf(reqjson.getString("XPlatformSID")));
        }
        if (reqjson.has("Channel"))
        {
            reqjson.put("Channel", Channel.valueOf(reqjson.getString("Channel")));
        }

        RequestBuilder reqBuilder = new RequestBuilder();
        Request req = reqBuilder.FromJSON(reqjson, RequestAOPsCallerIdAddressEdit.class, GReqType.Config, GReqCode.AOPsCallerIdAddressEdit);
        return req;
    }
    public static Request BuildAOPsScheduleConfig(String reqid, JSONObject reqjson) throws Exception
    {
        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Config.name());
        reqjson.put("ReqCode", GReqCode.AOPsScheduleConfig.name());
        RequestBuilder reqBuilder = new RequestBuilder();
        Request req = reqBuilder.FromJSON(reqjson, RequestAOPsScheduleConfig.class, GReqType.Config, GReqCode.AOPsScheduleConfig);
        return req;
    }



    public static Request BuildSurveyFormReq(String reqid, JSONObject reqjsnattrs) throws GravityUnhandledException, GravityRuntimeCheckFailedException, GravityIllegalArgumentException {
        RequestSurveyFormConfig req = new RequestSurveyFormConfig(reqid);

        req.setIsPublished(reqjsnattrs.getBoolean("IsPublished"));
        req.setSurvey(reqjsnattrs.getLong("Survey"));
        req.setFormCode(reqjsnattrs.getString("FormCode"));

        // Read attributes array
        JSONArray array = reqjsnattrs.getJSONArray("Attributes");
        ArrayList<SurveyAttribute> attributesList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++)
        {
            JSONObject attrJson = array.getJSONObject(i);
            SurveyAttribute attr = null;
            try
            {
                attr = JSONUtil.FromJSON(attrJson, SurveyAttribute.class);
                if (attr.getDataType()==null){
                    throw new GravityIllegalArgumentException("Found DataType "+attrJson.getString("DataType"));
                }
            }
            catch (Exception e)
            {
                throw new GravityUnhandledException(e);
            }

            attributesList.add(attr);
        }

        // Add attributes to request
        req.setAttributes(attributesList);

        return req;

    }



    public static Request BuildSurveyInfo(String reqid, JSONObject reqjson) throws Exception
    {
        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Control.name());
        reqjson.put("ReqCode", GReqCode.SurveyInfoFetch.name());
        RequestBuilder reqBuilder = new RequestBuilder();
        Request req = reqBuilder.FromJSON(reqjson, RequestSurveyInfoFetch.class, GReqType.Config, GReqCode.SurveyInfoFetch);
        return req;
    }

    public static Request BuildAOPsSLAConfigReq(String reqid, JSONObject reqjsnattrs) throws GravityUnhandledException, Exception {
        RequestAOPsSLAConfig req = new RequestAOPsSLAConfig(reqid);

        if(reqjsnattrs.has("Entity")){
            req.setEntity(EN.valueOf(reqjsnattrs.getString("Entity")));
        }
        if(reqjsnattrs.has("EntityId")){
            req.setEntityId(reqjsnattrs.get("EntityId").toString());
        }


        //set OMetrics
        if(reqjsnattrs.has("OIMetrics")){
            JSONObject ometrics= reqjsnattrs.getJSONObject("OIMetrics");
            RequestAOPsSLAConfig.OMetrics oMetrics = JSONUtil.FromJSON(ometrics, RequestAOPsSLAConfig.OMetrics.class);
            req.setOIMetrics(oMetrics);
        }


        //set OIRule

        if(reqjsnattrs.has("OIRule")){
            JSONObject oIRule= reqjsnattrs.getJSONObject("OIRule");

            ArrayList<RequestAOPsSLAConfig.ORule.OAlertConfig> alertConfigs=new ArrayList<>();
            RequestAOPsSLAConfig.ORule rule =req.new ORule();
            if(oIRule.has("Name")){
                rule.setName(oIRule.getString("Name"));
            }
            if(oIRule.has("RuleCondition")){
                rule.setRuleCondition(oIRule.getString("RuleCondition"));
            }

            if(oIRule.has("OIAlertConfigs")){
                JSONArray array = oIRule.getJSONArray("OIAlertConfigs");
                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject attrJson = array.getJSONObject(i);
                   RequestAOPsSLAConfig.ORule.OAlertConfig config = null;
                    try
                    {
                        config = JSONUtil.FromJSON(attrJson, RequestAOPsSLAConfig.ORule.OAlertConfig.class);
                    }
                    catch (Exception e)
                    {
                        throw new GravityUnhandledException(e);
                    }

                    alertConfigs.add(config);
                }
            }
            rule.setOIAlertConfigs(alertConfigs);
            req.setOIRule(rule);
        }


        return req;

    }
    public static Request BuildAOPAIPropertiesReq(String reqid, JSONObject reqjson) throws Exception
    {
        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Config.name());
        reqjson.put("ReqCode", GReqCode.AOPsAIPropertiesAdd.name());
        RequestBuilder reqBuilder = new RequestBuilder();
        Request req = reqBuilder.FromJSON(reqjson, RequestAOPsAIPropertiesAdd.class, GReqType.Config, GReqCode.AOPsAIPropertiesAdd);
        return req;
    }

    public static Request BuildAOPAIPropertiesEditReq(String reqid, JSONObject reqjson) throws Exception
    {
        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Config.name());
        reqjson.put("ReqCode", GReqCode.AOPsAIPropertiesEdit.name());
        RequestBuilder reqBuilder = new RequestBuilder();
        Request req = reqBuilder.FromJSON(reqjson, RequestAOPsAIPropertiesEdit.class, GReqType.Config, GReqCode.AOPsAIPropertiesEdit);
        return req;
    }

    public static Request BuildXAlertDRAddReq(String reqid, JSONObject reqjson) throws Exception
    {
        reqjson.put("ReqId", reqid);
        reqjson.put("ReqType", GReqType.Config.name());
        reqjson.put("ReqCode", GReqCode.XAlertDRAdd.name());
        RequestBuilder reqBuilder = new RequestBuilder();
        Request req = reqBuilder.FromJSON(reqjson, RequestXAlertDRAdd.class, GReqType.Config, GReqCode.XAlertDRAdd);
        return req;
    }

}
