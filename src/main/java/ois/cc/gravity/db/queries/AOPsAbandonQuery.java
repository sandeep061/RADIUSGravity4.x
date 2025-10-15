package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.BFCode;
import ois.radius.ca.enums.xsess.XSessStatus;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class AOPsAbandonQuery extends EntityQuery{

    public AOPsAbandonQuery() {
        super(EN.AOPsAbandon);
    }

    public AOPsAbandonQuery filterByAOPs(Long id)
    {
        AppendWhere("And AOPsAbandon.AOPs.Id =: id");
        _params.put("id", id);
        return this;
    }
    public AOPsAbandonQuery filterByAOPsCode(String code)
    {
        AppendWhere("And AOPsAbandon.AOPs.Code =: code");
        _params.put("code", code);
        return this;
    }
    public AOPsAbandonQuery filterByAbandonType(XSessStatus code)
    {
        AppendWhere("And AOPsAbandon.AbandonType =: code");
        _params.put("code", code);
        return this;
    }
    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAOPsCode(filters.get(name).get(0));
                    break;
                case "byabandontype":
                    filterByAbandonType(XSessStatus.valueOf(filters.get(name).get(0)));
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
