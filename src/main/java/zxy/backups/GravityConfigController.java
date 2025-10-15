//package ois.cc.gravity.ua.rest;
//
//import code.ua.events.Event;
//import code.ua.events.EventFailed;
//import code.ua.requests.Request;
//import jakarta.servlet.http.HttpServletRequest;
//
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.HashMap;
//
//import ois.radius.cc.entities.EN;
//import ois.radius.core.gravity.entities.util.AppUtil;
//import ois.radius.core.gravity.framework.events.EventCode;
//import ois.radius.core.gravity.framework.requests.GReqCode;
//import ois.radius.core.gravity.framework.requests.GReqType;
//import ois.radius.core.gravity.services.IRequestService;
//import ois.radius.core.gravity.services.ServiceManager;
//import ois.radius.core.gravity.services.ServiceRegistry;
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//
//@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, allowCredentials = "false")
//@RestController
//@RequestMapping("/gravity/services/v1")
//public class GravityConfigController extends GravityControllerAbase
//{
//
//    private static Logger logger = LoggerFactory.getLogger(GravityConfigController.class);
//    private ServiceRegistry sr = null;
//    private IRequestService reqSrvc;
//    private ServiceManager sm = null;
//
//    public GravityConfigController()
//    {
//        super();
//        sr = new ServiceRegistry();
//        sm = new ServiceManager();
////		reqsuservices = new RequestSUSigninService();
//    }
//
//
//    @PostMapping("/user-register")
//    public ResponseEntity<?> AppUserSignin(HttpServletRequest httprequest, @RequestBody String request)
//    {
////        String apikey = httprequest.getHeader("X-Api-Key");
//        logger.info("SUClient " + " >> " + (request == null ? "NULL" : request));
//
//        try
//        {
//            /**
//             * TBD:<br>
//             * User sign-in will be validate from RADIUS using API.<br>
//             * Token will generate on successful sign-in.<br>
//             * the token will be used as auth token for all request from this same user.
//             */
//
//            JSONObject req = BuildRequestBodyForSu(null, request, GReqType.Auth, GReqCode.UserRegister, null);
//
//            logger.info("Request " + req.toString());
//            String token = req.getString("Token");
//            Event ev = _srvcMgr.ProcessRequest(req, GetTenatCode(token),EN.CallerIDPlan);
//            return BuildResponse(ev);
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
//            EventFailed evf = new EventFailed(null, EventCode.Failed);
//            evf.setMessage(ex.getMessage());
//            return BuildResponse(evf);
//        }
//
//    }
//
//
//    private String GetTenatCode(String token) throws InvalidKeyException, NoSuchAlgorithmException,
//            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
//    {
//        String dytoken = AppUtil.Decrypt(token);
//        JSONObject jobj = new JSONObject(dytoken);
//        return jobj.getString("TenantCode");
//    }
//
//    @PostMapping("/{entity}")
//    public ResponseEntity<?> AddUser(HttpServletRequest httprequest, @RequestBody String request)
//    {
//        String apikey = httprequest.getHeader("X-Api-Key");
//        logger.info("USERClient " + " >> " + (request == null ? "NULL" : request));
//        try
//        {
//            JSONObject req = BuildRequestBody(EN.AgentMediaMap, request, GReqType.Config, GReqCode.EntityAdd, null);
//            logger.info("Request " + req.toString());
//            Event ev = _srvcMgr.ProcessRequest(req, GetTenatCode(apikey),EN.AgentMediaMap);
//            return BuildResponse(ev);
//
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
//            EventFailed evf = new EventFailed(null, EventCode.Failed);
//            evf.setMessage(ex.getMessage());
//            return BuildResponse(evf);
//
//        }
//
//    }
//
//
//    @PostMapping("/su-signin")
//    public ResponseEntity<?> AppSignin(HttpServletRequest httprequest, @RequestBody String request) throws Throwable
//    {
//
//        logger.info("SUClient " + " >> " + (request == null ? "NULL" : request));
//        String apikey = httprequest.getHeader("X-Api-Key");
//        try
//        {
//            JSONObject req = BuildRequestBody(null, request, GReqType.SU, GReqCode.SUSignin, null);
//
////            Event ev = _srvcMgr.ProcessSystemRequest(req);
//
//
//            Request sureq = sr.GetRequest(req);
//            Event ev = sr.GetSystemService(sureq, "sys", null).ProcessRequest(sureq);
//
//            /**
//             * TBD : Need to validate Event type, for failed event need to return valid HttpStatus.
//             */
//            return BuildResponse(ev);
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
//            EventFailed evf = new EventFailed(null, EventCode.Failed);
//            evf.setMessage(ex.getMessage());
//            return BuildResponse(evf);
//
//        }
//
//    }
//
//    @PostMapping("/tenant")
//    public ResponseEntity<?> AppAddTenant(@RequestBody String request) throws Throwable
//    {
//        logger.info("SUClient " + " >> " + (request == null ? "NULL" : request));
//
//        try
//        {
//            JSONObject req = BuildRequestBodyForSu(null, request, GReqType.SU, GReqCode.TenantAdd, null);
//            Request sureq = sr.GetRequest(req);
//            Event ev = sr.GetSystemService(sureq, "sys", null).ProcessRequest(sureq);
////			Event ev = _srvcMgr.ProcessSystemRequest(sureq);
//            /**
//             * TBD : Need to validate Event type, for failed event need to return valid HttpStatus.
//             */
//            return BuildResponse(ev);
////          return null;
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
////          EventFailed evf = new EventFailed(null, EventCode.Failed);
////          evf.setMessage(ex.getMessage());
////          return BuildResponse(evf);
//            return null;
//        }
//        /**
//         * TBD: Need to implement server error reason message for Exception and other fail cases.
//         */
//
//    }
//
//    @GetMapping(value = {"/tenant", "/tenant/{id}"})
//    public ResponseEntity<?> GetTenant(@PathVariable(required = false) String id) throws Throwable
//    {
//        logger.info("SUClient " + " >> Get Tenants ID : " + id);
////      RequestTenantFetchService reqtntser=new RequestTenantFetchService();
//        try
//        {
//            JSONObject req;
//            if (id != null)
//            {
//                HashMap<String, Object> hmenties = new HashMap<>();
//                hmenties.put("TenantId", id);
//                req = BuildRequestBody(null, null, GReqType.SU, GReqCode.TenantFetch, hmenties);
//            }
//            else
//            {
//                req = BuildRequestBody(null, null, GReqType.SU, GReqCode.TenantFetch, null);
//            }
//            Request sureq = sr.GetRequest(req);
//            Event ev = sr.GetSystemService(sureq, "sys", null).ProcessRequest(sureq);
////			Event ev = _srvcMgr.ProcessSystemRequest(req);
//
//            /**
//             * TBD : Need to validate Event type, for failed event need to return valid HttpStatus.
//             */
//            return BuildResponse(ev);
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
//            EventFailed evf = new EventFailed(null, EventCode.Failed);
//            evf.setMessage(ex.getMessage());
//            return BuildResponse(evf);
//        }
//
//    }
//
//    @PutMapping("/tenant/{id}")
//    public ResponseEntity<?> AppEditTenant(@RequestBody String request, @PathVariable("id") String id) throws Throwable
//    {
//        logger.info("SUClient " + " >> " + (request == null ? "NULL" : request));
//
//        try
//        {
//            HashMap<String, Object> hmenties = new HashMap<>();
//            hmenties.put("TenantId", id);
//            JSONObject req = BuildRequestBody(null, request, GReqType.SU, GReqCode.TenantEdit, hmenties);
//            Request sureq = sr.GetRequest(req);
//            Event ev = sr.GetSystemService(sureq, "sys", null).ProcessRequest(sureq);
//            /**
//             * TBD : Need to validate Event type, for failed event need to return valid HttpStatus.
//             */
//            return BuildResponse(ev);
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
//            EventFailed evf = new EventFailed(null, EventCode.Failed);
//            evf.setMessage(ex.getMessage());
//            return BuildResponse(evf);
//        }
//        /**
//         * TBD: Need to implement server error reason message for Exception and other fail cases.
//         */
//
//    }
//
//    @PostMapping("/tenant/{id}/start")
//    public ResponseEntity<?> AppStartTenant(@PathVariable("id") String id) throws Throwable
//    {
//        logger.info("SUClient " + " >> Start Tenant ID :  " + id);
//
//        try
//        {
//            HashMap<String, Object> hmenties = new HashMap<>();
//            hmenties.put("TenantId", id);
//            JSONObject req = BuildRequestBody(null, null, GReqType.SU, GReqCode.TenantStart, hmenties);
//            Request sureq = sr.GetRequest(req);
//            Event ev = sr.GetSystemService(sureq, "sys", null).ProcessRequest(sureq);
//            /**
//             * // Event ev = _srvcMgr.ProcessSystemRequest(req); /** TBD : Need to validate Event type, for failed event need to return valid HttpStatus.
//             */
//            return BuildResponse(ev);
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
//            EventFailed evf = new EventFailed(null, EventCode.Failed);
//            evf.setMessage(ex.getMessage());
//            return BuildResponse(evf);
//        }
//        /**
//         * TBD: Need to implement server error reason message for Exception and other fail cases.
//         */
//
//    }
//
//    @PostMapping("/tenant/{id}/stop")
//    public ResponseEntity<?> AppStopTenant(@PathVariable("id") String id) throws Throwable
//    {
//        logger.info("SUClient " + " >> Stop Tenant ID :  " + id);
//
//        try
//        {
//            HashMap<String, Object> hmenties = new HashMap<>();
//            hmenties.put("TenantId", id);
//            JSONObject req = BuildRequestBody(null, null, GReqType.SU, GReqCode.TenantStop, hmenties);
//            Request sureq = sr.GetRequest(req);
//            Event ev = sr.GetSystemService(sureq, "sys", null).ProcessRequest(sureq);
////           Event ev = _srvcMgr.ProcessSystemRequest(req);
//            /**
//             * TBD : Need to validate Event type, for failed event need to return valid HttpStatus.
//             */
//            return BuildResponse(ev);
//        }
//        catch (Exception ex)
//        {
//            logger.error(ex.getMessage(), ex);
//            EventFailed evf = new EventFailed(null, EventCode.Failed);
//            evf.setMessage(ex.getMessage());
//            return BuildResponse(evf);
//        }
//
//        /**
//         * TBD: Need to implement server error reason message for Exception and other fail cases.
//         */
//
//    }
//
//    private String GetToken(String token) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
//    {
//        String dytoken = AppUtil.Decrypt(token);
//        JSONObject jobj = new JSONObject(dytoken);
//        return jobj.getString("TenantCode");
//    }
//}
