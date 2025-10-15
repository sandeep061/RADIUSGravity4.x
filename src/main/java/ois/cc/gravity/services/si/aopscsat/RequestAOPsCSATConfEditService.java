package ois.cc.gravity.services.si.aopscsat;

import code.entities.AEntity;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.xsess.XSessType;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RequestAOPsCSATConfEditService extends RequestEntityEditService
{

    public RequestAOPsCSATConfEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable
    {
        HashMap<String, Object> attributes = reqenedit.getAttributes();
        if (attributes.containsKey("XSessType"))
        {
            XSessType xstype = reqenedit.getAttributeValueOf(XSessType.class, "XSessType");
            ValidateXSessType(xstype);
        }
        ValidateDisposionCodes(reqenedit);
    }

    private void ValidateDisposionCodes(RequestEntityEdit reqedit) throws Exception, GravityIllegalArgumentException
    {
        HashMap<String, Object> attributes = reqedit.getAttributes();
        if (attributes.containsKey("DispositionCodes"))
        {
            String dispositionCode = reqedit.getAttributeValueOf(String.class, "DispositionCodes");
            if (dispositionCode == null || dispositionCode.trim().isEmpty())
            {
                throw new GravityIllegalArgumentException("Invalid dispositionCodes",
                        EventFailedCause.ValueOutOfRange,
                        EvCauseRequestValidationFail.InvalidParamName);
            }

            dispositionCode = dispositionCode.trim();

            if ("*".equals(dispositionCode))
            {
                attributes.put("DispositionCodes", "*");
            }
            else if (dispositionCode.contains("*"))
            {

                throw new GravityIllegalArgumentException("Invalid dispositionCodes - '*' cannot be combined with other Disposition codes",
                        EventFailedCause.ValueOutOfRange,
                        EvCauseRequestValidationFail.InvalidParamName);
            }
            else
            {

                List<String> alDisCode = Arrays.stream(dispositionCode.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                JSONArray dispositionCodeArray = new JSONArray(alDisCode);
                attributes.put("DispositionCodes", dispositionCodeArray.toString());
            }
        }
    }

    private void ValidateXSessType(XSessType xstype) throws GravityIllegalArgumentException
    {
        if (!xstype.equals(XSessType.Inbound) || !xstype.equals(XSessType.Outbound))
        {
            throw new GravityIllegalArgumentException("Invalid XSessType",
                    EventFailedCause.ValueOutOfRange,
                    EvCauseRequestValidationFail.InvalidParamName);
        }
    }
}
