package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.BFCode;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.XPlatformID;
import ois.radius.ca.enums.XPlatformSID;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class AOPsBFQuery extends EntityQuery{
    public AOPsBFQuery() {
        super(EN.AOPsBF);
    }


    public AOPsBFQuery filterByAOPs(Long id)
    {
        AppendWhere("And AOPsBF.AOPs.Id =: id");
        _params.put("id", id);
        return this;
    }
    public AOPsBFQuery filterByAOPsCode(String code)
    {
        AppendWhere("And AOPsBF.AOPs.Code =: code");
        _params.put("code", code);
        return this;
    }
    public AOPsBFQuery filterByBFCode(BFCode code)
    {
        AppendWhere("And AOPsBF.BFCode =: code");
        _params.put("code", code);
        return this;
    }
    public AOPsBFQuery filterByIsEnable(Boolean isEnable)
    {
        AppendWhere("And AOPsBF.IsEnable =: isEnable");
        _params.put("isEnable", isEnable);
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
                case "bybfcode":
                    filterByBFCode(BFCode.valueOf(filters.get(name).get(0)));
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
