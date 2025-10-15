package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class OIRuleQuery extends EntityQuery {


    public OIRuleQuery() {
        super(EN.OIRule);
    }

    public JPAQuery filterByXALerts(Long id) {
        JPAQuery query = new JPAQuery("SELECT o FROM OIRule o JOIN o.OIAlertConfigs x WHERE x.id = :xAlertId");
        query.setParam("xAlertId", id);
        return query;
    }

    public OIRuleQuery filterByName(String name) {
        AppendWhere("And OIRule.Name=:Name");
        _params.put("Name", name);
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
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException {

    }
}
