package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.EndPointType;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class AgentMediaMapQuery extends EntityQuery{
    public AgentMediaMapQuery() {
        super(EN.AgentMediaMap);
    }

    public AgentMediaMapQuery filterByXServer(Long Id)
    {
        AppendWhere("And AgentMediaMap.XServer.Id =: id");
        _params.put("id", Id);

        return this;
    }

    public AgentMediaMapQuery filterByEndPointType(EndPointType endPointType)
    {
        AppendWhere("And AgentMediaMap.EndPointType =: endPointType");
        _params.put("endPointType", endPointType);

        return this;
    }

    public AgentMediaMapQuery filterByTerminal(Long tid)
    {
        AppendWhere("And AgentMediaMap.Terminal.Id =: tid");
        _params.put("tid", tid);

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
                case "byxserver":
                    filterByXServer(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byterminal":
                    filterByTerminal(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byendpointtype":
                    filterByEndPointType(EndPointType.valueOf(filters.get(name).get(0)));
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
