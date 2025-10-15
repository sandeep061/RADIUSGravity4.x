package ois.cc.gravity.framework.events;

import CrsCde.CODE.Common.Utils.DATEUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;

import java.io.Serializable;
import java.util.TimeZone;

public abstract class Event implements Serializable
{

    protected String EvId;
    private final EventType EvType;
    private final EventCode EvCode;

    //V:280223 - Timestamp and timezone offset added in all radius events.
    private final Integer EvTZOffset;

    /**
     * Time(When event sent) in millisecond.
     */
    private final Long EvTime;

    protected Event(EventType type, EventCode code)
    {
        this.EvId = UIDUtil.GenerateUniqueId();
        this.EvType = type;
        this.EvCode = code;

        this.EvTime = DATEUtil.Now().getTime();
        this.EvTZOffset = TimeZone.getDefault().getOffset(EvTime);
    }

    public String getEvId()
    {
        return EvId;
    }

    public EventType getEvType()
    {
        return EvType;
    }

    public EventCode getEvCode()
    {
        return EvCode;
    }

    public Integer getEvTZOffset()
    {
        return EvTZOffset;
    }

    public Long getEvTime()
    {
        return EvTime;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "{" + "EvId=" + EvId + ", EvType=" + EvType + ", EvCode=" + EvCode + '}';
    }

}

