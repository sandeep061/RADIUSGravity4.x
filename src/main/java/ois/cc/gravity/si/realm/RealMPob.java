/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.si.realm;

import CrsCde.CODE.Common.Utils.JSONUtil;
import java.util.ArrayList;
import java.util.HashMap;
import ois.cc.gravity.framework.objects.OContactList;
import ois.cc.gravity.framework.objects.pob;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.radius.cc.entities.tenant.cc.Campaign;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author rumana.begum
 * @since 12 Jun, 2024
 */
public class RealMPob
{

//    public static ArrayList<OContact> BuildContact(Campaign camp, String jsonstr) throws RADUnhandledException
//    {
//        try
//        {
//            ArrayList<OContact> allcon = new ArrayList<>();
//
//            if (jsonstr != null)
//            {
//                JSONObject jsonobj = new JSONObject(jsonstr);
//                if (jsonobj.has("Object"))
//                {
//                    Object contactJson = jsonobj.get("Object");
//                    if (contactJson instanceof JSONArray)
//                    {
//                        JSONArray jsonArray = (JSONArray) contactJson;
//                        for (int i = 0; i < jsonArray.length(); i++)
//                        {
//                            JSONObject conJson = jsonArray.getJSONObject(i);
//                            //TBD:For now we are checking this condition because we found json string without Attribute from realm. It will fix later
//                            if (conJson.has("Attributes"))
//                            {
//                                String attStr = conJson.getString("Attributes");
//                                conJson.put("Attributes", new JSONArray(attStr));
//                            }
//
//                            OContact con = JSONUtil.FromJSON(conJson, OContact.class);
//                            con.setCampaignId(camp.getId());
//                            allcon.add(build(con));
//                        }
//                    }
//                    else
//                    {
//                        JSONObject conJson = (JSONObject) contactJson;
//                        if (conJson.has("Attributes"))
//                        {
//                            String attStr = conJson.getString("Attributes");
//                            conJson.put("Attributes", new JSONArray(attStr));
//                        }
//                        OContact con = JSONUtil.FromJSON(conJson, OContact.class);
//                        con.setCampaignId(camp.getId());
//                        allcon.add(build(con));
//                    }
//                }
//                else if (jsonobj.has("Objects"))
//                {
//                    JSONArray jsonArray = jsonobj.getJSONArray("Objects");
//                    for (int i = 0; i < jsonArray.length(); i++)
//                    {
//
//                        JSONObject conJson = jsonArray.getJSONObject(i);
//                        if (conJson.has("Attributes"))
//                        {
//                            String attStr = conJson.getString("Attributes");
//                            conJson.put("Attributes", new JSONArray(attStr));
//                        }
//                        OContact con = JSONUtil.FromJSON(conJson, OContact.class);
//                        con.setCampaignId(camp.getId());
//                        allcon.add(build(con));
//                    }
//
//                }
//
//            }
//            return allcon;
//        }
//        catch (Exception ex)
//        {
//            throw new RADUnhandledException(ex);
//        }
//    }

//    public static ArrayList<OContactSession> BuildContactSess(Campaign camp, String jsonstr) throws RADUnhandledException
//    {
//        ArrayList<OContactSession> allconsess = new ArrayList<>();
//
//        if(jsonstr.isEmpty())
//        {
//            return allconsess;
//        }
//        JSONObject jsonobj;
//        try
//        {
//            if (jsonstr != null)
//            {
//                jsonobj = new JSONObject(jsonstr);
//                if (jsonobj.has("Object"))
//                {
//                    JSONObject conAddJson = jsonobj.getJSONObject("Object");
//
//                    if (conAddJson.has("Contact"))
//                    {
//                        Object conjsonobj = conAddJson.get("Contact");
//                        JSONObject conjson = (JSONObject) conjsonobj;
//                        if (conjson.has("Attributes"))
//                        {
//                            String attStr = conjson.getString("Attributes");
//                            conjson.put("Attributes", new JSONArray(attStr));
//                        }
//                        conAddJson.put("Contact", conjson);
//                    }
//
//                    OContactSession con = JSONUtil.FromJSON((JSONObject) conAddJson, OContactSession.class);
//                    con.setCampaignId(camp.getId());
//                    allconsess.add(build(con));
//
//                }
//                else if (jsonobj.has("Objects"))
//                {
//                    JSONArray jsonArray = jsonobj.getJSONArray("Objects");
//                    for (int i = 0; i < jsonArray.length(); i++)
//                    {
//                        JSONObject conAddJson = jsonArray.getJSONObject(i);
//
//                        if (conAddJson.has("Contact"))
//                        {
//                            Object conjsonobj = conAddJson.get("Contact");
//                            JSONObject conjson = (JSONObject) conjsonobj;
//                            if (conjson.has("Attributes"))
//                            {
//                                String attStr = conjson.getString("Attributes");
//                                conjson.put("Attributes", new JSONArray(attStr));
//                            }
//                            conAddJson.put("Contact", conjson);
//                        }
//
//                        OContactSession con = JSONUtil.FromJSON((JSONObject) conAddJson, OContactSession.class);
//                        con.setCampaignId(camp.getId());
//
//                        allconsess.add(build(con));
//                    }
//                }
//            }
//        }
//        catch (Throwable th)
//        {
//            throw new RADUnhandledException(th);
//        }
//        return allconsess;
////        try
////        {
////            ArrayList<OContactSession> allcon = new ArrayList<>();
////            JSONObject jsonobj = null;
////            if (jsonstr != null && jsonstr.contains("Object"))
////            {
////                jsonobj = new JSONObject(jsonstr);
////                Object contactJson = jsonobj.get("Object");
////                if (contactJson instanceof JSONArray)
////                {
////                    JSONArray jsonArray = jsonobj.getJSONArray("Object");
////                    for (int i = 0; i < jsonArray.length(); i++)
////                    {
////                        try
////                        {
////                            OContactSession con = JSONUtil.FromJSON(jsonArray.getJSONObject(i), OContactSession.class);
////                            allcon.add(con);
////                        }
////                        catch (Throwable th)
////                        {
////                            throw new RADUnhandledException(th);
////                        }
////                    }
////                }
////                else
////                {
////                    OContactSession consess = JSONUtil.FromJSON(((JSONObject) contactJson), OContactSession.class);
////                    consess = build(consess);
////                    allcon.add(consess);
////                }
////            }
////            return allcon;
////        }
////        catch (Exception ex)
////        {
////            throw new RADUnhandledException(ex);
////        }
//    }

//    private static OContactSession build(OContactSession oconss)
//    {
//        OContact ocon = oconss.getContact();
//        oconss.setContact(build(ocon));
//        return oconss;
//    }

//    private static OContact build(OContact ocon)
//    {
//        ocon.setALMConId(ocon.getId().toString());//TBD: this need to be finalize.We need alphanueric here.
//        return ocon;
//    }

