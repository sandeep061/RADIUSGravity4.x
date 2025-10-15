package ois.cc.gravity.objects;

import java.util.ArrayList;

public class OAOPsCDN extends AObject
{

    private String Code;

    private ois.radius.ca.enums.Channel Channel;

    private ois.radius.cc.entities.tenant.cc.AOPs AOPs;

    private ois.radius.cc.entities.tenant.cc.WorkFlow WorkFlow;

    private ArrayList<OAOPsCDNAddress> AOPsCDNAddresses;

    public OAOPsCDN()
    {
    }

    public String getCode()
    {
        return Code;
    }

    public void setCode(String code)
    {
        Code = code;
    }

    public ArrayList<OAOPsCDNAddress> getAOPsCDNAddresses()
    {
        return AOPsCDNAddresses;
    }

    public void setAOPsCDNAddresses(ArrayList<OAOPsCDNAddress> AOPsCDNAddresses)
    {
        this.AOPsCDNAddresses = AOPsCDNAddresses;
    }

    public ois.radius.cc.entities.tenant.cc.WorkFlow getWorkFlow()
    {
        return WorkFlow;
    }

    public void setWorkFlow(ois.radius.cc.entities.tenant.cc.WorkFlow workFlow)
    {
        WorkFlow = workFlow;
    }

    public ois.radius.cc.entities.tenant.cc.AOPs getAOPs()
    {
        return AOPs;
    }

    public void setAOPs(ois.radius.cc.entities.tenant.cc.AOPs AOPs)
    {
        this.AOPs = AOPs;
    }

    public ois.radius.ca.enums.Channel getChannel()
    {
        return Channel;
    }

    public void setChannel(ois.radius.ca.enums.Channel channel)
    {
        Channel = channel;
    }
}
