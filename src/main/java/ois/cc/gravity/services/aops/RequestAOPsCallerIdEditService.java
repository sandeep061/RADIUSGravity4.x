package ois.cc.gravity.services.aops;

import code.common.exceptions.CODEException;
import code.entities.AEntity;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.AOPsCallerId;
import ois.radius.cc.entities.tenant.cc.XPlatform;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;
import java.util.ArrayList;
import java.util.Objects;
import ois.cc.gravity.entities.util.AOPsUtil;

public class RequestAOPsCallerIdEditService extends RequestEntityEditService
{

    public RequestAOPsCallerIdEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable
    {
//        AOPsCallerId aoPsCallerId = (AOPsCallerId) thisentity;
//        if (reqenedit.getAttributes().containsKey("DefXPlatformUA"))
//        {
//
//            long defId = reqenedit.getAttributeValueOf(Long.class, "DefXPlatformUA");

//            if (aoPsCallerId.getXPlatformUAs() != null)
//            {
//                boolean exists = aoPsCallerId.getXPlatformUAs()
//                        .stream()
//                        .anyMatch(xpua -> Objects.equals(xpua.getId(), defId));
//
//                if (exists)
//                {
//                    throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, "DefXPlatformUA ID must not be in the XPlatformUAs list");
//                }
//            }

//        }
    }

//    @Override
//    protected void appendAttribute(RequestEntityEdit req, AEntity entity) throws Exception, CODEException, GravityException
//    {
//        AOPsCallerId aopscallerid = (AOPsCallerId) entity;
//        AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aopscallerid.getAOPs());
//        ArrayList<XPlatformUA> xplatformuas = new ArrayList<>();
//        ArrayList<String> reqxpltfrmua = req.getAttributeCollectionAppend().get("XPlatformUA");
//        if (aopscallerid.getDefXPlatformUA() != null)
//        {
//            if (reqxpltfrmua.contains(aopscallerid.getDefXPlatformUA().getId().toString()))
//            {
//                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, "DefXPlatformUA is not valid");
//            }
//        }
//        for (String id : reqxpltfrmua)
//        {
//            Long aopcdnid = Long.valueOf(id);
//
//            XPlatformUA xPlatformUA = _tctx.getDB().FindAssert(XPlatformUA.class, aopcdnid);
//            if (validateXPlatformUAWithXPlatform(xPlatformUA, aopscallerid.getXPlatform()))
//            {
//                //throw exception
//                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, "XPlatformUA is not valid");
//            }
//            xplatformuas.add(xPlatformUA);
//        }
//        aopscallerid.setXPlatformUAs(xplatformuas);
//    }
//
//    @Override
//    protected void removeAttribute(RequestEntityEdit req, AEntity entity) throws GravityException, Exception, CODEException
//    {
//
//        AOPsCallerId aopscallerid = (AOPsCallerId) entity;
//        AOPsUtil.IsAOPsInUnoldState(_tctx.getDB(), aopscallerid.getAOPs());
//        ArrayList<String> xplateformuaids = req.getAttributeCollectionRemove().get("xplatformua");
//        for (String id : xplateformuaids)
//        {
//            Long xplateformuaid = Long.valueOf(id);
//
//            XPlatformUA xPlatformUAentity = _tctx.getDB().FindAssert(XPlatformUA.class, xplateformuaid);
//            aopscallerid.getXPlatformUAs().remove(xPlatformUAentity);
//        }
//
//    }

    private Boolean validateXPlatformUAWithXPlatform(XPlatformUA xPlatformUA, XPlatform platform)
    {
        if (!xPlatformUA.getXPlatform().getId().equals(platform.getId()))
        {
            return true;
        }
        return false;
    }

}
