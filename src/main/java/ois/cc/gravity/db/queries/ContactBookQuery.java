package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactBookQuery extends EntityQuery
{

    public ContactBookQuery()
    {
        super(EN.ContactBook);
    }

    public ContactBookQuery filterByUser(String uid)
    {
        AppendWhere("And ContactBook.User.UserId =: uid");
        _params.put("uid", uid);
        return this;
    }

    public ContactBookQuery filterByAOPs(Long aopid)
    {
        AppendWhere("And ContactBook.AOPs.Id =:aopid");
        _params.put("aopid", aopid);
        return this;
    }

    public ContactBookQuery filterByName(String name)
    {
        AppendWhere("And ContactBook.Name =:name");
        _params.put("name", name);
        return this;
    }

    public ContactBookQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(ContactBook.Name) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public ContactBookQuery filterByAdminOrUserId(Long userId)
    {
        AppendWhere("And  ContactBook.User IS NULL OR ContactBook.User.Id = :uid");
        _params.put("uid", userId);
        return this;
    }
    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byuser":
                    filterByUser(filters.get(name).get(0));
                    break;
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException
    {

        for (HashMap<String, Boolean> hm : orderby)
        {
            for (String name : hm.keySet())
            {
                switch (name.toLowerCase())
                {
                    case "name":
                        orderByName(hm.get(name));
                        break;
                }

            }
        }
    }

    private ContactBookQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }
}
