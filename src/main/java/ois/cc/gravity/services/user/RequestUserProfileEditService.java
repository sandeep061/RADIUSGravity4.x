package ois.cc.gravity.services.user;

import code.ua.events.Event;
import code.ua.events.EventEntityEdited;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Profile;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserProfile;

public class RequestUserProfileEditService extends ARequestEntityService
{

    public RequestUserProfileEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityEdit reqedit = (RequestEntityEdit) request;
        User user = null;
        Profile profile = null;
        UserProfile uprofile = _tctx.getDB().FindAssert(EN.UserProfile.getEntityClass(), reqedit.getEntityId());
        if (reqedit.getAttributes().containsKey("User"))
        {
            user = _tctx.getDB().FindAssert(new UserQuery().filterByUserId(reqedit.getAttributeValueOf(String.class, "User")));
            uprofile.setUser(user);
        }

        if (reqedit.getAttributes().containsKey("Profile"))
        {
            profile = _tctx.getDB().FindAssert(EN.Profile.getEntityClass(), Long.valueOf(reqedit.getAttributeValueOf(String.class, "Profile")));
            uprofile.setProfile(profile);
        }

        _tctx.getDB().Update(_uac.getUserSession().getUser(), uprofile);

        EventEntityEdited ev = new EventEntityEdited(reqedit, uprofile);
        return ev;
    }

//    @Override
//    protected void DoPreProcess(RequestEntityEdit req, AEntity thisentity) throws Throwable
//    {
////        UserProfile userprof = (UserProfile) thisentity;
////        Long userid = userprof.getUser().getId();
////        User user = _tctx.getDB().FindAssert(EN.UserGroup.getEntityClass(), userid);
////
////        Long profileId = userprof.getProfile().getId();
////        Profile prof = _tctx.getDB().FindAssert(Profile.class, profileId);
////
////        UAAPServiceManager.This().MapPolicyUser(_tctx,prof.getCode(), user.getId());
//
//    }
}
