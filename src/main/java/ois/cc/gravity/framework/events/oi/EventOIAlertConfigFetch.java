package ois.cc.gravity.framework.events.oi;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OAlertConfig;

import java.util.ArrayList;

public class EventOIAlertConfigFetch extends EventOK {


    private Integer Offset;
    private Integer Limit;
    private Integer RecordCount;
    private  ArrayList<OAlertConfig> OIAlertConfig;

    public EventOIAlertConfigFetch(Request request) {
        super(request, EventCode.OIAlertConfigFetch);
    }

    public Integer getOffset() {
        return Offset;
    }

    public void setOffset(Integer offset) {
        Offset = offset;
    }

    public Integer getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(Integer recordCount) {
        RecordCount = recordCount;
    }

    public Integer getLimit() {
        return Limit;
    }

    public void setLimit(Integer limit) {
        Limit = limit;
    }

    public ArrayList<OAlertConfig> getOIAlertConfig() {
        return OIAlertConfig;
    }

    public void setOIAlertConfig(ArrayList<OAlertConfig> OIAlertConfig) {
        this.OIAlertConfig = OIAlertConfig;
    }
}
