package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.CallerIDPlan;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;

public class RequestCallerIDPlanEditService extends RequestEntityEditService
{

    public RequestCallerIDPlanEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit req, AEntity thisentity) throws Throwable
    {
        CallerIDPlan clPln = (CallerIDPlan) thisentity;

//        //Campaign must be Unload.
         AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), clPln.getAOPs());

    }

}

