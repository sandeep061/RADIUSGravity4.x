package ois.cc.gravity.framework.requests.aops;

import code.entities.EntityState;
import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.ca.enums.aops.CallbackType;
import ois.radius.ca.enums.Channel;

import java.util.List;

public class RequestDispositionEdit extends Request
{

    @Param(Optional = false)
    private Long DispositionId;

    //Regex removed for time being to support arobic laguage.
//    @Param(Optional = true, Regex = "^[a-zA-Z0-9-_\\s]+$",Length = 32)
    @Param(Optional = true, Length = 512)
    private String Name;

    @Param(Optional = true)
    private List<Channel> Channels;

    @Param(Optional = true)
    private Boolean IsScheduledType;

    private Boolean IsAutoSetSchedule;

    private Long ScheduleAfter;

    private ois.radius.ca.enums.aops.CallbackType CallbackType;

    private code.entities.EntityState EntityState;

    private Integer DispSeq;

    private Boolean IsDefault;

    public RequestDispositionEdit(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.DispositionEdit);
    }

    public Long getDispositionId()
    {
        return DispositionId;
    }

    public void setDispositionId(Long DispositionId)
    {
        this.DispositionId = DispositionId;
    }

    public Boolean getDefault() {
        return IsDefault;
    }

    public void setDefault(Boolean aDefault) {
        IsDefault = aDefault;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String Name)
    {
        this.Name = Name;
    }

    public List<Channel> getChannels()
    {
        return Channels;
    }

    public void setChannels(List<Channel> Channels)
    {
        this.Channels = Channels;
    }

    public Boolean getIsScheduledType()
    {
        return IsScheduledType;
    }

    public void setIsScheduledType(Boolean IsScheduledType)
    {
        this.IsScheduledType = IsScheduledType;
    }

    public Boolean getIsAutoSetSchedule()
    {
        return IsAutoSetSchedule;
    }

    public void setIsAutoSetSchedule(Boolean IsAutoSetSchedule)
    {
        this.IsAutoSetSchedule = IsAutoSetSchedule;
    }

    public Long getScheduleAfter()
    {
        return ScheduleAfter;
    }

    public void setScheduleAfter(Long ScheduleAfter)
    {
        this.ScheduleAfter = ScheduleAfter;
    }

    public CallbackType getCallbackType()
    {
        return CallbackType;
    }

    public void setCallbackType(CallbackType CallbackType)
    {
        this.CallbackType = CallbackType;
    }

    public EntityState getEntityState()
    {
        return EntityState;
    }

    public void setEntityState(EntityState EntityState)
    {
        this.EntityState = EntityState;
    }

    public Integer getDispSeq()
    {
        return DispSeq;
    }

    public void setDispSeq(Integer DispSeq)
    {
        this.DispSeq = DispSeq;
    }

}
