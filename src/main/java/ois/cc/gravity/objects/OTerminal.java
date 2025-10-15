package ois.cc.gravity.objects;

import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.tenant.cc.XServer;

public class OTerminal extends AObject{


    private String Code;

    private String Name;

    private String Address;

    private String LoginId;

    private String Password;

    private Channel Channel;

    private XServer XServer;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLoginId() {
        return LoginId;
    }

    public void setLoginId(String loginId) {
        LoginId = loginId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Channel getChannel() {
        return Channel;
    }

    public void setChannel(Channel channel) {
        Channel = channel;
    }

    public XServer getXServer() {
        return XServer;
    }

    public void setXServer(XServer XServer) {
        this.XServer = XServer;
    }
}
