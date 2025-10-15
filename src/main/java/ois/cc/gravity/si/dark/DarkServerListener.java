/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.si.dark;

import code.common.exceptions.CODEException;
import ois.cc.gravity.AppProps;
import ois.cc.gravity.context.ServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vn.radius.cc.platform.events.Event;
import org.vn.radius.cc.platform.server.ICCEventListener;

/**
 * @author Manoj-PC
 * @since Mar 5, 2024
 */
public class DarkServerListener implements ICCEventListener {

    private final Logger _logger = LoggerFactory.getLogger(getClass());
    private DarkServer _darkServer;

    public DarkServerListener() {

    }

    @Override
    public void OnServerConnected() {
        _logger.info("Connected to CCServer...");

    }

    @Override
    public void OnServerClosed() {
        _logger.info("CCServer connection closed...");

        Thread reconnectThread = new Thread(() -> {
            while (true) {
                try {
                    _logger.info("Attempting to reconnect to CCServer...");

                    // Your actual reconnect logic here
                    boolean connected = attemptReconnect();

                    if (connected) {
                        _logger.info("Reconnected to CCServer successfully.");
                        break;
                    } else {
                        _logger.warn("Reconnect attempt failed. Retrying...");
                    }
                    Thread.currentThread().sleep(5000);

                } catch (Exception | CODEException e) {
                    _logger.error("Reconnect attempt threw an exception", e);
                }
            }
        });

        reconnectThread.setName("CCServer-Reconnect-Thread");
        reconnectThread.start();
    }

    private boolean attemptReconnect() throws CODEException, Exception {
        _darkServer = ServerContext.This().get_darkServer();

        _darkServer = new DarkServer(AppProps.RAD_Dark_IP, AppProps.RAD_Dark_Port, new DarkServerListener());
        this._darkServer.Connect();

        _logger.debug("Simulating reconnect logic...");
        return true;
    }

    @Override
    public void OnEventReceived(Event ev) {
        try {
            //this is for send asunc events to the UAs.
            _logger.info("Gravirt << Dark : " + ev);
        } catch (Throwable ex) {
            _logger.error(ex.getMessage(), ex);
        }

    }

    @Override
    public void OnException(Exception e) {
        _logger.error(e.getMessage(), e);
    }

//    @Override
//    public void OnEventSyncThreadNotFound(Event_Sync es, String string)
//    {
//        _logger.debug("No sync event found for request id =: " + string);
//    }
//
//    @Override
//    public void OnException(Throwable thrwbl)
//    {
//        _logger.error(thrwbl.getMessage(), thrwbl);
//    }
}
