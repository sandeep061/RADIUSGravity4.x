package ois.cc.gravity.framework.events.common;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;

public class EventVersionInfoFetched extends EventOK
{

    private String Version;

    public EventVersionInfoFetched(Request request, String version)
    {
        super(request, EventCode.VersionInfoFetched);
        this.Version = version;
    }

    public String getVersion()
    {
        return this.Version;
    }

    public void setVersion(String Version)
    {
        this.Version = Version;
    }
}
