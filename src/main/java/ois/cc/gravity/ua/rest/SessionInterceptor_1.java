///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package ois.cc.gravity.ua.rest;
//
//import CrsCde.CODE.Common.Utils.EnumUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import ois.cc.gravity.ua.UACRegistry;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//import ois.cc.gravity.framework.events.Event;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.HandlerMapping;
//import java.util.Map;
//
///**
// * @author Deepak
// */
//@Component
//@CrossOrigin(origins = "*", allowedHeaders = "*")
//public class SessionInterceptor implements HandlerInterceptor
//{
//
//    static final Logger logger = LoggerFactory.getLogger(SessionInterceptor.class);
//
//    private UACRegistry _uacReg;
//    private RESTReqSessData _reqSessData;
//
//    @Autowired
//    public SessionInterceptor(RESTReqSessData _reqSessData)
//    {
//        this._uacReg = UACRegistry.This();
//        this._reqSessData = _reqSessData;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
//    {
//        try
//        {
//            logger.info(request.getRequestURI());
//
//            //Validate HTTPRequestHeader
//            assertHeader(request);
//            
//            //
//            if (request.getRequestURI().equals("/gravity/services/v1/c/user-register")
//                    || request.getRequestURI().equals("/gravity/services/v1/c/tenant-start")
//                    || request.getRequestURI().equals("/gravity/services/v1/c/tenant-stop"))
//            {
//                return true;
//            }
//
//            //Validate apikey
//            assertAPIKey();
//            
////            String apikey = request.getHeader("X-Api-Key");
////            if (apikey == null)
////            {
////                response.setStatus(400);
////                response.getWriter().write("X-Api-Key is Empty");
////                return false;
////            }
//
//            //Validate UAC 
//            assertUAC();
//            
////            UAClient uac = _uacReg.Get(apikey);
////            if (uac == null)
////            {
////                //TBD: need to send proper event.
//////                EventNoUASessionExists ev = new EventNoUASessionExists();
////                response.getWriter().write("No UASession exist");
////            }
////            this._reqSessData.setUAClient(uac);
//
//            //Validate Entity name from request
//            assertEntityName();
//                    
////            Event ev = initEntityFromRequest(request);
////            if (ev != null)
////            {
////                response.getWriter().write(JSONUtil.ToJSON(ev).toString());
////            }
//
//            return true;
//        }
//        catch (Throwable th)
//        {
//            //TBD:Need to send appropriate event
//            logger.error(th.getMessage(), th);
//        }
//        return false;
//    }
//
//    private void assertHeader(HttpServletRequest httprequest) throws Exception
//    {
//        if()
//        {
//            
//        }
//        else
//        {
//            Event ev;
//            new Exception(ev);
//        }
//    }
//
//    private Event initEntityFromRequest(HttpServletRequest req) throws Exception
//    {
//        Map<String, String> pathVariables = (Map<String, String>) req
//                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
//
//        String entity = null;
//        if (pathVariables.containsKey("entity"))
//        {
//            entity = pathVariables.get("entity");
//        }
//
//        EN en = EnumUtil.ValueOf(EN.class, entity);
//        if (entity != null && en == null)
//        {
//            this._reqSessData.setEN(en);
//
//        }
//        //TBD: Throw a exception.
////            EventInvalidEntity ev = new EventInvalidEntity();
//        return null;
//    }
//
//}
