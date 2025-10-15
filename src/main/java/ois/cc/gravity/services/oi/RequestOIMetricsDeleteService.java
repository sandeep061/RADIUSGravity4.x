package ois.cc.gravity.services.oi;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.OIMetricsDataQuery;
import ois.cc.gravity.db.queries.OIMetricsMapQuery;
import ois.cc.gravity.db.queries.OIMetricsQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.oi.OIMetrics;
import ois.radius.cc.entities.tenant.oi.OIMetricsData;
import ois.radius.cc.entities.tenant.oi.OIMetricsMap;

import java.util.ArrayList;
import java.util.List;

public class RequestOIMetricsDeleteService extends ARequestEntityService {
    public RequestOIMetricsDeleteService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityDelete req= (RequestEntityDelete) request;

        ArrayList<AEntity>entity=new ArrayList<>();

        OIMetrics mectric = _tctx.getDB().FindAssert(EN.OIMetrics.getEntityClass(), req.getEntityId());
         entity.addAll(getAllMetricsMap(mectric.getId()));
        entity.addAll(getAllMetricsData(mectric.getId()));
         entity.addAll(getAllOirules(mectric.getId()));
          entity.add(mectric);
        //delete All
         _tctx.getDB().DeleteEntities(_uac.getUserSession().getUser(),entity);

        EventEntityDeleted ev=new EventEntityDeleted(req,mectric);
        return ev;
    }
    private List<OIMetricsData> getAllMetricsData(long id) throws CODEException, GravityException {
        ArrayList<OIMetricsData> metricslists = _tctx.getDB().Select(new OIMetricsDataQuery().filterByMetrics(id));
         return metricslists;
    }

    private List<OIMetricsMap> getAllMetricsMap(long id) throws CODEException, GravityException {
        ArrayList<OIMetricsMap> metricsmaplists = _tctx.getDB().Select(new OIMetricsMapQuery().filterByOIMetrics(id));
        return metricsmaplists;
    }

    private List<OIMetricsMap> getAllOirules(long id) throws CODEException, GravityException {
        JPAQuery query=new JPAQuery("SELECT r FROM OIRule r JOIN r.OIMetrics m WHERE m.id = :metricId");
          query.setParam("metricId",id);
        ArrayList<OIMetricsMap> metricsmaplists = (ArrayList<OIMetricsMap>) _tctx.getDB().Select(query);
        return metricsmaplists;
    }
}
