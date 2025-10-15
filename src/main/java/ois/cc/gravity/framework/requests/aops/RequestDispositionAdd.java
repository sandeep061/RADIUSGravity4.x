package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.ca.enums.aops.CallbackType;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.xsess.XSessStatus;

import java.util.List;

public class RequestDispositionAdd extends Request
{

    @Param(Optional = false, Regex = "^[a-zA-Z](?!.*__)[a-zA-Z0-9_]+$", Length = 32)
    private String Code;

    //Regex removed for time being to support arobic laguage.
//    @Param(Optional = false, Regex = "^[a-zA-Z0-9-_\\s]+$", Length = 32)
    @Param(Optional = false, Length = 512)
    private String Name;

    /**
     * It can be null be Non-Campaign dispositions.
     */
    @Param(Optional = true)
    private Long AOPs;

    /**
     * //V:260421.2
     */
    @Param(Optional = true)
    private XSessStatus.Category Category;

    @Param(Optional = false)
    private List<Channel> Channels;

    /**
     * It can be null for below 2 cases. <br>
     * - If it is not SchduledType. <br>
     * - If it is a Sub-Disposition. <br>
     */
    @Param(Optional = true)
    private Boolean IsScheduledType;

    private Boolean IsAutoSetSchedule;

    private Long ScheduleAfter;

    private ois.radius.ca.enums.aops.CallbackType CallbackType;

    private Long SuperDispId;

    private Integer DispSeq;

    private Boolean IsDefault;

    public RequestDispositionAdd(String requestid)
    {
        super(requestid, GReqType.Config,GReqCode.DispositionAdd);
    }

    public String getCode()
    {
        return Code;
    }

    public void setCode(String Code)
    {
        this.Code = Code;
    }

    public Boolean getDefault() {
        return IsDefault==null?false:IsDefault;
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

    public Long getAOPs()
    {
        return AOPs;
    }

    public void setAOPs(Long campid)
    {
        this.AOPs = campid;
    }

    public XSessStatus.Category getCategory()
    {
        return Category;
    }

    public void setCategory(XSessStatus.Category Category)
    {
        this.Category = Category;
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
        return IsScheduledType == null ? Boolean.FALSE : IsScheduledType;
    }

    public void setIsScheduledType(Boolean IsScheduledType)
    {
        this.IsScheduledType = IsScheduledType;
    }

    public Boolean getIsAutoSetSchedule()
    {
        return IsAutoSetSchedule == null ? Boolean.FALSE : IsAutoSetSchedule;
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

    public Long getSuperDispId()
    {
        return SuperDispId;
    }

    public void setSuperDispId(Long SuperDispId)
    {
        this.SuperDispId = SuperDispId;
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