    public static HashMap<String, Long> BuildContactListCodeId(String jsonstr)
    {
        HashMap<String, Long> hmidcods = new HashMap<>();
        JSONObject jsonobj = null;
        if (jsonstr != null && jsonstr.contains("Object"))
        {
            jsonobj = new JSONObject(jsonstr);
            JSONArray jsonArray = jsonobj.getJSONArray("Object");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //add to hmidcods;
                hmidcods.put(jsonObject.getString("Code"), jsonObject.getLong("Id"));
            }
        }
        return hmidcods;
    }

    public static ArrayList<OContactList> BuildContactList(Campaign camp, String jsonstr) throws GravityUnhandledException
    {
        ArrayList<OContactList> oconlists = new ArrayList<>();
        JSONObject jsonobj;
        try
        {
            if (jsonstr != null)
            {
                jsonobj = new JSONObject(jsonstr);
                if (jsonobj.has("Entity"))
                {
                    JSONObject conAddJson = jsonobj.getJSONObject("Entity");

                    OContactList conList = JSONUtil.FromJSON((JSONObject) conAddJson, OContactList.class);
//                    conList.setCampaign(pob.Build(camp));
                    oconlists.add(conList);

                }
                else if (jsonobj.has("Entities"))
                {
                    JSONArray jsonArray = jsonobj.getJSONArray("Entities");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject conLstJson = jsonArray.getJSONObject(i);
                        OContactList conList = JSONUtil.FromJSON((JSONObject) conLstJson, OContactList.class);
//                        conList.setCampaign(pob.Build(camp));
                        oconlists.add(conList);
                    }
                }
            }
        }
        catch (Throwable th)
        {
            throw new GravityUnhandledException(th);
        }
        return oconlists;
    }

    //    public static ArrayList<OContact> BuildContactListCodeId(String jsonstr) throws Exception
