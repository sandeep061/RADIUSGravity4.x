package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

import java.util.HashMap;

public class RequestAOPsBFPropertiesConfig extends Request {

    private Long AOPsBF;

    private HashMap<String,String> Attributes;

    public RequestAOPsBFPropertiesConfig(String requestid) {
        super(requestid, GReqType.Config, GReqCode.AOPsBFPropertiesConfig);
        this.Attributes = new HashMap<>();
    }

    public Long getAOPsBF() {
        return AOPsBF;
    }

    public void setAOPsBF(Long AOPsBF) {
        this.AOPsBF = AOPsBF;
    }

    public HashMap<String, String> getAttributes() {
        return Attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        Attributes = attributes;
    }
}
