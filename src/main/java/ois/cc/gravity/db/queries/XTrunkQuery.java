package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.TrunkTech;
import ois.radius.cc.entities.EN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class XTrunkQuery extends EntityQuery
{

    private final Logger _logger = LoggerFactory.getLogger(getClass());

    public XTrunkQuery()
    {
        super(EN.XTrunk);
    }

    public XTrunkQuery filterByCode(String code)
    {
        AppendWhere("And XTrunk.Code=:code");
        _params.put("code", code);

        return this;
    }

    public XTrunkQuery filterByName(String name)
    {
        AppendWhere("And XTrunk.Name=:name");
        _params.put("name", name);

        return this;
    }

    public XTrunkQuery filterByXServer(Long id)
    {
        AppendWhere("And XTrunk.XServer.Id=:id");
        _params.put("id", id);

        return this;
    }

    public XTrunkQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(XTrunk.Name) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public XTrunkQuery filterByTrunkTech(TrunkTech tech)
    {
        AppendWhere("And XTrunk.TrunkTech=:tech");
        _params.put("tech", tech);

        return this;
    }

    private XTrunkQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private XTrunkQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {

        for (String name : filters.keySet())
        {
            String fltrKey = name.toLowerCase();
            _logger.info("filter Name :" + fltrKey);
            switch (fltrKey)
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
                case "bytrunktech":
                    filterByTrunkTech(TrunkTech.valueOf(filters.get(name).get(0)));
                    break;
                case "byxserver":
                    filterByXServer(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException
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
}
