package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Utils.DATEUtil;
import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.realm.objects.OContactAddress;
import code.ua.events.Event;
import code.ua.requests.Request;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ois.cc.gravity.db.queries.CrossCXContactMapQuery;
import ois.cc.gravity.framework.requests.aops.RequestCrossCXContactMapAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.BFCode;
import ois.radius.ca.enums.CrossCXMapType;
import ois.radius.cc.entities.tenant.cc.CrossCXContactMap;
import ois.cc.gravity.db.queries.AOPsBFPropertiesQuery;
import ois.cc.gravity.db.queries.AOPsBFQuery;
import ois.cc.gravity.db.queries.AOPsQuery;
import ois.cc.gravity.entities.util.PINUtil;
import ois.cc.gravity.framework.events.aops.EventCrossCXContactMapFetched;
import ois.cc.gravity.objects.OCrossCXContactMap;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.services.exceptions.GravityUnhandledRealMException;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.AOPsBF;
import ois.radius.cc.entities.tenant.cc.AOPsBFProperties;
import org.json.JSONArray;

public class RequestCrossCXContactMapAddService extends ARequestEntityService
{

    public RequestCrossCXContactMapAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestCrossCXContactMapAdd req = (RequestCrossCXContactMapAdd) request;

        AOPs aop = _tctx.getDB().FindAssert(new AOPsQuery().filterByCode(req.getAOPsCode()));
        AOPsBF aopbf = _tctx.getDB().FindAssert(new AOPsBFQuery().filterByAOPs(aop.getId()).filterByBFCode(BFCode.CrossCX).filterByIsEnable(Boolean.TRUE));
        AOPsBFProperties bfprop = _tctx.getDB().FindAssert(new AOPsBFPropertiesQuery().filterByAOPsBF(aopbf.getId()).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapType));
        
        if(req.getPriAddress().equals(req.getSecAddress()))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.PrimaryAndSecondaryAddressShouldNotSame, "[PrimaryAddress,Secondary=="+req.getPriAddress()+","+req.getSecAddress()+"]");
        }
        
        CrossCXContactMap crx = BuildCrossCXContactMap(req, aop, aopbf);
        //required AOPsBFProperties 
        ValidateAOPsBFProperties(crx.getAOPs().getId());
        //check validation
        if (bfprop.getConfValue().equals(CrossCXMapType.DIDPool.name()))
        {
            ValidateContactWithDid(crx);
        }
        else
        {
            ValidateContactWithDidAndPin(crx);
        }

        _tctx.getDB().Insert(_uac.getUserSession().getUser(), crx);

        OCrossCXContactMap OCrossCXContactMap = BuildOCrossCXContactMap(crx);

        EventCrossCXContactMapFetched event = new EventCrossCXContactMapFetched(request);
        event.setCrossCXContactMap(OCrossCXContactMap);
        return event;
    }

    private CrossCXContactMap BuildCrossCXContactMap(RequestCrossCXContactMapAdd req, AOPs aop, AOPsBF aopbf) throws GravityException, CODEException, Exception
    {
        CrossCXContactMap crcxmap = new CrossCXContactMap();

        AOPsBFProperties aoPsBFProperties = _tctx.getDB().FindAssert(new AOPsBFPropertiesQuery().filterByAOPs(aop.getId()).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapType));
        OContactAddress priContactAdd = findActiveContactAddressFromAddressStr(req.getAOPsCode(), req.getPriAddress());
        OContactAddress secContactAdd = findActiveContactAddressFromAddressStr(req.getAOPsCode(), req.getSecAddress());

        crcxmap.setAOPs(aop);
        crcxmap.setPriContact(priContactAdd.getContactId().toString(), priContactAdd.getId().toString(), getContactAddress(priContactAdd, req.getPriAddress()));
        crcxmap.setSecContact(secContactAdd.getContactId().toString(), secContactAdd.getId().toString(), getContactAddress(secContactAdd, req.getSecAddress()));

        crcxmap.setAOPsBF(aopbf);
        if (aoPsBFProperties.getConfValue().equals(CrossCXMapType.DIDPool.name()))
        {
            crcxmap.setDID(getDID(aop.getId()));
        }
        else
        {
            crcxmap.setDID(getDID(aop.getId()));
            crcxmap.setPIN(getPin(aop.getId()));
        }

        crcxmap.setUCXConMapId(UIDUtil.GenerateUniqueId());
        setExparyTime(crcxmap, aop.getId());

        return crcxmap;
    }

    private void ValidateContactWithDid(CrossCXContactMap crocxmap) throws CODEException, GravityException
    {
        ArrayList<CrossCXContactMap> crossxpricon = _tctx.getDB().Select(new CrossCXContactMapQuery().filterByPriContactId(crocxmap.getPriContactId()).filterByPriAddressId(crocxmap.getPriAddressId()).filterByPriAddress(crocxmap.getPriAddress()).filterByDID(crocxmap.getDID()));
        ArrayList<CrossCXContactMap> crossxseccon = _tctx.getDB().Select(new CrossCXContactMapQuery().filterBySecContactId(crocxmap.getSecContactId()).filterBySecAddressId(crocxmap.getSecAddressId()).filterBySecAddress(crocxmap.getSecAddress()).filterByDID(crocxmap.getDID()));
        if (!crossxpricon.isEmpty() || !crossxseccon.isEmpty())
        {
            //TBD:throw an exception
        }
    }

    private void ValidateContactWithDidAndPin(CrossCXContactMap crocxmap) throws CODEException, GravityException
    {
        ArrayList<CrossCXContactMap> crossxpricon = _tctx.getDB().Select(new CrossCXContactMapQuery().filterByPriContactId(crocxmap.getPriContactId()).filterByPriAddressId(crocxmap.getPriAddressId()).filterByPriAddress(crocxmap.getPriAddress()).filterByDID(crocxmap.getDID()).filterByPIN(crocxmap.getPIN()));
        ArrayList<CrossCXContactMap> crossxseccon = _tctx.getDB().Select(new CrossCXContactMapQuery().filterBySecContactId(crocxmap.getSecContactId()).filterBySecAddressId(crocxmap.getSecAddressId()).filterBySecAddress(crocxmap.getSecAddress()).filterByDID(crocxmap.getDID()).filterByPIN(crocxmap.getPIN()));
        if (!crossxpricon.isEmpty() || !crossxseccon.isEmpty())
        {
            //TBD:throw an exception
        }
    }

