package ois.cc.gravity.framework.events.aops;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OAOPsCDN;

import java.util.ArrayList;

public class EventAOPsCDNFetch extends EventOK
{

    private ArrayList<OAOPsCDN> AOPsCDNs;
    public EventAOPsCDNFetch(Request request)
    {
        super(request, EventCode.AOPsCDN);
    }

    public ArrayList<OAOPsCDN> getAOPsCDNs()
    {
        return AOPsCDNs;
    }

    public void setAOPsCDNs(ArrayList<OAOPsCDN> AOPsCDNs)
    {
        this.AOPsCDNs = AOPsCDNs;
    }
}
