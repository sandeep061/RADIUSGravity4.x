package ois.cc.gravity.ua.rest;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.LOGUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import jakarta.servlet.http.HttpServletRequest;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.services.ServiceManager;
import ois.cc.gravity.services.ServiceRegistry;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vn.radius.cc.platform.events.Event;
import org.vn.radius.cc.platform.events.EventCode;
import org.vn.radius.cc.platform.requests.RequestWithJSON;

import ois.cc.gravity.si.dark.DarkServer;

@CrossOrigin(origins =
{
    "*"
}, allowedHeaders =
{
    "*"
}, allowCredentials = "false")
@RestController
@RequestMapping("/gravity-api/v1/d")
public class RESTControllerDark
{

    private static Logger _logger = LoggerFactory.getLogger(RESTControllerDark.class);

    private ServiceRegistry _srvcReg = null;
    private ServiceManager _srvcMgr = null;

    private RESTRequestDTO _reqDTO;

    public RESTControllerDark(RESTRequestDTO reqdto)
    {

        _srvcReg = new ServiceRegistry();
        _srvcMgr = new ServiceManager();
        this._reqDTO = reqdto;
    }

    @PostMapping("/n")
    public ResponseEntity<?> TenantConfig(HttpServletRequest httprequest, @RequestBody String requestbody) throws Throwable
    {
        DarkServer darkServer = ServerContext.This().get_darkServer();
        JSONObject darkjobj = new JSONObject();
        JSONObject nuobj = new JSONObject(requestbody);
        if (nuobj.has("Tenant"))
        {
            JSONObject tenant = nuobj.getJSONObject("Tenant");
            String tntstate = tenant.getString("State");
            if (tntstate.equals("Start"))
            {
                darkjobj.put("ReqCode", "TenantStart");
            }
            else
            {
                darkjobj.put("ReqCode", "TenantStop");
            }
            String tntcode = tenant.getString("Code");
            darkjobj.put("TenantCode", tntcode);
            darkjobj.put("ReqType", "Control");
            darkjobj.put("ReqId", UIDUtil.GenerateUniqueId());
//            darkjobj.put("Token", darkServer.getDarkToken(tntcode));
        }
        RequestWithJSON reqjson = new RequestWithJSON(UIDUtil.GenerateUniqueId());
        reqjson.setJSONPayload(darkjobj.toString());

        org.vn.radius.cc.platform.events.Event event = darkServer.SendSyncRequest(reqjson);
        return BuildResponse(event);

    }

    @PostMapping
    public ResponseEntity<?> POSTRequest(HttpServletRequest httprequest, @RequestBody String requestbody) throws Throwable
    {
        String accessToken = httprequest.getHeader("access_token");

        JSONObject reqJsn = new JSONObject(requestbody);
        if (reqJsn.has("ReqCode") && !reqJsn.getString("ReqCode").equals("UserRegister"))
        {
            reqJsn.put("Token", accessToken);
        }
        RequestWithJSON reqjson = new RequestWithJSON(UIDUtil.GenerateUniqueId());
        reqjson.setJSONPayload(reqJsn.toString());
        Event event = ServerContext.This().get_darkServer().SendSyncRequest(reqjson);
        return BuildResponse(event);
    }

    @PostMapping("/tenantstart")
    public ResponseEntity<?> TenantStart(HttpServletRequest httprequest, @RequestBody String requestbody) throws Throwable
    {
        RequestWithJSON reqjson = new RequestWithJSON(UIDUtil.GenerateUniqueId());
        reqjson.setJSONPayload(requestbody);
        Event event = ServerContext.This().get_darkServer().SendSyncRequest(reqjson);
        return BuildResponse(event);

    }
    @GetMapping("/version")
    public ResponseEntity<?> VersionInfoFetch(HttpServletRequest httprequest) throws Throwable
    {
        RequestWithJSON reqjson = new RequestWithJSON(UIDUtil.GenerateUniqueId());
        JSONObject jobj = new JSONObject();
        jobj.put("ReqType", "Control");
        jobj.put("ReqCode", "VersionInfoFetch");
        jobj.put("ReqId", UIDUtil.GenerateUniqueId());
        reqjson.setJSONPayload(jobj.toString());
        Event event = ServerContext.This().get_darkServer().SendSyncRequest(reqjson);
        return BuildResponse(event);

    }

    protected ResponseEntity<?> BuildResponse(Event ev)
    {
        try
        {

            JSONObject evJSON = JSONUtil.ToJSON(ev);
            String resJsnStr = evJSON.toString();

            if (ev instanceof org.vn.radius.cc.platform.events.Event)
            {
                _logger.debug(LOGUtil.ArgString(resJsnStr));
                return ResponseEntity.status(HttpStatus.OK).body(resJsnStr);
            }
            if (ev.getEvCode().equals(EventCode.OK) || ev.getEvType().equals(org.vn.radius.cc.platform.events.EventType.Success))
            {
                _logger.debug(LOGUtil.ArgString(resJsnStr));
                return ResponseEntity.status(HttpStatus.OK).body(resJsnStr);
            }
            else
            {
                _logger.debug(LOGUtil.ArgString(resJsnStr));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resJsnStr);
            }
        }
        catch (Exception ex)
        {
            _logger.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
        }

    }
}
