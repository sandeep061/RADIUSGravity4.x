package ois.cc.gravity.objects;

import ois.radius.ca.enums.EndPointType;

public class OXServerEndpointProperties extends AObject{

    private EndPointType EndpointType;

    private OXServer XServer;

    private String PropKey;

    private String PropValue;

    public EndPointType getEndpointType() {
        return EndpointType;
    }

    public void setEndpointType(EndPointType endpointType) {
        EndpointType = endpointType;
    }

    public OXServer getXServer() {
        return XServer;
    }

    public void setXServer(OXServer XServer) {
        this.XServer = XServer;
    }

    public String getPropKey() {
        return PropKey;
    }

    public void setPropKey(String propKey) {
        PropKey = propKey;
    }

    public String getPropValue() {
        return PropValue;
    }

    public void setPropValue(String propValue) {
        PropValue = propValue;
    }
}
