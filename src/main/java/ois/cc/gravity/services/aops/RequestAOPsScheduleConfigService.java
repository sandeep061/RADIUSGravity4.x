package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import code.common.exceptions.CODEException;
import code.ua.events.Event;
import code.ua.events.EventFailedCause;
import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AOPsScheduleQuery;
import ois.cc.gravity.entities.util.UtilAOPsSH;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.framework.requests.aops.RequestAOPsScheduleConfig;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.AOPsSchedule;
import org.json.JSONObject;
import org.vn.radius.cc.platform.requests.RequestWithJSON;
import org.vn.radius.cc.platform.requests.gravity.RequestAOPsScheduleAlter;


import java.text.ParseException;
import java.util.Date;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;

public class RequestAOPsScheduleConfigService extends ARequestEntityService
{

    public RequestAOPsScheduleConfigService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestAOPsScheduleConfig req = (RequestAOPsScheduleConfig) request;
        Event ev = null;

        AOPs camp = _tctx.getDB().FindAssert(AOPs.class, req.getAOPs());
        /**
         * Logic :- <br>
         * -We have to check is AOPsSchedule is exist for this AOPs or not. <br>
         * -If found then we have to update AOPsSchedule. else we have to create a AOPsSchedule entity add in DB set to the campaign as well. <br>
         *
         */
        AOPsSchedule campSch = _tctx.getDB().Find(new AOPsScheduleQuery().filterByAops(camp.getId()));

        if (campSch == null)
        {
            campSch = new AOPsSchedule();
            campSch.setAOPs(camp);
            campSch.setIsScheduleEnable(req.getScheduleEnable());
            campSch.setStartDate(req.getStartDate());
            campSch.setEndDate(req.getEndDate());
            campSch.setStartHour(req.getStartHour());
            campSch.setEndHour(req.getEndHour());
            campSch.setDays(req.getDays());
            campSch.setMonths(req.getMonths());
            campSch.setWeeks(req.getWeeks());
            campSch.setAOPSType(req.getAOPSType());
        }
        else
        {
            campSch.setIsScheduleEnable(req.getScheduleEnable() == null ? campSch.getIsScheduleEnable() : req.getScheduleEnable());
            campSch.setStartDate(req.getStartDate() == null ? campSch.getStartDate() : req.getStartDate());
            campSch.setEndDate(req.getEndDate() == null ? campSch.getEndDate() : req.getEndDate());
            campSch.setStartHour(req.getStartHour() == null ? campSch.getStartHour() : req.getStartHour());
            campSch.setEndHour(req.getEndHour() == null ? campSch.getEndHour() : req.getEndHour());
            campSch.setDays(req.getDays() == null ? campSch.getDays() : req.getDays());
            campSch.setMonths(req.getMonths() == null ? campSch.getMonths() : req.getMonths());
            campSch.setWeeks(req.getWeeks() == null ? campSch.getWeeks() : req.getWeeks());
            campSch.setAOPSType(req.getAOPSType() == null ? campSch.getAOPSType() : req.getAOPSType());
        }
        validateStartAndEndTime(req, campSch);
        if (campSch.getId() == null)
        {
            _tctx.getDB().Insert(_uac.getUserSession().getUser(), campSch);
        }
        else
        {
            _tctx.getDB().Update(_uac.getUserSession().getUser(), campSch);
        }

        RequestAOPsScheduleAlter config=new RequestAOPsScheduleAlter(UIDUtil.GenerateUniqueId());
        config.setAOPsId(camp.getId());
        config.setToken(_sCtx.get_darkServer().getDarkToken(_tctx.getTenant().getCode()));

        JSONObject obj= JSONUtil.ToJSON(config);

        RequestWithJSON reqjson=new RequestWithJSON(UIDUtil.GenerateUniqueId());
        reqjson.setJSONPayload(obj.toString());

        _sCtx.get_darkServer().SendRequest(reqjson);
         ev = new EventOK(req, EventCode.AOPsScheduled);
        return ev;

    }

//    private org.vn.radius.cc.platform.events.Event AopsScheduledReqToDark(RequestEntityAdd req) throws Throwable
//    {
//        RequestAOPsScheduleConfig requestAOPsScheduleConfig = BuildAOPsScheduleConfigReq(req);
//        JSONObject jsonObject = JSONUtil.ToJSON(requestAOPsScheduleConfig);
//        RequestWithJSON reqjson = new RequestWithJSON(UIDUtil.GenerateUniqueId());
//        reqjson.setJSONPayload(jsonObject.toString());
//
//        org.vn.radius.cc.platform.events.Event event = ServerContext.This().get_darkServer().SendSyncRequest(reqjson);
//        return event;
//    }

    private void validateStartAndEndTime(RequestAOPsScheduleConfig req, AOPsSchedule campsch) throws CODEException, ParseException, GravityUnhandledException, GravityIllegalArgumentException {

        try
        {
            Date stdate = UtilAOPsSH.getStartAt(campsch);
            Date enddate = UtilAOPsSH.getEndAt(campsch);

            if (!stdate.before(enddate))
            {
                throw new GravityIllegalArgumentException("ScheduledOn",EventFailedCause.ValueOutOfRange,EvCauseRequestValidationFail.DataBoundaryLimitViolation);
            }
        }
        catch (ParseException pex)
        {
            _logger.error(pex.getMessage(), pex);
            throw new GravityUnhandledException(pex);
        }
    }


}
