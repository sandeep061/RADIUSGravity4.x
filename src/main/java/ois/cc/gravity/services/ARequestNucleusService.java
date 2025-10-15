package ois.cc.gravity.services;

import code.ua.events.Event;
import code.ua.requests.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  abstract class ARequestNucleusService extends ARequestService
{
    protected Logger _logger = LoggerFactory.getLogger(getClass());

    public ARequestNucleusService()
    {
        super();
    }

    protected abstract Event ProcessNucRequest(Request request) throws Throwable;

    @Override
    public final Event DoProcessRequest(Request request) throws Throwable
    {
        return ProcessNucRequest(request);
    }


}

