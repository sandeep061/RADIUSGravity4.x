package ois.cc.gravity.framework.events.xs;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OXServerEndpointProperties;

import java.util.ArrayList;

public class EventXServerEndpointPropertiesFetched extends EventOK {

    private ArrayList<OXServerEndpointProperties> XServerEndpointProperties = new ArrayList<>();

    private Integer Offset;

    private Integer Limit;

    private Integer RecordCount;

    public EventXServerEndpointPropertiesFetched(Request request) {
        super(request, EventCode.XServerEndpointPropertiesFetched);
    }

    public ArrayList<OXServerEndpointProperties> getXServerEndpointProperties() {
        return XServerEndpointProperties;
    }

    public void setXServerEndpointProperties(ArrayList<OXServerEndpointProperties> XServerEndpointProperties) {
        this.XServerEndpointProperties = XServerEndpointProperties;
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
