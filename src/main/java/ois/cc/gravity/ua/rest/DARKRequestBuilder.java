package ois.cc.gravity.ua.rest;

import CrsCde.CODE.Common.Utils.JSONUtil;
import org.json.JSONObject;
import org.vn.radius.cc.platform.requests.Request;
import org.vn.radius.cc.platform.requests.RequestCode;
import org.vn.radius.cc.platform.requests.RequestType;
import org.vn.radius.cc.platform.requests.config.RequestEntityAdd;
import org.vn.radius.cc.platform.requests.config.RequestEntityDelete;
import org.vn.radius.cc.platform.requests.config.RequestEntityEdit;
import org.vn.radius.cc.platform.requests.config.RequestEntityFetch;
import org.vn.radius.cc.platform.requests.config.admin.RequestAgentSkillDelete;
import org.vn.radius.cc.platform.requests.config.admin.RequestDispositionAdd;
import org.vn.radius.cc.platform.requests.config.admin.RequestDispositionEdit;

public class DARKRequestBuilder
{

    public static Request Build(String reqstr) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception
    {
        JSONObject reqJson = new JSONObject(reqstr);

        RequestType type = null;
        RequestCode code = null;
        try
        {
            type = RequestType.valueOf(reqJson.get(UIParams.ReqType.name()).toString());
            code = RequestCode.valueOf(reqJson.get(UIParams.ReqCode.name()).toString());
        }
        catch (IllegalArgumentException rex)
        {
            String msg = "";
            String errorMsg = rex.getMessage();
            if (errorMsg.contains("org.vn.radius.cc.platform.requests.RequestType"))
            {
                msg = UIParams.ReqType.name() + " not found.";
            }
            else if (errorMsg.contains("org.vn.radius.cc.platform.requests.RequestCode"))
            {
                msg = UIParams.ReqCode.name() + " not found.";
            }
            throw new Exception(msg);
        }
        Class cls = FindClass(type, code);
        Request request = JSONUtil.FromJSON(reqJson, cls);

        return request;
    }

    private static Class FindClass(RequestType type, RequestCode code) throws Exception
    {
        Class procCls = null;
        String CC_PLATFORM_REQ_BASE_PKG = "org.vn.radius.cc.platform.requests";
        String[] arrPkgs =
        {
            CC_PLATFORM_REQ_BASE_PKG,
            CC_PLATFORM_REQ_BASE_PKG + ".framework",
            CC_PLATFORM_REQ_BASE_PKG + ".agent",
            CC_PLATFORM_REQ_BASE_PKG + ".admin",
            CC_PLATFORM_REQ_BASE_PKG + ".config",
            CC_PLATFORM_REQ_BASE_PKG + ".config.admin",
            CC_PLATFORM_REQ_BASE_PKG + ".system",
            CC_PLATFORM_REQ_BASE_PKG + ".user",
            CC_PLATFORM_REQ_BASE_PKG + ".common",
            CC_PLATFORM_REQ_BASE_PKG + ".campaign",
            CC_PLATFORM_REQ_BASE_PKG + ".x",
            CC_PLATFORM_REQ_BASE_PKG + ".agent.telephony",
            CC_PLATFORM_REQ_BASE_PKG + ".agent.chat",
            CC_PLATFORM_REQ_BASE_PKG + ".agent.email",
            CC_PLATFORM_REQ_BASE_PKG + ".agent.video",
            CC_PLATFORM_REQ_BASE_PKG + ".agent.sms",
            CC_PLATFORM_REQ_BASE_PKG + ".agent.social"
        };

        String className = "";

        //For all the entities to edit , fetch and Add there is the common Request for each.
        switch (type)
        {
            case Config ->
            {
                switch (code)
                {
                    case EntityAdd ->
                    {
                        return RequestEntityAdd.class;
                    }
                    case EntityEdit ->
                    {
                        return RequestEntityEdit.class;
                    }
                    case EntityDelete ->
                    {
                        return RequestEntityDelete.class;
                    }
                    case EntityFetch ->
                    {
                        return RequestEntityFetch.class;
                    }
                    case DispositionAdd ->
                    {
                        return RequestDispositionAdd.class;
                    }
                    case DispositionEdit ->
                    {
                        return RequestDispositionEdit.class;
                    }
                    case AgentSkillDelete ->
                    {
                        return RequestAgentSkillDelete.class;
                    }
                }
            }
            default ->
                className = "Request" + code.name();
        }

        /**
         * Look for Processors specific for an entity.
         */
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

        return procCls;
    }

}
