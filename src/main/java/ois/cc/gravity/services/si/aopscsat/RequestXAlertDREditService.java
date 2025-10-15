package ois.cc.gravity.services.si.aopscsat;

import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;

public class RequestXAlertDREditService extends ARequestEntityService {
    public RequestXAlertDREditService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        return null;
    }
}
