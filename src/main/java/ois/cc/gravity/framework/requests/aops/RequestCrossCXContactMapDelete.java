package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestCrossCXContactMapDelete extends Request {

    private String UCXConMapId;

    public RequestCrossCXContactMapDelete(String requestid) {
        super(requestid, GReqType.Config, GReqCode.CrossCXContactMapDelete);
    }

    public String getUCXConMapId() {
        return UCXConMapId;
    }

    public void setUCXConMapId(String UCXConMapId) {
        this.UCXConMapId = UCXConMapId;
    }
}
