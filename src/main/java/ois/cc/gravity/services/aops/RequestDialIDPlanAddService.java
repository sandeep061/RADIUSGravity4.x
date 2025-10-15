package ois.cc.gravity.services.aops;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
public class RequestDialIDPlanAddService extends RequestEntityAddService
{

    public RequestDialIDPlanAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd req) throws Throwable
    {
        //Campaign is mandatory attibute in request.
        Long campid = req.getAttributeValueOf(Long.class, EN.AOPs.name());
        if (campid == null)
        {
            throw new GravityIllegalArgumentException("Campaign", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        AOPs aops = _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), campid);

        //Check campaign state,it must be in Unload.
         AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aops);

    }


}

