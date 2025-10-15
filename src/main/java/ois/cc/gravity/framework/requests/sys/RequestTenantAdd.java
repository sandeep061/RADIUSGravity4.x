package ois.cc.gravity.framework.requests.sys;

import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.cc.gravity.framework.requests.auth.RequestSUAbase;

public class RequestTenantAdd extends RequestSUAbase
{
    private String TenantCode;

    private String TenantName;

    private String DefAdminLoginId;

    private String DefAdminPassword;

    public RequestTenantAdd(String requestid, GReqType type, GReqCode code)
    {
        super(requestid, type, code);

    }

    public String getTenantCode()
    {
        return TenantCode;
    }

    public void setTenantCode(String TenantCode)
    {
        this.TenantCode = TenantCode;
    }

    public String getTenantName()
    {
        return TenantName;
    }

    public void setTenantName(String TenantName)
    {
        this.TenantName = TenantName;
    }

    public String getDefAdminLoginId()
    {
        return DefAdminLoginId;
    }

    public void setDefAdminLoginId(String DefAdminLoginId)
    {
        this.DefAdminLoginId = DefAdminLoginId;
    }

    public String getDefAdminPassword()
    {
        return DefAdminPassword;
    }

    public void setDefAdminPassword(String DefAdminPassword)
    {
        this.DefAdminPassword = DefAdminPassword;
    }

}

