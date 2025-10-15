package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XAICategory;
import ois.radius.ca.enums.XAIPlatformID;
import ois.radius.ca.enums.XAIPlatformSID;

import java.util.ArrayList;

public class RequestAOPsAIPropertiesAdd extends Request {

    @Param(Optional = false)
    private Long AOPs;

    @Param(Optional = false)
    private XAICategory XAICategory;

    @Param(Optional = false)
    private XAIPlatformID XAIPlatformID;

    @Param(Optional = false)
    private ArrayList<XAIPlatformSID> XAIPlatformSIDs;

    private ArrayList<Channel> Channels;

    /**
     * It will store the properties of XAIPlatformSIDs in JSON. {XPlatformKey:'',XPlatformUAKey:''}
     */
    @Param(Optional = false)
    private String Properties;

    public RequestAOPsAIPropertiesAdd(String requestid) {
        super(requestid, GReqType.Config, GReqCode.AOPsAIPropertiesAdd);
    }

    public Long getAOPs() {
        return AOPs;
    }

    public void setAOPs(Long AOPs) {
        this.AOPs = AOPs;
    }

    public XAICategory getXAICategory() {
        return XAICategory;
    }

    public void setXAICategory(XAICategory XAICategory) {
        this.XAICategory = XAICategory;
    }

    public XAIPlatformID getXAIPlatformID() {
        return XAIPlatformID;
    }

    public void setXAIPlatformID(XAIPlatformID XAIPlatformID) {
        this.XAIPlatformID = XAIPlatformID;
    }

    public ArrayList<XAIPlatformSID> getXAIPlatformSIDs() {
        return XAIPlatformSIDs;
    }

    public void setXAIPlatformSIDs(ArrayList<XAIPlatformSID> XAIPlatformSIDs) {
        this.XAIPlatformSIDs = XAIPlatformSIDs;
    }

    public String getProperties() {
        return Properties;
    }

    public void setProperties(String properties) {
        Properties = properties;
    }

    public ArrayList<Channel> getChannels() {
        return Channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        Channels = channels;
    }
}
