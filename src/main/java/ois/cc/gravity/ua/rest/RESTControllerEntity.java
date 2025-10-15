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
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.ua.UAClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin(origins =
{
    "*"
}, allowedHeaders =
{
    "*"
}, allowCredentials = "false")
@RestController
@RequestMapping("/gravity-api/v1/e")
public class RESTControllerEntity
{

    private static Logger logger = LoggerFactory.getLogger(RESTControllerEntity.class);
    private ServiceRegistry _srvcReg = null;
    private ServiceManager _srvcMgr = null;

    private RESTRequestDTO _reqDTO;

    public RESTControllerEntity(RESTRequestDTO reqdto)
    {

        _srvcReg = new ServiceRegistry();
        _srvcMgr = new ServiceManager();
        this._reqDTO = reqdto;
    }

    private ResponseEntity<?> DoPreProcessRequest(String entity, JSONObject reqjson, GReqType type, GReqCode code, String entityid, EN suben, String subenid, HashMap<String, ArrayList<String>> colattr, String ForceDelete, String ecode, Long id) throws Exception, GravityUnhandledException, GravityRuntimeCheckFailedException, GravityIllegalArgumentException {

        Event ev = null;
        Request req = null;
        logger.trace("Req Code Getting RestControllerEntity Class" + code);
        Long enid = null;
        String enidstr = null;
        EN enEntity = EnumUtil.ValueOf(EN.class, entity);
        if (entityid != null)
        {
            switch (enEntity)
            {
                case CrossCXContactMap, SurveyData:
                    enidstr = entityid;
                    break;
                default:
                    enid = Long.valueOf(entityid);
            }
        }

        switch (code)
        {

            case EntityAdd:
                if (colattr != null)
                {
                    req = RESTRequestBuilder.BuildReqEdit(_reqDTO.getReqId(), reqjson, _reqDTO.getEN(), enid, suben, subenid, ColAttrMapType.Append, colattr);
                }
                else
                {
                    req = RESTRequestBuilder.BuildReqAdd(_reqDTO.getReqId(), reqjson, _reqDTO.getEN(), suben, subenid);
                }
                break;
            case EntityEdit:
                req = RESTRequestBuilder.BuildReqEdit(_reqDTO.getReqId(), reqjson, _reqDTO.getEN(), enid, suben, subenid, null, null);
                break;
            case EntityDelete:
                switch (enEntity)
                {
                    case CrossCXContactMap:
                        req = RESTRequestBuilder.BuildReqCrossCXContactDelete(_reqDTO.getReqId(), enidstr);
                        break;
                    default:
                        if (colattr != null)
                        {
                            req = RESTRequestBuilder.BuildReqEdit(_reqDTO.getReqId(), reqjson, _reqDTO.getEN(), enid, suben, subenid, ColAttrMapType.Remove, colattr);
                        }
                        else
                        {
                            req = RESTRequestBuilder.BuildReqDelete(_reqDTO.getReqId(), reqjson, _reqDTO.getEN(), enid, ForceDelete);
                        }
                        break;
                }
                break;
            case EntityFetch:
                switch (enEntity)
                {
                    case SurveyData:
                        req = RESTRequestBuilder.BuildReqSurveyDataFetch(_reqDTO.getReqId(), enidstr);
                        break;
                    default:
                        req = RESTRequestBuilder.BuildReqFtech(_reqDTO.getReqId(), _reqDTO.getEN(), enid, suben, subenid, reqjson);
                        break;
                }
                break;
            case AOPsCDNAddressEdit:
                req = RESTRequestBuilder.BuildAOPsCdnAddressEdit(_reqDTO.getReqId(), reqjson, ecode, id);
                break;
            case AOPsCallerIdAddressEdit:
                req = RESTRequestBuilder.BuildAOPsCalleridEdit(_reqDTO.getReqId(), reqjson, ecode, id);
                break;
            case AOPsScheduleConfig:
                req = RESTRequestBuilder.BuildAOPsScheduleConfig(_reqDTO.getReqId(), reqjson);
                break;

        }

        logger.trace(_reqDTO.getUAClient() + " -> " + JSONUtil.ToJSON(req).toString());

        UAClient uac = _reqDTO.getUAClient();
        ev = _srvcMgr.ProcessRequest(uac, req);
        ResponseEntity<?> resEn = BuildResponse(ev);
        return resEn;
    }

    enum ColAttrMapType
    {
        Append, Remove;
    }

