package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.XAICategory;
import ois.radius.ca.enums.XAIPlatformID;
import ois.radius.ca.enums.XAIPlatformSID;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class AOPsAIPropertiesQuery extends EntityQuery{
    public AOPsAIPropertiesQuery() {
        super(EN.AOPsAIProperties);
    }

    public AOPsAIPropertiesQuery filterByAOPs(Long id)
    {
        AppendWhere("And AOPsAIProperties.AOPs.Id =: id");
        _params.put("id", id);
        return this;
    }
    public AOPsAIPropertiesQuery filterByXAICategory(XAICategory category)
    {
        AppendWhere("And AOPsAIProperties.XAICategory =: category");
        _params.put("category", category);
        return this;
    }
    public AOPsAIPropertiesQuery filterByXAIPlatformID(XAIPlatformID xAIPlatformID)
    {
        AppendWhere("And AOPsAIProperties.XAIPlatformID =: xAIPlatformID");
        _params.put("xAIPlatformID", xAIPlatformID);
        return this;
    }

    public AOPsAIPropertiesQuery filterByXAIPlatformSID(ArrayList<XAIPlatformSID> xAIPlatformsID)
    {
        AppendWhere("And AOPAIProperties.XAIPlatformSID =: xAIPlatformsID");
        _params.put("xAIPlatformsID", xAIPlatformsID);
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
                case "byaopsxaicategory":
                    filterByXAICategory(XAICategory.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopsxaiplatformid":
                    filterByXAIPlatformID(XAIPlatformID.valueOf(filters.get(name).get(0)));
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
