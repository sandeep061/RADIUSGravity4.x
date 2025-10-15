package ois.cc.gravity.framework.events;

import CrsCde.CODE.Common.Utils.ExceptionUtil;
import code.ua.requests.Request;

public abstract class EventFailed extends Event_Sync
{

    private String Message;
    private String CausedBy;
    private String StackTrace;

    public EventFailed(Request request, EventCode code)
    {
        super(request, EventType.Failed, code);
    }

    public EventFailed(Request request, EventCode code, Throwable ex)
    {
        this(request, code);

        setException(ex);
    }

    public final void setException(Throwable ex)
    {
        this.Message = ex.getMessage();
        this.CausedBy = ExceptionUtil.CausedBy(ex);
        this.StackTrace = ExceptionUtil.StackTrace(ex);
    }

    public String getMessage()
    {
        return Message;
    }

    public void setMessage(String Message)
    {
        this.Message = Message;
    }

    public String getCausedBy()
    {
        return CausedBy;
    }

    public void setCausedBy(String CausedBy)
    {
        this.CausedBy = CausedBy;
    }

    public String getStackTrace()
    {
        return StackTrace;
    }

    public void setStackTrace(String StackTrace)
    {
        this.StackTrace = StackTrace;
    }

}

