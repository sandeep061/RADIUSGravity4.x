package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.common.exceptions.CODEException;
import code.entities.AEntity;
import ois.cc.gravity.db.queries.ContactBookAddressQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.ContactBookAddress;

public class RequestContactBookAddressAddService extends RequestEntityAddService
{

    public RequestContactBookAddressAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPostBuildProcess(RequestEntityAdd reqenadd, AEntity entity) throws Throwable
    {
        ContactBookAddress conadd = (ContactBookAddress) entity;
        ValidatContactBookAddress(conadd);
    }

    private void ValidatContactBookAddress(ContactBookAddress conadd) throws CODEException, GravityException
    {
        if (conadd.getChannel().equals(Channel.Call))
        {
            ContactBookAddress address = _tctx.getDB().Find(new ContactBookAddressQuery().filterByChannel(conadd.getChannel()).filterByContactBook(conadd.getContactBook().getId()).filterByAddress(conadd.getAddress()));
            if (address != null)
            {
                throw new GravityEntityExistsException(EN.ContactBookAddress.name(), "Address,Channel,ContactBook", OPRelational.Eq, conadd.getAddress() + "," + conadd.getChannel() + "," + conadd.getContactBook().getId());
            }
        }
    }
}
