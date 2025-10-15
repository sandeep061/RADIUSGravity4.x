package ois.cc.gravity.framework.requests.sys;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestVersionInfoFetch extends Request
{

    public RequestVersionInfoFetch(String requestid)
    {
        super(requestid, GReqType.Control, GReqCode.VersionInfoFetch);
    }
}
