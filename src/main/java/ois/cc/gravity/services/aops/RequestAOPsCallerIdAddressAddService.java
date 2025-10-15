package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.DATEUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCallerIdAddressQuery;
import ois.cc.gravity.db.queries.AOPsCallerIdQuery;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.CLISelectionStrategy;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;

import java.util.ArrayList;

public class RequestAOPsCallerIdAddressAddService extends ARequestEntityService
{

    public RequestAOPsCallerIdAddressAddService(UAClient uac)
    {
        super(uac);
    }

    private ArrayList<NameValuePair> entities = new ArrayList<>();
    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        /**
         * TBD:we need to implement composite unique for channel,address,aops in service
         */
        AOPsCallerIdAddress aopsCallerIdAddress = null;
        Channel channel = null;
        Boolean IsDefault = false;
        Long aopsId = null;
        String address = null;
        CLISelectionStrategy cliStrategy = null;

        ArrayList<AEntity> entites = new ArrayList<>();

        RequestEntityAdd reqenadd = (RequestEntityAdd) request;

        if (!reqenadd.getAttributes().containsKey("Channel"))
        {
            throw new GravityIllegalArgumentException("Channel", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!reqenadd.getAttributes().containsKey("AOPs"))
        {
            throw new GravityIllegalArgumentException("AOPs", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (reqenadd.getAttributes().containsKey("CLISelectionStrategy"))
        {
            cliStrategy = reqenadd.getAttributeValueOf(CLISelectionStrategy.class, "CLISelectionStrategy");
        }

        channel = reqenadd.getAttributeValueOf(Channel.class, "Channel");
        aopsId = reqenadd.getAttributeValueOf(Long.class, "AOPs");
        AOPs aops = _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), aopsId);

        if (channel.equals(Channel.Call))
        {
            if (!reqenadd.getAttributes().containsKey("Address"))
            {
                throw new GravityIllegalArgumentException("Address", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
            address = reqenadd.getAttributeValueOf(String.class, "Address");
            AOPsCallerIdAddress clid = _tctx.getDB().Find(new AOPsCallerIdAddressQuery().filterByChannel(channel).filterByAddress(address).forNotAOPsBy(aops.getId()));
            if (clid != null)
            {
                //throw exception
                throw new GravityEntityExistsException(clid.getClass().getSimpleName(), "Address", OPRelational.Eq, clid.getId());
            }
        }

        aopsCallerIdAddress = EntityBuilder.New(EN.AOPsCallerIdAddress);
        aopsCallerIdAddress.setChannel(channel);
        aopsCallerIdAddress.setAddress(address);
        if (reqenadd.getAttributes().containsKey("IsDefault"))
        {
            IsDefault = reqenadd.getAttributeValueOf(Boolean.class, "IsDefault");
//            if(IsDefault){
//                ArrayList<AOPsCallerIdAddress>defclids=_tctx.getDB().Select(new AOPsCallerIdAddressQuery().filterByChannel(channel).fetchAOPsCallerIdByAOPs(aops.getId()).filterByIsDefault(IsDefault));
//                if(!defclids.isEmpty())
//                {
//                    AOPsCallerIdAddress defCLI = defclids.get(0);
//                    defCLI.setIsDefault(Boolean.FALSE);
//                    entities.add(new NameValuePair(ENActionList.Action.Update.name(), defCLI));
//                }
//            }
        }
        if (address != null)
        {
            ArrayList<AOPsCallerId> aopscallerids = _tctx.getDB().Select(new AOPsCallerIdQuery().filterByChannel(channel));
            if (!aopscallerids.isEmpty())
            {
                for (AOPsCallerId callerIdEntity : aopscallerids)
                {
                    AOPsCallerIdAddress aopscalleridaddress = _tctx.getDB().Find(new AOPsCallerIdAddressQuery().filterByAOPsCallerId(callerIdEntity.getId()));
                    if (aopscalleridaddress != null && address.equals(aopscalleridaddress.getAddress()))
                    {
                        throw new GravityEntityExistsException(EN.AOPsCallerIdAddress.name(), "Address", OPRelational.Eq, aopscalleridaddress.getAddress());
                    }
                }
            }
        }
        aopsCallerIdAddress.setIsDefault(IsDefault);
        aopsCallerIdAddress=assertAOPsCalleridAddressOfXPlatfrom(aopsCallerIdAddress, reqenadd);
        AOPsCallerId aopscallerid = buildAOPsCallerid(aops, channel, cliStrategy);
        aopsCallerIdAddress.setAOPsCallerId(aopscallerid);
//        entites.add(aopscallerid);
        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopscallerid));
        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsCallerIdAddress));
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);
        EventEntityAdded ev = new EventEntityAdded(reqenadd, aopsCallerIdAddress);
        ev.setMessage("AOPsCallerId.Code = " + aopsCallerIdAddress.getAOPsCallerId().getCode());
        return ev;
    }

    private AOPsCallerId buildAOPsCallerid(AOPs aops, Channel ch, CLISelectionStrategy cliSelectionStrategy) throws CODEException, GravityException
    {

        //Code will be
        /**
         * -Fist 3 char of AOPs.Code. <br>
         * -Name of Channel. <br>
         * -First 3 char of workflow. <br>
         * -Current time <br>
         *
         * Eg: CallerId_Cro_Call_CXW_1743655204992, Here AOPs is 'CrossX_Process', Channel is Call, WorkFlow is 'CXWorkFlow'.
         */
        String code = "CL_" + aops.getCode().substring(0, 3) + "_" + ch + "_" + DATEUtil.Now().getTime();
        AOPsCallerId aopsCallerid = new AOPsCallerId();
        aopsCallerid.setChannel(ch);
        aopsCallerid.setAOPs(aops);
        aopsCallerid.setCode(code);
        aopsCallerid.setCLISelectionStrategy(cliSelectionStrategy);
        return aopsCallerid;
    }

    private AOPsCallerIdAddress assertAOPsCalleridAddressOfXPlatfrom(AOPsCallerIdAddress address, RequestEntityAdd req) throws GravityException, Exception, CODEException
    {
        if (!address.getChannel().equals(Channel.Call))
        {
            if (!req.getAttributes().containsKey("XPlatformSID"))
            {
                throw new GravityIllegalArgumentException("XPlatformSID", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
            if (!req.getAttributes().containsKey("XPlatformID"))
            {
                throw new GravityIllegalArgumentException("XPlatformID", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
            if (!req.getAttributes().containsKey("XPlatformUA"))
            {
                throw new GravityIllegalArgumentException("XPlatformUA", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
            if (req.getAttributes().containsKey("XPlatformUA"))
            {
                Long uaid = req.getAttributeValueOf(Long.class, "XPlatformUA");
                AOPsCallerIdAddress calleradd = _tctx.getDB().Find(new AOPsCallerIdAddressQuery().filterByXplatformua(uaid));
                if (calleradd != null)
                {
                    throw new GravityEntityExistsException(EN.AOPsCallerIdAddress.name(), "AOPsCallerIdAddress,XPlatformUA", OPRelational.Eq, calleradd.getId() + "," + uaid);
                }
            }

            XPlatformUA ua = _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), req.getAttributeValueOf(Long.class, "XPlatformUA"));
            address.setXPlatformID(req.getAttributeValueOf(XPlatformID.class, "XPlatformID"));
            address.setXPlatformSID(req.getAttributeValueOf(XPlatformSID.class, "XPlatformSID"));
            address.setXPlatformUA(ua);
        }
        return address;
    }
}
