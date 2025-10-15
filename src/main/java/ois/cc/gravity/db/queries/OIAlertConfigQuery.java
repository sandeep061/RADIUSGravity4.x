package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class OIAlertConfigQuery extends EntityQuery {
    public OIAlertConfigQuery() {
        super(EN.OIAlertConfig);
    }

    public OIAlertConfigQuery filterByXALerts(Long id) {
        AppendWhere("And :xalertId in [OIAlertConfig.XAlertIDs]");
        _params.put("xalertId", id);
        return this;
    }

    public JPAQuery filterByXALertsJPA(Long id) {
        JPAQuery query=new JPAQuery("SELECT o FROM OIAlertConfig o JOIN o.XAlertIDs x WHERE x.id = :xAlertId");
        query.setParam("xAlertId",id);
        return query;
    }

    public OIAlertConfigQuery filterByUser(String id) {
        AppendWhere("And OIAlertConfig.User.UserId=:id");
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
                case "byxalertid":
                    filterByXALerts(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byuser":
                    filterByUser(filters.get(name).get(0));
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
