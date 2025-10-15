package ois.cc.gravity.services;

import code.ua.events.Event;
import code.ua.requests.Request;
import ois.cc.gravity.ua.UAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ARequestCmdService extends ARequestService
{
    protected Logger _logger = LoggerFactory.getLogger(getClass());

    public ARequestCmdService()
    {
        super();
    }

    protected abstract Event ProcessCmdRequest(Request request) throws Throwable;

    @Override
    public final Event DoProcessRequest(Request request) throws Throwable
    {
        return ProcessCmdRequest(request);
    }


}
