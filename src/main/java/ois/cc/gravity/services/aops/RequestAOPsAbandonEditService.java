package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.xsess.XSessStatus;
import ois.radius.cc.entities.tenant.cc.AOPsAbandon;

public class RequestAOPsAbandonEditService extends RequestEntityEditService
{

    public RequestAOPsAbandonEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable
    {
        if (reqenedit.getAttributes().containsKey("AbandonType"))
        {
            XSessStatus abandonType = reqenedit.getAttributeValueOf(XSessStatus.class, "AbandonType");
            if (!abandonType.getCategory().equals(XSessStatus.Category.Abandon))
            {
                //throw exception
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, abandonType + " not a valid " + XSessStatus.Category.Abandon.name() + " Category");
            }
        }
        if (reqenedit.getAttributes().containsKey("Action"))
        {
            try
            {
                reqenedit.getAttributeValueOf(AOPsAbandon.Action.class, "Action");
            }
            catch (Exception ex)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, reqenedit.getAttributes().get("Action").toString());
            }
        }
    }
}
