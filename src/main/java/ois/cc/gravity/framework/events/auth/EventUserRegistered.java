package ois.cc.gravity.framework.events.auth;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;


/**
 *
 * @author Deepak
 */
public class EventUserRegistered extends EventOK
{
    public EventUserRegistered(Request request)
    {
        super(request, EventCode.UserRegistered);
    }

    private String AccessToken;

    private String TenantCode;


    public String getTenantCode() {
        return TenantCode;
    }

    public void setTenantCode(String tenantCode) {
        TenantCode = tenantCode;
    }


    public String getAccessToken()
    {
        return AccessToken;
    }

    public void setAccessToken(String Token)
    {
        this.AccessToken = Token;
    }

}
