package ois.cc.gravity.entities.util;

import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntitiesUtil
{

   private static Logger logger = LoggerFactory.getLogger(EntitiesUtil.class);

    public static String getEntityClassName(EN en, Campaign campaign)
    {
        return en.getEntityClass().getName() + campaign.getCode();
    }

    public static String getEntityTableName(EN en,Campaign campaign)
    {
        return (en.name() + campaign.getCode());
    }
}
