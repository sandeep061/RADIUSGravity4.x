package ois.cc.gravity.entities.util;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalObjectStateException;
import ois.radius.ca.enums.aops.AOPsState;
import ois.radius.cc.entities.tenant.cc.AOPs;

public class AOPsUtil
{

    public static AOPsState GetAOPsState(MySQLDB db, AOPs aops) throws CODEException, GravityException, Exception
    {
        JPAQuery aopsquery = new JPAQuery("SELECT a.AOPsState FROM AOPsStatus a WHERE a.AOPsId = :aopsId");
        aopsquery.setParam("aopsId", aops.getId());

        AOPsState aopsState = db.SelectScalar(aopsquery);
        if (aopsState != null)
        {
            return aopsState;
        }
        return null;
    }

    public static void IsAOPsInUnoldState(MySQLDB db, AOPs aops) throws CODEException, GravityException, Exception
    {
        AOPsState aopSt = GetAOPsState(db, aops);
        if (aopSt != null && !aopSt.equals(AOPsState.Stop))
        {
            throw new GravityIllegalObjectStateException(aops.getClass().getSimpleName(), aops.getId().toString(), aopSt, AOPsState.Stop);
        }
    }


}
