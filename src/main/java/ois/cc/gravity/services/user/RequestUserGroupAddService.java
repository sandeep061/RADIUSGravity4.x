package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.db.jpa.JPAQuery;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.*;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;

public class RequestUserGroupAddService extends RequestEntityAddService
{
    public RequestUserGroupAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd req) throws Throwable {
        if (!req.getAttributes().containsKey("Code"))
        {
            throw new GravityIllegalArgumentException("Code",EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }
        JPAQuery qry = new JPAQuery("Select u from UserGroup u where u.Code=:code");
        qry.setParam("code", req.getAttributeValueOf(String.class, "Code").toString().toUpperCase());
        UserGroup usergrp = _coreDB.Find(EN.UserGroup, qry);

        if (usergrp != null)
        {
            String user = usergrp.getClass().getSimpleName();
            GravityEntityExistsException ex = new GravityEntityExistsException(user, "Id", OPRelational.Eq, usergrp.getId());
            throw ex;
        }

    }

}

