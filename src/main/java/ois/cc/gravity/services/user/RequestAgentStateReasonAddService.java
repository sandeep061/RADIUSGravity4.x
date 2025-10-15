package ois.cc.gravity.services.user;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.Limits;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;

public class RequestAgentStateReasonAddService extends RequestEntityAddService
{

    public RequestAgentStateReasonAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {
        if (reqenadd.getAttributes().containsKey("Timeout"))
        {
            Integer timeout = reqenadd.getAttributeValueOf(Integer.class, "Timeout");
            if (timeout < Limits.AgentStateChange_Timeout_MIN || timeout > Limits.AgentStateChange_Timeout_MAX)
            {
                throw new GravityIllegalArgumentException("Timeout", EventFailedCause.DataBoundaryLimitViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
        }

        if (reqenadd.getAttributes().containsKey("ExtendTime"))
        {
            Integer extnd = reqenadd.getAttributeValueOf(Integer.class, "ExtendTime");
            if (extnd > Limits.AgentStateChange_ExtendTime_MAX)
            {
                throw new GravityIllegalArgumentException("ExtendTime", EventFailedCause.DataBoundaryLimitViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }


}
