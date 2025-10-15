package ois.cc.gravity.services.aops;

import code.ua.events.Event;
import code.ua.events.EventEntityEdited;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.ContactBook;
import ois.radius.cc.entities.tenant.cc.User;

import java.util.HashMap;

public class RequestContactBookEditService extends ARequestEntityService
{

    public RequestContactBookEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityEdit reqedit = (RequestEntityEdit) request;
        AOPs aops = null;
        User user = null;
        ContactBook conbook = _tctx.getDB().FindAssert(EN.ContactBook.getEntityClass(), reqedit.getEntityId());
        HashMap<String, Object> attributes = reqedit.getAttributes();

        if (attributes.containsKey("AOPs"))
        {
            aops = _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), Long.valueOf(reqedit.getAttributeValueOf(String.class, "AOPs")));
        }
        if (attributes.containsKey("User"))
        {
            user = _tctx.getDB().FindAssert(new UserQuery().filterByUserId(reqedit.getAttributeValueOf(String.class, "User")));
        }

        /*
         * If this is send by a Agent then we need to set the user.
         */
        if (_uac.getUserType().equals(UserRole.Agent))
        {
            user = _uac.getUserSession().getUser();
        }

        conbook.setAOPs(aops);
        conbook.setUser(user);
        buildContactBook(reqedit, conbook);
        _tctx.getDB().Update(_uac.getUserSession().getUser(), conbook);

        EventEntityEdited ev = new EventEntityEdited(reqedit, conbook);
        return ev;
    }

    private void buildContactBook(RequestEntityEdit req, ContactBook cbook) throws Exception
    {
        if (req.getAttributes().containsKey("Name"))
        {
            cbook.setName(req.getAttributeValueOf(String.class, "Name"));
        }
        if (req.getAttributes().containsKey("Department"))
        {
            cbook.setDepartment(req.getAttributeValueOf(String.class, "Department"));
        }
        if (req.getAttributes().containsKey("Designation"))
        {
            cbook.setDesignation(req.getAttributeValueOf(String.class, "Designation"));
        }
        if (req.getAttributes().containsKey("Organization"))
        {
            cbook.setOrganization(req.getAttributeValueOf(String.class, "Organization"));
        }
        if (req.getAttributes().containsKey("Location"))
        {
            cbook.setLocation(req.getAttributeValueOf(String.class, "Location"));
        }
    }
}
