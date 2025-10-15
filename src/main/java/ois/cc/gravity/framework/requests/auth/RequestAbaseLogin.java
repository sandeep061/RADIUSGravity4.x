package ois.cc.gravity.framework.requests.auth;

import code.ua.requests.Request;
import code.ua.requests.RequestCode;
import code.ua.requests.RequestType;

public class RequestAbaseLogin extends Request
{

    public RequestAbaseLogin(String requestid, RequestType type, RequestCode code)
    {
        super(requestid, type, code);
    }
}
