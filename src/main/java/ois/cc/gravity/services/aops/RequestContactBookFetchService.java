package ois.cc.gravity.services.aops;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.ContactBookAddressQuery;
import ois.cc.gravity.db.queries.ContactBookQuery;
import ois.cc.gravity.framework.events.aops.EventContactBookFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.OContactBook;
import ois.cc.gravity.objects.OContactBookAddress;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.ContactBook;
import ois.radius.cc.entities.tenant.cc.ContactBookAddress;
import java.util.ArrayList;
import ois.cc.gravity.entities.util.UserUtil;

public class RequestContactBookFetchService extends ARequestEntityService
{

    public RequestContactBookFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

        try
        {
            ContactBookQuery enQry = new ContactBookQuery();

            if (reqFetch.getFilters() != null)
            {
                enQry.doApplyFilters(reqFetch.getFilters());
            }

            //If this request is sent by Agent then we have to send ContactBook of this agent only.
            if (UserUtil.GetUserRole(_tctx, _uac.getUserSession().getUser()).equals(UserRole.Agent))
            {
                enQry.filterByAdminOrUserId(_uac.getUserSession().getUser().getId());
            }

            enQry.ApplyOrderBy(reqFetch.getOrderBy());

            Integer recCount = null;

            Boolean reqcnt = reqFetch.getIncludeCount() != null && reqFetch.getIncludeCount();

            if (reqcnt)
            {
                recCount = _tctx.getDB().SelectCount(enQry);

            }
            if (reqFetch.getLimit() != null)
            {
                enQry.setLimit(reqFetch.getLimit());
            }

            if (reqFetch.getOffset() != null)
            {
                enQry.setOffset(reqFetch.getOffset());
            }

            JPAQuery ctq = enQry.toSelect();
            ArrayList<OContactBook> ocontbooks = null;

            ArrayList<ContactBook> contactbooks = _tctx.getDB().Select(getClass(), ctq);
            ocontbooks = buildOConactBook(contactbooks);

            EventContactBookFetched event = new EventContactBookFetched(reqFetch);
            event.setoContactBooks(ocontbooks);
            event.setRecordCount(recCount);
            return event;
        }
        catch (GravityException rex)
        {
            return BuildExceptionEvents(reqFetch, rex);
        }

    }

    protected Event BuildExceptionEvents(RequestEntityFetch reqfetch, GravityException rex) throws GravityNoSuchFieldException, GravityException
    {
        if (rex instanceof GravityNoSuchFieldException)
        {
            GravityNoSuchFieldException fex = (GravityNoSuchFieldException) rex;
            throw fex;
        }
        else
        {
            throw rex;
        }
    }

    private OContactBook buildOContactBook(ContactBook contactbook) throws CODEException, GravityException
    {
        OContactBook oconbook = new OContactBook();

        ArrayList<ContactBookAddress> conAddress = _tctx.getDB().Select(new ContactBookAddressQuery().filterByContactBook(contactbook.getId()));

        oconbook.setAOPs(contactbook.getAOPs());
        oconbook.setUser(contactbook.getUser());
        oconbook.setContactBookAddress(buildOContactBookAddress(conAddress));
        oconbook.setDepartment(contactbook.getDepartment());
        oconbook.setDesignation(contactbook.getDesignation());
        oconbook.setLocation(contactbook.getLocation());
        oconbook.setOrganization(contactbook.getOrganization());
        oconbook.setName(contactbook.getName());
        oconbook.setId(contactbook.getId());
        oconbook.setCreatedBy(contactbook.getCreatedBy());
        oconbook.setCreatedOn(contactbook.getCreatedOn());
        oconbook.setEditedBy(contactbook.getEditedBy());
        oconbook.setEditedOn(contactbook.getEditedOn());
        return oconbook;
    }

    private ArrayList<OContactBookAddress> buildOContactBookAddress(ArrayList<ContactBookAddress> adds)
    {
        ArrayList<OContactBookAddress> oaddress = new ArrayList<>();
        for (ContactBookAddress add : adds)
        {
            OContactBookAddress oadd = new OContactBookAddress();
            oadd.setAddress(add.getAddress());
            oadd.setAddressType(add.getAddressType());
            oadd.setChannel(add.getChannel());
            oadd.setId(add.getId());
            oadd.setXPlatformID(add.getXPlatformID());
            oaddress.add(oadd);

        }
        return oaddress;
    }

    private ArrayList<OContactBook> buildOConactBook(ArrayList<ContactBook> contactbooks) throws CODEException, GravityException
    {
        ArrayList<OContactBook> ocontbooks = new ArrayList<>();

        for (ContactBook conbook : contactbooks)
        {
            ocontbooks.add(buildOContactBook(conbook));
        }
        return ocontbooks;

    }
}
