package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;

public class RequestSkillEditService extends RequestEntityEditService
{

    public RequestSkillEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity entity) throws Throwable
    {
        if (reqenedit.getAttributes().containsKey("Queue"))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UnAuthorizedRequest);
        }
    }
}
