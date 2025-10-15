package ois.cc.gravity.services.user;

import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.UserGroupUserQuery;
import ois.cc.gravity.entities.util.UserUtil;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserGroup;
import ois.radius.cc.entities.tenant.cc.UserGroupUser;

public class RequestUserGroupUserAddService extends RequestEntityAddService
{
    public RequestUserGroupUserAddService(UAClient uac)
    {
        super(uac);
    }
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {
        if (!reqenadd.getAttributes().containsKey("User"))
        {
            throw new GravityIllegalArgumentException("User", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
        String userid = reqenadd.getAttributeValueOf(String.class, "User");
        User user = _tctx.getNucleusCtx().GetUserById(_tctx.getTenant().getCode(), userid, reqenadd.getAttributeValueOf(UserRole.class, "UserRole").toString());
    }
    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd reqAdd = (RequestEntityAdd) request;
        _thisen = reqAdd.getEntityName();

        DoPreProcess(reqAdd);
        if (_thisen == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqAdd.getEntityName().name());
            return ev;
        }
        if (!reqAdd.getAttributes().containsKey("UserGroup"))
        {
            throw new GravityIllegalArgumentException("User", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
        Long usergrpid = reqAdd.getAttributeValueOf(Long.class, "UserGroup");
        UserGroup usergrp = _tctx.getDB().FindAssert(UserGroup.class, usergrpid);
        if (!reqAdd.getAttributes().containsKey("User"))
        {
            throw new GravityIllegalArgumentException("User", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
        String userid = reqAdd.getAttributeValueOf(String.class, "User");

//       Fetch User by UserId
        JPAQuery query=new JPAQuery("Select u from User u where u.UserId=:uid");
        query.setParam("uid",userid);
        User user = _tctx.getDB().FindAssert(EN.User,query);

       UserGroupUser dbusergrpuser= _tctx.getDB().Find(new UserGroupUserQuery().filterByUserGroup(usergrp.getId()).filterByUserId(user.getUserId()));

       if(dbusergrpuser!=null){
           throw new GravityEntityExistsException(EN.UserGroupUser.name(),user.getName()+" User Is Already Mapped with this UserGroup "+usergrp.getCode());
       }
        UserGroupUser usergrpuser=new UserGroupUser();
        usergrpuser.setUser(user);
        usergrpuser.setUserGroup(usergrp);
        usergrpuser.setUserRole(UserRole.valueOf(reqAdd.getAttributeValueOf(String.class,"UserRole")));
        _tctx.getDB().Insert(_uac.getUserSession().getUser(),usergrpuser);
        EventEntityAdded ev = new EventEntityAdded(request, usergrpuser);
        return ev;
    }
}
