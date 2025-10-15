package ois.cc.gravity.framework.requests.auth;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestClearTemporaryState extends Request {
    public RequestClearTemporaryState(String requestid) {
        super(requestid, GReqType.Control, GReqCode.ClearTemporaryState);
    }
    private String TenantCode;

    public String getTenantCode() {
        return TenantCode;
    }

    public void setTenantCode(String tenantCode) {
        TenantCode = tenantCode;
    }
}
