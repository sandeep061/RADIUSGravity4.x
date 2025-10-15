package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;


public class RequestAOPsCDNAddressEdit extends Request
{
    @Param(Optional = false)
    private Long AOPsId;
    @Param(Optional = false)
    private String AOPsCDNCode;
    @Param(Optional = false)
    private ois.radius.ca.enums.Channel Channel;
    private String Address;
    private Long XPlatformUA;
    private ois.radius.ca.enums.XPlatformID XPlatformID;
    private ois.radius.ca.enums.XPlatformSID XPlatformSID;
    private Boolean IsWeb;

    public RequestAOPsCDNAddressEdit(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.AOPsCDNAddressEdit);
    }

    public Long getAOPsId()
    {
        return AOPsId;
    }

    public void setAOPsId(Long AOPsId)
    {
        this.AOPsId = AOPsId;
    }

    public String getAOPsCDNCode()
    {
        return AOPsCDNCode;
    }

    public void setAOPsCDNCode(String AOPsCDNCode)
    {
        this.AOPsCDNCode = AOPsCDNCode;
    }

    public Long getXPlatformUA()
    {
        return XPlatformUA;
    }

    public void setXPlatformUA(Long XPlatformUA)
    {
        this.XPlatformUA = XPlatformUA;
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
}
