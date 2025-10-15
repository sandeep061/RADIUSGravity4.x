package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import java.util.ArrayList;
import java.util.HashMap;

public class TerminalConfigQuery  extends EntityQuery
{

    public TerminalConfigQuery()
    {
        super(EN.TerminalConfig);
    }

    public TerminalConfigQuery filterByKey(String name)
    {
        AppendWhere("And TerminalConfig.ConfKey =: name ");
        _params.put("name", name);
        return this;
    }

    public TerminalConfigQuery filterByCode(String code)
    {
        AppendWhere("And TerminalConfig.Code =: code ");
        _params.put("code", code);
        return this;
    }

    public TerminalConfigQuery filterByKeyLike(String name)
    {
        AppendWhere("And Lower(TerminalConfig.ConfKey) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public TerminalConfigQuery filterByQueue(Long qid)
    {
        AppendWhere("And TerminalConfig.Queue.Id =: qid");
        _params.put("qid", qid);

        return this;
    }

    public TerminalConfigQuery filterByChannel(Channel channel)
    {
        AppendWhere("And TerminalConfig.Channel =: chn");
        _params.put("chn", channel);

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
                    filterByKey(filters.get(name).get(0));
                    break;
                case "bykeylike":
                    filterByKeyLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byqueue":
                    filterByQueue(Long.valueOf(filters.get(name).get(0)));
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
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }
}



