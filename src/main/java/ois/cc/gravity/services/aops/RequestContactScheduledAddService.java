//package ois.radius.core.gravity.services.aops;
//
//import CrsCde.CODE.Common.Enums.OPRelational;
//import code.ua.events.Event;
//import code.ua.events.EventOK;
//import code.ua.events.EventRequestValidationFailed;
//import code.ua.requests.Request;
//import jakarta.persistence.EntityNotFoundException;
//import ois.radius.ca.enums.aops.CallbackType;
//import ois.radius.cc.entities.tenant.cc.Campaign;
//import ois.radius.cc.entities.tenant.cc.ContactScheduled;
//import ois.radius.cc.entities.tenant.cc.Skill;
//import ois.radius.core.gravity.context.TenantContext;
//import ois.radius.core.gravity.db.MySQLDB;
//import ois.radius.core.gravity.db.queries.ContactQuery;
//import ois.radius.core.gravity.db.queries.SkillQuery;
//import ois.radius.core.gravity.entities.util.UtilContactSH;
//import ois.radius.core.gravity.framework.events.common.EvCauseRequestValidationFail;
//import ois.radius.core.gravity.framework.requests.aops.RequestContactScheduledAdd;
//import ois.radius.core.gravity.services.ARequestEntityService;
//import ois.radius.core.gravity.services.exceptions.GravityEntityNotFoundException;
//
//import java.util.Date;
//
//public class RequestContactScheduledAddService extends RequestContactScheduledAbaseService
//{
//    public RequestContactScheduledAddService(TenantContext tctx)
//    {
//        super(tctx);
//    }
//
//    @Override
//    protected Event DoProcessContactScheduled(Request request) throws Throwable
//    {
//        RequestContactScheduledAdd req = (RequestContactScheduledAdd) request;
//        MySQLDB db = _tctx.getDB();
//
//        Campaign camp = db.FindAssert(Campaign.class, req.getCampaignCode());
//
//        ContactQuery ctq = new ContactQuery(camp);
//        ctq.filterById(req.getContactId());
//        Contact contact = db.FindAssert(ctq);
//
//        //validate contact state
//        assertContactState(contact);
//
//        ContactAddress conAddr = contact.getContactAddresses().stream()
//                .filter(ca -> ca.getId().equals(req.getContactAddressId())).findFirst().orElse(null);
//        if (conAddr == null)
//        {
//            GravityEntityNotFoundException ex = new GravityEntityNotFoundException(EN.ContactAddress.name());
//            ex.setCondition("ContactAddress", OPRelational.Eq, req.getContactAddressId());
//            throw ex;
//        }
//
//        /**
//         * Admin can't add a ContactScheduled of Personal callback type.
//         */
//        if (req.getCallbackType().equals(CallbackType.Personal))
//        {
//            EventRequestValidationFailed ev = new EventRequestValidationFailed(req, "CallbackType", EvCauseRequestValidationFail.ParamValueOutOfRange);
//            return ev;
//        }
//
//        //TBD: take care of timezone here.
//        Date schdOn = req.getScheduledOn();
//        Agent agent = null;
//        Skill skill = null;
//
//        switch (req.getCallbackType())
//        {
//            case Agent:
//                if (req.getAgentId() == null)
//                {
//                    Event ev = new EventRequestValidationFailed(request, "Agent", EvCauseRequestValidationFail.NonOptionalConstraintViolation);
//                    return ev;
//                }
//                agent = db.FindAssert(EN.Agent.getEntityClass(), req.getAgentId());
//                break;
//            case Skill:
//                if (req.getSkillId() == null)
//                {
//                    Event ev = new EventRequestValidationFailed(request, "Skill", EvCauseRequestValidationFail.NonOptionalConstraintViolation);
//                    return ev;
//                }
//                SkillQuery skqry = new SkillQuery();
//                skqry.filterByAops(camp.getId())
//                        .filterById(req.getSkillId());
//                skill = db.FindAssert(skqry);
//                break;
//        }
//
//        ContactScheduled consch = UtilContactSH.AssertBuildContactSH(_cctx, _thisAdmin, conAddr, schdOn, req.getCallbackType(), agent, skill, req.getComments());
//        UtilContactSH.UpsertContactSH(_cctx, _thisAdmin, consch);
//
//        Event ev = new EventOK(req);
//        return ev;
//
//    }
//
//}
