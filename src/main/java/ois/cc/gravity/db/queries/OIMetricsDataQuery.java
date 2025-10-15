package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class OIMetricsDataQuery extends EntityQuery {

    public OIMetricsDataQuery() {
        super(EN.OIMetricsData);
    }


    public OIMetricsDataQuery filterByEntityId(String EntityID) {
        AppendWhere("And OIMetricsData.EntityID=:EntityID");
        _params.put("EntityID", EntityID);
        return this;
    }

    public OIMetricsDataQuery filterByMetrics(long id) {
        AppendWhere("And OIMetricsData.Metrics.Id=:id");
        _params.put("id", id);
        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet()) {
            String fltrKey = name.toLowerCase();

            switch (fltrKey) {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byoimetrics":
                    filterByMetrics(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byentityid":
                    filterByEntityId(filters.get(name).get(0));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException {

        for (HashMap<String, Boolean> hm : orderby) {
            for (String name : hm.keySet()) {
                switch (name.toLowerCase()) {
                    case "id":
                        orderById(hm.get(name));
                        break;
                    case "code":
                        orderByCode(hm.get(name));
                        break;

                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private OIMetricsDataQuery orderByCode(Boolean get) {
        setOrederBy("Code", get);
        return this;
    }
}
