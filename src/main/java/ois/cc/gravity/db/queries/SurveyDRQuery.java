/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;

import java.util.ArrayList;
import java.util.HashMap;

import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

/**
 * @author Sandeepkumar.Sahoo
 * @since Aug 11, 2025
 */
public class SurveyDRQuery extends EntityQuery {


    public SurveyDRQuery filterBySurvey(Long id) {
        AppendWhere("And SurveyDR.Survey.Id=:id");
        _params.put("id", id);

        return this;
    }
    public SurveyDRQuery filterByUSUID(String id) {
        AppendWhere("And SurveyDR.USUID=:id");
        _params.put("id", id);

        return this;
    }

    public SurveyDRQuery() {
        super(EN.SurveyDR);
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet()) {
            String fltrKey = name.toLowerCase();

            switch (fltrKey) {
                case "bysurvey":
                    filterBySurvey(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byusuid":
                    filterByUSUID(filters.get(name).get(0));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
