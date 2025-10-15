package ois.cc.gravity.objects;


import ois.radius.cc.entities.tenant.cc.AOPsCallerIdAddress;

import java.util.ArrayList;

public class OAOPsCallerId extends AObject
{
    private String Code;

    private ois.radius.cc.entities.tenant.cc.AOPs AOPs;

    private ois.radius.ca.enums.Channel Channel;

    private ois.radius.ca.enums.CLISelectionStrategy CLISelectionStrategy;

    private AOPsCallerIdAddress DefXPlatformUA;

    private ArrayList<OAOPsCallerIdAddress> AOPsCallerIdAddress;
    public OAOPsCallerId()
    {
    }

    public ArrayList<OAOPsCallerIdAddress> getAOPsCallerIdAddress()
    {
        return AOPsCallerIdAddress;
    }

    public void setAOPsCallerIdAddress(ArrayList<OAOPsCallerIdAddress> AOPsCallerIdAddress)
    {
        this.AOPsCallerIdAddress = AOPsCallerIdAddress;
    }

    public String getCode()
    {
        return Code;
    }

    public void setCode(String code)
    {
        Code = code;
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

    public ois.radius.ca.enums.CLISelectionStrategy getCLISelectionStrategy()
    {
        return CLISelectionStrategy;
    }

    public void setCLISelectionStrategy(ois.radius.ca.enums.CLISelectionStrategy CLISelectionStrategy)
    {
        this.CLISelectionStrategy = CLISelectionStrategy;
    }

    public AOPsCallerIdAddress getDefXPlatformUA()
    {
        return DefXPlatformUA;
    }

    public void setDefXPlatformUA(AOPsCallerIdAddress defXPlatformUA)
    {
        DefXPlatformUA = defXPlatformUA;
    }
}
