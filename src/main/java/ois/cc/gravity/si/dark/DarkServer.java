/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.si.dark;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.LOGUtil;
import CrsCde.CODE.Common.Utils.UIDUtil;
import CrsCde.CODE.Socket.NIO.ObjectSocketChannel;
import code.common.exceptions.CODEException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.si.nucleus.NucleusServerContext;
import ois.radius.cc.entities.sys.Tenant;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vn.radius.cc.platform.events.Event;
import org.vn.radius.cc.platform.events.EventWithJSONSync;
import org.vn.radius.cc.platform.events.Event_Sync;
import org.vn.radius.cc.platform.requests.RequestWithJSON;
import org.vn.radius.cc.platform.requests.user.RequestUserRegister;
import org.vn.radius.cc.platform.server.ICCEventListener;

/**
 *
 * @author Manoj-PC
 * @since Mar 5, 2024
 */
public class DarkServer
{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String _hostName;

    private final int _portNo;

    private final ICCEventListener _evProc;

    private ObjectSocketChannel _osc;

    private CTKSrvrListnr _srvrListnr;
    private Thread _thListnr;

    private final HashMap<String, Thread> _hmSyncTh;

    /**
     * RequestId - Event XML
     */
    private final HashMap<String, Event> _hmSyncEv;

    /**
     * Tenant.Code-Dark_auth_code.
     */
    private final HashMap<String, String> _darkToken;

    /**
     * Create a Server object to connect and communicate with RADIUS Server.
     *
     * @param hostname
     * @param portno
     * @param listener
     * @throws CODEException when listener is found null.
     */
    public DarkServer(String hostname, int portno, ICCEventListener listener) throws CODEException
    {
        if (listener == null)
        {
            throw new CODEException("ICCEventListener can't be NULL.");
        }
        this._hostName = hostname;
        this._portNo = portno;
        this._evProc = listener;

        this._hmSyncTh = new HashMap<>();
        this._hmSyncEv = new HashMap<>();
        this._darkToken = new HashMap<>();
    }

    public void DarkTokenInit(Tenant tnt) throws GravityUnhandledException
    {
        try
        {
        if (IsConnected())
        {
            if (_darkToken.containsKey(tnt.getCode()))
            {
                logger.debug(tnt.getCode() + " is already initatied,So init process ignored...");
                return;
            }
            // send user req
            RequestUserRegister darkuserreq = new RequestUserRegister(UIDUtil.GenerateUniqueId());
            darkuserreq.setApplicationCode("DARK");
            darkuserreq.setForceLogoutActiveSessions(Boolean.FALSE);
            darkuserreq.setRemoteIP("192.168.60.155");
            darkuserreq.setToken(ServerContext.This().GetTenantCtxByCode(tnt.getCode()).getNucleusCtx().get_tenantToken());
            darkuserreq.setUserRole("Service");
            darkuserreq.setTenantCode(tnt.getCode());

            JSONObject obj = JSONUtil.ToJSON(darkuserreq);

            RequestWithJSON reqjson = new RequestWithJSON(UIDUtil.GenerateUniqueId());

            reqjson.setJSONPayload(obj.toString());

            Event event = ServerContext.This().get_darkServer().SendSyncRequest(reqjson);
            if (event.getEvType().equals(org.vn.radius.cc.platform.events.EventType.Success))
            {
                JSONObject eventjson = JSONUtil.ToJSON(event);
                if (eventjson.has("AccessToken"))
                {
                    _darkToken.put(tnt.getCode(), eventjson.getString("AccessToken"));
                    logger.debug(tnt.getCode() + " is initatied...");
                }
            }

        }
        }
        catch(Throwable ex)
        {
            throw  new GravityUnhandledException(ex);
        }
    }
    
    public String getDarkToken(String tntcode) throws GravityUnhandledException
    {
        Tenant tnt = NucleusServerContext.This().GetTenantByCode(tntcode);
        if(!_darkToken.containsKey(tnt.getCode()))
        {
            DarkTokenInit(tnt);
        }
        return _darkToken.get(tntcode);
        
    }

