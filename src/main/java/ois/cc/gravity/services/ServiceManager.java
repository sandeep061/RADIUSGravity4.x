/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.ua.events.*;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.common.EventRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.*;
import ois.cc.gravity.ua.UAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import ois.cc.gravity.framework.events.common.EventAttributeValidationFailed;
import code.common.exceptions.CODEEntityNotFoundException;
import org.vn.radius.cc.platform.events.common.EventObjectIllegalTypeException;

/**
 *
 * @author Manoj-PC
 * @since Aug 11, 2024
 */
public class ServiceManager
{

    private static final Logger _logger = LoggerFactory.getLogger(ServiceManager.class);

    private static ServiceManager _this;

    private static ServiceRegistry _srvcReg;

    private final List<UAClient> _uacList;


    public ServiceManager()
    {

        _srvcReg = ServiceRegistry.This();
        _uacList = new ArrayList<>();

    }

    public static ServiceManager This()
    {
        if (_this == null)
        {
            _this = new ServiceManager();
        }

        return _this;
    }

    public synchronized <T extends Event> T ProcessRequest(UAClient uac,Request request)
    {
        Event respEv = null;

        if (_logger.isDebugEnabled())
        {
            try
            {
                _logger.debug(toString() + " -> " + (request == null ? "NULL" : JSONUtil.ToJSON(request)));
            }
            catch (Exception ex)
            {
                _logger.error(ex.getMessage(), ex);
            }
        }
        else
        {
            _logger.info(toString() + " -> " + (request == null ? "NULL" : request.toString()));
        }

        try
        {

            IRequestService reqProc = _srvcReg.GetService(uac,request);
            respEv = reqProc.ProcessRequest(request);
        }
        /**
         * Any unknown or unexpected exception.
         */
        catch (CODEEntityNotFoundException renfex)
        {
            EventEntityNotFound ev = new EventEntityNotFound(request, renfex.getEntityName());
            ev.setCondition(renfex.getCondition());
            respEv = ev;
        }
        catch (GravityEntityExistsException renexts)
        {
            EventEntityExists ev = new EventEntityExists(request, renexts.getEntityName());
            ev.setCondition(renexts.getCondition());
            respEv = ev;
        }
        catch (GravityNoSuchFieldException rnsfe)
        {
            EventInvalidAttribute ev = new EventInvalidAttribute(request, rnsfe.getClassName(), rnsfe.getFlieldName());
            respEv = ev;
        }
        catch (GravityAttributeConstraintFailedException racx)
        {
            
            EventAttributeValidationFailed ev = new EventAttributeValidationFailed(request, racx.getEntityName(), racx.getAttributeName(), racx.getFailedCause().name());
            ev.setMessage(racx.getMessage());
            respEv = ev;
        }
        catch (GravityUniqueConstraintViolationException uex){
            EventEntityExists ex=new EventEntityExists(request,uex.getEntityName());
            ex.setCondition(uex.getCondition());
            respEv= ex;
        }
        catch (GravityRuntimeCheckFailedException rachx)
        {
            switch (rachx.getEvCause())
            {
                case UnAuthorizedRequest:
                    respEv = new EventUnAuthorizedRequest(request);
                    break;
                default:
                    EventRuntimeCheckFailed evRun = new EventRuntimeCheckFailed(request, rachx.getEvCause());
                    evRun.setMessage(rachx.getMessage());
                    respEv = evRun;
            }

        }
        catch (GravityIllegalArgumentException ragx)
        {
            EventRequestValidationFailed resev = new EventRequestValidationFailed(request, ragx.getArgName(), ragx.getEvCause());
            resev.setMessage(ragx.getMessage());
            respEv = resev;
        }
        catch (GravityIllegalObjectStateException rosx)
        {
            respEv = new EventObjectIllegalState(request, rosx.getObjectType(), rosx.getObjectId(), rosx.getFound(), (Object[]) rosx.getExpected());
        }
        catch (GravityUnhandledRealMException rex){
            ois.cc.gravity.framework.events.common.EventProcessFailed event=new ois.cc.gravity.framework.events.common.EventProcessFailed(request,rex.getEventstr());
            respEv= event;
        }
        catch (Throwable ex)
        {
            _logger.error(ex.getMessage(), ex);

            respEv = new EventException(request, ex);
        }
        finally
        {
            //remove from the sync uac list.
            synchronized (_uacList)
            {
                _uacList.remove(uac);
            }
        }

        if (_logger.isDebugEnabled())
        {
            try
            {
                _logger.debug(toString() + " <- " + JSONUtil.ToJSON(respEv));
            }
            catch (Exception ex)
            {
                _logger.error(ex.getMessage(), ex);
            }
        }
        else
        {
            _logger.info(toString() + " <- " + respEv.toString());
        }

        return (T) respEv;
    }

    private ResponseEntity<?> BuildResponse(HttpStatus status, String obj)
    {
        _logger.info("Build Response : " + obj);
        return ResponseEntity.status(status).body(obj);
    }

}
