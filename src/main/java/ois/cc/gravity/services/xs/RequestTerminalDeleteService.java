package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AgentMediaMap;
import ois.radius.cc.entities.tenant.cc.Terminal;
import ois.radius.cc.entities.tenant.cc.UserMedia;
import ois.radius.cc.entities.tenant.cc.UserMediaSession;

import java.util.ArrayList;
import java.util.List;

public class RequestTerminalDeleteService extends ARequestEntityService {

    public RequestTerminalDeleteService(UAClient uac) {
        super(uac);
    }

    private ArrayList<NameValuePair> entities = new ArrayList<>();

    @Override
    protected final Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityDelete req = (RequestEntityDelete) request;

        Terminal term = _tctx.getDB().FindAssert(EN.Terminal.getEntityClass(), req.getEntityId());
        UserMediaSession userMediaSession = getUserMediaSession(term);
        if (userMediaSession != null) {
            if (userMediaSession.getEndAt() == null) {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TerminalIsInUse, "Terminal " + term.getAddress());
            }
        }

        List<AgentMediaMap> agentMediaMaps = getAgentMediaMap(term);
        for (AgentMediaMap agmap : agentMediaMaps)
        {
            //set terminal to null
            agmap.setTerminal(null);
            // set auto register of UserMedia as false and terminal unmapped.
            UserMedia userMedia = getUserMedia(agmap);
            if (userMedia != null)
            {
                userMedia.setAutoRegister(Boolean.FALSE);
                entities.add(new NameValuePair<>(ENActionList.Action.Update.name(), userMedia));
            }
            entities.add(new NameValuePair<>(ENActionList.Action.Update.name(), agmap));
        }

        //TBD:
        // sendTerminalDeleteToProvider(term);

        entities.add(new NameValuePair(ENActionList.Action.Delete.name(), term));

        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);
        return new EventSuccess(req);
    }

    //    private void sendTerminalDeleteToProvider(Terminal term) throws RADXSPIException, RADStoreEntityNotFoundException
//    {
//
//        AIXServer aixs = _cctx.getXServerStore().GetByIdAssert(term.getXServer().getId());
//
//        XProviderStub providerStub = aixs.getProviderStub();
//        if (providerStub != null)
//        {
//            providerStub.DeleteTerminal(term.getAddress());
//        }
//    }
    private UserMediaSession getUserMediaSession(Terminal ter) throws CODEException, GravityException {
        JPAQuery query = new JPAQuery("SELECT u FROM UserMediaSession u WHERE u.XServerId = :xsid AND u.Address = :address ORDER BY u.Id DESC");
        query.setParam("xsid", ter.getXServer().getId());
        query.setParam("address", ter.getAddress());
        return _tctx.getDB().Find(UserMediaSession.class, query);
    }

    private List<AgentMediaMap> getAgentMediaMap(Terminal ter) throws CODEException, GravityException {
        JPAQuery query = new JPAQuery("SELECT am FROM AgentMediaMap am WHERE am.Terminal.Id=:tid");
        query.setParam("tid", ter.getId());
        return _tctx.getDB().Select(AgentMediaMap.class, query);
    }

    private UserMedia getUserMedia(AgentMediaMap amm) throws CODEException, GravityException {
        JPAQuery query = new JPAQuery("SELECT um FROM UserMedia um WHERE um.AutoRegAgentMedia.Id=:aid");
        query.setParam("aid", amm.getId());
        UserMedia um = _tctx.getDB().Find(UserMedia.class, query);
        return um;
    }

}
