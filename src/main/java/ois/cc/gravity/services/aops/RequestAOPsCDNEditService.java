package ois.cc.gravity.services.aops;

import code.entities.AEntity;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.XPlatform;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

public class RequestAOPsCDNEditService extends RequestEntityEditService {

    public RequestAOPsCDNEditService(UAClient uac) {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable {

    }



    private Boolean validateXPlatformUAWithXPlatform(XPlatformUA xPlatformUA, XPlatform platform) {
        return !xPlatformUA.getXPlatform().getId().equals(platform.getId());
    }

}
