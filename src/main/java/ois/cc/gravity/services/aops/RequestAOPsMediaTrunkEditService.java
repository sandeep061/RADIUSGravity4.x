//package ois.cc.gravity.services.aops;
//
//import code.entities.AEntity;
//import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
//import ois.cc.gravity.services.common.RequestEntityEditService;
//import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
//import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
//import ois.cc.gravity.ua.UAClient;
//
//public class RequestAOPsMediaTrunkEditService extends RequestEntityEditService {
//    public RequestAOPsMediaTrunkEditService(UAClient uac) {
//        super(uac);
//    }
//
//    @Override
//    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable {
//        if (reqenedit.getAttributes().containsKey("Priority")) {
//            Integer priority = reqenedit.getAttributeValueOf(Integer.class, "Priority");
//            if (priority < 0 || priority > 10) {
//                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, " Priority value must between 0 to 10");
//            }
//        }
//    }
//}
//
