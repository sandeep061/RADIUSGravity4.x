package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Enums.OPRelational;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.XServerQuery;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XServer;
import ois.cc.gravity.Limits;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import org.vn.radius.cc.platform.xspi.ProviderID;

public class RequestXServerAddService extends RequestEntityAddService
{

    public RequestXServerAddService(UAClient uac)
    {
        super(uac);
    }

    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd req = (RequestEntityAdd) request;

        validateRequestParams(req);
		
		 if(req.getAttributes().containsKey("Name"))
        {
            XServerQuery query = new XServerQuery().filterByName(req.getAttributeValueOf(String.class,"Name"));
              XServer xserver=_tctx.getDB().Find(query);
              if(xserver!=null){
                  throw new GravityEntityExistsException(EN.XServer.name(), "Name", OPRelational.Eq, xserver.getName());
              }
        }

        XServer xs = new XServer(ProviderID.valueOf(req.getAttribute("ProviderID").toString()));
        // Build and Insert entity.
        EntityBuilder.BuildEntity(_tctx.getDB(), xs, req.getAttributes());
//        db.Insert(_uac.getUserSession().getUser(), xs);

        _tctx.getDB().Insert(_uac.getUserSession().getUser(),xs);
        EventEntityAdded ev = new EventEntityAdded(req, xs);
        return ev;
    }

    private void validateRequestParams(RequestEntityAdd req) throws GravityIllegalArgumentException, Exception
    {
        if (!req.getAttributes().containsKey("Code"))
        {
            throw new GravityIllegalArgumentException("Code",
                    EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        }
        if (!req.getAttributes().containsKey("ProviderID"))
        {
            throw new GravityIllegalArgumentException("ProviderID",
                    EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
        } else
        {
            ProviderID provId = ProviderID.valueOf(req.getAttribute("ProviderID").toString());
            if (provId == null)
            {
                throw new GravityIllegalArgumentException("ProviderID",
                        EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
            }
            req.getAttributes().put("ProviderID", provId);
        }
        if (req.getAttributes().containsKey("AgentSocketUrl"))
        {
            String agUrl = req.getAttribute("AgentSocketUrl").toString();
            if (agUrl.length() > Limits.StringValue_Max_Length)
            {
                throw new GravityIllegalArgumentException("AgentSocketUrl",
                        EventFailedCause.DataLengthLimitExceeds, EvCauseRequestValidationFail.InvalidParamName);
            }
        }

        /**
         * Note : We will not process EndpointProps here. It should process in XServer edit req. <br>
         * - So, we are removing the EndPointProps from the request if user by mistakenly sent else it may create issue in EntityBuilder.
         */
        if (req.getAttributes().containsKey("EndPointTypeProps"))
        {
            req.getAttributes().remove("EndPointTypeProps");
        }

    }
}

