package ois.cc.gravity.db.queries;

import ois.radius.cc.entities.EN;
import ois.cc.gravity.AppConst;

import java.util.HashMap;

public class EntityQueryFactory
{

    private static HashMap<EN, Class> _hmEnQryCls = new HashMap<>();

    public static EntityQuery CreateQueryBuilder(EN en) throws Exception
    {
        if (!_hmEnQryCls.containsKey(en))
        {
            Class qryCls;
            try
            {
                qryCls = Class.forName(AppConst.EN_QUERY_BASE_PKG + "." + en.name() + "Query");
            }
            catch (ClassNotFoundException cex)
            {
                qryCls = EntityQueryImpl.class;
            }

            _hmEnQryCls.put(en, qryCls);
        }

        Class qryCls = _hmEnQryCls.get(en);
        if (qryCls.equals(EntityQueryImpl.class))
        {
            return (EntityQuery) qryCls.getConstructor(EN.class).newInstance(en);
        }
        else
        {
            return (EntityQuery) qryCls.getConstructor().newInstance();
        }
    }
}