//    {
//        ArrayList<OContact> hmidcods = new ArrayList<>();
//        JSONObject jsonobj = null;
//        if (jsonstr != null)
//        {
//            jsonobj = new JSONObject(jsonstr);
//            JSONArray jsonArray = jsonobj.getJSONArray("Entities");
//            for (int i = 0; i < jsonArray.length(); i++)
//            {
//                OContact con = JSONUtil.FromJSON(jsonArray.getJSONObject(i), OContact.class);
//                hmidcods.add(con);
//
//            }
//        }
//        return hmidcods;
//    }
//    public static ArrayList<OContact> BuildContactListCodeId(String jsonstr) throws Exception
//    {
//         ArrayList<OContact> allcons = new ArrayList<>();
//        JSONObject jsonobj = null;
//        if (jsonstr != null)
//        {
//            jsonobj = new JSONObject(jsonstr);
//            JSONArray jsonArray = jsonobj.getJSONArray("Entities");
//            for (int i = 0; i < jsonArray.length(); i++)
//            {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//    {
//        ArrayList<ContactList> allconlist = new ArrayList<>();
//        if (jsonstr != null)
//        {
//            JSONArray jsonArray = new JSONArray(jsonstr);
//            for (int i = 0; i < jsonArray.length(); i++)
//            {
//                ContactList con = JSONUtil.FromJSON(jsonArray.getJSONObject(i), ContactList.class);
//                allconlist.add(con);
//            }
//        }
//        return allconlist;
//    }
    public static HashMap<String, Long> BuildAllCampaign(String jsonstr)
    {
        HashMap<String, Long> hmidcods = new HashMap<>();
        JSONObject jsonobj = null;
        if (jsonstr != null && jsonstr.contains("Object"))
        {
            jsonobj = new JSONObject(jsonstr);
            JSONArray jsonArray = jsonobj.getJSONArray("Object");
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //add to hmidcods;
                hmidcods.put(jsonObject.getString("Code"), jsonObject.getLong("Id"));
            }
            return hmidcods;
        }
        return hmidcods;

    }

//    public static ArrayList<OContactAddress> BuildContactAddress(String jsonstr) throws RADUnhandledException
//    {
//        ArrayList<OContactAddress> alladdr = new ArrayList<>();
//        JSONObject jsonobj;
//        try
//        {
//            if (jsonstr != null && jsonstr.contains("Object"))
//            {
//                jsonobj = new JSONObject(jsonstr);
//
//                JSONObject conAddrJson = jsonobj.getJSONObject("Object");
//                OContactAddress con = buildConAddr(conAddrJson);
//                alladdr.add(con);
//
//            }
//
//            //For Now  Entity is considers (eg:-CampaignContactAddressFetch)REALM
//            if (jsonstr != null)
//            {
//                jsonobj = new JSONObject(jsonstr);
//                if (jsonobj.has("Entity"))
//                {
//                    JSONObject conAddJson = jsonobj.getJSONObject("Entity");
//
//                    OContactAddress con = buildConAddr(conAddJson);
//                    alladdr.add(con);
//
//                }
//                else if (jsonobj.has("Entities"))
//                {
//                    JSONArray jsonArray = jsonobj.getJSONArray("Entities");
//                    for (int i = 0; i < jsonArray.length(); i++)
//                    {
//                        JSONObject conAddJson = jsonArray.getJSONObject(i);
//                        OContactAddress con = buildConAddr(conAddJson);
//                        alladdr.add(con);
//                    }
//                }
//            }
//        }
//        catch (Throwable th)
//        {
//            throw new RADUnhandledException(th);
//        }
//        return alladdr;
//    }
//
//    private static OContactAddress buildConAddr(JSONObject reqObj) throws Exception
//    {
////        Object get = reqObj.get("ContactId");
////
////        JSONObject objJson = reqObj.getJSONObject("Contact");
//
//        OContactAddress conAddr = JSONUtil.FromJSON(reqObj, OContactAddress.class);
//
//        if (reqObj.has("Contact"))
//        {
//            conAddr.setContactId(reqObj.getJSONObject("Contact").get("Id").toString());
//        }
//
//        return conAddr;
//    }

//    public static ArrayList<OContactAttribute> BuildContactAttribute(Campaign camp, String jsonstr) throws RADUnhandledException
//    {
//        ArrayList<OContactAttribute> allconattr = new ArrayList<>();
//        JSONObject jsonobj;
//        if (jsonstr != null && jsonstr.contains("Object"))
//        {
//            jsonobj = new JSONObject(jsonstr);
//            JSONArray jsonArray = jsonobj.getJSONArray("Object");
//            for (int i = 0; i < jsonArray.length(); i++)
//            {
//                try
//                {
//                    OContactAttribute conattr = JSONUtil.FromJSON(jsonArray.getJSONObject(i), OContactAttribute.class);
//                    conattr.setCampaignId(camp.getId());
//                    allconattr.add(conattr);
//                }
//                catch (Throwable th)
//                {
//                    throw new RADUnhandledException(th);
//                }
//
//            }
//        }
//        else
//        {
//
//            JSONArray jsonArray = new JSONArray(jsonstr);
//            for (int i = 0; i < jsonArray.length(); i++)
//            {
//                try
//                {
//                    OContactAttribute conattr = JSONUtil.FromJSON(jsonArray.getJSONObject(i), OContactAttribute.class);
//                    conattr.setCampaignId(camp.getId());
//                    allconattr.add(conattr);
//                }
//                catch (Throwable th)
//                {
//                    throw new RADUnhandledException(th);
//                }
//
//            }
//        }
//        return allconattr;
//    }

}
