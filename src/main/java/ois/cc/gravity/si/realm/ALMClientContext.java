package ois.cc.gravity.si.realm;

import CrsCde.CODE.Common.Utils.UIDUtil;
import code.common.exceptions.CODEException;
import code.realm.fw.events.EventRealmEntitiesFetched;
import code.realm.fw.events.EventRealmFailed;
import code.realm.fw.requests.process.RequestProcessContactAddressFetch;
import code.realm.fw.requests.process.RequestProcessContactFetch;
import code.realm.fw.util.FWUtil;
import code.realm.objects.OContact;
import code.realm.objects.OContactAddress;
import code.ua.events.Event;
import code.ua.events.EventCode;
import code.ua.events.EventEntityNotFound;
import code.ua.events.EventFailed;
import com.google.gson.Gson;
import ois.cc.gravity.AppProps;
import ois.cc.gravity.framework.objects.OContactList;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.services.exceptions.GravityUnhandledRealMException;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.aops.AOPsType;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.Campaign;
import ois.radius.cc.entities.tenant.cc.Process;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ALMClientContext
{

    public final Logger logger = LoggerFactory.getLogger(getClass());
    
    public String _tenantCode;

    private String _tenantToken;

    private final RealMInvoker _invoker;

    ALMClientContext(String tntcode)
    {
        this._tenantCode = tntcode;
        this._invoker = new RealMInvoker();
    }

     void initTenantToken(String token)
    {
        this._tenantToken = token;
    }
    
    public String getTenantToken()
    {
        return _tenantToken;
    }

    public String getTenantCode()
    {
        return _tenantCode;
    }
    


    //    public void TenantLogin(CTClient tenant) throws RADUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/user-signin";
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("LoginId", tenant.getDefAdminLoginId());
//        reqJson.put("Password", tenant.getDefAdminPassword());
//        reqJson.put("TenantCode", tenant.getCode());
//        String body = _invoker.SendToRealMSerice(url, "POST", reqJson, null);
//        JSONObject bodyJson = new JSONObject(body);
//
//        //set _suToken.
//        _tenantToken = bodyJson.getString("Token");
//    }
    public void CreateAOPs(AOPs camp) throws GravityUnhandledException
    {
        switch (camp.getAOPsType())
        {
            case Process:
                createCampaign(camp);
                break;
            case Campaign:
                createCampaign(camp);
                break;
        }

    }

    public void MapChannelWithAOPs(AOPs camp, Channel ch) throws GravityUnhandledException
    {
        switch (camp.getAOPsType())
        {
            case Process:
                mapChannelWithAops(camp, ch);
                break;
            case Campaign:
                mapChannelWithAops(camp, ch);
                break;
        }
    }

    public void IsSchemaCreated() throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign";
        JSONObject reqJson = new JSONObject();

        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
    }

    public void CreateAOPsPropterties(Campaign camp) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/campaign-properties";
        JSONObject reqJson = new JSONObject();
        reqJson.put("BaseAttrSize", 8);
        reqJson.put("IntrAttrSize", 16);

        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
    }

    public HashMap<String, Long> GetAllCampaign() throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign";
        HashMap<String, Long> hmCamps = new HashMap<>();
        //TBD: need to recheck.body can be null
        String body = _invoker.SendToRealMSerice(url, "GET", null, _tenantToken);
        if (body == null)
        {
            return hmCamps;
        }

        JSONObject jsonobj = new JSONObject(body);

        JSONArray GetAllTenants = jsonobj.getJSONArray("Entities");
        for (int i = 0; i < GetAllTenants.length(); i++)
        {
            JSONObject jsonObject = GetAllTenants.getJSONObject(i);
            //add to hmidcods;
            hmCamps.put(jsonObject.getString("Code"), jsonObject.getLong("Id"));
        }
        return hmCamps;

    }

    public void DeleteCampaign(AOPs aops) throws GravityUnhandledException
    {
        String url =null;
        if(aops.getAOPsType().equals(AOPsType.Campaign)){
            url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + aops.getCode();
        }
        else{
            url = AppProps.RAD_RealM_Service_Base_URL + "/process/" + aops.getCode();
        }

        JSONObject reqJson = new JSONObject();

        _invoker.SendToRealMSerice(url, "DELETE", reqJson, _tenantToken);
    }

    public String GetCampaign(String campCode) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campCode;
        String body = _invoker.SendToRealMSerice(url, "GET", null, _tenantToken);
        if (body == null)
        {

        }
        JSONObject bodyJson = new JSONObject(body);
        JSONObject jsonObject = bodyJson.getJSONObject("Entity");

        if (jsonObject == null)
        {
            return null;
        }
        //TBD:jsonObject can be null.
        //TBD throw an axception
        return jsonObject.getString("Code");
    }

    //    private void ActiveCampaign(String campcode) throws GravityUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode;
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("IsActive", true);
//        String body = _invoker.SendToRealMSerice(url, "PATCH", reqJson, _tenantToken);
//        System.out.println("Body:" + body);
//    }
//
//    private void InActiveCampaign(String campcode) throws GravityUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode;
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("IsActive", false);
//        String body = _invoker.SendToRealMSerice(url, "PATCH", reqJson, _tenantToken);
//        System.out.println("Body:" + body);
//    }
    public Long getCampaignIdByCode(String campcode) throws GravityUnhandledException
    {
        HashMap<String, Long> GetAllTenant = GetAllCampaign();
        return GetAllTenant.get(campcode);
    }

    public Long getContactListIdByCode(String campcode, String conlistcode) throws GravityUnhandledException
    {
        try
        {
            HashMap<String, Long> GetAllTenant = GetHmOfAllContactList(campcode);
            return GetAllTenant.get(conlistcode);
        }
        catch (Throwable th)
        {
            throw new GravityUnhandledException(th);
        }

    }

    //    public void StartCampaign(AICampaign aicamp) throws Exception, RADUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + aicamp.getCampaign().getCode() + "/start";
