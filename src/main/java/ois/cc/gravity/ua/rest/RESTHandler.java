/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.ua.rest;

import CrsCde.CODE.Common.Utils.EnumUtil;
import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.LOGUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import code.ua.events.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ois.cc.gravity.AppConst;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.cc.gravity.ua.UACRegistry;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @author Deepak
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RESTHandler implements HandlerInterceptor
{

    static final Logger _logger = LoggerFactory.getLogger(RESTHandler.class);

    private UACRegistry _uacReg;
    private RESTRequestDTO _reqDTO;

//    @Autowired
    public RESTHandler(RESTRequestDTO reqdto)
    {
        this._uacReg = UACRegistry.This();
        this._reqDTO = reqdto;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        if (_logger.isTraceEnabled())
        {
            LOGUtil.TraceLogArgs(_logger, RESTUtil.toLog(request), handler);
        }
//        _logger.trace("Req Id " + request.getRequestId());
        try
        {
            String uri = request.getRequestURI();

            uri = uri.replaceAll("/+$", "");
            _logger.trace("Given Url PreHandle Method " + uri);
            switch (uri)
            {
                case AppConst.GRAVITY_C_BASE_URL_REGISTER:
                case AppConst.GRAVITY_C_BASE_URL_LOGOUT:
                case AppConst.GRAVITY_S_BASE_URL_SIGNIN:
                case AppConst.GRAVITY_C_BASE_URL_CLEARTEMPSTATE:
                case AppConst.GRAVITY_CTRL_BASE_URL_DARK:
                case AppConst.GRAVITY_C_BASE_URL_VERSIONFETCH:
                case AppConst.DARK_C_BASE_URL_VERSIONFETCH:
                case AppConst.GRAVITY_HEALTH:
                    preHandleC(request, response, handler);
                    break;
                case AppConst.GRAVITY_N_BASE_URL:
                case AppConst.DARK_TENANT_START:
                case AppConst.DARK_N_BASE_URL:
                    preHandleN(request, response, handler);
                    break;
                default:
                    if (uri.startsWith(AppConst.GRAVITY_C_SURVEYINFO)) {
                        preHandleC(request, response, handler);
                    } else {
                        preHandleE(request, response, handler);
                    }
            }

        }
        catch (Throwable th)
        {
            /**
             * We are expecting two kinds of exceptions here, <br>
             * 1 - our internal exception thrown from assert methods, which will carry the events to send as respone <br>
             * 2 - other unhandled exceptions which will be send as errors <br>
             */
            _logger.error(th.getMessage(), th);

            code.ua.events.Event ev;
            if (th instanceof EventWrapperException evWrapEx)
            {
                ev = evWrapEx.getEvent();
            }
            else
            {
                ev = new EventException(request.getHeader(UIParams.ReqId.getVal()), GReqType.System, GReqCode.System, th);
            }

            WriteEvent(response, ev);

            return false;
        }
        return true;
    }

    private void WriteEvent(HttpServletResponse response, code.ua.events.Event ev)
    {

        try
        {
            String evstr = JSONUtil.ToJSON(ev).toString();
            _logger.trace("Event : " + evstr);

            response.getWriter().write(evstr);
        }
        catch (Exception e)
        {
            _logger.error(e.getMessage(), e);
        }
    }

    public void preHandleC(HttpServletRequest request, HttpServletResponse response, Object handler) throws EventWrapperException
    {
        assertReqId(request);
    }

    public void preHandleE(HttpServletRequest request, HttpServletResponse response, Object handler) throws EventWrapperException
    {
        assertReqId(request);

        assertUAClient(request);
        UAClient uac = this._reqDTO.getUAClient();
        _logger.trace(uac.toString() + " >> " + (request == null ? "NULL" : request));
        assertEntityFromRequest(request);
    }

    public void preHandleN(HttpServletRequest request, HttpServletResponse response, Object handler) throws EventWrapperException
    {
        this._reqDTO.setReqId(UIDUtil.GenerateUniqueId());
    }

    private void assertReqId(HttpServletRequest request) throws EventWrapperException
    {
        String reqid = request.getHeader(UIParams.ReqId.getVal());
        if (reqid == null)
        {
            reqid = "NA";//TBD:need to change the event.
//            EventRequestValidationFailed ev = new EventRequestValidationFailed(reqid, GReqType.System, GReqCode.System, UIParams.ReqId.getVal(), EventFailedCause.NonOptionalConstraintViolation);
//            throw new EventWrapperException(ev);
        }
        this._reqDTO.setReqId(reqid);
    }

    private void assertUAClient(HttpServletRequest request) throws EventWrapperException
    {
        String reqid = this._reqDTO.getReqId();
        String accessToken = request.getHeader("access_token");

        if (accessToken == null)
        {
            throw new EventWrapperException(new EventRequestValidationFailed(reqid, GReqType.System, GReqCode.System, "access_token", EventFailedCause.NonOptionalConstraintViolation));
        }
        UAClient uac = _uacReg.Get(accessToken);
        if (uac == null)
        {
            EventNoUASessionExists ev = new EventNoUASessionExists(reqid, GReqType.System, GReqCode.System);
            throw new EventWrapperException(ev);
        }
        this._reqDTO.setUAClient(uac);

    }

    private void assertEntityFromRequest(HttpServletRequest req) throws EventWrapperException
    {
        Map<String, String> pathVariables = (Map<String, String>) req
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String reqid = req.getHeader(UIParams.ReqId.getVal());
        String entity;
        if (!pathVariables.containsKey("entity"))
        {
            EventRequestValidationFailed ev = new EventRequestValidationFailed(reqid, GReqType.System, GReqCode.System, "Entity", EventFailedCause.NonOptionalConstraintViolation);
            throw new EventWrapperException(ev);
        }

        entity = pathVariables.get("entity");
        EN en = null;
        try
        {
            en = EnumUtil.ValueOf(EN.class, entity);
            if (en == null)
            {
                throw new EventWrapperException(new EventInvalidEntity(reqid, GReqType.System, GReqCode.System, entity));
            }
        }
        catch (Exception ex)
        {
            throw new EventWrapperException(new EventException(reqid, GReqType.System, GReqCode.System, ex));
        }

        this._reqDTO.setEN(en);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        _logger.trace("HTTP Status : " + response.getStatus());
    }

    }
