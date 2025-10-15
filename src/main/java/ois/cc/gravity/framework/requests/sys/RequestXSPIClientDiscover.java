package ois.cc.gravity.framework.requests.sys;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestXSPIClientDiscover extends Request
{

    @Param(Optional = false)
    protected String XServerCode;
    
    @Param(Optional = false)
    protected String TenantCode;

    public RequestXSPIClientDiscover(String requestid)
    {
        super(requestid, GReqType.Control, GReqCode.XSPIClientDiscover);
    }

    public String getTenantCode()
    {
        return TenantCode;
    }

    public void setTenantCode(String tenantCode)
    {
        TenantCode = tenantCode;
    }

    public String getXServerCode()
    {
        return XServerCode;
    }

    public void setXServerCode(String XServerCode)
    {
        this.XServerCode = XServerCode;
    }
}
