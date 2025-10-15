package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityEdited;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.Limits;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.aops.RequestDispositionEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.*;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.aops.CallbackType;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Disposition;
import ois.radius.cc.entities.tenant.cc.Disposition0;
import ois.radius.cc.entities.tenant.cc.Disposition1;
import ois.radius.ca.enums.Channel;
import org.json.JSONObject;
import org.vn.radius.cc.platform.exceptions.RADException;
import org.vn.radius.cc.platform.requests.RequestWithJSON;
import org.vn.radius.cc.platform.requests.gravity.RequestDispositionAlter;

import java.util.List;

public class RequestDispositionEditService extends ARequestEntityService
{

    public RequestDispositionEditService(UAClient uac)
    {
        super(uac);
    }

    Disposition _disp;

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestDispositionEdit req = (RequestDispositionEdit) request;
        _disp = _tctx.getDB().FindAssert(Disposition.class, req.getDispositionId());

        if (_disp instanceof Disposition0)
        {
            if (req.getDefault() != null && req.getDefault().equals(Boolean.TRUE))
            {
                checkDefault(req, _disp);
            }
            validateSuperDispReqParams(req);
            _disp = buildSuperDisp(req);
        }
        else if (_disp instanceof Disposition1)
        {
            validateSubDispReqParams(req);
            _disp = buildSubDisp(req);
        }

        //update entity to db.
        try
        {
            _tctx.getDB().Update(_uac.getUserSession().getUser(), _disp);
            if (_disp.getAOPs() != null)
            {
                sendDisposionAlterReqToDark(_disp.getAOPs().getId());
            }
        }
        catch (Throwable e)
        {

            if (e instanceof GravityUniqueConstraintViolationException ex)
            {
                _tctx.getDB().Find(EN.Disposition.getEntityClass(), req.getDispositionId());
                throw new GravityUniqueConstraintViolationException(e.getCause(), ex.getEntityName(), ex.getCondition());
            }
            throw e;

        }

