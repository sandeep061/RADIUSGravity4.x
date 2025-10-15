package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.entities.EntityState;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Terminal;
import ois.radius.cc.entities.tenant.cc.XServer;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;


public class RequestTerminalAddService extends RequestEntityAddService
{

	public RequestTerminalAddService(UAClient uac)
	{
		super(uac);
		
	}

//	@Override
//	public Event DoProcessRequest(Request request) throws Throwable
//	{
//		
//		return null;
//		
//	}
	@Override
    protected void DoPreProcess(RequestEntityAdd req) throws Throwable
    {

        if (req.getAttribute("XServer") == null)
        {
            throw new GravityIllegalArgumentException("XServer", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        Long xid = req.getAttributeValueOf(Long.class, "XServer");
        XServer xs =_tctx.getDB().FindAssert(EN.XServer.getEntityClass(), xid);
		
		if(req.getAttributes().containsKey("Address"))
        {
            JPAQuery query = new JPAQuery("SELECT t FROM Terminal t WHERE t.XServer.Id = :xid AND t.Address = :add And t.EntityState=:entstate");
            query.setParam("xid", xid);
            query.setParam("add", req.getAttributeValueOf(String.class,"Address"));
            query.setParam("entstate", EntityState.Active);

            Terminal terminal=_tctx.getDB().FindFromDB(Terminal.class,query);
            if(terminal!=null){
                throw new GravityEntityExistsException(EN.XServer.name(), "Address", OPRelational.Eq, terminal.getAddress());
            }
        }

        //XServer must be in start state during add Terminal.
//        AIXServer aixs = _uac.getCCtx().getXServerStore().GetById(xid);
//        if (aixs == null)
//        {
//            throw new GravityIllegalArgumentException(EN.XServer.name(), xid.toString(), ProviderState.Shutdown, ProviderState.InService);
//        }

    }

    @Override
    protected void DoPostBuildProcess(RequestEntityAdd reqenadd, AEntity entity) throws Throwable
    {
        Terminal terminal = (Terminal) entity;
        StringBuilder regxSb = new StringBuilder();
//        if (!UtilAddress.IsValidTerminalAddress(terminal.getChannel(), terminal.getAddress(), regxSb))
//        {
//            throw new GravityIllegalArgumentException("Address", EventFailedCause.RegularExpressionViolation);
//
//        }

//        AIXServer aixs = _tctx.getXServerStore().GetByIdAssert(terminal.getXServer().getId());

//        XProviderStub providerStub = aixs.getProviderStub();
//        //There are some scenario when media server stop process is not completed then orivder stub can be null.
//        if (providerStub == null)
//        {
//            _logger.trace("Provider stub is found null in AIXServer : [" + aixs + "].So unable to send terminal add req to provider.");
//        }
//        else
//        {
//            providerStub.AddTerminal(terminal.getCode(), terminal.getAddress(), terminal.getAttributes());
//        }
    }
	

}
