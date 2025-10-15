package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class AOPsMediaTrunkQuery extends EntityQuery{

    public AOPsMediaTrunkQuery() {
        super(EN.AOPsMediaTrunk);
    }


    public AOPsMediaTrunkQuery filterByAOPs(Long aopsid)
    {
        AppendWhere("And AOPsMediaTrunk.AOPs.Id=:aopsid");
        _params.put("aopsid", aopsid);

        return this;
    }

    public AOPsMediaTrunkQuery filterByXServer(Long xid)
    {
        AppendWhere("And AOPsMediaTrunk.XServer.Id=:xid");
        _params.put("xid", xid);

        return this;
    }
    public AOPsMediaTrunkQuery filterByXTrunk(Long xid)
    {
        AppendWhere("And AOPsMediaTrunk.XTrunk.Id=:xid");
        _params.put("xid", xid);
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
                case "byxserver":
                    filterByXServer(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byXTrunk":
                    filterByXTrunk(Long.valueOf(filters.get(name).get(0)));
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
