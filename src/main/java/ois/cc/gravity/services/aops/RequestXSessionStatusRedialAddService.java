package ois.cc.gravity.services.aops;

import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.Limits;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.xsess.XSessStatus;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.radius.cc.entities.tenant.cc.XSessionStatusRedial;

public class RequestXSessionStatusRedialAddService extends ARequestEntityService
{
    public RequestXSessionStatusRedialAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd reqAdd= (RequestEntityAdd) request;
        int redialDelay= Limits.RedialDelay;
        AOPs aops=null;
        if(!reqAdd.getAttributes().containsKey("Category")){
            throw new GravityIllegalArgumentException("Category", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if(!reqAdd.getAttributes().containsKey("AOPs")){
            throw new GravityIllegalArgumentException("AOPs", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

      aops=_tctx.getDB().FindAssert(EN.AOPs.getEntityClass(),reqAdd.getAttributeValueOf(Long.class,"AOPs"));

        if(reqAdd.getAttributes().containsKey("RedialDelay")){
            redialDelay=reqAdd.getAttributeValueOf(Integer.class,"RedialDelay");
            if(redialDelay<Limits.RedialDelay_Min|| redialDelay>Limits.RedialDelay){
                throw new GravityIllegalArgumentException("RedialDelay", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.ParamValueOutOfRange);
            }
        }



          String category=reqAdd.getAttributeValueOf(String.class,"Category");
        XSessionStatusRedial xsRedial=new XSessionStatusRedial();
        xsRedial.setRedialDelay(redialDelay);
        xsRedial.setCategory(XSessStatus.Category.valueOf(category));
        xsRedial.setAOPs(aops);
        _tctx.getDB().Insert(_uac.getUserSession().getUser(),xsRedial);
        EventEntityAdded ev = new EventEntityAdded(reqAdd, xsRedial);
        return ev;
    }
}
