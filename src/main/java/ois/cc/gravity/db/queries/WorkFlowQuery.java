package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.EN;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkFlowQuery extends EntityQuery
{

    public WorkFlowQuery()
    {
        super(EN.WorkFlow);
    }

    public WorkFlowQuery filterByCode(String code)
    {
        AppendWhere("And WorkFlow.Code=:code");
        _params.put("code", code);

        return this;
    }

    public WorkFlowQuery filterByName(String name)
    {
        AppendWhere("And WorkFlow.Name=:name");
        _params.put("name", name);

        return this;
    }

    public WorkFlowQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(WorkFlow.Name) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public WorkFlowQuery filterByAops(Long id)
    {
        AppendWhere("And WorkFlow.AOPs.Id=:id");
        _params.put("id", id);
        return this;
    }
    public WorkFlowQuery filterByAopsCode(String code)
    {
        AppendWhere("And WorkFlow.AOPs.Code=:code");
        _params.put("code", code);
        return this;
    }

    public WorkFlowQuery filterByChannel(Channel channel)
    {
        AppendWhere("And WorkFlow.Channel =: Channel");
        _params.put("Channel", channel);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bycode":
                    filterByCode(filters.get(name).get(0));
                    break;
                case "byname":
                    filterByName(filters.get(name).get(0));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byaops":
                    filterByAops(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAopsCode(filters.get(name).get(0));
                    break;
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws GravityIllegalArgumentException
    {
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
                    case "name":
                        orderByName(hm.get(name));
                        break;

                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private WorkFlowQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private WorkFlowQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

}
