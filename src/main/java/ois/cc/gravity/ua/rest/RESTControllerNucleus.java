package ois.cc.gravity.ua.rest;

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
import ois.cc.gravity.ua.UAClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins =
{
    "*"
}, allowedHeaders =
{
    "*"
}, allowCredentials = "false")
@RestController
@RequestMapping("/gravity-api/v1/n")
public class RESTControllerNucleus
{

    private static Logger logger = LoggerFactory.getLogger(RESTControllerNucleus.class);
    private ServiceRegistry _srvcReg = null;
    private ServiceManager _srvcMgr = null;

    private RESTRequestDTO _reqDTO;

    public RESTControllerNucleus(RESTRequestDTO reqdto)
    {
        _srvcReg = new ServiceRegistry();
        _srvcMgr = new ServiceManager();
        this._reqDTO = reqdto;
    }

    private ResponseEntity<?> DoPreProcessRequest(JSONObject reqjson, GReqType type, String code) throws Exception
    {

        Event ev = null;
        Request req = null;
        switch (code)
        {
            case "Start":
                req = RESTRequestBuilder.BuildReqStartTenant(_reqDTO.getReqId(), reqjson);
                break;
            case "Stop":
                req = RESTRequestBuilder.BuildReqStopTenant(_reqDTO.getReqId(), reqjson);
                break;
        }

        UAClient uac = _reqDTO.getUAClient();
        ev = _srvcMgr.ProcessRequest(uac, req);
        ResponseEntity<?> resEn = BuildResponse(ev);
        return resEn;
    }

    @PostMapping
    public ResponseEntity<?> TenantConfig(HttpServletRequest httprequest, @RequestBody String request)
    {
        JSONObject reqJson = new JSONObject(request);
        ResponseEntity<?> resp = null;
        try
        {
            if (reqJson.has("Tenant"))
            {
                JSONObject tenant = reqJson.getJSONObject("Tenant");
                String code = tenant.getString("State");
//                UAClient uac = _reqDTO.getUAClient();
                resp = DoPreProcessRequest(tenant, GReqType.Control, code);
            }
            else
            {
                String msg="Tenant start/stop process ignore due to not getting tenant data";
                logger.info(msg);
                throw new Exception(msg);
            }
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Config, GReqCode.EntityAdd, th);
            return BuildResponse(evf);
        }

    }

    protected ResponseEntity<?> BuildResponse(Event ev)
    {
        try
        {
            JSONObject evJSON = JSONUtil.ToJSON(ev);
            String resJsnStr = evJSON.toString();
            //   logger.info("Build Response : " + resJsnStr);

            if (ev.getEvCode() == code.ua.events.EventCode.OK || ev.getEvType().equals(EventType.OK)
                    || ev.getEvCode() == code.ua.events.EventCode.Success)
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
