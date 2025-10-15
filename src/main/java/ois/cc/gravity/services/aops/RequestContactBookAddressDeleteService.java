package ois.cc.gravity.services.aops;

import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.entities.util.UserUtil;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.ContactBook;
import ois.radius.cc.entities.tenant.cc.ContactBookAddress;
import ois.radius.cc.entities.tenant.cc.User;

public class RequestContactBookAddressDeleteService extends ARequestEntityService
{

    public RequestContactBookAddressDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;
        ContactBookAddress conAddress = _tctx.getDB().FindAssert(EN.ContactBookAddress.getEntityClass(), req.getEntityId());
        ContactBook conbook = _tctx.getDB().FindAssert(EN.ContactBook.getEntityClass(), conAddress.getId());
        User user = _uac.getUserSession().getUser();
        if (UserUtil.GetUserRole(_tctx, user).equals(UserRole.Agent)
                && !conbook.getCreatedBy().equals(user.getId()))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UnAuthorizedAccessToContactBookAddress, "User Id==" + user.getUserId());
        }
        _tctx.getDB().DeleteEntity(_uac.getUserSession().getUser(), conAddress);

        return new EventEntityDeleted(req, conAddress);

    }
}
