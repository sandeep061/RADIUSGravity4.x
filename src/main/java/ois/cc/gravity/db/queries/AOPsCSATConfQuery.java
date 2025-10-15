package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.xsess.XSessType;
import ois.radius.cc.entities.EN;

import java.util.ArrayList;
import java.util.HashMap;

public class AOPsCSATConfQuery extends EntityQuery{
    public AOPsCSATConfQuery() {
        super(EN.AOPsCSATConf);
    }

    public AOPsCSATConfQuery filterByAOPs(Long aopid)
    {
        AppendWhere("And AOPsCSATConf.AOPs.Id =: aopid");
        _params.put("aopid", aopid);
        return this;
    }
    public AOPsCSATConfQuery filterByAOPsCode(String code)
    {
        AppendWhere("And AOPsCSATConf.AOPs.Code =: code");
        _params.put("code", code);
        return this;
    }

    public AOPsCSATConfQuery filterBySurvey(Long sid)
    {
        AppendWhere("And AOPsCSATConf.Survey.Id =: sid");
        _params.put("sid", sid);
        return this;
    }

    public AOPsCSATConfQuery filterByChannel(Channel ch)
    {
        AppendWhere("And AOPsCSATConf.Channel =: ch");
        _params.put("ch", ch);
        return this;
    }

    public AOPsCSATConfQuery filterByXSessType(XSessType xstype)
    {
        AppendWhere("And AOPsCSATConf.XSessType =: xstype");
        _params.put("xstype", xstype);
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
                case "bychannel":
                    filterByChannel(Channel.valueOf(filters.get(name).get(0)));
                    break;
                case "byxsesstype":
                    filterByXSessType(XSessType.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAOPsCode(filters.get(name).get(0));
                    break;
                case "bysurvey":
                    filterBySurvey(Long.valueOf(filters.get(name).get(0)));
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
