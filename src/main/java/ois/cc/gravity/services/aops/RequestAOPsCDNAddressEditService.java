package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.common.exceptions.CODEException;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCDNAddressQuery;
import ois.cc.gravity.db.queries.AOPsCDNQuery;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.aops.RequestAOPsCDNAddressEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.services.exceptions.GravityEntityNotFoundException;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsCDN;
import ois.radius.cc.entities.tenant.cc.AOPsCDNAddress;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

public class RequestAOPsCDNAddressEditService extends ARequestEntityService
{

    public RequestAOPsCDNAddressEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        /**
         * AOPsId,APOsCDNCOde,AOPsCDNAddress
         */
        RequestAOPsCDNAddressEdit reqedit = (RequestAOPsCDNAddressEdit) request;

        //validate AOPsId
        _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), reqedit.getAOPsId());
        /**
         * TBD validate with xplatformUA
         */
        XPlatformUA xPlatformUA = null;
        AOPsCDNAddress aopadd=null;
        AOPsCDN aopcdn = assertAOPsCDN(reqedit);


        switch (aopcdn.getChannel())
        {
            case Call:
                if (reqedit.getAddress() == null)
                {
                    //throw noptional filed.
                    throw new GravityIllegalArgumentException("Address", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
                }
                aopadd = _tctx.getDB().Find(new AOPsCDNAddressQuery().filterByAddress(reqedit.getAddress()));
                if (aopadd != null)
                {
                    throw new GravityEntityExistsException(aopadd.getClass().getSimpleName(), "Address", OPRelational.Eq, aopadd.getAddress());
                }
                break;
            default:
                if (reqedit.getXPlatformUA() != null)
                {
                    xPlatformUA = _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), reqedit.getXPlatformUA());
                    AOPsCDNAddress aopscdn = _tctx.getDB().Find(new AOPsCDNAddressQuery().filterByXplatformua(xPlatformUA.getId()));
                    if (aopscdn != null)
                    {
                        throw new GravityEntityExistsException(EN.AOPsCDNAddress.name(), "AOPsCDNAddress,XPlatformUA", OPRelational.Eq, aopscdn.getId() + "," + xPlatformUA.getId());
                    }
                }
                break;
        }
         aopadd = new AOPsCDNAddress();
        aopadd.setAOPsCDN(aopcdn);
        aopadd.setAddress(reqedit.getAddress());
        aopadd.setIsWeb(reqedit.getWeb());
        aopadd.setChannel(reqedit.getChannel());
        aopadd.setAOPsCDN(aopcdn);
        aopadd.setXPlatformUA(xPlatformUA);
        aopadd.setXPlatformSID(reqedit.getXPlatformSID());
        aopadd.setXPlatformID(reqedit.getXPlatformID());

        _tctx.getDB().Insert(_uac.getUserSession().getUser(), aopadd);
        EventEntityAdded ev = new EventEntityAdded(reqedit, aopadd);

        return ev;
    }

    private AOPsCDN assertAOPsCDN(RequestAOPsCDNAddressEdit req) throws CODEException, GravityException
    {
        AOPsCDN aopCDN = _tctx.getDB().Find(new AOPsCDNQuery().filterByCode(req.getAOPsCDNCode()).filterByAOPs(req.getAOPsId()));
        if (aopCDN == null)
        {
            //throw  exception
            throw new GravityEntityNotFoundException(EN.AOPsCDN.name(), "AOPsId,AOPsCDN ", OPRelational.Eq, req.getAOPsCDNCode() + "," + req.getAOPsId());
        }
        return aopCDN;
    }

}
