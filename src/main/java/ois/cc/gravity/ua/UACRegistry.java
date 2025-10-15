package ois.cc.gravity.ua;

import CrsCde.CODE.Common.Collections.DRHashMap;
import CrsCde.CODE.Common.Utils.LOGUtil;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.sys.Tenant;
import ois.radius.cc.entities.tenant.cc.UserSession;
import ois.cc.gravity.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UACRegistry
{

    private Logger _logger = LoggerFactory.getLogger(UACRegistry.class);

    private final Object _mutex = new Object();

    /**
     * Token-UAClient.
     */
    private final HashMap<String, UAClient> _hmTokenUAC;

    private final DRHashMap<UAClient, Tenant> _hmTenantUAC;

    private static UACRegistry _this;

    private UACRegistry()
    {
        _hmTokenUAC = new HashMap<>();
        _hmTenantUAC = new DRHashMap<>();
    }

    public static UACRegistry This()
    {
        if (_this == null)
        {
            _this = new UACRegistry();
        }
        return _this;
    }

    public void MapLoggedInUACs(UAClient uac, Tenant client)
    {
        synchronized (_mutex)
        {
            _hmTenantUAC.put(uac, client);
            _logger.debug(uac + " added in _hmClientUACMap.");
        }
    }

    public void UnmapLoggedInUACs(UAClient uac)
    {
        _logger.trace(LOGUtil.ArgString(uac));

        synchronized (_hmTenantUAC)
        {
            Tenant client = uac.getCtClient();
            if (_hmTenantUAC.containsKey(uac))
            {
                _hmTenantUAC.remove(uac);
                _logger.debug(uac + " removed successfully from _hmClientUACMap.");
            }
            else
            {
                _logger.warn(uac + " not found in : " + client);
            }
        }
    }

    public UAClient NewUAC(String token, TenantContext tcxt, UserSession usrsess)
    {
        UAClient uac = new UAClient(tcxt, token, usrsess);
        this.Put(token, uac);

        return uac;
    }

    /**
     * First and only method to add a new UAClient to registry.
     *
     * @param uac
     */
    private void Put(String token, UAClient uac)
    {
        synchronized (_mutex)
        {
            _hmTokenUAC.put(token, uac);
        }
    }

    public UAClient Get(String token)
    {
        synchronized (_mutex)
        {
            return _hmTokenUAC.get(token);
        }
    }

    /**
     * This methods will return list of anonymous UAClients( i.e. those socket connections for which there is no Usersession associated).
     *
     * @return
     */
//    public List<UAClient> findAllAnonymusUACs()
//    {
//        synchronized (_mutex)
//        {
//            return _hmSockMap.values().stream()
//                    .filter(uac -> DATEUtil.Diff(DATEUtil.Now(), uac.getCreatedOn(), DATEUtil.Unit.MILLISECOND) >= AppConst.UA_ANONYMOUS_CLIENT_DETECTION_DELAY
//                            && uac.getUserSession() == null)
//                    .collect(Collectors.toList());
//        }
//    }
//    public UAClient findBy_Client_Channel_TermAddress(Tenant client, Channel channel, String termaddr)
//    {
//        synchronized (_hmClientUACMap)
//        {
//            return findBy_Client(client)
//                    .stream()
//                    .filter(uac -> (uac.getUserSession() != null && uac.getUserSession().getUserMediaSession(channel) != null
//                            && uac.getUserSession().getUserMediaSession(channel).getAddress().equals(termaddr)))
//                    .findAny()
//                    .orElse(null);
//        }
//    }
    public List<UAClient> findBy_Client_UserType(Tenant tenant, UserRole userole)
    {
        synchronized (_hmTenantUAC)
        {
            return findBy_Client(tenant)
                    .stream()
                    .filter(uac -> uac.getUserSession() != null && uac.getUserType().equals(userole))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Find an existing UAC object for a loginid for specific client.
     *
     * @param tenant
     * @param loginid
     * @return
     */
    public UAClient findBy_Client_LoginId(Tenant tenant, String loginid)
    {
        synchronized (_hmTenantUAC)
        {
            return findBy_Client(tenant)
                    .stream().filter(uac -> uac.getUserSession() != null && uac.getUserSession().getUser().getLoginId().equals(loginid))
                    .findAny().orElse(null);
        }

    }

    /**
     * Find all existing UAC object for a loginid for specific client. <br>
     * - This is applicable for only Admin uacs as we have multiple user session for single admin user.
     *
     * @param tenant
     * @param loginid
     * @return
     */
    public List<UAClient> findAllBy_Client_LoginId(Tenant tenant, String loginid)
    {
        synchronized (_hmTenantUAC)
        {
            return findBy_Client(tenant)
                    .stream()
                    .filter(uac -> uac.getUserSession() != null && uac.getUserSession().getUser().getLoginId().equals(loginid))
                    .collect(Collectors.toList());
        }

    }

    /**
     * Find list of UAC connected for this client.
     *
     * @param tenant
     * @return
     */
    public Set<UAClient> findBy_Client(Tenant tenant)
    {
        synchronized (_hmTenantUAC)
        {
            Set<UAClient> loggedInUACs = _hmTenantUAC.keySet(tenant);
            return loggedInUACs == null ? new HashSet<>() : loggedInUACs;
        }
    }

    public void ClearUACsOnTenantStop(Tenant client)
    {
        synchronized (_hmTenantUAC)
        {
            Set<UAClient> uacs = _hmTenantUAC.keySet(client);
            if (uacs == null)
            {
                _logger.trace("No UAClient found for Client : " + client);
                return;
            }
//            uacs.forEach(uac ->
//            {
//                uac.ClientDisconnected();
//            });

        }
    }

    public void Remove(UAClient uac)
    {
        if (uac == null)
        {
            return;
        }
        synchronized (_mutex)
        {
//            if (uac.getSockChan() != null)
//            {
//                _hmSockMap.remove(uac.getSockChan());
//            }
        }
    }
}
