package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class XAlertIDQuery extends EntityQuery {
    public XAlertIDQuery() {
        super(EN.XAlertID);
    }

    public XAlertIDQuery filterByChannel(Channel ch) {
        AppendWhere("And XAlertID.Channel =: ch");
        _params.put("ch", ch);
        return this;
    }

    public XAlertIDQuery filterByXpatform(Long id) {
        AppendWhere("And XAlertID.XPlatform.Id =: id");
        _params.put("id", id);
        return this;
    }

    public XAlertIDQuery filterByXpatformua(long uaid) {
        AppendWhere("And XAlertID.XPlatformUA.Id =: uaid");
        _params.put("uaid", uaid);
        return this;
    }

    public XAlertIDQuery filterByAOPsCSATConf(long csatid) {
        AppendWhere("And XAlertID.AOPsCSATConf.Id =: csatid");
        _params.put("csatid", csatid);
        return this;
    }

    public XAlertIDQuery filterByNonAOPsCSATConf() {
        AppendWhere("And XAlertID.AOPsCSATConf IS NULL");
        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {
        for (String name : filters.keySet()) {
            switch (name.toLowerCase()) {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatform":
                    filterByXpatform(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "byxplatformua":
                    filterByXpatformua(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscsatconf":
                    if (Long.valueOf(filters.get(name).get(0)) == 0L) {
                        filterByNonAOPsCSATConf();
                    } else {
                        filterByAOPsCSATConf(Long.valueOf(filters.get(name).get(0)));
                    }

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
