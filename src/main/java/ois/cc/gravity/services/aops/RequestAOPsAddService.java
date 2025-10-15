package ois.cc.gravity.services.aops;

import code.ua.events.*;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.*;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.aops.AOPsType;
import ois.radius.ca.enums.aops.ProcessType;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.cc.gravity.db.queries.AOPsQuery;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.services.common.RequestEntityAddService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import code.ua.requests.Request;

public class RequestAOPsAddService extends RequestEntityAddService
{

    private static Logger logger = LoggerFactory.getLogger(RequestAOPsAddService.class);

    public RequestAOPsAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable
    {

        RequestEntityAdd req = (RequestEntityAdd) request;

        /**
         * Maximum 128 Campaign can be added including all EntityState.<br>
         */
        int campSize = _tctx.getDB().Select(new AOPsQuery()).size();
        if (campSize > 128)
        {
            // TBD: Need to send a failed event.
        }
        if (!req.getAttributes().containsKey("Code"))
        {
            throw new GravityIllegalArgumentException("Code", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
        if (!req.getAttributes().containsKey("Name"))
        {
            throw new GravityIllegalArgumentException("Name", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
        if (!req.getAttributes().containsKey("AOPSType"))
        {
            throw new GravityIllegalArgumentException("AOPSType", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }

        AOPsType aopsType = req.getAttributeValueOf(AOPsType.class, "AOPSType");

        EN en = null;

        AOPs aops = null;
        try
        {
            switch (aopsType)
            {
                case Campaign ->
                    en = EN.Campaign;
                case Process ->
                {
                    if (!req.getAttributes().containsKey("ProcessType"))
                    {
                        throw new GravityIllegalArgumentException("ProcessType", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
                    }
                    ProcessType.valueOf(req.getAttributeValueOf(String.class, "ProcessType"));
                    en = EN.Process;
                }
            }

            aops = EntityBuilder.New(en);
            EntityBuilder.BuildEntity(_tctx.getDB(), aops, req.getAttributes());

            _tctx.getALMCtx().CreateAOPs(aops);


            _tctx.getDB().Insert(_uac.getUserSession().getUser(), aops);

        }
        catch (GravityNoSuchFieldException fex)
        {
            EventInvalidAttribute ev = new EventInvalidAttribute(request, "Campaign", fex.getFlieldName());
            return ev;
        }
        catch (GravityUnhandledException gex)
        {
            throw new GravityUnhandledRealMException(gex.getMessage());
        }
        catch (GravityUniqueConstraintViolationException uex)
        {
            EventEntityExists evEntityExist = new EventEntityExists(request, aopsType.name());
            evEntityExist.setCondition(uex.getCondition());
            return evEntityExist;
        }

        EventEntityAdded ev = new EventEntityAdded(req, aops);
        return ev;
    }

}
