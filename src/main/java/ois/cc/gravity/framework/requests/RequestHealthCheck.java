package ois.cc.gravity.framework.requests;

import code.ua.requests.Request;

public class RequestHealthCheck extends Request {
    public RequestHealthCheck(String requestid) {
        super(requestid, GReqType.Config, GReqCode.HealthCheck);
    }
}
