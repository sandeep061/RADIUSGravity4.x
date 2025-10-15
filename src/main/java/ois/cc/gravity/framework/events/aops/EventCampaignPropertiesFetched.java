package ois.cc.gravity.framework.events.aops;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;

import java.util.HashMap;
import ois.radius.cc.entities.tenant.cc.AOPsProperties;

public class EventCampaignPropertiesFetched extends EventOK
{
    private HashMap<AOPsProperties.Keys,String> AOPsProperties ;

    private Integer Offset;
    private Integer Limit;
    private Integer RecordCount;

    public EventCampaignPropertiesFetched(Request request)
    {
        super(request, EventCode.AOPsPropertiesFetched);
        this.AOPsProperties = new HashMap<>();
    }

    public HashMap<ois.radius.cc.entities.tenant.cc.AOPsProperties.Keys, String> getAOPsProperties()
    {
        return AOPsProperties;
    }

    public void setAOPsProperties(HashMap<ois.radius.cc.entities.tenant.cc.AOPsProperties.Keys, String> AOPsProperties)
    {
       this.AOPsProperties = AOPsProperties;
    }

    public Integer getOffset() {
        return Offset;
    }

    public void setOffset(Integer offset) {
        Offset = offset;
    }

    public Integer getLimit() {
        return Limit;
    }

    public void setLimit(Integer limit) {
        Limit = limit;
    }

    public Integer getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(Integer recordCount) {
        RecordCount = recordCount;
    }
}
