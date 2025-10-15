package ois.cc.gravity.framework.events.common;

import code.ua.events.EventFailed;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;

public class EventRequestValidationFailed extends EventFailed
{

    private String ParamName;
    private EvCauseRequestValidationFail EvCause;

    public EventRequestValidationFailed(Request request, String paramname, EvCauseRequestValidationFail evcause)
    {
        super(request, EventCode.RequestValidationFailed);
        this.ParamName = paramname;
        this.EvCause = evcause;
    }

    public EventRequestValidationFailed(Request request, EvCauseRequestValidationFail evcause, String... paramnames)
    {
        super(request, EventCode.RequestValidationFailed);
        this.ParamName = String.join(",", paramnames);
        this.EvCause = evcause;
    }
}
