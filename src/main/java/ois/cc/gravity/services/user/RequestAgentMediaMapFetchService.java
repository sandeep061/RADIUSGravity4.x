package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.PWDUtil;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.AgentMediaMapQuery;
import ois.cc.gravity.entities.util.AppUtil;
import ois.cc.gravity.framework.events.xs.EventAgentMediaMapFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.OAgentMediaMap;
import ois.cc.gravity.objects.OTerminal;
import ois.cc.gravity.objects.OXServer;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AgentMediaMap;
import ois.radius.cc.entities.tenant.cc.Terminal;
import ois.radius.cc.entities.tenant.cc.XServer;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;

public class RequestAgentMediaMapFetchService extends ARequestEntityService {
    public RequestAgentMediaMapFetchService(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {
        RequestEntityFetch reqFetch = (RequestEntityFetch) request;
        EN en = reqFetch.getEntityName();

        if (en == null) {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqFetch.getEntityName().name());
            return ev;
        }

        try {
            AgentMediaMapQuery enQry = new AgentMediaMapQuery();
            if (reqFetch.getFilters() != null) {
                enQry.doApplyFilters(reqFetch.getFilters());
            }
            enQry.ApplyOrderBy(reqFetch.getOrderBy());

            Integer recCount = null;

            Boolean reqcnt = reqFetch.getIncludeCount() != null && reqFetch.getIncludeCount();

            if (reqcnt) {
                recCount = _tctx.getDB().SelectCount(enQry);

            }
            if (reqFetch.getLimit() != null) {
                enQry.setLimit(reqFetch.getLimit());
            }

            if (reqFetch.getOffset() != null) {
                enQry.setOffset(reqFetch.getOffset());
            }

            JPAQuery ctq = enQry.toSelect();


            ArrayList<AgentMediaMap> agentmediamaps = _tctx.getDB().Select(getClass(), ctq);
            ArrayList<OAgentMediaMap> oAgentMediaMaps = new ArrayList<>();
            for (AgentMediaMap agentmediamap : agentmediamaps) {

                OAgentMediaMap omap= BuildOAgentMediaMap(agentmediamap);
                System.out.println(AppUtil.Decrypt(omap.getTerminal().getPassword()));
                oAgentMediaMaps.add(omap);

            }


            EventAgentMediaMapFetched ev = new EventAgentMediaMapFetched(request);
            ev.setEntities(oAgentMediaMaps);
            ev.setRecordCount(recCount);
            return ev;
        } catch (GravityException rex) {
            return BuildExceptionEvents(reqFetch, rex);
        }


    }

    protected Event BuildExceptionEvents(RequestEntityFetch reqfetch, GravityException rex) throws GravityNoSuchFieldException, GravityException {
        if (rex instanceof GravityNoSuchFieldException) {
            GravityNoSuchFieldException fex = (GravityNoSuchFieldException) rex;
            throw fex;
        } else {
            throw rex;
        }
    }

    private OAgentMediaMap BuildOAgentMediaMap(AgentMediaMap mediaMap) throws Exception {

        OAgentMediaMap omediaMap = new OAgentMediaMap();
        omediaMap.setAuthParams(BuildAuthParams(mediaMap.getAuthParams()));
        omediaMap.setEndPointType(mediaMap.getEndPointType());
        omediaMap.setId(mediaMap.getId());
        omediaMap.setTerminal(BuildOTerminal(mediaMap.getTerminal()));
        omediaMap.setXServer(BuildOXserver(mediaMap.getXServer()));
       return omediaMap;
    }

    private String BuildAuthParams(String props) throws Exception {
        Properties authprops = JSONUtil.FromJSON(props, Properties.class);
        if (authprops != null || !authprops.isEmpty())
        {
            String key = "Password";
            if(authprops.containsKey(key))
            {
                String pwd = authprops.getProperty(key);
                if(!pwd.isEmpty()&& !pwd.startsWith("*>_"))
                {
                    authprops.put(key, AppUtil.Encrypt(pwd));
                }
            }

        }
        return new JSONObject(authprops).toString();

    }

    private OTerminal BuildOTerminal(Terminal term) throws  NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if(term!=null){
        OTerminal oterm=new OTerminal();
        oterm.setAddress(term.getAddress());
        oterm.setChannel(term.getChannel());
        oterm.setCode(term.getCode());
        oterm.setPassword(AppUtil.Encrypt(term.getPassword()));
        oterm.setLoginId(term.getLoginId());
        oterm.setId(term.getId());
       return oterm;
        }
        else {
            return null;
        }
    }

    private OXServer BuildOXserver(XServer xs)  {

        if(xs!=null) {
            OXServer oxs = new OXServer();
            oxs.setProviderID(xs.getProviderID());
            oxs.setCode(xs.getCode());
            oxs.setDescription(xs.getDescription());
            oxs.setName(xs.getName());
            oxs.setId(xs.getId());
            oxs.setEndPointTypeProps(xs.GetAllEndpointProps());
            return oxs;
        }
        else{
            return null;
        }
    }
}

