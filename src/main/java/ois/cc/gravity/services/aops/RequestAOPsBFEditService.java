package ois.cc.gravity.services.aops;

import code.common.exceptions.CODEException;
import code.entities.AEntity;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalObjectTypeException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.aops.AOPsType;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;

public class RequestAOPsBFEditService extends RequestEntityEditService
{

    public RequestAOPsBFEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable
    {
        if (reqenedit.getAttributes().containsKey("AOPs"))
        {
            Long id = reqenedit.getAttributeValueOf(Long.class, "AOPs");
            ValidateAOPs(id);
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
