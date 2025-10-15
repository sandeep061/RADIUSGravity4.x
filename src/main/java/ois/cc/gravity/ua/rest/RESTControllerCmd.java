package ois.cc.gravity.ua.rest;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.Event;
import code.ua.events.EventException;
import code.ua.events.EventFailedCause;
import code.ua.events.EventNoUASessionExists;
import code.ua.events.EventRequestValidationFailed;
import code.ua.events.EventType;
import code.ua.requests.Request;
import jakarta.servlet.http.HttpServletRequest;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.cc.gravity.services.ServiceManager;
import ois.cc.gravity.services.ServiceRegistry;
import ois.cc.gravity.ua.UACRegistry;
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
@RequestMapping("/gravity-api/v1/c")
public class RESTControllerCmd
{

    private static Logger logger = LoggerFactory.getLogger(RESTControllerCmd.class);
    private ServiceRegistry _srvcReg = null;
    private ServiceManager _srvcMgr = null;

    private RESTRequestDTO _reqDTO;

    public RESTControllerCmd(RESTRequestDTO reqdto)
    {
        _srvcReg = new ServiceRegistry();
        _srvcMgr = new ServiceManager();
        this._reqDTO = reqdto;
    }

    private ResponseEntity<?> DoPreProcessRequest(JSONObject reqjson, GReqType type, GReqCode code) throws Exception
    {

        Event ev = null;
        Request req = null;
        switch (code)
        {
            case Register:
                req = RESTRequestBuilder.BuildReqRegister(_reqDTO.getReqId(), reqjson.getString("tenant_code"), reqjson.getString("access_token"), reqjson.getString("role"));
                break;
            case Logout:
                req = RESTRequestBuilder.BuildReqLogout(_reqDTO.getReqId(), reqjson.getString("access_token"), reqjson.getString("role"));
                break;
            case ClearTemporaryState:
                String tntcode = reqjson.getString("TenantCode");
                req = RESTRequestBuilder.BuildReqClearTemporaryState(_reqDTO.getReqId(), tntcode);
                break;
            case VersionInfoFetch:
                req = RESTRequestBuilder.BuildVersionInfoFetch(_reqDTO.getReqId());
                break;
            case SurveyInfoFetch:
                req = RESTRequestBuilder.BuildSurveyInfo(_reqDTO.getReqId(), reqjson);
                break;

        }

        UAClient uac = _reqDTO.getUAClient();
        ev = _srvcMgr.ProcessRequest(uac, req);
        ResponseEntity<?> resEn = BuildResponse(ev);
        return resEn;
    }

    private void assertUAClient(HttpServletRequest request) throws EventWrapperException
    {
        String reqid = this._reqDTO.getReqId();
        String accessToken = request.getHeader("access_token");

        if (accessToken == null)
        {
            throw new EventWrapperException(new EventRequestValidationFailed(reqid, GReqType.System, GReqCode.System, "access_token", EventFailedCause.NonOptionalConstraintViolation));
        }
        UAClient uac = UACRegistry.This().Get(accessToken);
        if (uac == null)
        {
            EventNoUASessionExists ev = new EventNoUASessionExists(reqid, GReqType.System, GReqCode.System);
            throw new EventWrapperException(ev);
        }
        this._reqDTO.setUAClient(uac);

    }

    @PostMapping("/register")
    public ResponseEntity<?> AppUserSignin(HttpServletRequest httprequest, @RequestBody String request)
    {
        logger.info("UserRegister " + " >> " + (request == null ? "NULL" : request));

        JSONObject reqJson = new JSONObject(request);
        try
        {
            UAClient uac = _reqDTO.getUAClient();
            ResponseEntity<?> resp = DoPreProcessRequest(reqJson, GReqType.Auth, GReqCode.Register);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Auth, GReqCode.Register, th);
            return BuildResponse(evf);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> AppUserLogout(HttpServletRequest httprequest, @RequestBody String request)
    {
        logger.info("UserRegister " + " >> " + (request == null ? "NULL" : request));

        JSONObject reqJson = new JSONObject(request);
        try
        {
            UAClient uac = _reqDTO.getUAClient();
            ResponseEntity<?> resp = DoPreProcessRequest(reqJson, GReqType.Auth, GReqCode.Logout);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Auth, GReqCode.Logout, th);
            return BuildResponse(evf);
        }

    }

    @GetMapping("/version")
    public ResponseEntity<?> VersionFetch(HttpServletRequest httprequest)
    {
        logger.info("UserRegister " + " >> " + (httprequest == null ? "NULL" : httprequest));

        JSONObject reqJson = new JSONObject();
        try
        {
            UAClient uac = _reqDTO.getUAClient();
            ResponseEntity<?> resp = DoPreProcessRequest(reqJson, GReqType.Control, GReqCode.VersionInfoFetch);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Auth, GReqCode.Logout, th);
            return BuildResponse(evf);
        }

    }

    @GetMapping(value =
    {
        "/surveyinfo/{uid}"
    })
    public ResponseEntity<?> GetRequest(HttpServletRequest httprequest, @PathVariable(name = "uid", required = false) String uid)
    {

        logger.info("GetSurveyInfo " + " >> " + (httprequest == null ? "NULL" : httprequest));

        JSONObject reqJson = new JSONObject();
        try
        {
//            UAClient uac = _reqDTO.getUAClient();
            reqJson.put("USUID", uid);
            assertUAClient(httprequest);
            ResponseEntity<?> resp = DoPreProcessRequest(reqJson, GReqType.Control, GReqCode.SurveyInfoFetch);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Auth, GReqCode.Logout, th);
            return BuildResponse(evf);
        }

    }

    @DeleteMapping("/cleartempstate")
    public ResponseEntity<?> UpdateEntityInitialState(HttpServletRequest httprequest, @RequestParam(name = "tenantcode") String tenantcode)
    {
        JSONObject reqJson = new JSONObject();
        reqJson.put("TenantCode", tenantcode);
        try
        {
            UAClient uac = _reqDTO.getUAClient();
            ResponseEntity<?> resp = DoPreProcessRequest(reqJson, GReqType.Control, GReqCode.ClearTemporaryState);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Auth, GReqCode.Logout, th);
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
