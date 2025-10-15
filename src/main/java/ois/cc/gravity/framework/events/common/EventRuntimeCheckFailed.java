package ois.cc.gravity.framework.events.common;

import code.ua.events.EventFailed;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;

public class EventRuntimeCheckFailed extends EventFailed
{

    private final EvCauseRuntimeCheckFailed EvCause;

    public EventRuntimeCheckFailed(Request request, EvCauseRuntimeCheckFailed evcause)
    {
        super(request, EventCode.RuntimeCheckFailed);
        this.EvCause = evcause;
    }

    public EvCauseRuntimeCheckFailed getEvCause()
    {
        return EvCause;
    }

}
