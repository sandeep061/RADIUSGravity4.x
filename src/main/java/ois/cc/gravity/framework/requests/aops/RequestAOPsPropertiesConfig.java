package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

import java.util.HashMap;

public class RequestAOPsPropertiesConfig extends Request
{

    @Param(Optional = false)
    private Long Campaign;

    @Param(Optional = false)
    private HashMap<String, String> Attributes;

    public RequestAOPsPropertiesConfig(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.AOPsPropertiesConfig);

        this.Attributes = new HashMap<>();
    }

    public Long getCampaign()
    {
        return Campaign;
    }

    public void setCampaign(Long CampaignCode)
    {
        this.Campaign = CampaignCode;
    }

    public HashMap<String, String> getAttributes()
    {
        return Attributes;
    }

    public void setAttributes(HashMap<String, String> Attributes)
    {
        this.Attributes = Attributes;
    }

    public Object getAttribute(String name)
    {
        return this.Attributes.get(name);
    }

    public void setAttribute(String name, String value)
    {
        this.Attributes.put(name, value);
    }

}