//        _invoker.SendToRealMSerice(url, "POST", null, _tenantToken);
//    }
//
//    public void StopCampaign(AICampaign aicamp) throws Exception, RADUnhandledException
//    {
//
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + aicamp.getCampaign().getCode() + "/stop";
//        _invoker.SendToRealMSerice(url, "POST", null, _tenantToken);
//
////        //update campaign state in realm.
////        EditCampaign(aicamp.getCampaign(), CampaignState.Stop);
////        InActiveCampaign(aicamp.getCampaign().getCode());
//    }
//    public ArrayList<OContactAttribute> GetCampaignAttribute(Campaign camp) throws Exception, GravityUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-attribute";
//        JSONObject campAttr = new JSONObject();
//
//        String jsonStr = _invoker.SendToRealMSerice(url, "GET", campAttr, _tenantToken);
//
//        return RealMPob.BuildContactAttribute(camp, jsonStr);
//    }
    public void CreateContactList(Campaign camp) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-list";
        JSONObject reqJson = new JSONObject();

        reqJson.put("ContactListName", "DefConList_" + camp.getId());
        reqJson.put("ContactListCode", "DefConList_" + camp.getId());
        reqJson.put("ContactListDescription", "DefConList_" + camp.getId());
        reqJson.put("isActive", Boolean.TRUE);
        String jsonstr = _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);

    }

    public HashMap<String, Long> GetHmOfAllContactList(String camcode) throws Exception, GravityUnhandledException
    {
        Long campId = getCampaignIdByCode(camcode);
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list";
        JSONObject reqJson = new JSONObject();

        String jsonstr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);

        return RealMPob.BuildContactListCodeId(jsonstr);

    }

    public ArrayList<OContactList> GetAllContactList(Campaign camp, HashMap<String, ArrayList<String>> filters) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-list";
        JSONObject reqJson = new JSONObject();

        if (!filters.isEmpty())
        {
            try
            {
                Gson gson = new Gson();
                String jsonFilters = gson.toJson(filters);
                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
                url = url + "?filters=" + fltrs;
            }
            catch (Throwable th)
            {
                throw new GravityUnhandledException(th);
            }
        }
        String jsonstr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);

        return RealMPob.BuildContactList(camp, jsonstr);
    }

    public OContactList GetContactListByCode(Campaign camp, String listcode) throws GravityUnhandledException
    {

        OContactList oconlist = null;
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-list/" + listcode;
        JSONObject reqJson = new JSONObject();

        String jsonstr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);

        ArrayList<OContactList> conlists = RealMPob.BuildContactList(camp, jsonstr);
        if (!conlists.isEmpty())
        {
            oconlist = conlists.get(0);
        }
        return oconlist;
    }

    public Boolean IsRecordListExist(Campaign camp, String listcode) throws GravityUnhandledException, Exception, GravityRuntimeCheckFailedException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/record-list/" + listcode;
        JSONObject reqJson = new JSONObject();
        String jsonstr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
        JSONObject respobj = new JSONObject(jsonstr);
        String respEvCode = respobj.getString("EvCode");
        //If Campaogn/Proocess code not exist in ALM.
        //-Check entity name if its Campaign or Process then throw exception.
        if (respEvCode.equals("EntityNotFound"))
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.CampaignNotFoundInALM, "[Campaoign.Code==" + camp.getCode() + "]");
        }
        if (respobj.has("Objects"))
        {
            JSONArray recLstArr = respobj.getJSONArray("Objects");
            if (!recLstArr.isEmpty())
            {
                return true;
            }
        }

        return false;
    }

//    public OContactList GetContactListById(Campaign camp, String reqlistId) throws GravityUnhandledException
//    {
//        OContactList oconlist = null;
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byid", new ArrayList<>(Arrays.asList(reqlistId)));
//        ArrayList<OContactList> conlists = GetAllContactList(camp, filter);
//        if (!conlists.isEmpty())
//        {
//            oconlist = conlists.get(0);
//        }
//        return oconlist;
//
//    }

    //    public void CreateContact(String camcode, String conlist, HashMap<Channel, String> conaddr, HashMap<String, Object> hmconattrs) throws RADUnhandledException
