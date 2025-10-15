package ois.cc.gravity.framework.events.xs;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OAgentMediaMap;

import java.util.ArrayList;

public class EventAgentMediaMapFetched extends EventOK {


    private ArrayList<OAgentMediaMap> Entities = new ArrayList<>();

    private Integer Offset;

    private Integer Limit;

    private Integer RecordCount;
    public EventAgentMediaMapFetched(Request request) {
        super(request, EventCode.AgentMediaMapFetched);
    }

    public ArrayList<OAgentMediaMap> getEntities() {
        return Entities;
    }

    public void setEntities(ArrayList<OAgentMediaMap> entities) {
        Entities = entities;
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
