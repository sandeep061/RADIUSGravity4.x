package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestCrossCXContactMapAdd extends Request {

    @Param(Optional = false)
    private String PriAddress;

    @Param(Optional = false)
    private String SecAddress;

    @Param(Optional = false)
    private String AOPsCode;

    public RequestCrossCXContactMapAdd(String requestid) {
        super(requestid, GReqType.Config, GReqCode.CrossCXContactMapAdd);
    }
    public void setPriAddress(String priAddress) {
        PriAddress = priAddress;
    }
    public String getSecAddress() {
        return SecAddress;
    }
    public void setSecAddress(String secAddress) {
        SecAddress = secAddress;
    }
    public String getPriAddress() {
        return PriAddress;
    }

    public String getAOPsCode() {
        return AOPsCode;
    }

    public void setAOPsCode(String AOPsCode) {
        this.AOPsCode = AOPsCode;
    }
}