//    {
////        Long campId = getCampaignIdByCode(camcode);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camcode + "/contact-list/" + conlist + "/contact";
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("Contact", new JSONObject());
//
//        //TBD:will get valur from hashmap.
////        JSONArray contactrecords = new JSONArray();
////        if (hmconattrs != null)
////        {
////            for (Map.Entry conattr : hmconattrs.entrySet())
////            {
////                JSONObject conJsonObj = new JSONObject();
////                conJsonObj.put((String) conattr.getKey(), conattr.getValue());
////
////                contactrecords.put(conJsonObj);
////
////            }
////        }
//        // Now for IB calls json object expected in realm side so array is replaced with obj.
//        if (hmconattrs != null)
//        {
//            reqJson.put("Attributes", new JSONObject(hmconattrs));
//        }
//        else
//        {
//            reqJson.put("Attributes", new JSONObject());
//        }
//        JSONArray contactaddr = new JSONArray();
//        for (Map.Entry conaddrjsn : conaddr.entrySet())
//        {
//            JSONObject conJsonObj = new JSONObject();
//            conJsonObj.put("Channel", conaddrjsn.getKey());
//            conJsonObj.put("Address", conaddrjsn.getValue());
//            conJsonObj.put("AddressType", AddressType.Work.name());
//
//            contactaddr.put(conJsonObj);
//
//        }
//        reqJson.put("ContactAddresses", contactaddr);
//
//        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//    }
//    public String CreateContact(String camcode, String conlist, Set<OContactAddress> oconaddrs, HashMap<String, Object> hmconattrs) throws RADUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camcode + "/contact-list/" + conlist + "/contact";
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("Contact", new JSONObject());
//
//        if (hmconattrs != null)
//        {
//            reqJson.put("Attributes", new JSONObject(hmconattrs));
//        }
//        else
//        {
//            reqJson.put("Attributes", new JSONObject());
//        }
//
//        JSONArray contactaddr = new JSONArray();
//        for (OContactAddress oaddr : oconaddrs)
//        {
//            JSONObject conJsonObj = new JSONObject();
//            conJsonObj.put("Channel", oaddr.getChannel());
//            conJsonObj.put("Address", oaddr.getAddress());
//            conJsonObj.put("AddressType", oaddr.getAddressType());
//
//            contactaddr.put(conJsonObj);
//        }
//        reqJson.put("ContactAddresses", contactaddr);
//
//        String jsonstring = _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//        JSONObject obj = new JSONObject(jsonstring);
//        JSONObject conjson = obj.getJSONObject("KeyValue");
//        String conid = conjson.getString("ContactId");
//
//        return conid;
//    }
//
//    public OContact GetContactById(Campaign camp, String conid) throws RADUnhandledException
//    {
//        OContact ocon = null;
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byid", new ArrayList<>(Arrays.asList(conid)));
//        ArrayList<OContact> allCons = GetCampContact(camp, filter);
//        if (!allCons.isEmpty())
//        {
//            ocon = allCons.get(0);
//        }
//        return ocon;
//
//    }
//
//    public OContact GetContactByIdAssert(Campaign camp, String conid) throws RADUnhandledException, RADRuntimeCheckFailedException
//    {
//
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byid", new ArrayList<>(Arrays.asList(conid)));
//        ArrayList<OContact> allCons = GetCampContact(camp, filter);
//        if (allCons == null || allCons.isEmpty())
//        {
//            throw new RADRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ContactNotFoundFromALM);
//        }
//        return allCons.get(0);
//
//    }
//
//    public ArrayList<OContact> GetAllContact(Campaign camp, String conlist, HashMap<String, ArrayList<String>> filters) throws RADUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-list/" + conlist + "/contact";
//        if (filters != null && !filters.isEmpty())
//        {
//            try
//            {
//                Gson gson = new Gson();
//                String jsonFilters = gson.toJson(filters);
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "?filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//        }
//
//        JSONObject reqJson = new JSONObject();
//        String jsonStr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//
//        return RealMPob.BuildContact(camp, jsonStr);
//    }
//
//    public ArrayList<OContactSession> FetchBulkContact(Campaign camp, HashMap<String, ArrayList<String>> filters, Long agid) throws RADUnhandledException
//    {
////        Long campId = getCampaignIdByCode(camcode);
////        Long listId = getContactListIdByCode(camcode, listcode);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/bulk-contactsession?maxcount=128&fetchby=" + agid;
//        if (filters != null && !filters.isEmpty())
//        {
//            try
//            {
//                Gson gson = new Gson();
//                String jsonFilters = gson.toJson(filters);
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "&filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//        }
//
//        JSONObject reqJson = new JSONObject();
//        String jsonStr = "";
//        try
//        {
//            jsonStr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//        }
//        catch (RADUnhandledException ex)
//        {
//            logger.debug(ex.getMessage(), ex);
//        }
//
//        return RealMPob.BuildContactSess(camp, jsonStr);
//    }
//
//    public OContactSession FetchSingleContact(Campaign camp, HashMap<String, ArrayList<String>> filters, Long agid) throws RADUnhandledException, RADRuntimeCheckFailedException
//    {
//
////        Long campId = getCampaignIdByCode(camcode);
////        Long listId = getContactListIdByCode(camcode, conlist);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-session?fetchby=" + agid;
//
//        if (filters != null && !filters.isEmpty())
//        {
//            try
//            {
//                Gson gson = new Gson();
//                String jsonFilters = gson.toJson(filters);
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "&filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//        }
//        JSONObject reqJson = new JSONObject();
//        String jsonStr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//
//        ArrayList<OContactSession> alConSess = RealMPob.BuildContactSess(camp, jsonStr);
//        if (alConSess.isEmpty())
//        {
//            throw new RADRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ContactNotFoundFromALM);
//        }
//        return alConSess.get(0);
//    }
//
//    public OContactSession FetchSingleContactById(Campaign camp, String conid, Long agid) throws RADUnhandledException, RADRuntimeCheckFailedException
//    {
//
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byid", new ArrayList<>(Arrays.asList(conid)));
//        return FetchSingleContact(camp, filter, agid);
//    }

    /**
     * This method can return multiple contact when filter value have duplicate contact.Like if Address uniqueness is disabled then we will get all contact of
     * same address.
     * <p>
     * // * @param camp // * @param filters // * @return // * @throws RADUnhandledException //
     */