//    private OContact FindActiveContactsByAddressAndAttr(String campcode, String address) throws CODEException, Exception, GravityException
//    {
//        AOPs aop = _tctx.getDB().FindAssert(new AOPsQuery().filterByCode(campcode));
//        HashMap<String, ArrayList<String>> filters = new HashMap<>();
//        filters.put("bycontactaddress", new ArrayList<>(Arrays.asList(address)));
//
//        ArrayList<OContact> ocontact = _tctx.getALMCtx().GetProcessContact(aop.getCode(), filters);
//
//        if (ocontact.isEmpty())
//        {
//            throw new GravityUnhandledRealMException("Contact Not Found In REALM With this Contact Address " + address);
//        }
//        return ocontact.get(0);
//    }
    public OContactAddress findActiveContactAddressFromAddressStr(String campcode, String address) throws CODEException, Exception, GravityUnhandledException, GravityRuntimeCheckFailedException
    {
        HashMap<String, ArrayList<String>> filters = new HashMap<>();
        filters.put("byaddress", new ArrayList<>(Arrays.asList(address)));
        try
        {
            ArrayList<OContactAddress> campAddress = new ArrayList<>();
            campAddress =  _tctx.getALMCtx().GetProcessContactAddress(campcode, filters);
            if (campAddress.isEmpty())
            {
                throw new GravityUnhandledRealMException("ContactAddress Not Found in Realm with this Address " + address);
            }

            return campAddress.get(0);

        }
        catch (GravityUnhandledRealMException ex)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ContactNotFoundFromALM, "Contact Not Found In REALM With this Contact Address " + address);
        }

    }

    private String getContactAddress(OContactAddress contactAddress, String conAddress) throws GravityUnhandledRealMException, Exception
    {
        if (!contactAddress.getAddress().equals(conAddress))
        {
            throw new GravityUnhandledRealMException("This is not Valid Contact Address " + conAddress);
        }
        return conAddress;
    }

    private String getDID(Long aopid) throws GravityException, CODEException, Exception
    {
        AOPsBFProperties bfproperties = _tctx.getDB().FindAssert(new AOPsBFPropertiesQuery().filterByAOPs(aopid).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapDIDs));
        String dids = bfproperties.getConfValue();
        JSONArray jarray = new JSONArray(dids);
        ArrayList<String> bfdidlist = JSONUtil.FromJSON(jarray.toString(), ArrayList.class);

        //fetch dids from crosscxconnect entity
        JPAQuery query = new JPAQuery("SELECT c.DID FROM CrossCXContactMap c WHERE c.AOPs.Id = :aops");
        query.setParam("aops", aopid);
        List<String> crosscxdids = _tctx.getDB().Select(query);

        //get unused one did
        if (crosscxdids != null)
        {
            bfdidlist.removeAll(crosscxdids);
            if (bfdidlist.isEmpty())
            {
                //throw an exception
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, "DIDs Limit Exceeds");
            }
        }
        return bfdidlist.get(0);

    }

    private String getPin(long aopid) throws CODEException, GravityException
    {

        JPAQuery query = new JPAQuery("SELECT c.PIN FROM CrossCXContactMap c WHERE c.AOPs.Id = :aops");
        query.setParam("aops", aopid);
        List<String> existingPins = _tctx.getDB().Select(query);
        Set<String> existingPinSet = new HashSet<>(existingPins);

        AOPsBFProperties bfproperties = _tctx.getDB().FindAssert(new AOPsBFPropertiesQuery().filterByAOPs(aopid).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapPINLen));
        int length = Integer.parseInt(bfproperties.getConfValue());

        PINUtil pin = new PINUtil(existingPinSet);
        return pin.GeneratePin(length);

    }

    private OCrossCXContactMap BuildOCrossCXContactMap(CrossCXContactMap cromap)
    {
        OCrossCXContactMap oobj = new OCrossCXContactMap();
        oobj.setAOPsCode(cromap.getAOPs().getCode());
        oobj.setDID(cromap.getDID());
        oobj.setPIN(cromap.getPIN());
        oobj.setPriAddress(cromap.getPriAddress());
        oobj.setSecAddress(cromap.getSecAddress());
        oobj.setUCXConMapId(cromap.getUCXConMapId());
        return oobj;
    }

    private void setExparyTime(CrossCXContactMap cromap, long aopid) throws GravityException, CODEException
    {
        AOPsBFProperties dbbfproperties = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPs(aopid).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapTimeout));
        if (dbbfproperties == null)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AOPsBFPropertiesNotFound, " AOPsBFProperties not found with this Key " + AOPsBFProperties.Key.CrossCX_MapTimeout);
        }
        int minute = Integer.parseInt(dbbfproperties.getConfValue());
        if (minute < 5)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, " CrossCX_MapTimeout Key value should be gratter than 5");
        }
        cromap.setExpiryTime(DATEUtil.Add(DATEUtil.Now(), DATEUtil.Unit.MINUTE, minute));
    }

    private void ValidateAOPsBFProperties(long aopid) throws GravityException, CODEException
    {
        _tctx.getDB().FindAssert(new AOPsBFPropertiesQuery().filterByAOPs(aopid).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapDIDs));
        _tctx.getDB().FindAssert(new AOPsBFPropertiesQuery().filterByAOPs(aopid).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapTimeout));
        _tctx.getDB().FindAssert(new AOPsBFPropertiesQuery().filterByAOPs(aopid).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapPINLen));

    }
}
