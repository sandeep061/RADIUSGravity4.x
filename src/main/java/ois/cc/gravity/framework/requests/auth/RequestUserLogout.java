package ois.cc.gravity.framework.requests.auth;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestUserLogout extends Request
{
    private String Token;
    private String UserRole;
    public RequestUserLogout(String requestid)
    {
        super(requestid, GReqType.User, GReqCode.Logout);
    }

    public String getUserRole()
    {
        return UserRole;
    }

    public void setUserRole(String userRole)
    {
        UserRole = userRole;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
