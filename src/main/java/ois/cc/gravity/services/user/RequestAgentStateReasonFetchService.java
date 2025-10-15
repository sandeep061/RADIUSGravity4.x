//package ois.cc.gravity.services.user;
//
//import code.ua.events.Event;
//import code.ua.requests.Request;
//import ois.cc.gravity.services.ARequestEntityService;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.tenant.cc.AgentStateReason;
//import ois.cc.gravity.db.queries.AgentStateReasonQuery;
//import ois.cc.gravity.framework.events.common.EventEntitiesFetched;
//import ois.cc.gravity.framework.requests.user.RequestAgentStateReasonFetch;
//
//import java.util.ArrayList;
//
//public class RequestAgentStateReasonFetchService extends ARequestEntityService
//{
//
//    public RequestAgentStateReasonFetchService(UAClient uac)
//    {
//        super(uac);
//    }
//
//    @Override
//    protected Event DoProcessEntityRequest(Request request) throws Throwable
//    {
//        RequestAgentStateReasonFetch req = (RequestAgentStateReasonFetch) request;
//
//        AgentStateReasonQuery asrq = new AgentStateReasonQuery();
//
//        if (req.getAgentState() != null)
//        {
//            asrq.filterByAgentState(req.getAgentState());
//        }
//
//        ArrayList<AgentStateReason> alasr = _tctx.getDB().Select(asrq);
//
//        EventEntitiesFetched ev = new EventEntitiesFetched(req, new ArrayList<>(alasr));
//        return ev;
//    }
//}
//
