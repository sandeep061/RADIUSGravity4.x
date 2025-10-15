package ois.cc.gravity.framework.events.common;

import code.ua.events.EventFailed;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;

public class EventProcessFailed extends EventFailed
{
    private String _Message;

    public EventProcessFailed(Request request, String msg)
    {
        super(request, EventCode.ProcessFailed);
        this._Message =msg;
    }


    public String getMessage()
    {
        return _Message;
    }

    public void setMessage(String message)
    {
        this._Message = message;
    }
}