        EventEntityEdited ev = new EventEntityEdited(request, _disp);
        return ev;
    }

    private void validateSuperDispReqParams(RequestDispositionEdit req) throws RADException, GravityException
    {
        Disposition0 disp = (Disposition0) _disp;

        //validate the scheduled type and auto scheduled related attributes.
        validateScheduledTypeForSuperDisp(req, disp);

        //validate the channels of disposition.
        validateDispChannels(req);

    }

    private void validateScheduledTypeForSuperDisp(RequestDispositionEdit req, Disposition0 supdisp) throws GravityIllegalArgumentException
    {
        Boolean isScheduled = req.getIsScheduledType();
        Boolean isAutoSch = req.getIsAutoSetSchedule();

        if (isScheduled == null || isAutoSch == null)
        {
            return;
        }

        //If in request IsScheduledType is true but in entity it is false.
        if (isAutoSch
                && (supdisp.getIsAutoSetSchedule() == null || !supdisp.getIsAutoSetSchedule()))
        {

            if (req.getCallbackType() == null)
            {
                throw new GravityIllegalArgumentException("CallbackType", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
            if (req.getCallbackType().equals(CallbackType.Agent))
            {
                throw new GravityIllegalArgumentException("CallbackType", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
            }

            //If IsScheduledType is true and IsAutoSetSchedule is also true then we need to validate the request.
            if (req.getScheduleAfter() == null)
            {
                throw new GravityIllegalArgumentException("ScheduleAfter", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
            if (req.getScheduleAfter() < Limits.ContactScheduledOnAfterNow || req.getScheduleAfter() > Limits.ContactScheduledOnAfter_MAX)
            {
                throw new GravityIllegalArgumentException("ScheduleAfter", EventFailedCause.DataBoundaryLimitViolation, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    private void validateSubDispReqParams(RequestDispositionEdit req) throws RADException, GravityException
    {
        //for now we are validating only channels for sub-disposition.
        validateDispChannels(req);
    }

    /**
     * This method will validate channels of disposition. <br>
     * -Sub-disposition's channels must be the subset of super disposition's channel. <br>
     * -Channel can't be removed from disposition. <br>
     *
     * @param req
     * @throws RADException
     */
    private void validateDispChannels(RequestDispositionEdit req) throws GravityException, GravityIllegalArgumentException
    {
        //Check if channel is edited or not.
        if (req.getChannels() == null || req.getChannels().isEmpty())
        {
            return;
        }

        Disposition disp = (Disposition) _disp;
        if (disp instanceof Disposition1)
        {
            Disposition superDsip = ((Disposition1) disp).getSuper();
            if (!superDsip.getChannels().containsAll(req.getChannels()))
            {
                //TBD:send that sub-disposition channels are not subset of it's super disposition.
                throw new GravityIllegalArgumentException("Channels", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
            }
        }

        //TBD: send cahnnel can't removed once added.
        if (!req.getChannels().containsAll(disp.getChannels()))
        {
            throw new GravityIllegalArgumentException("Channels", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private Disposition0 buildSuperDisp(RequestDispositionEdit req) throws GravityAttributeConstraintFailedException, RADException, NoSuchFieldException
    {
        Disposition0 disp0 = (Disposition0) _disp;

        String name = req.getName() == null ? _disp.getName() : req.getName();
        _disp.setName(name);

        Boolean isdefault = req.getDefault() == null ? disp0.getIsDefault() : req.getDefault();
        disp0.setIsDefault(isdefault);
        Boolean isScheduled = req.getIsScheduledType() == null ? disp0.getIsScheduledType() : req.getIsScheduledType();
        Boolean isAutoSet = req.getIsAutoSetSchedule() == null ? disp0.getIsAutoSetSchedule() : req.getIsAutoSetSchedule();

        if (isScheduled)
        {
            disp0.setIsScheduledType(true);
            if (isAutoSet)
            {
                disp0.setIsAutoSetSchedule(isAutoSet);
                disp0.setCallbackType(req.getCallbackType() == null ? disp0.getCallbackType() : req.getCallbackType());
                disp0.setScheduleAfter(req.getScheduleAfter() == null ? disp0.getScheduleAfter() : req.getScheduleAfter());
            }
            else
            {
                disp0.setIsAutoSetSchedule(false);
                disp0.setCallbackType(null);
                disp0.setScheduleAfter(null);
            }
        }
        else
        {
            disp0.setIsScheduledType(false);
            disp0.setIsAutoSetSchedule(false);
            disp0.setScheduleAfter(null);
            disp0.setCallbackType(null);

        }

        List<Channel> channels = req.getChannels() == null ? _disp.getChannels() : req.getChannels();
        _disp.setChannels(channels);
        _disp.setDispSeq(req.getDispSeq() == null ? Limits.Max_NoOf_Disposition : req.getDispSeq());

        return disp0;
    }

    private Disposition1 buildSubDisp(RequestDispositionEdit req) throws GravityAttributeConstraintFailedException, NoSuchFieldException, GravityUnhandledException
    {
        Disposition1 disp1 = (Disposition1) _disp;

        String name = req.getName() == null ? _disp.getName() : req.getName();
        _disp.setName(name);

        List<Channel> chns = req.getChannels() == null || req.getChannels().isEmpty() ? disp1.getSuper().getChannels() : req.getChannels();
        _disp.setChannels(chns);
        _disp.setDispSeq(req.getDispSeq() == null ? Limits.Max_NoOf_Disposition : req.getDispSeq());
        //we can't change campaign and category of a disposition so no need to set them.

        return disp1;
    }

    private void checkDefault(RequestDispositionEdit req, Disposition dbdisp) throws CODEException, GravityException, CODEException
    {
        JPAQuery query = new JPAQuery("SELECT d FROM Disposition0 d WHERE d.Category = :category AND d.IsDefault = true AND d.AOPs.Id=:id AND d.Deleted=:deleted");
        query.setParam("category", dbdisp.getCategory());
        query.setParam("id", dbdisp.getAOPs().getId());
        query.setParam("deleted", Boolean.FALSE);
        Disposition0 disp = _tctx.getDB().Find(EN.Disposition0, query);
        if (disp != null)
        {
            throw new GravityEntityExistsException(EN.Disposition.name(), "Code,Category,isdefault", OPRelational.Eq, disp.getCode() + "," + disp.getCategory().name() + "," + disp.getIsDefault());
        }
    }

    private void sendDisposionAlterReqToDark(long campid) throws Throwable
    {
        RequestDispositionAlter reqdispAlter = new RequestDispositionAlter(UIDUtil.GenerateUniqueId());
        reqdispAlter.setAOPsId(campid);
        reqdispAlter.setToken(_sCtx.get_darkServer().get_darkToken().get(_tctx.getTenant().getCode()));

        JSONObject jsonObject = JSONUtil.ToJSON(reqdispAlter);
        RequestWithJSON reqjson = new RequestWithJSON(UIDUtil.GenerateUniqueId());
        reqjson.setJSONPayload(jsonObject.toString());

        ServerContext.This().get_darkServer().SendRequest(reqjson);
    }
}