//    public ArrayList<OContact> GetCampContact(Campaign camp, HashMap<String, ArrayList<String>> filters) throws RADUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact";
//
//        if (filters != null && !filters.isEmpty())
//        {
//            Gson gson = new Gson();
//            String jsonFilters = gson.toJson(filters);
//            try
//            {
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "?filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//
//        }
//        JSONObject reqJson = new JSONObject();
//        String jsonStr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//
//        return RealMPob.BuildContact(camp, jsonStr);
//    }
//
//    public void ReactiveContactAddress(String campcode, String conlist, Long contactId, Long conaddrId) throws RADUnhandledException
//    {
//        Long campId = getCampaignIdByCode(campcode);
//        Long listId = getContactListIdByCode(campcode, conlist);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/contact/" + contactId + "/address/" + conaddrId + "/reactive";
//        JSONObject reqJson = new JSONObject();
//
//        reqJson.put("deleted", Boolean.FALSE);
//
//        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//    }
//
//    public ArrayList<OContactAddress> GetAllContactAddressOfAContact(String campcode, String conlist, String contactId, HashMap<String, ArrayList<String>> filters) throws RADUnhandledException
//    {
//
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-list/" + conlist + "/contact/" + contactId + "/address";
//        if (filters != null && !filters.isEmpty())
//        {
//            try
//            {
//                Gson gson = new Gson();
//                String jsonFilters = gson.toJson(filters);
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "?filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//        }
//
//        JSONObject reqJson = new JSONObject();
//
//        String jsonstr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//
//        return RealMPob.BuildContactAddress(jsonstr);
//    }
//
//    public ArrayList<OContactAddress> GetCampContactAddresses(Campaign camp, HashMap<String, ArrayList<String>> filters) throws RADUnhandledException
//    {
//
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-address";
//        if (filters != null && !filters.isEmpty())
//        {
//            try
//            {
//                Gson gson = new Gson();
//                String jsonFilters = gson.toJson(filters);
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "?filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//
//        }
//        JSONObject reqJson = new JSONObject();
//
//        String jsonstr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//
//        return RealMPob.BuildContactAddress(jsonstr);
//    }
//
//    public OContactAddress GetContactAddressesById(Campaign camp, Long conaddrId) throws RADUnhandledException
//    {
//
//        OContactAddress oconaddr = null;
//
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byid", new ArrayList<>(Arrays.asList(conaddrId.toString())));
//        ArrayList<OContactAddress> allConaddr = GetCampContactAddresses(camp, filter);
//        if (!allConaddr.isEmpty())
//        {
//            oconaddr = allConaddr.get(0);
//        }
//        return oconaddr;
//    }
//
//    public OContactAddress GetContactAddressesByIdAssert(Campaign camp, Long conaddrId) throws RADUnhandledException, RADRuntimeCheckFailedException
//    {
//
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byid", new ArrayList<>(Arrays.asList(conaddrId.toString())));
//        ArrayList<OContactAddress> allConaddr = GetCampContactAddresses(camp, filter);
//        if (allConaddr == null || allConaddr.isEmpty())
//        {
//            throw new RADRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ContactAddressNotFoundFromALM);
//        }
//        return allConaddr.get(0);
//    }
//
//    public ArrayList<OContact> GetContacts(Campaign camp, HashMap<String, ArrayList<String>> filters) throws RADUnhandledException
//    {
//
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact";
//        if (filters != null && !filters.isEmpty())
//        {
//            try
//            {
//                Gson gson = new Gson();
//                String jsonFilters = gson.toJson(filters);
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "?filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//
//        }
//        JSONObject reqJson = new JSONObject();
//
//        String jsonstr = null;
//        try
//        {
//            jsonstr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//        }
//        catch (Throwable th)
//        {
//            logger.error(th.getMessage());
//        }
//
//        return RealMPob.BuildContact(camp, jsonstr);
//    }
//
//    //    public ArrayList<OContact> GetContact(String campcode, HashMap<String, ArrayList<String>> filters) throws RADUnhandledException
////    {
////
////        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact?fetchby=agent";
////        if (!filters.isEmpty())
////        {
////            try
////            {
////                Gson gson = new Gson();
////                String jsonFilters = gson.toJson(filters);
////                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
////                url = url + "?filterby=" + fltrs;
////            }
////            catch (Throwable th)
////            {
////                throw new RADUnhandledException(th);
////            }
////
////        }
////        JSONObject reqJson = new JSONObject();
////
////        String jsonstr = _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
////
////        return RealMPob.BuildContact(jsonstr);
////    }
//    public void UpdateContactAddressesByListCode(String campcode, String conlistcode, String conid, Long conaddrid, OContactAddress oconaddr) throws RADUnhandledException
//    {
//
//        try
//        {
//            String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-list/" + conlistcode + "/contact/" + conid + "/address/" + conaddrid;
//            JSONObject reqJson = new JSONObject();
//
//            if (oconaddr != null)
//            {
//
//                JSONObject conAddrJson = JSONUtil.ToJSON(oconaddr);
//                reqJson = conAddrJson;
//
//            }
//            _invoker.SendToRealMSerice(url, "PUT", reqJson, _tenantToken);
//        }
//        catch (Exception | RADUnhandledException th)
//        {
//            throw new RADUnhandledException(th);
//        }
//
//    }
//
//    public void CreateContactAddressesByListId(Campaign camp, String conlistid, String conid, OContactAddress oconAddr) throws RADUnhandledException
//    {
//
//        try
//        {
//            OContactList conlist = GetContactListById(camp, conlistid);
//
//            String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-list/" + conlist.getCode() + "/contact/" + conid + "/address";
//            JSONObject reqJson = new JSONObject();
//
//            if (oconAddr != null)
//            {
//
//                JSONObject conAttrJson = JSONUtil.ToJSON(oconAddr);
//                reqJson = conAttrJson;
//
//            }
//            _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//        }
//        catch (Exception | RADUnhandledException th)
//        {
//            throw new RADUnhandledException(th);
//        }
//
//    }
//
//    public void UpdateContactAddressesByListId(Campaign camp, String conlistid, String conid, Long conaddrid, OContactAddress oconAddr) throws RADUnhandledException
//    {
//
//        OContactList conlist = GetContactListById(camp, conlistid);
//
//        UpdateContactAddressesByListCode(camp.getCode(), conlist.getCode(), conid, conaddrid, oconAddr);
////        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-list" + conlist.getCode() + "/contact" + conid + "/address" + conaddrid;
////        JSONObject reqJson = new JSONObject();
////
////        if (Attributes != null)
////        {
////            Set<Map.Entry<String, Object>> entrySet = Attributes.entrySet();
////            for (Entry e : entrySet)
////            {
////                reqJson.put((String) e.getKey(), e.getValue());
////
////            }
////        }
////        _invoker.SendToRealMSerice(url, "PUT", reqJson, _tenantToken);
//
//    }
//
//    public void RemoveContactAddressesByListId(Campaign camp, String conlistid, String conid, Long conaddrid) throws RADUnhandledException
//    {
//        try
//        {
//            OContactList conlist = GetContactListById(camp, conlistid);
//            String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + camp.getCode() + "/contact-list/" + conlist.getCode() + "/contact/" + conid + "/address/" + conaddrid;
//            JSONObject reqJson = new JSONObject();
//
//            _invoker.SendToRealMSerice(url, "DELETE", reqJson, _tenantToken);
//        }
//        catch (Exception | RADUnhandledException th)
//        {
//            throw new RADUnhandledException(th);
//        }
//
//    }
//    public void UpdateContactByConListID(Campaign camp, String conlistid, OContact ocon, HashMap<String, Object> hmconattrs) throws RADUnhandledException
//    {
//
//        OContactList conlist = GetContactListById(camp, conlistid);
//        UpdateContactByConListCode(camp.getCode(), conlist.getCode(), ocon, hmconattrs);
////        JSONObject reqJson = new JSONObject();
////
////        if (Attributes != null)
////        {
////            Set<Map.Entry<String, Object>> entrySet = Attributes.entrySet();
////            for (Entry e : entrySet)
////            {
////                reqJson.put((String) e.getKey(), e.getValue());
////
////            }
////        }
////        _invoker.SendToRealMSerice(url, "PUT", reqJson, _tenantToken);
//
//    }
//
//    public void UpdateContactByConListCode(String campcode, String conlistcode, OContact ocon, HashMap<String, Object> hmconattrs) throws RADUnhandledException
//    {
//
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-list/" + conlistcode + "/contact/" + ocon.getALMConId();
//        JSONObject reqJson = new JSONObject();
//
//        reqJson.put("Contact", new JSONObject(ocon));
//
//        if (hmconattrs != null)
//        {
//            reqJson.put("Attributes", new JSONObject(hmconattrs));
//        }
//        else
//        {
//            reqJson.put("Attributes", new JSONObject());
//        }
//        JSONArray contactaddr = new JSONArray();
//        reqJson.put("ContactAddresses", contactaddr);
//        _invoker.SendToRealMSerice(url, "PUT", reqJson, _tenantToken);
//
//    }
    public void MarkAddressAsDND(String campcode, String conlist, String ContactId, Long conaddrId) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-list/" + conlist + "/contact/" + ContactId + "/address/" + conaddrId + "/mark-dnd";
        JSONObject reqJson = new JSONObject();

        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
    }

    //    public void DiscardBuffer(String campcode) throws GravityUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/DiscardBuffer";
