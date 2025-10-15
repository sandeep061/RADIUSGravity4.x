package ois.cc.gravity.framework.requests.auth;

import code.ua.requests.Request;
import code.ua.requests.RequestCode;
import code.ua.requests.RequestType;

public class RequestSUAbase extends Request
{
    private String TenantCode;

    public RequestSUAbase(String requestid, RequestType type, RequestCode code)
    {
        super(requestid, type, code);
    }

    public String getTenantCode()
    {
        return TenantCode;
    }

    public void setTenantCode(String tenantCode)
    {
        TenantCode = tenantCode;
    }
}
