package ois.cc.gravity.framework.requests.auth;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestUserRegister extends Request
{

    private String TenantCode;
    private String AuthToken;
    private String UserRole;

    public RequestUserRegister(String requestid)
    {
        super(requestid, GReqType.User, GReqCode.Register);
    }

    public String getTenantCode() {
        return TenantCode;
    }

    public void setTenantCode(String tenantCode) {
        TenantCode = tenantCode;
    }

    public String getAuthToken()
    {
        return AuthToken;
    }

    public void setAuthToken(String AuthToken)
    {
        this.AuthToken = AuthToken;
    }

    public String getUserRole()
    {
        return UserRole;
    }

    public void setUserRole(String userRole)
    {
        UserRole = userRole;
    }
}
