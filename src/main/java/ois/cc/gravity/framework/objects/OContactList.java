package ois.cc.gravity.framework.objects;

public class OContactList extends AObject {

    private String Code;

    private String Name;

    private String Description;

    private Integer NoOfRecs;

    private Integer NoOfDNCs;

    private String State;

    private Integer Priority;

    private Integer Factor;

//    private OCampaign Campaign;

    private Integer ChurnLimit;

    public OContactList()
    {
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String Name)
    {
        this.Name = Name;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String Description)
    {
        this.Description = Description;
    }

    public Integer getPriority()
    {
        return Priority;
    }

    public void setPriority(Integer Priority)
    {
        this.Priority = Priority;
    }

    public Integer getFactor()
    {
        return Factor;
    }

    public void setFactor(Integer Factor)
    {
        this.Factor = Factor;
    }

//    public OCampaign getCampaign()
//    {
//        return Campaign;
//    }
//
//    public void setCampaign(OCampaign Campaign)
//    {
//        this.Campaign = Campaign;
//    }

    public String getCode()
    {
        return Code;
    }

    public void setCode(String Code)
    {
        this.Code = Code;
    }

    public String getState()
    {
        return State;
    }

    public void setState(String State)
    {
        this.State = State;
    }

    public Integer getChurnLimit()
    {
        return ChurnLimit;
    }

    public void setChurnLimit(Integer ChurnLimit)
    {
        this.ChurnLimit = ChurnLimit;
    }

    public Integer getNoOfRecs()
    {
        return NoOfRecs;
    }

    public void setNoOfRecs(Integer NoOfRecs)
    {
        this.NoOfRecs = NoOfRecs;
    }

    public Integer getNoOfDNCs()
    {
        return NoOfDNCs;
    }

    public void setNoOfDNCs(Integer NoOfDNCs)
    {
        this.NoOfDNCs = NoOfDNCs;
    }

}
