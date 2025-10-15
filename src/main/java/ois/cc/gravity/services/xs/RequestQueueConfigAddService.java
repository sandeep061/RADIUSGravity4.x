package ois.cc.gravity.services.xs;




import CrsCde.CODE.Common.Enums.OPRelational;
import code.entities.AEntity;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Queue;
import ois.cc.gravity.db.queries.QueueQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;


public class RequestQueueConfigAddService extends RequestEntityAddService
{

    public RequestQueueConfigAddService(UAClient uac)
    {

        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd req) throws Throwable
    {
        /**
         * @since V:190723 <br>
         * Code and Name should be same in skill and Queue thats why we dont have setter method for Name
         */
        if (req.getAttributes().containsKey("Code"))
        {
            Queue queue = _tctx.getDB().Find(new QueueQuery().filterByCode(req.getAttributeValueOf(String.class, "Code")));
            if (queue != null)
            {
                GravityEntityExistsException reex = new GravityEntityExistsException(queue.getClass().getSimpleName(), "Queue", OPRelational.Eq, queue.getId());
                throw reex;
            }
        }
        if (req.getAttributes().containsKey("Name"))
        {
            req.getAttributes().remove("Name");
        }

//        if (req.getAttribute("XServer") == null)
//        {
//            throw new GravityIllegalArgumentException("XServer", EvCauseRequestValidationFail.NonOptionalConstraintViolation);
//        }
        Long qid = req.getAttributeValueOf(Long.class, "Queue");
        _tctx.getDB().FindAssert(EN.Queue.getEntityClass(), qid);
//      Queue queue= _tctx.getDB().FindAssert(EN.QueueConfig.getEntityClass(), qid);

        //XServer must be in start state during add queue.
//        AIXServer aixs = _uac.getCCtx().getXServerStore().GetById(xid);
//        if (aixs == null)
//        {
//            throw new RADIllegalObjectStateException(EN.XServer.name(), xid.toString(), ProviderState.Shutdown, ProviderState.InService);
//        }

    }

    @Override
    protected void DoPostBuildProcess(RequestEntityAdd reqenadd, AEntity entity) throws Throwable
    {
        Queue queue = (Queue) entity;
//        AIXServer aixs = _cctx.getXServerStore().GetByIdAssert(queue.getXServer().getId());
//
//        XProviderStub providerStub = aixs.getProviderStub();
//
//        providerStub.AddQueue(queue.getCode(), queue.getAddress(), queue.getAttributes());

    }

}
