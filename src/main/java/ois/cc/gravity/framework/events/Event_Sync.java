package ois.cc.gravity.framework.events;

import code.ua.requests.Request;
import code.ua.requests.RequestCode;
import code.ua.requests.RequestType;

public abstract class Event_Sync extends Event
{

    /**
     * Request attributes
     */
    protected String ReqId;
    protected RequestType ReqType;
    protected RequestCode ReqCode;

    public Event_Sync(Request request, EventType type, EventCode code)
    {
        super(type, code);
        this.ReqId = request.getReqId();
        this.ReqType = request.getReqType();
        this.ReqCode = request.getReqCode();
    }

    public String getRequestId()
    {
        return ReqId;
    }

    public void setRequestId(String RequestId)
    {
        this.ReqId = RequestId;
    }

    public RequestCode getReqCode()
    {
        return ReqCode;
    }

    public void setReqCode(RequestCode reqcode)
    {
        this.ReqCode = reqcode;
    }

    public RequestType getReqType()
    {
        return ReqType;
    }

    public void setReqType(RequestType reqtype)
    {
        this.ReqType = reqtype;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "{" + "EvId=" + EvId + ", EvType=" + getEvType() + ", EvCode=" + getEvCode() + ", ReqId=" + ReqId + ", GReqCode=" + ReqCode + '}';
    }
}
