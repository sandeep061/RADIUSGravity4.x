package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.DialIDPlan;

public class RequestDialIDPlanEditService extends RequestEntityEditService
{

    public RequestDialIDPlanEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit req, AEntity thisentity) throws Throwable
    {
        DialIDPlan dlPln = (DialIDPlan) thisentity;

        //Campaign must be Unload.
      AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(),dlPln.getAOPs());
    }



}
