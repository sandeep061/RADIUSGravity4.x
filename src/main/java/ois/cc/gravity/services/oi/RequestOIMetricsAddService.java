package ois.cc.gravity.services.oi;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.MetricsKey;
import ois.radius.cc.entities.MetricsUnit;

public class RequestOIMetricsAddService extends RequestEntityAddService {
    public RequestOIMetricsAddService(UAClient uac) {
        super(uac);
    }


    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable {

        RequestEntityAdd req = (RequestEntityAdd) reqenadd;

        if (!req.getAttributes().containsKey("Code"))
        {
            throw new GravityIllegalArgumentException("Code", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
        if (req.getAttributes().containsKey("Dimension"))
        {
            EN.valueOf(req.getAttributes().get("Dimension").toString());
        }
        if (req.getAttributes().containsKey("MetricsKey"))
        {
            MetricsKey.valueOf(req.getAttributes().get("MetricsKey").toString());
        }

        if (req.getAttributes().containsKey("Unit"))
        {
            MetricsUnit.valueOf(req.getAttributes().get("Unit").toString());
        }

    }
}
