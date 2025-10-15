package ois.cc.gravity.services.xs;

import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.entities.AEntity_ad;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.Queue;
import ois.radius.cc.entities.tenant.cc.Skill;
import ois.radius.cc.entities.tenant.cc.XServer;
import ois.cc.gravity.db.queries.SkillQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityException;

public class RequestQueueEditService  extends RequestEntityEditService {
    public RequestQueueEditService(UAClient uac) {
        super(uac);
    }
    @Override
    protected void DoPreProcess(RequestEntityEdit req, AEntity entity_ad) throws GravityException, CODEException, Exception
    {
        //Check mapped media server must be in InService.
        Queue queue = (Queue) entity_ad;
        Long xsid = queue.getXServer().getId();
//        AIXServer aixs = _cctx.getXServerStore().GetById(xsid);
//        //xserver must be in INSERVICE state
//        if (aixs == null)
//        {
//            throw new RADIllegalObjectStateException(EN.XServer.name(), xsid.toString(), ProviderState.Shutdown, ProviderState.InService);
//        }

        //Check mapped campaign of skill must be in unload state.
        Skill sk = _tctx.getDB().Find(new SkillQuery().filterByQueue(queue.getId()));
        if (sk != null)
        {
            Long campid = sk.getAOPs().getId();
//            AICampaign aicamp = _uac.getCCtx().getCampaignStore().GetById(campid);
//            if (aicamp != null)    //campaign must be in UnLoad
//            {
//                throw new RADIllegalObjectStateException(EN.Campaign.name(), campid.toString(), aicamp.getRtCampaign().getCampaignState(), CampaignState.Unload);
//            }
        }

    }

    @Override
    protected void DoPostBuildProcess(RequestEntityEdit req, AEntity entity) throws GravityException, Exception
    {
        Queue reqQue = (Queue) entity;

        XServer xServer = reqQue.getXServer();
//        AIXServer aixs = _cctx.getXServerStore().GetByIdAssert(xServer.getId());
//        XProviderStub providerStub = aixs.getProviderStub();
//        try
//        {
//            providerStub.EditQueue(reqQue.getAddress(), reqQue.getAttributes());
//        }
//        catch (RADXSPIException ex)
//        {
////            _tctx.getDB().Refresh(entity);
//            throw ex;
//        }
    }
}
