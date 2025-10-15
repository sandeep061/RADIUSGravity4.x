/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.services.sys;

import CrsCde.CODE.Common.Utils.UIDUtil;
import code.ua.events.Event;
import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.framework.events.EventCode;
import ois.cc.gravity.framework.requests.sys.RequestTenantStart;
import ois.cc.gravity.services.ARequestNucleusService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.radius.cc.entities.sys.Tenant;


/**
 * This processor start the Tenant. <br>
 * It will also create the default root admin for this client, if not created already. <br>
 * - this step is done in Start client rather Add client, since during Add client the context will not initialise. <br>
 * - only during start client context will init, hence we will check for already added or not, then will add. <br>
 * Tenant start will be done only by user request, hence we don't need to implement the logic in any common place.
 * <br>
 *
 * @author Suman
 * @since 28 Feb, 2018
 */
public class RequestTenantStartService extends ARequestNucleusService
{

    @Override
    protected Event ProcessNucRequest(Request request) throws Throwable
    {
        RequestTenantStart reqStart = (RequestTenantStart) request;

        Tenant client = _sCtx.getNucleusCtx().GetTenantByCodeAssert(reqStart.getTenantCode());

        /**
         * Check if ctclient is already started.
         */
        TenantContext tCtx = _sCtx.GetTenantCtxByCode(client.getCode());
        if (tCtx != null)
        {
            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TenantAlreadyStarted, "[Tenant.Code==" + reqStart.getTenantCode() + "]");
        }

        /**
         * Do start client.
         */
        _sCtx.InitTenantContext(client);
        tCtx = _sCtx.GetTenantCtxByCode(client.getCode());
        tCtx.Start();
        _logger.info("Tenant Stated in Gravity " + tCtx.getTenant().getCode());
        
        /**
         * Send tenant start request to dark. <br>
         * - Need to check following conditions. <br>
         * 1.If tenant start request is failed 
         * 
         */
//        sendTenantStartReqToDark(reqStart.getTenantCode());

        EventOK ev = new EventOK(request, EventCode.TenantStarted);
        return ev;
    }

        private void sendTenantStartReqToDark(String tntcode) throws Throwable
    {
        org.vn.radius.cc.platform.requests.system.RequestTenantStart reqtntstart = new org.vn.radius.cc.platform.requests.system.RequestTenantStart(UIDUtil.GenerateUniqueId());
        reqtntstart.setTenantCode(tntcode);
        ServerContext.This().get_darkServer().SendSyncRequest(reqtntstart);
    }
}
