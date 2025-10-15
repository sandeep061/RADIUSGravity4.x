package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.AddressType;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.ContactBook;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactBookAddressQuery extends EntityQuery{
    public ContactBookAddressQuery() {
        super(EN.ContactBookAddress);
    }

    public ContactBookAddressQuery filterByAddress(String add)
    {
        AppendWhere("And ContactBookAddress.Address =: add");
        _params.put("add",add);
        return this;
    }
    public ContactBookAddressQuery filterByAddressType(AddressType addtype)
    {
        AppendWhere("And ContactBookAddress.AddressType =: addtype");
        _params.put("addtype",addtype);
        return this;
    }

    public ContactBookAddressQuery filterByChannel(Channel Channel)
    {
        AppendWhere("And ContactBookAddress.Channel =:Channel");
        _params.put("Channel", Channel);
        return this;
    }
    public ContactBookAddressQuery filterByXPlatformID(XPlatformID XPlatID)
    {
        AppendWhere("And ContactBookAddress.XPlatformID =:XPlatID");
        _params.put("XPlatID", XPlatID);
        return this;
    }
    public ContactBookAddressQuery filterByContactBook(Long conbookid)
    {
        AppendWhere("And ContactBookAddress.ContactBook.Id =:conbookid");
        _params.put("conbookid", conbookid);
        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet()) {
            switch (name.toLowerCase()) {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byAddress":
                    filterByAddress(filters.get(name).get(0));
                    break;
                case "byaddresstype":
                    filterByAddressType(AddressType.valueOf(filters.get(name).get(0)));
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatformid":
                    filterByXPlatformID(XPlatformID.valueOf(filters.get(name).get(0)));
                    break;
                case "bycontactbook":
                    filterByContactBook(Long.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);

            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException {

    }
}
