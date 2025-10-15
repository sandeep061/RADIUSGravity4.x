///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package ois.cc.gravity.ua.rest;
//
//import CrsCde.CODE.Common.Utils.JSONUtil;
//import CrsCde.CODE.Common.Utils.UIDUtil;
//import code.ua.events.Event;
//import code.ua.events.EventType;
//
//import java.util.HashMap;
//import ois.radius.cc.entities.EN;
//import ois.radius.core.gravity.framework.requests.GReqCode;
//import ois.radius.core.gravity.framework.requests.GReqType;
//import ois.radius.core.gravity.services.ServiceManager;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
///**
// *
// * @author Manoj-PC
// * @since Aug 11, 2024
// */
//public class GravityControllerAbase
//{
//
//    protected static Logger logger = LoggerFactory.getLogger(GravityControllerAbase.class);
//
//    protected ServiceManager _srvcMgr;
//
//    public GravityControllerAbase()
//    {
//        this._srvcMgr = ServiceManager.This();
//    }
//
//    protected JSONObject BuildRequestBody(EN en, String reqstr, GReqType type, GReqCode code,
//            HashMap<String, Object> hmenties)
//    {
//        JSONObject reqJson = new JSONObject(/* reqstr */);
//
////        if (reqstr != null)
////        {
////			reqJson = new JSONObject(/* reqstr */);
////        }
//        reqJson.put("ReqId", UIDUtil.GenerateUniqueId());
//        reqJson.put("GReqType", type.name());
//        reqJson.put("GReqCode", code.name());
//        if (en != null)
//        {
//            reqJson.put("EntityName", en.name());
//        }
//        if (reqstr != null)
//        {
//           JSONObject reqJsn =  new JSONObject(reqstr);
//
//           if(reqJsn.has("AttributeCollectionAppend")||reqJsn.has("AttributeCollectionRemove")) {
//               if (reqJsn.has("AttributeCollectionAppend")) {
//                   reqJson.put("AttributeCollectionAppend", reqJsn.getJSONObject("AttributeCollectionAppend"));
//               }
//               if (reqJsn.has("AttributeCollectionRemove")) {
//                   reqJson.put("AttributeCollectionRemove", reqJsn.getJSONObject("AttributeCollectionRemove"));
//               }
//           }
//            else
//            {
//                reqJson.put("Attributes", new JSONObject(reqstr));
//            }
//
//
//
//        }
//
//        if (hmenties != null && !hmenties.isEmpty())
//        {
//            for (String key : hmenties.keySet())
//            {
//                reqJson.put(key, hmenties.get(key));
//            }
//        }
//
//        return reqJson;
//    }
//
//    protected JSONObject BuildRequestBodyForSu(EN en, String reqstr, GReqType type, GReqCode code,
//            HashMap<String, Object> hmenties)
//    {
//
//        JSONObject reqJson = new JSONObject(reqstr);
//        reqJson.put("ReqId", UIDUtil.GenerateUniqueId());
//        reqJson.put("GReqType", type.name());
//        reqJson.put("GReqCode", code.name());
//        if (en != null)
//        {
//            reqJson.put("EntityName", en.name());
//        }
//        return reqJson;
//    }
//
//    protected ResponseEntity<?> BuildResponse(Event ev)
//    {
//        try
//        {
//
//            JSONObject evJSON = JSONUtil.ToJSON(ev);
//
//            String resJsnStr = evJSON.toString();
//            logger.info("Build Response : " + resJsnStr);
//            if (ev.getEvCode() == code.ua.events.EventCode.OK || ev.getEvType().equals(EventType.OK)
//                    || ev.getEvCode() == code.ua.events.EventCode.Success)
//            {
//                return ResponseEntity.status(HttpStatus.OK).body(resJsnStr);
//            }
//            else
//            {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resJsnStr);
//            }
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
//        }
//
//    }
//
//}
