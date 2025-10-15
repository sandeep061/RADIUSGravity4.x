package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class OIMetricsMapQuery extends EntityQuery{
    public OIMetricsMapQuery() {
        super(EN.OIMetricsMap);
    }


    public OIMetricsMapQuery filterByEntityId(String EntityID)
    {
        AppendWhere("And OIMetricsMap.EntityID=:EntityID");
        _params.put("EntityID", EntityID);
        return this;
    }

    public OIMetricsMapQuery filterByEN(EN Entity)
    {
        AppendWhere("And OIMetricsMap.Entity=:EntityID");
        _params.put("EntityID", Entity);
        return this;
    }

    public OIMetricsMapQuery filterByOIMetrics(Long id)
    {
        AppendWhere("And OIMetricsMap.OIMetrics.Id=:id");
        _params.put("id", id);
        return this;
    }

    public JPAQuery filterByXALerts(Long id) {
        JPAQuery query=new JPAQuery("SELECT o FROM OIMetricsMap o JOIN o.OIAlerts x WHERE x.id = :xAlertId");
        query.setParam("xAlertId",id);
        return query;
    }
    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet())
        {
            String fltrKey = name.toLowerCase();

            switch (fltrKey)
            {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byentityid":
                    filterByEntityId(filters.get(name).get(0));
                    break;
                case "byoimetrics":
                    filterByOIMetrics(Long.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException {

        for (HashMap<String, Boolean> hm : orderby)
        {
            for (String name : hm.keySet())
            {
                switch (name.toLowerCase())
                {
                    case "id":
                        orderById(hm.get(name));
                        break;

                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
