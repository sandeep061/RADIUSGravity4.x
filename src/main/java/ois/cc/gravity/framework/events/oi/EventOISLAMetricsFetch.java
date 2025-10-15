package ois.cc.gravity.framework.events.oi;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.objects.OISLAMetrics;

import java.util.ArrayList;

public class EventOISLAMetricsFetch extends EventOK {

    private ArrayList<OISLAMetrics> OISLAMetrics;

    public EventOISLAMetricsFetch(Request request) {
        super(request, EventCode.OISLAMetricsFetch);
    }

    public ArrayList<OISLAMetrics> getOISLAMetrics() {
        return OISLAMetrics;
    }

    public void setOISLAMetrics(ArrayList<OISLAMetrics> OISLAMetrics) {
        this.OISLAMetrics = OISLAMetrics;
    }
}