    @PostMapping(value =
    {
        "/{entity}"
    })
    public ResponseEntity<?> POSTRequest(HttpServletRequest httprequest, @PathVariable(value = "entity", required = true) String entity, @RequestBody String request)
    {
        JSONObject reqJson = new JSONObject(request);
        try
        {
            UAClient uac = _reqDTO.getUAClient();

            ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityAdd, null, null, null, null, null, null, null);
            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Config, GReqCode.EntityAdd, th);
            return BuildResponse(evf);
        }
    }

    @PostMapping("/{entity}/{id}/{attribute}")
    public ResponseEntity<?> POSTRequestMap(HttpServletRequest httprequest, @PathVariable("entity") String entity, @PathVariable("id") String entityid, @PathVariable("attribute") String attribute, @RequestBody String request)
    {
        JSONObject reqJson = new JSONObject(request);

        try
        {

            HashMap<String, ArrayList<String>> appendAttr = new HashMap<>();
            ArrayList<String> ids = JSONUtil.FromJSON(reqJson.getJSONArray("UIDs").toString(), ArrayList.class);
            appendAttr.put(attribute, ids);

            ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityAdd, entityid, null, null, appendAttr, null, null, null);

            return resp;
        }
        catch (Throwable th)
        {
            logger.error(th.getMessage(), th);

            EventException evf = new EventException(_reqDTO.getReqId(), GReqType.Config, GReqCode.EntityAdd, th);
            evf.setMessage(th.getMessage());
            return BuildResponse(evf);
        }
    }

    @PutMapping(value =
    {
            "/{entity}", "/{entity}/{id}", "/{subentity}/{id}/{entity}"
    })
    public ResponseEntity<?> PUTRequest(HttpServletRequest httprequest, @RequestBody String request, @PathVariable("entity") String entity, @PathVariable(value = "id", required = false) String entityid, @PathVariable(value = "subentity", required = false) String subentity)
    {
        JSONObject reqJson = new JSONObject(request);
        ResponseEntity<?> resp = null;
        try
        {

            EN suben = null;
            if (subentity != null)
            {
                suben = EnumUtil.ValueOf(EN.class, subentity);
            }
            switch (entity)
            {
                case "aopsschedule":
                    resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.AOPsScheduleConfig, null, null, null, null, null, null, null);
                    break;
                default:
                    resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityEdit, entityid, suben, null, null, null, null, null);
                    break;
            }

//             resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityEdit, entityid, suben, null, null, null, null, null);
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

    @PatchMapping("/{entity}")
    public ResponseEntity<?> PatchRequest(HttpServletRequest httprequest, @RequestBody String request, @PathVariable("entity") String entity, @RequestParam(value = "aopscdncode", required = false) String aopscdncode, @RequestParam(value = "aopscalleridcode", required = false) String aopscalleridcode, @RequestParam(value = "aopsid", required = false) Long aopsid)
    {
        JSONObject reqJson = new JSONObject(request);
        ResponseEntity<?> resp = null;
        try
        {
            switch (entity)
            {
                case "aopscdnaddress":
                    resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.AOPsCDNAddressEdit, null, null, null, null, null, aopscdncode, aopsid);
                    break;
                case "aopscalleridaddress":
                    resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.AOPsCallerIdAddressEdit, null, null, null, null, null, aopscalleridcode, aopsid);
                    break;

            }
//            ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityEdit, null, null, null, null, null,code,id);
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

    @DeleteMapping("/{entity}/{id}")
    public ResponseEntity<?> DeleteRequest(HttpServletRequest httprequest, @PathVariable(name = "entity", required = true) String entity, @PathVariable(name = "id", required = true) String entityid, @RequestParam(name = "ForceDelete", required = false) String ForceDelete)
    {
        try
        {
            ResponseEntity<?> resp = DoPreProcessRequest(entity, null, GReqType.Config, GReqCode.EntityDelete, entityid, null, null, null, ForceDelete, null, null);
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

    @DeleteMapping("/{entity}/{id}/{attribute}")
    public ResponseEntity<?> POSTRequestUnMap(HttpServletRequest httprequest, @PathVariable("entity") String entity, @PathVariable("id") String entityid, @PathVariable("attribute") String attribute, @RequestParam(name = "UIDs") String ids)
    {
        try
        {
            JSONObject reqJson = new JSONObject();
            HashMap<String, ArrayList<String>> removeAttr = new HashMap<>();
            ArrayList<String> alUIDs = JSONUtil.FromJSON(ids, ArrayList.class);
            removeAttr.put(attribute, alUIDs);

            ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityDelete, entityid, null, null, removeAttr, null, null, null);
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

    @GetMapping(value =
    {
        "/{entity}", "/{entity}/{uid}", "/{subentity}/{subenid}/{entity}"
    })
    public ResponseEntity<?> GetRequest(HttpServletRequest httprequest, @PathVariable(name = "entity", required = true) String entity, @PathVariable(name = "uid", required = false) String uid, @PathVariable(name = "subentity", required = false) String subentity, @PathVariable(name = "subenid", required = false) String subenid, @RequestParam(name = "filters", required = false) String filters, @RequestParam(name = "orderby", required = false) String orderby, @RequestParam(name = "limit", required = false) Integer limit, @RequestParam(name = "offset", required = false) Integer offset, @RequestParam(name = "includecount", required = false) Boolean recordcount)
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

            String reqid = httprequest.getHeader(UIParams.ReqId.getVal());
            logger.trace("Request ID: " + reqid);
            ResponseEntity<?> resp = DoPreProcessRequest(entity, reqJson, GReqType.Config, GReqCode.EntityFetch, uid, suben, subenid, null, null, null, null);

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

    protected ResponseEntity<?> BuildResponse(Event ev)
    {
        try
        {
//            ArrayList<AEntity> resList = new ArrayList<>();
//            if(ev instanceof EventEntitiesFetched)
//            {
//                EventEntitiesFetched evf = (EventEntitiesFetched)ev;
//                ArrayList<AEntity> entities = evf.getEntities();
//                for(AEntity e: entities)
//                {
//                    AEntity unproxy = HibernateUtil.unproxy(e);
//                    resList.add(unproxy);
//                }
//                evf.setEntities(resList);
//            }
            JSONObject evJSON = JSONUtil.ToJSON(ev);
            String resJsnStr = evJSON.toString();
            //   logger.info("Build Response : " + resJsnStr);

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
