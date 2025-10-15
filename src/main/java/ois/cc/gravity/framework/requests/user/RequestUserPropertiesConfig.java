package ois.cc.gravity.framework.requests.user;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

import java.util.HashMap;

public class RequestUserPropertiesConfig extends Request
{
    private String UserId;

    private HashMap<String,String> Attributes;

    public RequestUserPropertiesConfig(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.UserPropertiesConfig);
        this.Attributes = new HashMap<>();
    }

    public String getUserId()
    {
        return UserId;
    }

    public void setUserId(String userId)
    {
        UserId = userId;
    }

    public HashMap<String, String> getAttributes()
    {
        return Attributes;
    }

    public void setAttributes(HashMap<String, String> attributes)
    {
        Attributes = attributes;
    }
}
