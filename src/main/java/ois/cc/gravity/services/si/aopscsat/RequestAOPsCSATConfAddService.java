package ois.cc.gravity.services.si.aopscsat;

import code.common.exceptions.CODEException;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.db.queries.DispositionQuery;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.xsess.XSessType;
import ois.radius.cc.entities.EN;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RequestAOPsCSATConfAddService extends RequestEntityAddService
{

    public RequestAOPsCSATConfAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {
        HashMap<String, Object> attributes = reqenadd.getAttributes();
        if (attributes.containsKey("XSessType"))
        {
            XSessType xstype = reqenadd.getAttributeValueOf(XSessType.class, "XSessType");
            ValidateXSessType(xstype);
        }
        ValidateDisposionCodes(reqenadd);
    }

    private void ValidateDisposionCodes(RequestEntityAdd reqenadd) throws Exception, GravityException, CODEException
    {
        HashMap<String, Object> attributes = reqenadd.getAttributes();
        if (attributes.containsKey("DispositionCodes"))
        {
            String dispositionCode = reqenadd.getAttributeValueOf(String.class, "DispositionCodes");
            if (dispositionCode == null || dispositionCode.trim().isEmpty())
            {
                throw new GravityIllegalArgumentException("Invalid dispositionCodes",
                        EventFailedCause.ValueOutOfRange,
                        EvCauseRequestValidationFail.InvalidParamName);
            }

            dispositionCode = dispositionCode.trim();

            /**
             * DispositionsCodes must be either '*' or Array of codes with csv. Make sure '*' should be the element of that array. <br>
             * - '*' represent allow all.
             */
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

                List<String> alDisCodes = Arrays.stream(dispositionCode.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                //TBD:validate disposition codes are valid or not
                for (String code : alDisCodes)
                {
                    _tctx.getDB().FindAssert(new DispositionQuery(EN.Disposition).filterByCode(code));
                }
                JSONArray dispositionCodeArray = new JSONArray(alDisCodes);
                attributes.put("DispositionCodes", dispositionCodeArray.toString());
            }
        }
    }

    private void ValidateXSessType(XSessType xstype) throws GravityIllegalArgumentException
    {
        if (!xstype.equals(XSessType.Inbound) && !xstype.equals(XSessType.Outbound))
        {
            throw new GravityIllegalArgumentException("Invalid XSessType",
                    EventFailedCause.ValueOutOfRange,
                    EvCauseRequestValidationFail.InvalidParamName);
        }
    }

}