//        JSONObject reqJson = new JSONObject();
//
//        _invoker.SendToRealMSerice(url, "PUT", reqJson, _tenantToken);
//    }
//    public void MarkInUse(String campcode, String almcsid, ContactPreview conprv) throws RADUnhandledException
//    {
//
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-mark-in-use";
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("SessionId", almcsid);
////        reqJson.put("DialerId", conprv.getDialerId());//Commected due to getting error in realm. (dialerId is Long there)
//        reqJson.put("AutoDial", conprv.getAutoDial());
//        reqJson.put("PreviewStartAt", DATEUtil.ToString(conprv.getStartTime() , DATEFormats.dd__MMM__yyyy__HHmmss) );
//        reqJson.put("PreviewEndAt", DATEUtil.ToString(conprv.getEndTime(), DATEFormats.dd__MMM__yyyy__HHmmss));
//        reqJson.put("ContactSessionAction", conprv.getPreviewAction());
//
//        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//    }
//
//    public void MarkInUseNonOb(String campcode, String conId) throws RADUnhandledException
//    {
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byid", new ArrayList<>(Arrays.asList(conId)));
//
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/non-obcontact-mark-inuse";
//        if (!filter.isEmpty())
//        {
//            try
//            {
//                Gson gson = new Gson();
//                String jsonFilters = gson.toJson(filter);
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "?filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//
//        }
//
//        JSONObject reqJson = new JSONObject();
//
//        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//    }
//
//    public void MarkReadyNonOb(String campcode, String conid) throws RADUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/non-obcontact-mark-ready";
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byid", new ArrayList<>(Arrays.asList(conid)));
//
//        try
//        {
//            Gson gson = new Gson();
//            String jsonFilters = gson.toJson(filter);
//            String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//            url = url + "?filters=" + fltrs;
//        }
//        catch (Throwable th)
//        {
//            throw new RADUnhandledException(th);
//        }
//
//        JSONObject reqJson = new JSONObject();
//        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//    }
    public void MarkSchedule(String campcode) throws Exception, GravityUnhandledException
    {

        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact/mark-schedule";
        JSONObject reqJson = new JSONObject();
        reqJson.put("agentId", 1);
        reqJson.put("contactId", 1);
        reqJson.put("sessionId", "1707452531950");
        reqJson.put("systemDisposition", "Some system disposition");

        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
    }

    //    public void DisposeContact(String campcode, String almcsid, Long agid, String dispcode, String dialerid) throws RADUnhandledException
