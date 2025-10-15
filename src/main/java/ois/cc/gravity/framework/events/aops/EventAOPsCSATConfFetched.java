package ois.cc.gravity.framework.events.aops;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OAOPsCSATConf;

import java.util.ArrayList;

public class EventAOPsCSATConfFetched extends EventOK {

    private ArrayList<OAOPsCSATConf> OAOPsCSATConfs = new ArrayList<>();
    private Integer Offset;
    private Integer Limit;
    private Integer RecordCount;

    public EventAOPsCSATConfFetched(Request request) {
        super(request, EventCode.AOPsCSATConf);
    }

    public ArrayList<OAOPsCSATConf> getOAOPsCSATConfs() {
        return OAOPsCSATConfs;
    }

    public void setOAOPsCSATConfs(ArrayList<OAOPsCSATConf> OAOPsCSATConfs) {
        this.OAOPsCSATConfs = OAOPsCSATConfs;
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
