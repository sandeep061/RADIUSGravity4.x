package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import code.ua.requests.Request;
import code.ua.requests.RequestCode;
import code.ua.requests.RequestType;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XAIPlatformID;
import ois.radius.ca.enums.XAIPlatformSID;

import java.util.ArrayList;

public class RequestAOPsAIPropertiesEdit extends Request {

   private Long Id;

    private XAIPlatformID XAIPlatformID;


    private ArrayList<XAIPlatformSID> XAIPlatformSIDs;

    private ArrayList<Channel> Channels;

    /**
     * It will store the properties of XAIPlatformSIDs in JSON. {XPlatformKey:'',XPlatformUAKey:''}
     */

    private String Properties;

    public RequestAOPsAIPropertiesEdit(String requestid) {
        super(requestid, GReqType.Config, GReqCode.AOPsAIPropertiesEdit);
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

    public ArrayList<Channel> getChannels() {
        return Channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        Channels = channels;
    }

    public String getProperties() {
        return Properties;
    }

    public void setProperties(String properties) {
        Properties = properties;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }
}
