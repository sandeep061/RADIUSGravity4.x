package ois.cc.gravity.services.survey;

import code.ua.events.EventFailedCause;
import java.util.HashMap;
import ois.cc.gravity.Limits;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;

public class RequestSurveyAddService extends RequestEntityAddService
{

    public RequestSurveyAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {
        HashMap<String, Object> attributes = reqenadd.getAttributes();
        if (attributes.containsKey("ExpiryTimeOut"))
        {
            int val = reqenadd.getAttributeValueOf(Integer.class, "ExpiryTimeOut");
            if (val > Limits.Survey_ExpiryTimeOut)
            {
                throw new GravityIllegalArgumentException("ExpiryTimeOut value between 0 to 128", "ExpiryTimeOut", EventFailedCause.ValueOutOfRange);
            }
        }
    }
}
