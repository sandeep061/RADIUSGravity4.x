 package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.MetricsKey;

import java.util.ArrayList;
import java.util.HashMap;

public class OIMetricsQuery extends EntityQuery{
    public OIMetricsQuery() {
        super(EN.OIMetrics);
    }

    public OIMetricsQuery filterByCode(String code)
    {
        AppendWhere("And OIMetrics.Code=:code");
        _params.put("code", code);
        return this;
    }

    public OIMetricsQuery filterByEntityId(String id)
    {
        AppendWhere("And OIMetrics.EntityID=:id");
        _params.put("id", id);
        return this;
    }
    public OIMetricsQuery filterByDimension(EN en)
    {
        AppendWhere("And OIMetrics.Dimension=:en");
        _params.put("en", en);
        return this;
    }

    public OIMetricsQuery filterByMetricsKey(MetricsKey key)
    {
        AppendWhere("And OIMetrics.MetricsKey=:key");
        _params.put("key", key);
        return this;
    }

    public OIMetricsQuery filterByStreamId(String StreamID)
    {
        AppendWhere("And OIMetrics.StreamID=:StreamID");
        _params.put("StreamID", StreamID);
        return this;
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
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "bystreamid":
                    filterByStreamId(filters.get(name).get(0));
                    break;
                case "byentityid":
                    filterByEntityId(filters.get(name).get(0));
                    break;
                case "bydimension":
                    filterByDimension(EN.valueOf(filters.get(name).get(0)));
                    break;
                case "bymetricskey":
                    filterByMetricsKey(MetricsKey.valueOf(filters.get(name).get(0)));
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
                    case "code":
                        orderByCode(hm.get(name));
                        break;

                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }
    private OIMetricsQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }
}
