package ois.cc.gravity.services.user;

import code.entities.AEntity;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.common.RequestEntityDeleteService;
import ois.cc.gravity.ua.UAClient;

public class RequestUserProfileDeleteService extends RequestEntityDeleteService
{

    public RequestUserProfileDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void doPreProcessDelete(RequestEntityDelete req, AEntity entity) throws Throwable
    {
//        UserProfile uprof = (UserProfile) entity;
//        Profile profile = uprof.getProfile();
//        ArrayList<Policy> fetchPolicies = UAAPServiceManager.This().FetchPoliciesByCode(_tctx,profile);
//        if (!fetchPolicies.isEmpty())
//        {
//            UAAPServiceManager.This().UnmapPolicyUser(_tctx,profile.getCode(), uprof.getUser().getId());
//        }

    }

}
