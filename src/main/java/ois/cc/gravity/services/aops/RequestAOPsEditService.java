package ois.cc.gravity.services.aops;

import code.ua.events.*;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.entities.util.AOPsUtil;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.services.exceptions.GravityUnhandledRealMException;
import ois.cc.gravity.services.exceptions.GravityUniqueConstraintViolationException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.radius.cc.entities.EN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import code.ua.requests.Request;

public class RequestAOPsEditService extends RequestEntityEditService
{

    private static Logger logger = LoggerFactory.getLogger(RequestAOPsEditService.class);

    public RequestAOPsEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        RequestEntityEdit req = (RequestEntityEdit) request;

        MySQLDB db = _tctx.getDB();

        EN en = req.getEntityName();
        AOPs aops = _tctx.getDB().FindAssert(en.getEntityClass(), req.getEntityId());

        /**
         * Campaign can be edit only when the campaign is in Unload state.
         */
        AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aops);
//        /**
//         * refer v:290820 in dev.wiki.server doc.
//         */
//        //Campaign type can modify from IB to Blended only.
//        if (req.getAttributes().containsKey("CampaignType"))
//        {
//            CampaignType reqCampType = CampaignType.valueOf(req.getAttribute("CampaignType").toString());
//            CampaignType foundCampType = camp.getCampaignType();
//            if (!foundCampType.equals(CampaignType.Inbound))
//            {
//                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.CampaignIllegalType,"[Campaign.Code , CampaignType =="+camp.getCode()+","+foundCampType+"]");
////                return new EventCampaignIllegalType(req, camp.getId(), camp.getCampaignType(), CampaignType.Inbound);
//            }
//
//            if (!reqCampType.equals(CampaignType.Blended))
//            {
//                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.CampaignIllegalType,"Required Blended Campaign but Found "+reqCampType);
//            }
//
//            camp.setCampaignType(reqCampType);
//        }
        EntityBuilder.BuildEntity(db, aops, req.getAttributes());

        try
        {
            _tctx.getALMCtx().EditCampaign(aops);
            db.Update(_uac.getUserSession().getUser(), aops);
        }
        catch (GravityUnhandledException gex)
        {
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AOPsAlreadyExistInALM, "[AOPs.Name==" + req.getAttributeValueOf(String.class, "Name") + "]");
            throw new GravityUnhandledRealMException(gex.getMessage());
        }
        catch (Throwable e)
        {

            if (e instanceof GravityUniqueConstraintViolationException ex)
            {
                db.Find(en.getEntityClass(), req.getEntityId());
                throw new GravityUniqueConstraintViolationException(e.getCause(), ex.getEntityName(), ex.getCondition());
            }
            throw e;

        }
        EventEntityEdited ev = new EventEntityEdited(req, aops);
        return ev;
    }

}
