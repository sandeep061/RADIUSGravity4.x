/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.survey;

import code.entities.AEntity;
import code.ua.events.EventFailedCause;
import java.util.HashMap;
import ois.cc.gravity.Limits;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;

/**
 *
 * @author Sandeepkumar.Sahoo
 * @since Aug 12, 2025
 */
public class RequestSurveyEditService extends RequestEntityEditService
{

    public RequestSurveyEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable
    {
        HashMap<String, Object> attributes = reqenedit.getAttributes();
        if (attributes.containsKey("ExpiryTimeOut"))
        {
            int val = reqenedit.getAttributeValueOf(Integer.class, "ExpiryTimeOut");
            if (val > Limits.Survey_ExpiryTimeOut)
            {
                throw new GravityIllegalArgumentException("ExpiryTimeOut value between 0 to 128", "ExpiryTimeOut", EventFailedCause.ValueOutOfRange);
            }
        }
    }
}
