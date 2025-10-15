package ois.cc.gravity.services.aops;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.db.queries.AOPsBFQuery;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.BFCode;
import ois.radius.ca.enums.aops.AOPsType;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;

import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityIllegalObjectTypeException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.radius.cc.entities.tenant.cc.AOPsBF;

import java.util.ArrayList;

public class RequestAOPsBFAddService extends RequestEntityAddService
{

    public RequestAOPsBFAddService(UAClient uac)
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

        //Check AOPs type if not process throw exception
        Long id = reqenadd.getAttributeValueOf(Long.class, "AOPs");
        ValidateAOPs(id);

        if (reqenadd.getAttributes().containsKey("BFCode"))
        {
            try
            {
                BFCode bfcode = reqenadd.getAttributeValueOf(BFCode.class, "BFCode");
            }
            catch (Exception ex)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, reqenadd.getAttributes().get("BFCode").toString());
            }
        }
        if (reqenadd.getAttributes().containsKey("IsEnable"))
        {
            boolean isenable = reqenadd.getAttributeValueOf(Boolean.class, "IsEnable");
            if (isenable)
            {
                ArrayList<AOPsBF> aopsbfs = _tctx.getDB().Select(new AOPsBFQuery().filterByAOPs(id).filterByIsEnable(isenable));
                if (!aopsbfs.isEmpty())
                {
                    throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AOPsBFAlreadyEnabled);
                }
            }
        }
    }

    private void ValidateAOPs(Long id) throws CODEException, GravityException
    {
        AOPs aop = _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), id);
        if (!aop.getAOPsType().equals(AOPsType.Process))
        {
            throw new GravityIllegalObjectTypeException(EN.AOPs.name(), aop.getId().toString(), aop.getAOPsType().name(), AOPsType.Process.name());
        }
    }
}
