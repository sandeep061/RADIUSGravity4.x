package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.PWDUtil;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import code.entities.EntityState;
import ois.cc.gravity.entities.util.AppUtil;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Terminal;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import org.vn.radius.cc.platform.exceptions.RADException;

public class RequestTerminalEditService extends RequestEntityEditService
{

    public RequestTerminalEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit req, AEntity entity) throws Throwable
    {
        Terminal term = (Terminal) entity;

        Long xsid = term.getXServer().getId();

        if (req.getAttributes().containsKey("Address"))
        {
            JPAQuery query = new JPAQuery("SELECT t FROM Terminal t WHERE t.XServer.Id = :xid AND t.Address = :add And t.EntityState=:entstate");
            query.setParam("xid", xsid);
            query.setParam("add", req.getAttributeValueOf(String.class, "Address"));
            query.setParam("entstate", EntityState.Active);

            Terminal terminal = _tctx.getDB().FindFromDB(Terminal.class, query);
            if (terminal != null)
            {
                throw new GravityEntityExistsException(EN.XServer.name(), "Address", OPRelational.Eq, terminal.getAddress());
            }
        }
        if (req.getAttributes().containsKey("Password"))
        {
            String password = req.getAttributeValueOf(String.class, "Password");
            if (!password.equals(AppUtil.Encrypt(term.getPassword())))
            {
                term.setPassword(password);
            }
        }
//        AIXServer aixs = _tctx.getXServerStore().GetById(xsid);
//        if (aixs == null)
//        {
//            throw new RADIllegalObjectStateException(EN.XServer.name(), xsid.toString(), ProviderState.Shutdown, ProviderState.InService);
//        }

        //V:010324
//        JPAQuery qry = new JPAQuery("Select rt from RtTerminal rt Where rt.Name =: addr And rt.XServerId =: xid");
//        qry.setParam("addr", term.getAddress());
//        qry.setParam("xid", term.getXServer().getId());
//
//        RtTerminal rtTerm = _cctx.getRtDB().Find(qry);
//        AITerminal aiTerm = _tctx.getTerminalStore().GetById(term.getId());
//        if (aiTerm != null && aiTerm.getRtTerminal() != null)
//        {
//            RtTerminal rtTerm = aiTerm.getRtTerminal();
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TerminalIsInUse, "[Agent.LoginId == " + rtTerm.getLoggedInAgent().getLoginId() + "]");
//        }
    }

    @Override
    protected void DoPostBuildProcess(RequestEntityEdit req, AEntity entity) throws Exception, RADException
    {
        Terminal reqTerm = (Terminal) entity;

//        AIXServer aixs = _tctx.getXServerStore().GetByIdAssert(reqTerm.getXServer().getId());
//        XProviderStub providerStub = aixs.getProviderStub();
//
//        try
//        {
//            providerStub.EditTerminal(reqTerm.getAddress(), reqTerm.getAttributes());
//        }
//        catch (GravityXSPIException ex)
//        {
////            _tctx.getDB().Refresh(entity);
//            throw ex;
//        }
    }
}
