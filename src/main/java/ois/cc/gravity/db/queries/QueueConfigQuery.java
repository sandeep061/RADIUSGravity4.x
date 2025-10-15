package ois.cc.gravity.db.queries;

import code.ua.events.EventFailedCause;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import java.util.ArrayList;
import java.util.HashMap;

public class QueueConfigQuery extends EntityQuery
{

    public QueueConfigQuery()
    {
        super(EN.QueueConfig);
    }

    public QueueConfigQuery filterByKey(String name)
    {
        AppendWhere("And QueueConfig.ConfKey =: name ");
        _params.put("name", name);
        return this;
    }

    public QueueConfigQuery filterByCode(String code)
    {
        AppendWhere("And QueueConfig.Code =: code ");
        _params.put("code", code);
        return this;
    }

    public QueueConfigQuery filterByKeyLike(String name)
    {
        AppendWhere("And Lower(QueueConfig.ConfKey) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public QueueConfigQuery filterByQueue(Long qid)
    {
        AppendWhere("And QueueConfig.Queue.Id =: qid");
        _params.put("qid", qid);

        return this;
    }

    public QueueConfigQuery filterByQueueAddress(String qaddr)
    {
        AppendWhere("And QueueConfig.Queue.Address =: qid");
        _params.put("qid", qaddr);

        return this;
    }

    public QueueConfigQuery filterByChannel(Channel channel)
    {
        AppendWhere("And QueueConfig.Channel =: chn");
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
                case "byConfKey":
                    filterByKey(filters.get(name).get(0));
                    break;
                case "bykeylike":
                    filterByKeyLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byqueue":
                    filterByQueue(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byqueueaddress":
                    filterByQueueAddress(filters.get(name).get(0));
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
