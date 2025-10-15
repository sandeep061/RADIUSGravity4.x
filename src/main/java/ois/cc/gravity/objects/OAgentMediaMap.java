package ois.cc.gravity.objects;

import ois.radius.ca.enums.EndPointType;

public class OAgentMediaMap extends AObject{

    private OXServer XServer;

    private EndPointType EndPointType;

    private OTerminal Terminal;

    private String AuthParams;

    public OXServer getXServer() {
        return XServer;
    }

    public void setXServer(OXServer XServer) {
        this.XServer = XServer;
    }

    public EndPointType getEndPointType() {
        return EndPointType;
    }

    public void setEndPointType(EndPointType endPointType) {
        EndPointType = endPointType;
    }

    public OTerminal getTerminal() {
        return Terminal;
    }

    public void setTerminal(OTerminal terminal) {
        Terminal = terminal;
    }

    public String getAuthParams() {
        return AuthParams;
    }

    public void setAuthParams(String authParams) {
        AuthParams = authParams;
    }
}
