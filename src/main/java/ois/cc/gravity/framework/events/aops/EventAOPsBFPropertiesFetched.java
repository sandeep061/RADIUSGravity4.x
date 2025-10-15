package ois.cc.gravity.framework.events.aops;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;

import java.util.HashMap;

public class EventAOPsBFPropertiesFetched extends EventOK
{

    private HashMap<String, String> AOPsBFProperties;

    private Integer Offset;
    private Integer Limit;
    private Integer RecordCount;

    public EventAOPsBFPropertiesFetched(Request request)
    {
        super(request, EventCode.AOPsBFPropertiesFetched);
        this.AOPsBFProperties = new HashMap<>();
    }

    public HashMap<String, String> getAOPsBFProperties()
    {
        return AOPsBFProperties;
    }

    public void setAOPsBFProperties(HashMap<String, String> AOPsBFProperties)
    {
        this.AOPsBFProperties = AOPsBFProperties;
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