    public void DarkTokenDeInit(Tenant tnt)
    {

        Iterator<Map.Entry<String, String>> iterator = _darkToken.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();

            if (key.equalsIgnoreCase(tnt.getCode()))
            {
                iterator.remove(); // Safe removal
            }
        }

    }

    /**
     * Connect to RADIUS Server
     *
     * @return
     * @throws CODEException in case connection failed due to some IOException.
     * @throws java.lang.Exception
     */
    public boolean Connect() throws CODEException, Exception
    {
        try
        {
            this._osc = new ObjectSocketChannel();
            this._srvrListnr = new CTKSrvrListnr(_osc, _evProc, _hmSyncTh, _hmSyncEv);

            _osc.Connect(_hostName, _portNo);
            _osc.ConfigureBlocking(Boolean.TRUE);

        }
        catch (IOException ioex)
        {
            throw new CODEException("Unable to connect RADIUS Server.", ioex);
        }

        if (!_osc.IsConnected())
        {
            return false;
        }

        /**
         * _evProc can't be NULL in any case. A client must have a listener.
         */
        _evProc.OnServerConnected();

        _thListnr = new Thread(_srvrListnr);
        _thListnr.setName("Th_RAD_CC_Server_Listener-" + _osc.GetLocalPort());
        _thListnr.start();

        return true;

    }

    public boolean IsConnected()
    {
        if (_osc == null)
        {
            return false;
        }
        return _osc.IsConnected();
    }

    public void Close() throws Exception
    {
        doClose();
    }

    void doClose()
    {
        /**
         * Notify all threads waiting for response, with NULL response
         */
        Set<String> arr = (Set) _hmSyncTh.keySet();
        Iterator iterator = arr.iterator();
        while (iterator.hasNext())
        {
            Thread tmpTh = null;
            String reqId = (String) iterator.next();
            if (_hmSyncTh.containsKey(reqId) && (tmpTh = _hmSyncTh.get(reqId)) != null)
            {
                synchronized (tmpTh)
                {
                    logger.trace("Notifying thread " + tmpTh.getName() + ", as its waiting for RequestId " + reqId);
                    tmpTh.notifyAll();
                }
            }
        }

        _evProc.OnServerClosed();

        _hmSyncEv.clear();
        _hmSyncTh.clear();

        _osc.Close();
        _srvrListnr.Stop();

        /**
         * Send a Asynchronous message for server disconnection Not Implemented yet
         */
    }

    private static Long _maxReqTimeout = 128 * 1000L;

    /**
     * Send Synchronous Request wait until event is received for this request.
     *
     * @param request
     * @return Event XML
     * @throws Exception
     * @throws code.socket.exceptions.CODEObjectSerializationFailedException
     * @throws code.socket.exceptions.CODESocketWriteFailedException
     */
    public Event SendSyncRequest(org.vn.radius.cc.platform.requests.Request request) throws Throwable
    {
        if (Thread.currentThread().getName().equals(_thListnr.getName()))
        {
            throw new Exception("Can't send sync request using listener thread.");
        }

        //V:271121 . In case duplicate Id fournd for request, throw this exception.
        if (_hmSyncTh.containsKey(request.getReqId()))
        {
            throw new IllegalArgumentException("Another sync request with RequestId: " + request.getReqId() + " is already in process.");
        }

        // No need to alter duplicate request id, if we do so the napplication may not get actual Id hey have sent.
        // _idChker.AssertUnique(request);
        String reqId = request.getReqId();
        _hmSyncTh.put(reqId, Thread.currentThread());

        synchronized (Thread.currentThread())
        {
            WriteRequest(request);

            try
            {
                Thread.currentThread().wait(_maxReqTimeout);
            }
            catch (InterruptedException e)
            {
            }
        }

        Event event = _hmSyncEv.get(reqId);
        //When Sync event received. i.e. 1st event having same request id.
        // any further event containing same reqId will treated as async event.
        _hmSyncTh.remove(reqId);

        if (event instanceof EventWithJSONSync evjsn)
        {
            event = evjsn.getJSONPayload();
        }
        logger.trace(event + " returned to Gravity...");
        return event;
    }

    /**
     * Send a request to server.
     *
     * V.28032018.1 <br>
     * Uniqueness of Id checking included.
     *
     * @param request
     * @throws java.lang.Exception
     *
     */
    public void SendRequest(org.vn.radius.cc.platform.requests.Request request) throws Throwable
    {
//        _idChker.AssertUnique(request);
        WriteRequest(request);
    }

    private void WriteRequest(org.vn.radius.cc.platform.requests.Request request) throws Throwable
    {
        try
        {
            if (!IsConnected())
            {
                throw new IllegalStateException("Socket channel not connected for " + _hostName);
            }

            logger.debug("Gravity >> Dark : " + request);
            _osc.Write(request);
        }
        catch (Throwable ex)
        {
            //logger.error(ex.getMessage(), ex);
            //handle any disconnections here and do appropriate action.

            /**
             * V:081221 - We decided to throw exception from here. <br>
             * If we don't throw from here, application still feels its up and keep sending request. This happens if application doesn't handle the onClose
             * event appropriately. <br>
             * Handling of specific exception wrt IO is anyway done at ObjectSocketChannel.
             */
            throw ex;
        }
    }

    class CTKSrvrListnr implements Runnable
    {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private ObjectSocketChannel _osc;

        private ICCEventListener _evProc;

        private HashMap<String, Thread> _hmSyncTh;

        private HashMap<String, Event> _hmSyncEv;

        private boolean _isRun;

        public CTKSrvrListnr(ObjectSocketChannel appsock, ICCEventListener evproc, HashMap hmsyncth, HashMap hmsyncev)
        {
            this._osc = appsock;
            this._evProc = evproc;
            this._isRun = true;
            this._hmSyncTh = hmsyncth;
            this._hmSyncEv = hmsyncev;

        }

        /**
         */
        @Override
        public void run()
        {
            while (_isRun)
            {
                if (!_osc.IsConnected())
                {
                    _isRun = false;

                    logger.trace("Initiating Close as Server socket disconnected.");
                    doClose();
                }
                else
                {
                    Event event = null;
                    try
                    {
                        logger.debug("waiting for event...");
                        event = (Event) _osc.ReadChannel();
                        logger.debug("Gravity << Dark : " + event);
                        if (event == null)
                        {
                            continue; //do nothing. any new disconnection will be handled by _osc.IsConnected()
                        }

                        //If we receive Sync event, then check for breack sync request.
                        if (event instanceof Event_Sync)
                        {
                            Event_Sync evSyn = (Event_Sync) event;
                            String reqId = evSyn.getRequestId();

                            logger.trace(LOGUtil.ArgString(reqId));

                            Thread tmpTh = null;
                            if (_hmSyncTh.containsKey(reqId) && (tmpTh = _hmSyncTh.get(reqId)) != null)
                            {
                                logger.trace(evSyn + "  found for Request{" + reqId + "}");
                                synchronized (tmpTh)
                                {
                                    _hmSyncEv.put(reqId, event);
                                    tmpTh.notifyAll();
                                    logger.trace("Event" + evSyn + " thread notified...");
                                }
                                //When Sync event received. i.e. 1st event having same request id.
                                //  on receive of 1st event we remove the reqId frm _hmSyncEv in SendSyncRequest() method
                                //  any further event containing same reqId will treated as async event.
                            }
                            else
                            {
                                logger.trace("No Sync event found for Request{" + reqId + "} so processed as async event...");
                                _evProc.OnEventReceived(event);
                            }
                        }
                        else
                        {
                            logger.trace("Async event" + event + " received.");
                            _evProc.OnEventReceived(event);
                        }

                    }
                    catch (Exception ex)
                    {
                        logger.error(ex.getMessage(), ex);

                        _evProc.OnException(ex);
                    }
                }
            }
        }

        public void Stop()
        {
            _isRun = false;
        }

    }

    public HashMap<String, String> get_darkToken()
    {
        return _darkToken;
    }
}
