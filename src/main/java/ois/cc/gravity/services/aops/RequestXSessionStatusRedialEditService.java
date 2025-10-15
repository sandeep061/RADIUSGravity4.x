package ois.cc.gravity.services.aops;

import code.ua.events.Event;
import code.ua.events.EventEntityEdited;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.Limits;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XSessionStatusRedial;

public class RequestXSessionStatusRedialEditService extends ARequestEntityService
{
    public RequestXSessionStatusRedialEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityEdit req = (RequestEntityEdit) request;

        MySQLDB db = _tctx.getDB();

        EN en = req.getEntityName();
        XSessionStatusRedial XSesStRedial = _tctx.getDB().FindAssert(en.getEntityClass(), req.getEntityId());
        if(req.getAttributes().containsKey("RedialDelay")){
          int redialDelay=req.getAttributeValueOf(Integer.class,"RedialDelay");
            if(redialDelay> Limits.RedialDelay){
                throw new GravityIllegalArgumentException("RedialDelay", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.ParamValueOutOfRange);
            }
        }

        EntityBuilder.BuildEntity(db,XSesStRedial,req.getAttributes());
        db.Update(_uac.getUserSession().getUser(), XSesStRedial);
        EventEntityEdited ev = new EventEntityEdited(req,XSesStRedial);
        return ev;

    }
}
