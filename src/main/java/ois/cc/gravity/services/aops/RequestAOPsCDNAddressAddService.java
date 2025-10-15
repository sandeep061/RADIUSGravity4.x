package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.DATEUtil;
import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsCDNAddressQuery;
import ois.cc.gravity.db.queries.AOPsCDNQuery;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;

import java.util.ArrayList;

public class RequestAOPsCDNAddressAddService extends ARequestEntityService
{

    public RequestAOPsCDNAddressAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        AOPsCDNAddress aopsCDNAddress = null;
        Channel channel = null;
        Boolean IsWeb = false;
        Long aopsId = null;
        Long workFlowId = null;
        String address = null;
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
        if (!reqenadd.getAttributes().containsKey("WorkFlow"))
        {
            throw new GravityIllegalArgumentException("WorkFlow", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        channel = reqenadd.getAttributeValueOf(Channel.class, "Channel");
        aopsId = reqenadd.getAttributeValueOf(Long.class, "AOPs");
        workFlowId = reqenadd.getAttributeValueOf(Long.class, "WorkFlow");
        AOPs aops = _tctx.getDB().FindAssert(EN.AOPs.getEntityClass(), aopsId);
        WorkFlow workflow = _tctx.getDB().FindAssert(EN.WorkFlow.getEntityClass(), workFlowId);


        if (channel.equals(Channel.Call))
        {
            if (!reqenadd.getAttributes().containsKey("Address"))
            {
                throw new GravityIllegalArgumentException("Address", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
            address = reqenadd.getAttributeValueOf(String.class, "Address");
        }

        AOPsCDNAddress cdnAdd= _tctx.getDB().Find(new AOPsCDNAddressQuery().filterByChannel(channel).filterByAddress(address).forNotAOPsBy(aops.getId()));
        if(cdnAdd!=null){
            //throw exception
            throw new GravityEntityExistsException(cdnAdd.getClass().getSimpleName(), "Address", OPRelational.Eq, cdnAdd.getId());
        }
        aopsCDNAddress = EntityBuilder.New(EN.AOPsCDNAddress);
        aopsCDNAddress.setChannel(channel);
        if (reqenadd.getAttributes().containsKey("Address"))
        {
            aopsCDNAddress.setAddress(reqenadd.getAttributeValueOf(String.class, "Address"));
        }
        if (reqenadd.getAttributes().containsKey("IsWeb"))
        {
            IsWeb = reqenadd.getAttributeValueOf(Boolean.class, "IsWeb");
        }
        if (address != null)
        {
            ArrayList<AOPsCDN> aopscdns = _tctx.getDB().Select(new AOPsCDNQuery().filterByChannel(channel).filterByWorkFlow(workFlowId));
            if (!aopscdns.isEmpty())
            {
                for (AOPsCDN cdn : aopscdns)
                {
                    AOPsCDNAddress aopsaddress = _tctx.getDB().Find(new AOPsCDNAddressQuery().filterByAOPsCDN(cdn.getId()));
                    if (aopsaddress!=null && address.equals(aopsaddress.getAddress()))
                    {
                        throw new GravityEntityExistsException(EN.AOPsCDNAddress.name(), "Address", OPRelational.Eq, aopsaddress.getAddress());
                    }
                }
            }
        }
        aopsCDNAddress.setIsWeb(IsWeb);
        assertAOPsCDNAddressOfXPlatfrom(aopsCDNAddress, IsWeb, reqenadd);
        AOPsCDN aopcdn = buildAOPsCDN(aops, workflow, channel);
        aopsCDNAddress.setAOPsCDN(aopcdn);
        entites.add(aopcdn);
        entites.add(aopsCDNAddress);
        _tctx.getDB().Insert(_uac.getUserSession().getUser(), entites);
        EventEntityAdded ev = new EventEntityAdded(reqenadd, aopsCDNAddress);
        ev.setMessage("AOPsCDN.Code = " + aopsCDNAddress.getAOPsCDN().getCode());
        return ev;
    }

    private String getCDNCode(Channel ch, AOPs aops, WorkFlow workflow)
    {
        return ch.name().substring(0, 3) + aops.getCode().substring(0, 3) + workflow.getCode().substring(0, 3) + DATEUtil.Now().getTime();
    }

    private AOPsCDN buildAOPsCDN(AOPs aops, WorkFlow workflow, Channel ch) throws CODEException, GravityException
    {

        //Code will be
        /**
         * -Fist 3 char of AOPs.Code. <br>
         * -Name of Channel. <br>
         * -First 3 char of workflow. <br>
         * -Current time <br>
         *
         * Eg: CDN_Cro_Call_CXW_1743655204992, Here AOPs is 'CrossX_Process', Channel is Call, WorkFlow is 'CXWorkFlow'.
         */
        String code = "CDN_" + aops.getCode().substring(0, 3) + "_" + ch + "_" + workflow.getCode().substring(0, 3) + "_" + DATEUtil.Now().getTime();
        AOPsCDN aopsCDN = new AOPsCDN();
        aopsCDN.setCode(code);
        aopsCDN.setChannel(ch);
        aopsCDN.setAOPs(aops);
        aopsCDN.setWorkFlow(workflow);
        return aopsCDN;
    }

    private void assertAOPsCDNAddressOfXPlatfrom(AOPsCDNAddress address, boolean isweb, RequestEntityAdd req) throws GravityException, Exception, CODEException
    {
        if (!address.getChannel().equals(Channel.Call) && !isweb)
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
                AOPsCDNAddress cdnadd = _tctx.getDB().Find(new AOPsCDNAddressQuery().filterByXplatformua(uaid));
                if (cdnadd != null)
                {
                    throw new GravityEntityExistsException(EN.AOPsCDNAddress.name(), "AOPsCDNAddress,XPlatformUA", OPRelational.Eq, cdnadd.getId()+","+uaid);
                }
            }

            XPlatformUA ua = _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), req.getAttributeValueOf(Long.class, "XPlatformUA"));
            address.setXPlatformID(req.getAttributeValueOf(XPlatformID.class, "XPlatformID"));
            address.setXPlatformSID(req.getAttributeValueOf(XPlatformSID.class, "XPlatformSID"));
            address.setXPlatformUA(ua);
        }
    }
}
