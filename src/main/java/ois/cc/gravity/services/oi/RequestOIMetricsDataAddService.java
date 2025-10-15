package ois.cc.gravity.services.oi;

import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.ua.UAClient;

public class RequestOIMetricsDataAddService extends RequestEntityAddService {
    public RequestOIMetricsDataAddService(UAClient uac) {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable {

    }
}
