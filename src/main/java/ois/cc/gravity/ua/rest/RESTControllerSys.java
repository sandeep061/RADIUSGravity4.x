package ois.cc.gravity.ua.rest;

import CrsCde.CODE.Common.Utils.EnumUtil;
import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.Event;
import code.ua.events.EventException;
import code.ua.events.EventType;
import code.ua.requests.Request;
import jakarta.servlet.http.HttpServletRequest;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.cc.gravity.services.ServiceManager;
import ois.cc.gravity.services.ServiceRegistry;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static ois.cc.gravity.framework.requests.GReqCode.XSPIConnectConfig;

@CrossOrigin(origins = {"*"}, allowedHeaders = {"*"}, allowCredentials = "false")
@RestController
@RequestMapping("/gravity-api/v1/s")
public class RESTControllerSys
{

    private static final Logger logger = LoggerFactory.getLogger(RESTControllerSys.class);
    private ServiceRegistry _srvcReg = null;
    private ServiceManager _srvcMgr = null;

    private RESTRequestDTO _reqDTO;

    public RESTControllerSys(RESTRequestDTO reqdto)
    {
        _srvcReg = new ServiceRegistry();
        _srvcMgr = new ServiceManager();
        this._reqDTO = reqdto;
    }

    @PostMapping(value = {"/{entity}"})
    public ResponseEntity<?> POSTRequest(HttpServletRequest httprequest, @PathVariable(value = "entity", required = true) String entity, @RequestBody String request)
    {
        JSONObject reqJson = new JSONObject(request);
        try
        {
            UAClient uac = _reqDTO.getUAClient();

            ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.XSPIConnectAdd, null, null, null, null);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Config, GReqCode.EntityAdd, th);
            return BuildResponse(evf);
        }
    }

    @PutMapping(value = {"/{entity}", "/{entity}/{id}"})
    public ResponseEntity<?> PUTRequest(HttpServletRequest httprequest, @RequestBody String request, @PathVariable("entity") String entity, @PathVariable(value = "id", required = false) Long entityid)
    {
        JSONObject reqJson = new JSONObject(request);
        ResponseEntity<?> resp = null;
        try
        {
            switch (entity)
            {
                case "xspiconnect":
                    resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityEdit, entityid, null, null, null);
                    break;
                case "xspiclient":
                    resp = DoPreProcessRequest(entity, reqJson, GReqType.Control, GReqCode.XSPIClientConfig, null, null, null, null);
            }

            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Config, GReqCode.EntityEdit, th);
            evf.setMessage(th.getMessage());
            return BuildResponse(evf);
        }

    }

    @GetMapping(value = {"/{entity}", "/{entity}/{uid}"})
    public ResponseEntity<?> GetRequest(HttpServletRequest httprequest, @PathVariable(name = "entity", required = true) String entity, @PathVariable(name = "uid", required = false) Long uid, @PathVariable(name = "subentity", required = false) String subentity, @PathVariable(name = "subenid", required = false) String subenid, @RequestParam(name = "filters", required = false) String filters, @RequestParam(name = "orderby", required = false) String orderby, @RequestParam(name = "limit", required = false) Integer limit, @RequestParam(name = "offset", required = false) Integer offset, @RequestParam(name = "includecount", required = false) Boolean recordcount)
    {

        JSONObject reqJson = new JSONObject();
        logger.trace("Fetch Entity Name " + entity);
        try
        {
            HashMap<String, ArrayList<String>> reqFltrs = new HashMap<>();
            ArrayList<HashMap<String, Boolean>> reqOrderby = new ArrayList<>();
            Integer reqLimit = limit == null ? 128 : limit;
            Integer reqOffset = offset == null ? 0 : offset;
            Boolean recCount = recordcount != null && recordcount;

            EN suben = null;
            if (subentity != null)
            {
                suben = EnumUtil.ValueOf(EN.class, subentity);
            }
            if (uid != null)
            {
                reqFltrs.put("byid", new ArrayList<>(List.of(uid.toString())));
            }
            else
            {
                if (!(filters == null || filters.isEmpty()))
                {
                    reqFltrs = JSONUtil.FromJSON(filters, HashMap.class);
                    reqJson.put("Filters", reqFltrs);
                }
                if (!(orderby == null || orderby.isEmpty()))
                {
                    for (Object obj : new JSONArray(orderby))
                    {
                        JSONObject jsn = (JSONObject) obj;
                        HashMap<String, Boolean> hm = JSONUtil.FromJSON(jsn, HashMap.class);
                        reqOrderby.add(hm);
                    }
                    reqJson.put("OrderBy", reqOrderby);

                }
            }

            reqJson.put("Limit", reqLimit);
            reqJson.put("Offset", reqOffset);
            reqJson.put("IncludeCount", recCount);

            ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityFetch, uid, suben, subenid, null);

            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Config, GReqCode.EntityFetch, th);
            evf.setMessage(th.getMessage());
            return BuildResponse(evf);
        }

    }

    @GetMapping("/{entity}/{tenantcode}/{xservercode}")
    public ResponseEntity<?> GetDiscoverRequest(HttpServletRequest httprequest, @PathVariable(name = "entity", required = true) String entity,@PathVariable(name = "tenantcode") String tenantcode, @PathVariable(name = "xservercode") String xservercode ) throws GravityUnhandledException, Exception
    {

        JSONObject reqJson = new JSONObject();
        reqJson.put("XServerCode", xservercode);
        reqJson.put("TenantCode", tenantcode);

        ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Control, GReqCode.XSPIClientDiscover, null, null, null, null);

        return resp;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> AppUserSysSignin(HttpServletRequest httprequest, @RequestBody String request)
    {
        logger.info("UserRegister " + " >> " + (request == null ? "NULL" : request));

        JSONObject reqJson = new JSONObject(request);
        try
        {
            UAClient uac = _reqDTO.getUAClient();
            ResponseEntity<?> resp = DoPreProcessRequest(reqJson, GReqType.System, GReqCode.SUSignin);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Config, GReqCode.EntityAdd, th);
            return BuildResponse(evf);
        }

    }

    @DeleteMapping("/{entity}/{tenantcode}/{xservercode}")
    public ResponseEntity<?> DeleteRequest(HttpServletRequest httprequest, @PathVariable(name = "entity", required = true) String entity, @PathVariable(name = "tenantcode") String tenantcode, @PathVariable(name = "xservercode") String xservercode)
    {
        try
        {
            JSONObject reqJson = new JSONObject();
            reqJson.put("XServerCode", xservercode);
            reqJson.put("TenantCode", tenantcode);
            ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Control, GReqCode.XSPIClientDelete, null, null, null, null);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Config, GReqCode.EntityDelete, th);
            evf.setMessage(th.getMessage());
            return BuildResponse(evf);
        }
    }

    private ResponseEntity<?> DoPreProcessRequest(JSONObject reqjson, GReqType type, GReqCode code) throws Exception
    {

        Event ev = null;
        Request req = null;
        switch (code)
        {
            case SUSignin:
                req = RESTRequestBuilder.BuildSuSignInRqeuest(_reqDTO.getReqId(), type, code, reqjson.getString("LoginId"), reqjson.getString("Password"));
                break;
//            case Logout:
//                req = RESTRequestBuilder.BuildReqLogout(_reqDTO.getReqId(), reqjson.getString("access_token"));
//                break;
        }

        UAClient uac = _reqDTO.getUAClient();
        ev = _srvcMgr.ProcessRequest(uac, req);
        ResponseEntity<?> resEn = BuildResponse(ev);
        return resEn;
    }

    private ResponseEntity<?> DoPreProcessRequest(String entity, JSONObject reqjson, GReqType type, GReqCode code, Long entityid, EN suben, String subenid, HashMap<String, ArrayList<String>> colattr) throws Exception, GravityUnhandledException
    {

        Request req = null;
        logger.trace("Req Code Getting RestControllerEntity Class" + code);
        switch (code)
        {
            case EntityFetch:
                req = RESTRequestBuilder.BuildReqFtech(_reqDTO.getReqId(), _reqDTO.getEN(), entityid, suben, subenid, reqjson);
                break;
            case XSPIClientDiscover:
                req = RESTRequestBuilder.BuildReqXSPIConDiscover(_reqDTO.getReqId(), reqjson);
                break;
            case XSPIClientConfig:
                req = RESTRequestBuilder.BuildReqXSPIClientConfig(_reqDTO.getReqId(), reqjson);
                break;
            case XSPIClientDelete:
                req = RESTRequestBuilder.BuildReqXSPIClientDelete(_reqDTO.getReqId(), reqjson);
                break;
            case XSPIConnectConfig:
                req = RESTRequestBuilder.BuildReqXSPIConConfig(_reqDTO.getReqId(), reqjson);
                break;
            case XSPIConnectAdd:
                req = RESTRequestBuilder.BuildReqXSPIConnectAdd(_reqDTO.getReqId(), reqjson);
                break;
            case EntityEdit:
                req = RESTRequestBuilder.BuildReqEdit(_reqDTO.getReqId(), reqjson, _reqDTO.getEN(), entityid, suben, subenid, null, null);
                break;
        }

        if (logger.isDebugEnabled())
        {
            logger.trace(_reqDTO.getUAClient() + " -> " + JSONUtil.ToJSON(req).toString());
        }

        UAClient uac = _reqDTO.getUAClient();
        Event ev = _srvcMgr.ProcessRequest(uac, req);
        ResponseEntity<?> resEn = BuildResponse(ev);
        return resEn;
    }

    protected ResponseEntity<?> BuildResponse(Event ev)
    {
        try
        {
            JSONObject evJSON = JSONUtil.ToJSON(ev);
            String resJsnStr = evJSON.toString();

            if (ev.getEvCode() == code.ua.events.EventCode.OK || ev.getEvType().equals(EventType.OK) || ev.getEvCode() == code.ua.events.EventCode.Success)
            {
                return ResponseEntity.status(HttpStatus.OK).body(resJsnStr);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resJsnStr);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
        }

    }
}
