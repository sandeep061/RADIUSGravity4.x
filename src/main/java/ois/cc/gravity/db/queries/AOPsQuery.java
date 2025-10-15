/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.db.queries;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import ois.radius.ca.enums.aops.AOPsType;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AOPsQuery extends EntityQuery
{
    private final Logger _logger = LoggerFactory.getLogger(getClass());

    public AOPsQuery()
    {
        super(EN.AOPs);
    }

    public AOPsQuery filterByCode(String code)
    {
        AppendWhere("And AOPs.Code=:code");
        _params.put("code", code);

        return this;
    }

    public AOPsQuery filterByName(String name)
    {
        AppendWhere("And AOPs.Name=:name");
        _params.put("name", name);

        return this;
    }

    public AOPsQuery filterByNameLike(String name)
    {
        AppendWhere("And Lower(AOPs.Name) Like : name ");
        _params.put("name", "%" + name + "%");
        return this;
    }

    public AOPsQuery filterByCampaignType(AOPsType... type)
    {
        AppendWhere("And AOPs.AOPSType in (:types)");
        _params.put("types", List.of(type));

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException
    {
        for (String name : filters.keySet())
        {
            String fltrKey = name.toLowerCase();
            _logger.info("filter Name :"+fltrKey);
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
                case "bynamelike":
                    filterByNameLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "byaopstype":
                    List<AOPsType> campTypes = filters.get(name).stream()
                            .map((c) -> AOPsType.valueOf(c)).collect(Collectors.toList());
                    filterByCampaignType(campTypes.toArray(new AOPsType[campTypes.size()]));
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
                    case "aopstype":
                        orderByCampaignType(hm.get(name));
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

    private AOPsQuery orderByCode(Boolean get)
    {
        setOrederBy("Code", get);
        return this;
    }

    private AOPsQuery orderByName(Boolean get)
    {
        setOrederBy("Name", get);
        return this;
    }

    private AOPsQuery orderByCampaignType(Boolean get)
    {
        setOrederBy("CampaignType", get);
        return this;
    }
}
