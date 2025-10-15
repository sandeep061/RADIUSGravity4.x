//package ois.cc.gravity.services.aops;
//
//import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
//import ois.cc.gravity.services.common.RequestEntityAddService;
//import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
//import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
//import ois.cc.gravity.ua.UAClient;
//
//public class RequestAOPsMediaTrunkAddService extends RequestEntityAddService {
//    public RequestAOPsMediaTrunkAddService(UAClient uac) {
//        super(uac);
//    }
//
//    @Override
//    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable {
//        if(reqenadd.getAttributes().containsKey("Priority")){
//            Integer priority = reqenadd.getAttributeValueOf(Integer.class, "Priority");
//           if(priority<0 || priority>10){
//               throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange," Priority value must between 0 to 10");
//           }
//        }
//    }
//}
