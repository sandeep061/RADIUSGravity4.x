package ois.cc.gravity.ua;

import CrsCde.CODE.Common.Utils.DATEUtil;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.sys.Tenant;
import ois.radius.cc.entities.tenant.cc.UserSession;
import ois.cc.gravity.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class UAClient
{

    private final Logger _logger = LoggerFactory.getLogger(getClass());

    /**
     * Object lock.
     */
    final ReentrantLock _lock = new ReentrantLock();

    /**
     * an unique number to represent an UAClient in logs etc..
     */
    private final String _id;

    /**
     * Will be set in Login request.
     */
    private Tenant _tenant;

    private TenantContext _tCtx;

    private UserSession _sess;

    private Date _createdOn;

    private final String _gravity_auth_token;

    private String _dark_auth_token;

//    /**
//     * Reader Thread
//     */
//    RtThread rdrTh;
    UAClient(TenantContext tctx, String accss_token, UserSession usrsess)
    {
        this._tCtx = tctx;
        this._tenant = this._tCtx.getTenant();
        this._sess = usrsess;
        this._id = _sess.getSessionId();
        this._createdOn = DATEUtil.Now();
        this._gravity_auth_token = accss_token;
    }

    public String getId()
    {
        return _id;
    }

    public String getToken()
    {
        return _gravity_auth_token;
    }

    public String getDarkToken()
    {
        return _dark_auth_token;
    }

    public void setDark_auth_token(String _dark_auth_token)
    {
        this._dark_auth_token = _dark_auth_token;
    }

    
//
//    /**
//     * Start reading client request.
//     *
//     * @throws Exception
//     */
//    void Start() throws IOException
//    {
//        _selector = SelectorProvider.provider().openSelector();
//        SelectionKey sk = _sockChan.register(_selector, SelectionKey.OP_READ);
//
//        rdrTh = new RtThread(new RtThUAServerReader(getId(), _selector, this));
//        rdrTh.Start();
//    }
//    /**
//     * Stop UAClient. <br>
//     * i.e User LoggedOut or Disconnected, final termination of the server-client connection. <br>
//     * This method must be the last method of the entire session. <br>
//     *
//     * This method must be protected so other processors shall not able to invoke it. <br>
//     *
//     * Refer wikidoc for more.
//     *
//     */
//    void Stop()
//    {
//        /**
//         * Wakeup the thread in case selector is waiting, then close the selector.
//         */
//        try
//        {
//            _selector.close();
//        }
//        catch (IOException iex)
//        {
//
//        }
//
//        /**
//         * Stop Reader Thread.
//         */
//        rdrTh.Stop_Wait();
//
//        _osc.Close();
//    }
//    public Boolean IsConnected()
//    {
//        return _osc.IsConnected();
//    }
//
//    synchronized public Request Read() throws ClassNotFoundException, IOException, Exception
//    {
//        Request request = (Request) _osc.ReadChannel();
//
//        if (_logger.isDebugEnabled())
//        {
//            try
//            {
//                _logger.debug(toString() + " -> " + (request == null ? "NULL" : JSONUtil.ToJSON(request)));
//            }
//            catch (Exception ex)
//            {
//                _logger.error(ex.getMessage(), ex);
//            }
//        }
//        else
//        {
//            _logger.info(toString() + " -> " + (request == null ? "NULL" : request.toString()));
//        }
//
//        return request;
//    }
//
//    synchronized public void Write(Event event)
//    {
//        if (event == null)
//        {
//            return;
//        }
//
//        if (_logger.isDebugEnabled())
//        {
//            try
//            {
//                _logger.debug(toString() + " <- " + JSONUtil.ToJSON(event));
//            }
//            catch (Exception ex)
//            {
//                _logger.error(ex.getMessage(), ex);
//            }
//        }
//        else
//        {
//            _logger.info(toString() + " <- " + event.toString());
//        }
//
//        try
//        {
//            _osc.Write(event);
//        }
//        catch (CODEObjectSerializationFailedException | CODESocketWriteFailedException | Exception ioex)
//        {
//            //This exception must be handled carefully here only. since the invoke class may not be aware of how to handle such exception.
//            _logger.error(ioex.getMessage(), ioex);
//            //? need to write further logic here.
//
//        }
//    }
    public Tenant getCtClient()
    {
        return _tenant;
    }

    public void setCtClient(Tenant _tenant)
    {
        this._tenant = _tenant;

    }

    public TenantContext getTenantContext()
    {
        return _tCtx;
    }

    public void setCCtx(TenantContext _tCtx)
    {
        this._tCtx = _tCtx;
    }

    public UserSession getUserSession()
    {
        return _sess;
    }

    public void setUserSession(UserSession _sess)
    {
        this._sess = _sess;
    }

    public Date getCreatedOn()
    {
        return _createdOn;
    }

    /**
     * Return User.Id for user part of this UAC.
     *
     * @return NULL if no UserSession associated with this UAC.
     */
    public Long getUserId()
    {
        if (_sess != null && _sess.getUser() != null)
        {
            return _sess.getUser().getId();
        }

        return null;
    }

    public UserRole getUserType()
    {
        if (_sess != null && _sess.getUser() != null)
        {
            return _sess.getUserRole();
        }

        return null;
    }

//    /**
//     * V:291021.  <br>
//     * If UAC alredy added in DisconnectedHandler then no need to add again. <br>
//     * Implemented lock object so that first disconnect steps will be over before any other reason also arised. <br>
//     */
//    private UserSession.EndReason _uacDisconnectReason;
//
//    //Common steps for all disconnect scenarios
//    private void doUACDisconnect(UserSession.EndReason reason, AgentStateReason asr)
//    {
//        _logger.trace(LOGUtil.ArgString(reason, asr));
//
//        LockObject();
//
//        try
//        {
//            if (_uacDisconnectReason != null)
//            {
//                _logger.info("UACDisconnect of " + toString() + " already handeled for Reason : " + _uacDisconnectReason);
//                return;
//            }
//            _uaServer.HandleUACDisconnect(this, reason, asr);
//            _uacDisconnectReason = reason;
//        }
//        finally
//        {
//            UnlockObject();
//        }
//
//    }
//    /**
//     * Will be invoked when client's socket got disconnected from remote end abruptly.
//     */
//    void ClientDisconnected()
//    {
//        /**
//         * Is there any usersession created for this socket connection ? <br>
//         * - there may be a UA just connected, but never sent any request, in that case no usersession will be created. <br>
//         * - for these disconnection we need not process to handle disconnect session. <br>
//         */
//
//        if (_sess != null)
//        {
//            doUACDisconnect(UserSession.EndReason.ClientDisconnected, null);
//        }
//    }
//
//    void UserLoggedOut(AgentStateReason asr)
//    {
//        doUACDisconnect(UserSession.EndReason.UserLoggedOut, asr);
//    }
//
//    public void ForcedLoggedOut()
//    {
//        doUACDisconnect(UserSession.EndReason.ForcedLoggedOut, null);
//    }
//
//    public void DisconnectAnonymousClient()
//    {
//        doUACDisconnect(UserSession.EndReason.AnonymousClientTimeout, null);
//    }
//
//    /**
//     * To disconnect client explicitly by server.
//     */
//    void DisconnectClient()
//    {
//        doUACDisconnect(UserSession.EndReason.ServerStopped, null);
//    }
    public void LockObject()
    {
        _logger.trace("Trying to get object lock for " + toString());
        _lock.lock();
        _logger.trace("Got object lock for " + toString());
    }

    public void UnlockObject()
    {
        _lock.unlock();
        _logger.trace("Released object lock for " + toString());
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final UAClient other = (UAClient) obj;
        return Objects.equals(this._id, other._id);
    }

    @Override
    public String toString()
    {
        return "UAClient{"
                + "Tenant=" + _tenant.getCode()
                + ", User=" + _sess.getUser().getUserId()
                + ", SessionId='" + _id + '\''
                + '}';
    }
}
