package ois.cc.gravity.services.sys;

import code.ua.events.Event;
import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.framework.requests.sys.RequestTenantStop;
import ois.cc.gravity.services.ARequestNucleusService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.radius.cc.entities.sys.Tenant;

public class RequestTenantStopService extends ARequestNucleusService
{

    @Override
    protected Event ProcessNucRequest(Request request) throws Throwable
    {

        RequestTenantStop reqStop = (RequestTenantStop) request;

        Tenant client = _sCtx.getNucleusCtx().GetTenantByCodeAssert(reqStop.getTenantCode());

        TenantContext tCtx = _sCtx.GetTenantCtxByCode(client.getCode());
        if ((tCtx == null) )
        {
            /**
             * Client has already stopped. May be clean up not done properly. Do the clean up and return EventTenantStopped.
             */
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TenantNotStartedYet, "Start Tenant In Nucleus");
        }

        /**
         * Stop client
         */
         _sCtx.Stop(tCtx);


        /**
         * Return success event.
         */
        return new EventOK(request, EventCode.TenantStoped);
    }
   

}
