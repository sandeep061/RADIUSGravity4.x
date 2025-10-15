package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.events.EventRequestValidationFailed;
import code.ua.requests.Request;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.aops.CallbackType;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.*;
import ois.cc.gravity.Limits;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.framework.requests.aops.RequestDispositionAdd;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.radius.ca.enums.Channel;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.vn.radius.cc.platform.requests.RequestWithJSON;
import org.vn.radius.cc.platform.requests.gravity.RequestDispositionAlter;

public class RequestDispositionAddService extends ARequestEntityService
{

    public RequestDispositionAddService(UAClient uac)
    {
        super(uac);
    }

    MySQLDB coreDB = _tctx.getDB();

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestDispositionAdd req = (RequestDispositionAdd) request;

        /**
         * Common processor for both CM and NC Disposition management.
         */
        AOPs camp = null;
        if (req.getAOPs() != null)
        {
            camp = _tctx.getDB().Find(EN.AOPs.getEntityClass(), req.getAOPs());
        }

        //Find super disposition.
        /**
         * Determine if this request is for Super or Sub disposition. <br>
         * If the request attributes contain 'Super' then this request is for 'Sub' disposition. <br>
         */
        Disposition superDisp = null;
        if (req.getSuperDispId() == null)
        {
            Event ev = validateSuperDispReqParams(req, camp);
            if (ev != null)
            {
                return ev;
            }
        }
        else
        {
            superDisp = coreDB.FindAssert(Disposition.class, req.getSuperDispId());
            Event ev = validateSubDispReqParams(req, superDisp);
            if (ev != null)
            {
                return ev;
            }
        }

        Disposition disp = (superDisp == null
                ? buildSuper(req, camp)
                : buildSub(req, superDisp));

        coreDB.Insert(_uac.getUserSession().getUser(), disp);
        if (camp != null)
        {
            sendDisposionAlterReqToDark(camp.getId());
        }

