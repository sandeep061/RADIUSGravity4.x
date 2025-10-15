package ois.cc.gravity.services.oi;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.oi.OIAlertConfig;
import ois.radius.cc.entities.tenant.oi.OIRule;

import java.util.ArrayList;
import java.util.List;

public class RequestOIAlertConfigDeleteService extends ARequestEntityService {
    public RequestOIAlertConfigDeleteService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityDelete req = (RequestEntityDelete) request;
        ArrayList<AEntity> entites = new ArrayList<>();
        //find alertconfig
        OIAlertConfig config = _tctx.getDB().FindAssert(EN.OIAlertConfig.getEntityClass(), req.getEntityId());
        ArrayList<OIRule> oiRule = getOIRule(config.getId());

        entites.addAll(oiRule);
        entites.add(config);

        _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(),entites);

        EventEntityDeleted ev=new EventEntityDeleted(request,config);


        return ev;
    }

    private ArrayList<OIRule> getOIRule(long id) throws CODEException, GravityException {
        JPAQuery query = new JPAQuery("SELECT r FROM OIRule r JOIN r.OIAlertConfigs ac WHERE ac.id = :alertConfigId");
        query.setParam("alertConfigId", id);
        ArrayList<OIRule> rules = (ArrayList<OIRule>) _tctx.getDB().Select(query);
        return rules;

    }
}