//    {
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-dispose";
//        JSONObject reqJson = new JSONObject();
//        if (almcsid != null)
//        {
//            reqJson.put("SessionId", almcsid);
//        }
//        reqJson.put("AgentId", agid);
//        reqJson.put("DialerId", dialerid);
//        if (UtilDisposition.IsDispositionSystemType(dispcode))
//        {
//            reqJson.put("SystemDispotion", dispcode);
//        }
//        else
//        {
//            reqJson.put("AgentDisposition", dispcode);
//        }
//
//        //Disposition need to set.
////        reqJson.put("systemDisposition", "Some system disposition");
//        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//    }
//
//    public void MarkDone(String campcode, String almcsid) throws RADUnhandledException
//    {
//
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-mark-done";
//        JSONObject reqJson = new JSONObject();
//        if (almcsid != null)
//        {
//            reqJson.put("SessionId", almcsid);
//        }
//
//        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//    }
    public void RemoveAddressAsDND(String campcode, String conlist, String contactId, Long conaddrId) throws GravityUnhandledException
    {

        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-list/" + conlist + "/contact/" + contactId + "/address/" + conaddrId + "/remove-dnd";
        JSONObject reqJson = new JSONObject();

        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
    }

    public void EnableContactList(String campcode, String conlist) throws GravityUnhandledException
    {
        Long campId = getCampaignIdByCode(campcode);
        Long listId = getContactListIdByCode(campcode, conlist);
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/enable";
        JSONObject reqJson = new JSONObject();

        reqJson.put("IsActive", Boolean.TRUE);
        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
    }

    public void DisableContactList(String campcode, String conlist) throws GravityUnhandledException
    {
        Long campId = getCampaignIdByCode(campcode);
        Long listId = getContactListIdByCode(campcode, conlist);
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/disable";
        JSONObject reqJson = new JSONObject();

        reqJson.put("IsActive", Boolean.FALSE);
        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
    }

    //    public void UpdateContactForHold(String campcode, String conlist, Long conId) throws RADUnhandledException
