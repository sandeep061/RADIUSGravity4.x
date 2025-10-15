package ois.cc.gravity.services.aops;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.Limits;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.xsess.XSessStatus;
import ois.radius.cc.entities.tenant.cc.AOPsAbandon;
import org.json.JSONObject;

public class RequestAOPsAbandonAddService extends RequestEntityAddService
{

    public RequestAOPsAbandonAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {
        if (!reqenadd.getAttributes().containsKey("AOPs"))
        {
            throw new GravityIllegalArgumentException("AOPs", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        if (reqenadd.getAttributes().containsKey("AbandonType"))
        {
            XSessStatus abandonType = null;
            try
            {
                abandonType = reqenadd.getAttributeValueOf(XSessStatus.class, "AbandonType");

            }
            catch (Exception ex)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, reqenadd.getAttributes().get("AbandonType").toString());
            }
            if (!abandonType.getCategory().equals(XSessStatus.Category.Abandon))
            {
                //throw exception
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, abandonType + " not a valid " + XSessStatus.Category.Abandon.name() + " Category");
            }
        }
        if (reqenadd.getAttributes().containsKey("Action"))
        {
            try
            {
                reqenadd.getAttributeValueOf(AOPsAbandon.Action.class, "Action");
            }
            catch (Exception ex)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, reqenadd.getAttributes().get("Action").toString());
            }
        }
        if (reqenadd.getAttributes().containsKey("ActionProps"))
        {
            try
            {
                String actionProps = reqenadd.getAttributeValueOf(String.class, "ActionProps");
                JSONObject actionPropsJSON = new JSONObject(actionProps);
                validateActionProps(actionPropsJSON);
            }
            catch (Exception ex)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, reqenadd.getAttributes().get("Action").toString());
            }
        }
    }

    private void validateActionProps(JSONObject actionPropsJSON) throws GravityRuntimeCheckFailedException
    {
        if (actionPropsJSON.has(AOPsAbandon.ActionPropsKeys.ScheduleAfter.name()))
        {
            int val = Integer.parseInt(actionPropsJSON.getString(AOPsAbandon.ActionPropsKeys.ScheduleAfter.name()));
            if (val > Limits.ContactScheduledOnAfter_MAX)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, AOPsAbandon.ActionPropsKeys.ScheduleAfter.name() + " value Should not be Grater than " + Limits.ContactScheduledOnAfter_MAX);
            }
        }
    }
}
