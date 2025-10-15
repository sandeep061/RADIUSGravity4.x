package ois.cc.gravity.services.user;

import code.entities.AEntity;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.common.RequestEntityDeleteService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.UserGroupAops;

public class RequestUserGroupAopsDeleteService extends RequestEntityDeleteService
{
    public RequestUserGroupAopsDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void doPreProcessDelete(RequestEntityDelete req, AEntity entity) throws Throwable
    {
        UserGroupAops ugaops = (UserGroupAops) entity;

      AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), ugaops.getAOPs());
    }

}
