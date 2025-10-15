package ois.cc.gravity.framework.events.aops;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OAOPsCallerId;

import java.util.ArrayList;

public class EventAOPsCallerIdFetch extends EventOK
{
    private ArrayList<OAOPsCallerId> AOPsCallerIds;
    public EventAOPsCallerIdFetch(Request request)
    {
        super(request, EventCode.AOPsCallerId);
    }

    public ArrayList<OAOPsCallerId> getAOPsCallerIds()
    {
        return AOPsCallerIds;
    }

    public void setAOPsCallerIds(ArrayList<OAOPsCallerId> AOPsCallerIds)
    {
        this.AOPsCallerIds = AOPsCallerIds;
    }
}
