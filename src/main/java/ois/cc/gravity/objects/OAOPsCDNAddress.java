package ois.cc.gravity.objects;


import jakarta.persistence.*;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.tenant.cc.AOPsCDN;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

public class OAOPsCDNAddress extends AObject
{


    private ois.radius.ca.enums.Channel Channel;

    private String Address;

    private ois.radius.cc.entities.tenant.cc.XPlatformUA XPlatformUA;

    private ois.radius.ca.enums.XPlatformID XPlatformID;

    private ois.radius.ca.enums.XPlatformSID XPlatformSID;

    private Boolean IsWeb;

    private ois.radius.cc.entities.tenant.cc.AOPsCDN AOPsCDN;

    public OAOPsCDNAddress()
    {
    }

    public ois.radius.ca.enums.Channel getChannel()
    {
        return Channel;
    }

    public void setChannel(ois.radius.ca.enums.Channel channel)
    {
        Channel = channel;
    }

    public String getAddress()
    {
        return Address;
    }

    public void setAddress(String address)
    {
        Address = address;
    }

    public ois.radius.cc.entities.tenant.cc.XPlatformUA getXPlatformUA()
    {
        return XPlatformUA;
    }

    public void setXPlatformUA(ois.radius.cc.entities.tenant.cc.XPlatformUA XPlatformUA)
    {
        this.XPlatformUA = XPlatformUA;
    }

    public ois.radius.ca.enums.XPlatformID getXPlatformID()
    {
        return XPlatformID;
    }

    public void setXPlatformID(ois.radius.ca.enums.XPlatformID XPlatformID)
    {
        this.XPlatformID = XPlatformID;
    }

    public ois.radius.ca.enums.XPlatformSID getXPlatformSID()
    {
        return XPlatformSID;
    }

    public void setXPlatformSID(ois.radius.ca.enums.XPlatformSID XPlatformSID)
    {
        this.XPlatformSID = XPlatformSID;
    }

    public Boolean getWeb()
    {
        return IsWeb;
    }

    public void setWeb(Boolean web)
    {
        IsWeb = web;
    }

    public ois.radius.cc.entities.tenant.cc.AOPsCDN getAOPsCDN()
    {
        return AOPsCDN;
    }

    public void setAOPsCDN(ois.radius.cc.entities.tenant.cc.AOPsCDN AOPsCDN)
    {
        this.AOPsCDN = AOPsCDN;
    }
}