        EventEntityAdded ev = new EventEntityAdded(req, disp);
        return ev;
    }

    private Event validateSuperDispReqParams(RequestDispositionAdd req, AOPs camp) throws CODEException, GravityException, CODEException
    {
        //Till now we don't have validation for none-campaign disposition.
        if (camp == null)
        {
            return null;
        }

        Event ev = null;

        List<Channel> reqChnls = req.getChannels();

        /**
         * Need to check the supplied channels are valid for the supplied campaign or not.
         */
        if (!areChannelsValid(camp, reqChnls))
        {
            ev = new EventRequestValidationFailed(req, "Channels", EventFailedCause.ValueOutOfRange);
            return ev;
        }

        Boolean isScheduled = req.getIsScheduledType();//no need to check the IsScheduledType. If IsAutoScheduled is true then we assumed IsScheduledType is true.
        Boolean isAutoSch = req.getIsAutoSetSchedule();

        if (isAutoSch)
        {
            //For Super-Dispositiond if IsScheduledType and IsAutoSetSchedule is true then follwing fields are mandatory.
            CallbackType clBkTy = req.getCallbackType();
            if (clBkTy == null)
            {
                ev = new EventRequestValidationFailed(req, "CallbackType", EventFailedCause.NonEditableConstraintViolation);
                return ev;
            }

            if (clBkTy.equals(CallbackType.Agent))
            {
                ev = new EventRequestValidationFailed(req, "CallbackType", EventFailedCause.ValueOutOfRange);
                return ev;
            }

            Long schdAftr = req.getScheduleAfter();
            if (schdAftr == null)
            {
                ev = new EventRequestValidationFailed(req, "ScheduleAfter", EventFailedCause.NonOptionalConstraintViolation);
                return ev;
            }

            if (schdAftr < Limits.ContactScheduledOnAfterNow || schdAftr > Limits.ContactScheduledOnAfter_MAX)
            {
                ev = new EventRequestValidationFailed(req, "ScheduleAfter", EventFailedCause.DataBoundaryLimitViolation);
                return ev;
            }

        }
        return ev;
    }

    /**
     * This method will validate the request parameters of sub-dispositions. <br>
     * - Sub-disposition channels must be the subset of super disposition.
     *
     * @param req
     * @param superdisp
     * @return Fail event if found any request parameter violation else null.
     * @throws CODEException
     */
    private Event validateSubDispReqParams(RequestDispositionAdd req, Disposition superdisp) throws CODEException
    {
        if (!superdisp.getChannels().containsAll(req.getChannels()))
        {
            Event ev = new EventRequestValidationFailed(req, "Channels", EventFailedCause.ValueOutOfRange);
            return ev;
        }
        return null;
    }

    private Disposition0 buildSuper(RequestDispositionAdd req, AOPs camp) throws GravityException, CODEException, Exception
    {
        Disposition0 disp = new Disposition0();
        if (req.getDefault().equals(Boolean.TRUE))
        {
            checkDefault(req);
        }

        if (req.getDefault().equals(Boolean.TRUE))
        {
            checkDefault(req);
        }

        //-------- Common attributes for both CM and NC Dispositions -----------------
        disp.setCode(req.getCode());
        disp.setName(req.getName());
        disp.setChannels(req.getChannels());
        disp.setCategory(req.getCategory());
        disp.setDispSeq(req.getDispSeq() == null ? Limits.Max_NoOf_Disposition : req.getDispSeq());

        //If IsAutoSetSchedule is true then no need to check IsScheduledType.
        Boolean isSchType = req.getIsScheduledType() == true;
        disp.setIsScheduledType(isSchType);
        disp.setIsAutoSetSchedule(req.getIsAutoSetSchedule());

        disp.setCallbackType(req.getCallbackType());
        disp.setScheduleAfter(req.getScheduleAfter());
        disp.setIsDefault(req.getDefault());

        //-----------------------------------------------------------------------------
        if (camp == null)
        {
            //If campaign is null then return as it is a global disposition.
            return disp;
        }

//        disp.setCampaign(camp);
        disp.setAOPs(camp);
        return disp;
    }

    private Disposition1 buildSub(RequestDispositionAdd req, Disposition superdisp) throws GravityException, CODEException
    {
//        if(req.getDefault().equals(Boolean.TRUE)){
//            checkDefault(req);
//        }

        if (findLevel(superdisp) >= Limits.Disposition_Sub_Level)
        {
            throw new CODEException("Can't add sub-disposition beyond " + Limits.Disposition_Sub_Level + " level.");
        }
        //due to persitancebag Object
        List<Channel> channelsOfSup = new ArrayList<>(superdisp.getChannels());

        //No need to validate attributes (which we may receive) for super, as for those BuildEntity method will throw approaporate exception.
        Disposition1 disp = new Disposition1();
        disp.setCode(req.getCode());
        disp.setName(req.getName());
        disp.setSuper(superdisp);
        disp.setAOPs(superdisp.getAOPs());
        disp.setCategory(superdisp.getCategory());
        disp.setChannels(channelsOfSup);
        disp.setDispSeq(req.getDispSeq() == null ? Limits.Max_NoOf_Disposition : req.getDispSeq());

        return disp;
    }

    /**
     * Returns True, only when the requested channels are part of campaign.
     */
    private Boolean areChannelsValid(AOPs campaign, List<Channel> reqchans) throws CODEException, GravityException, CODEException
    {
        JPAQuery dbq = new JPAQuery("Select cm.Channel from AOPsMedia cm "
                + " Where cm.AOPs.Id =:campid"
        );
        dbq.setParam("campid", campaign.getId());
//        dbq.setParam("es", false);

        List<Channel> cmChns = _tctx.getDB().SelectList(dbq);
        return cmChns == null ? false : cmChns.containsAll(reqchans);
    }

    private Integer findLevel(Disposition disp)
    {
        int c = 1;
        while (disp instanceof Disposition1)
        {
            disp = ((Disposition1) disp).getSuper();
            c++;
        }

        return c;
    }

    private void checkDefault(RequestDispositionAdd req) throws CODEException, GravityException
    {
        JPAQuery query = new JPAQuery("SELECT d FROM Disposition0 d WHERE d.Category = :category AND d.IsDefault = true AND d.AOPs.Id=:id");
        query.setParam("category", req.getCategory());
        query.setParam("id", req.getAOPs());
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
