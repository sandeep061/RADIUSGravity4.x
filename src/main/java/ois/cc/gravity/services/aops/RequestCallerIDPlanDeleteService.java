package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.CallerIDPlan;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.common.RequestEntityDeleteService;

public class RequestCallerIDPlanDeleteService extends RequestEntityDeleteService
{

    public RequestCallerIDPlanDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void doPreProcessDelete(RequestEntityDelete req, AEntity entity) throws Throwable
    {
        CallerIDPlan clIdPln = (CallerIDPlan) entity;

        //Campaign must be Unload.
         AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), clIdPln.getAOPs());
    }
}