//    {
//        Long campId = getCampaignIdByCode(campcode);
//        Long listId = getContactListIdByCode(campcode, conlist);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/contact" + conId;
//        JSONObject reqJson = new JSONObject();
//        JSONObject conJson = new JSONObject();
//        conJson.put("hold_status", Boolean.TRUE);
//        reqJson.put("contact", conJson);
//
//        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
//    }
//    public void GetListWiseContactStateSummary(String campcode, String conlistcode) throws RADUnhandledException
//    {
//        Long campId = getCampaignIdByCode(campcode);
//        Long listId = getContactListIdByCode(campcode, conlistcode);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/contact_state_summary";
//        JSONObject reqJson = new JSONObject();
//
//        _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//    }
//
//    public void GetListWiseContactStateSummaryCampaign(String campcode, String conlist) throws RADUnhandledException
//    {
//        Long campId = getCampaignIdByCode(campcode);
//        Long listId = getContactListIdByCode(campcode, conlist);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/contact_state_summary_campaign";
//        JSONObject reqJson = new JSONObject();
//
//        _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//    }
    //    public void SetContactState(String campcode, String conlistcode, String contactId, ContactState constate) throws RADUnhandledException
//    {
//        //TBD: fetch contact from realm.
//
//        try
//        {
//
//            String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/contact-list" + conlistcode + "/contact" + contactId + "/state";
//            JSONObject reqJson = new JSONObject();
//            reqJson.put("ContactState", constate);
//
//            _invoker.SendToRealMSerice(url, "PUT", reqJson, _tenantToken);
//        }
//        catch (Throwable th)
//        {
//            throw new RADUnhandledException(th);
//        }
//    }
//    public void ExportContactWithChannelandAddresses(String campcode, String conlist, Channel chn, String address, HashMap<String, ArrayList<String>> filters) throws RADUnhandledException
//    {
//        Long campId = getCampaignIdByCode(campcode);
//        Long listId = getContactListIdByCode(campcode, conlist);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/export";
//        if (filters != null && !filters.isEmpty())
//        {
//            try
//            {
//                Gson gson = new Gson();
//                String jsonFilters = gson.toJson(filters);
//                String fltrs = URLEncoder.encode(jsonFilters, "UTF-8");
//                url = url + "?filters=" + fltrs;
//            }
//            catch (Throwable th)
//            {
//                throw new RADUnhandledException(th);
//            }
//
//        }
//        JSONObject reqJson = new JSONObject();
//
//        _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//    }
    public void DownloadListTemplate(String camcode, String conlist) throws GravityUnhandledException
    {
        Long campId = getCampaignIdByCode(camcode);
        Long listId = getContactListIdByCode(camcode, conlist);
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/download-template";
        JSONObject reqJson = new JSONObject();

        _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
    }

    //    public void GetAllContactListCSV(String campcode, String conlist) throws GravityUnhandledException
