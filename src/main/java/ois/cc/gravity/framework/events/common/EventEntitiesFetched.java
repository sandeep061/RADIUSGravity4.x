package ois.cc.gravity.framework.events.common;

import code.entities.AEntity;
import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;

import java.util.ArrayList;

public class EventEntitiesFetched extends EventOK
{
    private ArrayList<AEntity> Entities;

    private Integer Offset;
    private Integer Limit;
    private Integer RecordCount;

    public EventEntitiesFetched(Request request,ArrayList<AEntity> entities)
    {
        super(request, EventCode.EntitiesFetched);
        this.Entities= entities;
    }

    public Integer getOffset()
    {
        return Offset;
    }

    public void setOffset(Integer offset)
    {
        Offset = offset;
    }

    public Integer getLimit()
    {
        return Limit;
    }

    public void setLimit(Integer limit)
    {
        Limit = limit;
    }

    public Integer getRecordCount()
    {
        return RecordCount;
    }

    public void setRecordCount(Integer recordCount)
    {
        RecordCount = recordCount;
    }

    public ArrayList<AEntity> getEntities() {
        return Entities;
    }

    public void setEntities(ArrayList<AEntity> entities) {
        Entities = entities;
    }


}
