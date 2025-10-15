package ois.cc.gravity.services.exceptions;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.Event;

public class GravityUnhandledRealMException extends GravityUnhandledException
{
    private Event _event;

    private String _eventstr;
    public GravityUnhandledRealMException(Throwable cause)
    {
        super(cause);
    }


    public GravityUnhandledRealMException(Event event) throws Exception
    {

        this(new Exception(JSONUtil.ToJSON(event).toString()));
        this._event = event;
    }
    public GravityUnhandledRealMException(String event) throws Exception
    {

        this(new Exception(JSONUtil.ToJSON(event).toString()));
        this._eventstr = event;
    }
    public Event  getEvent()
    {
        return _event;
    }

    public String getEventstr()
    {
        return _eventstr;
    }
    
}
