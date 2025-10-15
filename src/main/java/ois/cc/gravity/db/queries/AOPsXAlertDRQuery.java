package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class AOPsXAlertDRQuery extends EntityQuery{
    public AOPsXAlertDRQuery() {
        super(EN.AOPsXAlertDR);
    }

    public AOPsXAlertDRQuery filterByAOPs(Long campid)
    {
        AppendWhere("And AOPsXAlertDR.AOPs.Id=:campid");
        _params.put("campid", campid);

        return this;
    }

    public AOPsXAlertDRQuery filterByUXID(String id)
    {
        AppendWhere("And AOPsXAlertDR.UXID=:uxid");
        _params.put("uxid", id);

        return this;
    }

    public AOPsXAlertDRQuery filterByUSUID(String id)
    {
        AppendWhere("And AOPsXAlertDR.USUID=:usuid");
        _params.put("usuid", id);

        return this;
    }

    public AOPsXAlertDRQuery filterByUAltID(String id)
    {
        AppendWhere("And AOPsXAlertDR.UAltID=:ultid");
        _params.put("ultid", id);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byusuid":
                    filterByUSUID(filters.get(name).get(0));
                    break;
                case "byuxid":
                    filterByUXID(filters.get(name).get(0));
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
