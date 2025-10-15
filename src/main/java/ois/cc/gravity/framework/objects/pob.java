package ois.cc.gravity.framework.objects;

import code.entities.AEntity;
import code.uaap.service.common.entities.app.Policy;
import ois.radius.cc.entities.tenant.cc.Profile;

public class pob
{

    private static void setBaseAttributes(AEntity entity, AObject object)
    {
        object.setId(entity.getId());
    }

    public static OProfile Build(Profile profile, Policy policy)
    {
        OProfile oprofile = new OProfile();
        setBaseAttributes(profile, oprofile);

        oprofile.setCode(profile.getCode());
        oprofile.setName(profile.getName());
        oprofile.setDescription(profile.getDescription());
        if (policy != null)
        {
            oprofile.setPolicy(policy);
        }

        return oprofile;
    }

//    public static OCampaign Build(Campaign camp)
//    {
//
//        OCampaign ocamp = new OCampaign();
//        setBaseAttributes(camp, ocamp);
//
//        ocamp.setName(camp.getName());
//        ocamp.setCode(camp.getCode());
//        ocamp.setDescription(camp.getDescription());
////        ocamp.setCampaignType(camp.getCampaignType());
//        ocamp.setChannels(camp.getChannels());
//
//        return ocamp;
//    }

}
