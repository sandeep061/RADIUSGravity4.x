package ois.cc.gravity.services.user;

import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Profile;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserProfile;

public class RequestUserProfileAddService extends ARequestEntityService
{

    public RequestUserProfileAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd reqadd = (RequestEntityAdd) request;
        ValidateRequestAttribute(reqadd);
        String userid = reqadd.getAttributeValueOf(String.class, "User");
        User user = _tctx.getDB().FindAssert(new UserQuery().filterByUserId(userid));

        Long profileId = reqadd.getAttributeValueOf(Long.class, "Profile");
        Profile prof = _tctx.getDB().FindAssert(EN.Profile.getEntityClass(), profileId);
        UserProfile userProfile = new UserProfile();
        userProfile.setProfile(prof);
        userProfile.setUser(user);

        _tctx.getDB().Insert(_uac.getUserSession().getUser(), userProfile);

        EventEntityAdded ev = new EventEntityAdded(reqadd, userProfile);
        return ev;
    }

    private void ValidateRequestAttribute(RequestEntityAdd reqadd) throws GravityIllegalArgumentException
    {

        if (!reqadd.getAttributes().containsKey("User"))
        {
            throw new GravityIllegalArgumentException("User", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }

        if (!reqadd.getAttributes().containsKey("Profile"))
        {
            throw new GravityIllegalArgumentException("Profile", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }

    }

}
