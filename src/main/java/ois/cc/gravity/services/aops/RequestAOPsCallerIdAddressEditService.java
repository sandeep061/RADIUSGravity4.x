package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Enums.OPRelational;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import java.util.ArrayList;
import ois.cc.gravity.db.queries.AOPsCallerIdAddressQuery;
import ois.cc.gravity.db.queries.AOPsCallerIdQuery;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.aops.RequestAOPsCallerIdAddressEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.services.exceptions.GravityEntityNotFoundException;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCallerId;
import ois.radius.cc.entities.tenant.cc.AOPsCallerIdAddress;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

public class RequestAOPsCallerIdAddressEditService extends ARequestEntityService
{

    public RequestAOPsCallerIdAddressEditService(UAClient uac)
    {
        super(uac);
    }

    private ArrayList<NameValuePair> entities = new ArrayList<>();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestAOPsCallerIdAddressEdit reqedit = (RequestAOPsCallerIdAddressEdit) request;

        XPlatformUA xPlatformUA = null;
        AOPsCallerIdAddress callerIdAddress = null;
        AOPsCallerId aopcallerid = assertAOPsCallerID(reqedit);

        if (reqedit.getDefault())
        {
            ArrayList<AOPsCallerIdAddress> defclids = _tctx.getDB().Select(new AOPsCallerIdAddressQuery().filterByChannel(reqedit.getChannel()).filterByAOPsCallerId(aopcallerid.getId()).filterByIsDefault(reqedit.getDefault()));
            if (!defclids.isEmpty())
            {
                AOPsCallerIdAddress defCLI = defclids.get(0);
                defCLI.setIsDefault(Boolean.FALSE);
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), defCLI));
            }
        }

        switch (aopcallerid.getChannel())
        {
            case Call:
                if (reqedit.getAddress() == null)
                {
                    //throw noptional filed.
                    throw new GravityIllegalArgumentException("Address", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
                }
                callerIdAddress = _tctx.getDB().Find(new AOPsCallerIdAddressQuery().filterByAddress(reqedit.getAddress()));
                if (callerIdAddress != null)
                {
                    throw new GravityEntityExistsException(aopcallerid.getClass().getSimpleName(), "Address", OPRelational.Eq, callerIdAddress.getAddress());
                }
                break;
            default:
                if (reqedit.getXPlatformUA() != null)
                {
                    xPlatformUA = _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), reqedit.getXPlatformUA());
                    AOPsCallerIdAddress callerId = _tctx.getDB().Find(new AOPsCallerIdAddressQuery().filterByXplatformua(xPlatformUA.getId()));
                    if (callerId != null)
                    {
                        throw new GravityEntityExistsException(EN.AOPsCallerIdAddress.name(), "XPlatformUA", OPRelational.Eq, xPlatformUA.getId());
                    }
                }
                break;
        }

        callerIdAddress = new AOPsCallerIdAddress();
        callerIdAddress.setAOPsCallerId(aopcallerid);
        callerIdAddress.setAddress(reqedit.getAddress());
        callerIdAddress.setIsDefault(reqedit.getDefault());
        callerIdAddress.setChannel(reqedit.getChannel());
        callerIdAddress.setXPlatformID(reqedit.getXPlatformID());
        callerIdAddress.setXPlatformSID(reqedit.getXPlatformSID());
        callerIdAddress.setXPlatformUA(xPlatformUA);
        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), callerIdAddress));
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);
        EventEntityAdded ev = new EventEntityAdded(reqedit, callerIdAddress);
        return ev;
    }

    private AOPsCallerId assertAOPsCallerID(RequestAOPsCallerIdAddressEdit req) throws CODEException, GravityException
    {
        AOPsCallerId aopcallerid = _tctx.getDB().Find(new AOPsCallerIdQuery().filterByCode(req.getAOPsCallerIdCode()).filterByAOPs(req.getAOPsId()));
        if (aopcallerid == null)
        {
            //throw  exception
            throw new GravityEntityNotFoundException(EN.AOPsCallerId.name(), "AOPsId,AOPsCallerId ", OPRelational.Eq, req.getAOPsCallerIdCode() + "," + req.getAOPsId());
        }
        return aopcallerid;
    }
}
