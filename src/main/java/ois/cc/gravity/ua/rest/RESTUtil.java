/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.ua.rest;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.LOGUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static ois.cc.gravity.ua.rest.RESTHandler._logger;
import org.slf4j.Logger;

/**
 *
 * @author suman
 * @since 31-Aug-2024
 */
public class RESTUtil
{

    private void WriteEvent(HttpServletResponse httpo, code.ua.events.Event ev, Logger logger)
    {
        LOGUtil.TraceLogArgs(logger, ev);

        try
        {
            String evstr = JSONUtil.ToJSON(ev).toString();
            _logger.trace("Event : " + evstr);

            httpo.getWriter().write(evstr);
        }
        catch (Exception e)
        {
            _logger.error(e.getMessage(), e);
        }
    }

    public static String toLog(HttpServletRequest httpq)
    {
        if (httpq == null)
        {
            return "null";
        }

        return "HttpServletRequest{"
                + "RequestURI=" + httpq.getRequestURI()
                + ", ReqId=" + httpq.getHeader(UIParams.ReqId.getVal())
                + '}';
    }

    public static String toLog(HttpServletResponse httpo)
    {
        if (httpo == null)
        {
            return "null";
        }

        return "HttpServletRequest{"
                + "Status=" + httpo.getStatus()
                + ", ReqId=" + httpo.getHeader(UIParams.ReqId.getVal())
                + '}';
    }
}
