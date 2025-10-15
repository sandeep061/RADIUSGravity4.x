package ois.cc.gravity.services;

import code.ua.events.Event;
import code.ua.events.EventFailedCause;
import code.ua.events.EventRequestValidationFailed;
import code.ua.events.EventUnAuthorizedRequest;
import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.framework.requests.GReqCode;
//import ois.cc.gravity.ua.AuthChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public abstract class ARequestService implements IRequestService
{

    protected Logger _logger = LoggerFactory.getLogger(getClass());
    /**
     * There can't be a RequestProcessor without an UAClient.
     */
    protected ServerContext _sCtx;
    //    protected TenantContext _tctx;
    //    protected final MySQLDB _sysDB;
//
    public ARequestService()
    {
//        this._uac = uac;
        this._sCtx = ServerContext.This();
//        this._tctx = _uac.getCCtx();
    }



    @Override
    public final Event ProcessRequest(Request request) throws Throwable
    {


        /**
         * Do request parameter validation.
         */
        Event ev = IsRequestParamsValid(request);
        if (ev != null)
        {
            return ev;
        }

        /**
         * Checking Authorized User
         */
        Event authEv = IsUserAuthorized(request);
        if (authEv != null)
        {
            return authEv;
        }
//
//        Event uaapEv = UAAPServiceManager.This().EvaluateUaap(request, _uac);
//        if (uaapEv != null)
//        {
//            return uaapEv;
//        }
//
        /**
         * Process request.
         */
        return DoProcessRequest(request);
    }

    protected abstract Event DoProcessRequest(Request request) throws Throwable;



    /**
     *
     * @param request
     * @return
     */
    private EventUnAuthorizedRequest IsUserAuthorized(Request request)
    {
        /**
         * For login and VersionInfo request we don't need to check for Authorized User.
         */
        if (request.getReqCode().equals(GReqCode.VersionInfoFetch)
                || request.getReqCode().equals(GReqCode.Login)
                || request.getReqCode().equals(GReqCode.OSLogin)
                || request.getReqCode().equals(GReqCode.AuthCheck))
        {
            return null;
        }

//        EN en = RequestContext.GetENFromRequest(request);
//        if (AuthChecker.IsAuthorized(_uac.getUserSession().getUser(), request.getReqCode(), en))
//        {
//            return null;
//        }
//        else
//        {
//            EventUnAuthorizedRequest ev = new EventUnAuthorizedRequest(request);
//            return ev;
//        }
        return null;
    }

    /**
     * Validate request param value based on Param annotation.
     *
     * @param request
     * @return NULL in case request is valid.
     */
    private EventRequestValidationFailed IsRequestParamsValid(Request request) throws IllegalAccessException
    {
        EventRequestValidationFailed ev;

        ArrayList<Field> flds = RequestContext.FieldsToValidate(request.getClass());
        for (Field fld : flds)
        {
            Param annot = fld.getAnnotation(Param.class);
            Object value = fld.get(request);

            /**
             * If field is set an non-optional, check for null. In case field is a String type check for empty string also.
             */
            if (!annot.Optional())
            {
                if (value == null
                        || (value instanceof String && ((String) value).isEmpty())
                        || (value instanceof Collection<?> && ((Collection) value).isEmpty()))
                {
                    ev = new EventRequestValidationFailed(request, fld.getName(), EventFailedCause.NonOptionalConstraintViolation);
                    return ev;
                }
            }

            /**
             * Check for regular expression defined.
             */
            if (!annot.Regex().isEmpty())
            {
                if (value != null
                        && value instanceof String)
                {
                    String strval = (String) value;
                    Pattern pattern = Pattern.compile(annot.Regex());
                    if (!pattern.matcher(strval).matches())
                    {
                        ev = new EventRequestValidationFailed(request, fld.getName(), EventFailedCause.RegularExpressionViolation);
                        return ev;
                    }
                }
            }

            /**
             * Check for length of value. <br>
             * default length is 255.
             */
            if (value != null && value instanceof String)
            {
                String strval = (String) value;
                if (strval.length() > annot.Length())
                {
                    ev = new EventRequestValidationFailed(request, fld.getName(), EventFailedCause.DataLengthLimitExceeds);
                    return ev;
                }
            }

        }

        return null;
    }
//    protected Event IsTenantStopInitiated(Request req, Tenant client) throws RADRuntimeCheckFailedException
//    {
//        TenantContext cctx = this._sCtx.GetTenantCtxByCode(client.getCode());
//
//        if (cctx == null)
//        {
//            return new EventCTClientNotStarted(req, client.getId());
//        }
//
//        if (cctx.getIsStopInitiated().get())
//        {
//            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.TenantStopAlreadyInitiated, "[Tenant == " + cctx.getTenant() + "]");
//        }
//
//        return null;
//    }

}