//    {
//        Long campId = getCampaignIdByCode(campcode);
//        Long listId = getContactListIdByCode(campcode, conlist);
//        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campId + "/contact-list/" + listId + "/get_list_detail";
//        JSONObject reqJson = new JSONObject();
//
//        _invoker.SendToRealMSerice(url, "GET", reqJson, _tenantToken);
//    }
    public void ValidateSchema(String campcode) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL + "/campaign/" + campcode + "/validateschema";
        JSONObject reqJson = new JSONObject();

        _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
    }

    private void createCampaign(AOPs aops) throws GravityUnhandledException
    {
        String url = null;
        switch (aops.getAOPsType())
        {
            case Campaign:
                url = AppProps.RAD_RealM_Service_Base_URL + "/campaign";
                JSONObject reqJson = new JSONObject();
                reqJson.put("CampaignCode", aops.getCode());
                reqJson.put("CampaignName", aops.getName());
                reqJson.put("CampaignDescription", aops.getDescription());
                _invoker.SendToRealMSerice(url, "POST", reqJson, _tenantToken);
                break;
            case Process:
                url = AppProps.RAD_RealM_Service_Base_URL + "/process";
                JSONObject reqJsonpro = new JSONObject();
                reqJsonpro.put("ProcessCode", aops.getCode());
                reqJsonpro.put("ProcessName", aops.getName());
                reqJsonpro.put("ProcessDescription", aops.getDescription());
                reqJsonpro.put("ProcessType", ((Process) aops).getProcessType());
                _invoker.SendToRealMSerice(url, "POST", reqJsonpro, _tenantToken);
                break;
        }

    }

    public void EditCampaign(AOPs aops) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL;
        JSONObject reqJson = new JSONObject();
        switch (aops.getAOPsType())
        {
            case Campaign ->
            {
                url = url + "/campaign/" + aops.getCode();
                reqJson.put("CampaignName", aops.getName());
                reqJson.put("CampaignDescription", aops.getDescription());
            }
            case Process ->
            {

                url = url + "/process/" + aops.getCode();
                reqJson.put( "ProcessType",((Process) aops).getProcessType().name());
                reqJson.put("ProcessName", aops.getName());
                reqJson.put("ProcessNameDescription", aops.getDescription());
            }
        }
        _invoker.SendToRealMSerice(url, "PUT", reqJson, _tenantToken);

    }

    private void mapChannelWithAops(AOPs aops, Channel ch) throws GravityUnhandledException
    {
        String url = AppProps.RAD_RealM_Service_Base_URL;
        JSONObject reqJson = new JSONObject();
        switch (aops.getAOPsType())
        {
            case Campaign ->
            {
                url = url + "/campaign/" + aops.getCode();
                reqJson.put("Channel", ch);
            }
            case Process ->
            {
                url = url + "/process/" + aops.getCode();
                reqJson.put("Channels", Collections.singletonList(ch));
            }
        }

        _invoker.SendToRealMSerice(url, "PUT", reqJson, _tenantToken);

    }

    public ArrayList<OContact> GetProcessContact(String process, HashMap<String, ArrayList<String>> filters) throws  CODEException, GravityUnhandledException {

        RequestProcessContactFetch almReq = new RequestProcessContactFetch(UIDUtil.GenerateUniqueId());
        almReq.setTenantCode(_tenantCode);
        almReq.setProcessCode(process);

        if (filters != null && !filters.isEmpty())
        {
            almReq.setFilters(filters);
        }

        String reqSerl = null;
        try
        {
            reqSerl = URLEncoder.encode(FWUtil.Serialize(almReq), "UTF-8");
        }

        catch (IOException ex)
        {
            throw new CODEException(ex);
        }

        String url = AppProps.RAD_RealM_Service_Base_URL + "/process/" + process + "/contact?request=" + reqSerl;

        Event ev = null;
        try
        {
            ev = _invoker.SendToRealMServiceRequest(url, "GET", almReq, _tenantToken);
        }
        catch (GravityUnhandledRealMException rex)
        {
            Event event = rex.getEvent();
            if (event != null && event instanceof EventEntityNotFound)
            {
                //do nothing
                return new ArrayList<>();
            }
            if (event != null && event instanceof EventRealmFailed)
            {
                EventRealmFailed realmFailed = (EventRealmFailed) event;
                if (realmFailed.getMessage().contains("No key Attributes found"))
                {
                    return new ArrayList<>();
                }
            }

            throw rex;
        }

        EventRealmEntitiesFetched evf = (EventRealmEntitiesFetched) ev;
        return evf.getObjects().stream().map(con -> (OContact) con).collect(Collectors.toCollection(ArrayList<OContact>::new));
    }
    
    public ArrayList<OContactAddress> GetProcessContactAddress(String process, HashMap<String, ArrayList<String>> filters) throws CODEException, GravityUnhandledException
    {

        try
        {
            RequestProcessContactAddressFetch req = new RequestProcessContactAddressFetch(UIDUtil.GenerateUniqueId());

            req.setTenantCode(_tenantCode);
            req.setProcessCode(process);

            if (filters != null && !filters.isEmpty())
            {
                req.setFilters(filters);
            }
            String reqSerl = null;
            try
            {
                reqSerl = URLEncoder.encode(FWUtil.Serialize(req), "UTF-8");
            }
            catch (IOException ex)
            {
                throw new CODEException(ex);
            }

            String url = AppProps.RAD_RealM_Service_Base_URL + "/process/" + process + "/contact-address?request=" + reqSerl;
            Event ev = null;
            try
            {
                ev = _invoker.SendToRealMServiceRequest(url, "GET", req, _tenantToken);
            }
            catch (GravityUnhandledRealMException rex)
            {
                Event event = rex.getEvent();
                if (event != null && event instanceof EventEntityNotFound)
                {
                    return new ArrayList<>();
                }
                throw rex;
            }

            EventRealmEntitiesFetched evf = (EventRealmEntitiesFetched) ev;
            return evf.getObjects().stream().map(addr -> (OContactAddress) addr).collect(Collectors.toCollection(ArrayList<OContactAddress>::new));
        }
        catch (GravityUnhandledRealMException rex)
        {
            Event event = rex.getEvent();
            if (event != null && event instanceof EventFailed evf)
            {
                if (evf.getEvCode().equals(EventCode.EntityNotFound))
                {
                    //do nothing
                    return new ArrayList<>();
                }
            }
            throw rex;
        }

    }


}
