package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.ContactBookAddressQuery;
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

import java.util.ArrayList;
import ois.cc.gravity.entities.util.UserUtil;

public class RequestContactBookDeleteService extends ARequestEntityService
{

    public RequestContactBookDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityDelete req = (RequestEntityDelete) request;
        ArrayList<AEntity> deleteEntities = new ArrayList<>();

        ContactBook conbook = _tctx.getDB().FindAssert(EN.ContactBook.getEntityClass(), req.getEntityId());

        User user = _uac.getUserSession().getUser();
        if (UserUtil.GetUserRole(_tctx, user).equals(UserRole.Agent)
                && !conbook.getCreatedBy().equals(user.getId()))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.UnAuthorizedAccessToContactBook, "User Id==" + user.getUserId());
        }

        ArrayList<ContactBookAddress> conBookAdd = _tctx.getDB().Select(new ContactBookAddressQuery().filterByContactBook(conbook.getId()));
        deleteEntities.addAll(conBookAdd);
        deleteEntities.add(conbook);

        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(), deleteEntities);

        return new EventEntityDeleted(req, conbook);

    }
}
