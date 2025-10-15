package ois.cc.gravity.framework.requests;

import code.ua.requests.Param;
import code.ua.requests.Request;

public class RequestAbase extends Request
{
//    @Param(Optional = false)
//    private Long TenantId;
    public RequestAbase(String requestid, GReqType type, GReqCode code)
    {
        super(requestid, type, code);
        // TODO Auto-generated constructor stub
    }
//    public Long getTenantId()
//    {
//        return TenantId;
//    }
//
//    public void setTenantId(Long TenantId)
//    {
//        this.TenantId = TenantId;
//    }
}
