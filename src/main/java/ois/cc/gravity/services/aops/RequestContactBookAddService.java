package ois.cc.gravity.services.aops;

import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.ContactBook;
import ois.radius.cc.entities.tenant.cc.User;

import java.util.HashMap;
import ois.radius.cc.entities.UserRole;

public class RequestContactBookAddService extends ARequestEntityService
{

    public RequestContactBookAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd reqadd = (RequestEntityAdd) request;
        AOPs aops = null;
        User user = null;
        HashMap<String, Object> attributes = reqadd.getAttributes();

        if (attributes.containsKey("AOPs"))
        {
            aops = _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), Long.valueOf(reqadd.getAttributeValueOf(String.class, "AOPs")));
        }
        if (attributes.containsKey("User"))
        {
            user = _tctx.getDB().FindAssert(new UserQuery().filterByUserId(reqadd.getAttributeValueOf(String.class, "User")));
        }
        
        /*
         * If this is send by a Agent then we need to set the user.
         */
        if (_uac.getUserType().equals(UserRole.Agent))
        {
            user = _uac.getUserSession().getUser();
        }

        ContactBook conbook = new ContactBook();
        conbook.setAOPs(aops);
        conbook.setUser(user);
        buildContactBook(reqadd, conbook);
        _tctx.getDB().Insert(_uac.getUserSession().getUser(), conbook);

        EventEntityAdded ev = new EventEntityAdded(reqadd, conbook);
        return ev;
    }

    private void buildContactBook(RequestEntityAdd req, ContactBook cbook) throws Exception
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
