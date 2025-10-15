package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.EN;

import java.util.*;

public class CrossCXContactMapQuery extends EntityQuery {


    public CrossCXContactMapQuery() {
        super(EN.CrossCXContactMap);
    }

    public CrossCXContactMapQuery filterByPriAddress(String pridd) {
        AppendWhere("And CrossCXContactMap.PriAddress=:pridd");
        _params.put("pridd", pridd);

        return this;
    }

    public CrossCXContactMapQuery filterByPriAddressId(String priAddid) {
        AppendWhere("And CrossCXContactMap.PriAddressId=:priAddid");
        _params.put("priAddid", priAddid);

        return this;
    }

    public CrossCXContactMapQuery filterByPriContactId(String priconid) {
        AppendWhere("And CrossCXContactMap.PriContactId=:priconid");
        _params.put("priconid", priconid);

        return this;
    }

    public CrossCXContactMapQuery filterBySecAddress(String secAdd) {
        AppendWhere("And CrossCXContactMap.SecAddress=:secadd");
        _params.put("secadd", secAdd);

        return this;
    }

    public CrossCXContactMapQuery filterBySecAddressId(String secAddid) {
        AppendWhere("And CrossCXContactMap.SecAddressId=:secAddid");
        _params.put("secAddid", secAddid);

        return this;
    }

    public CrossCXContactMapQuery filterBySecContactId(String secconid) {
        AppendWhere("And CrossCXContactMap.SecContactId=:secconid");
        _params.put("secconid", secconid);

        return this;
    }

    public CrossCXContactMapQuery filterByDID(String did) {
        AppendWhere("And CrossCXContactMap.DID=:did");
        _params.put("did", did);

        return this;
    }

    public CrossCXContactMapQuery filterByPIN(String pin) {
        AppendWhere("And CrossCXContactMap.PIN=:pin");
        _params.put("pin", pin);

        return this;
    }
    
       public CrossCXContactMapQuery filterByAOPs(long id) {
        AppendWhere("And CrossCXContactMap.AOPs.Id=:id");
        _params.put("id", id);

        return this;
    }
       
       public CrossCXContactMapQuery filterByAOPsCode(String code) {
        AppendWhere("And CrossCXContactMap.AOPs.Code=:code");
        _params.put("code", code);

        return this;
    }
    public CrossCXContactMapQuery filterByUCXConMapId(String id) {
        AppendWhere("And CrossCXContactMap.UCXConMapId=:id");
        _params.put("id", id);

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable {

        for (String name : filters.keySet()) {
            switch (name.toLowerCase()) {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bypriaddress":
                    filterByPriAddress(filters.get(name).get(0));
                    break;
                case "bypriaddressid":
                    filterByPriAddressId(filters.get(name).get(0));
                    break;
                case "bypricontactid":
                    filterByPriContactId(filters.get(name).get(0));
                    break;
                case "bysecaddress":
                    filterBySecAddress(filters.get(name).get(0));
                    break;
                case "bysecaddressid":
                    filterBySecAddressId(filters.get(name).get(0));
                    break;
                case "byseccontactid":
                    filterBySecContactId(filters.get(name).get(0));
                    break;
                case "bydid":
                    filterByDID(filters.get(name).get(0));
                    break;
                case "byUCXConMapId":
                    filterByUCXConMapId(filters.get(name).get(0));
                    break;
                case "byaops":
                    filterByAOPs(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byaopscode":
                    filterByAOPsCode(filters.get(name).get(0));
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
