package ois.cc.gravity.framework.events.auth;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;

public class EventUserLogout extends EventOK
{

    private String AccessToken;

    public EventUserLogout(Request request)
    {
        super(request, EventCode.UserLogout);
    }

    public String getAccessToken()
    {
        return AccessToken;
    }

    public void setAccessToken(String accessToken)
    {
        AccessToken = accessToken;
    }
}
