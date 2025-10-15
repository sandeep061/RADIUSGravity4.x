package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Utils.JSONUtil;
import CrsCde.CODE.Common.Utils.PWDUtil;
import code.db.jpa.JPAQuery;
import code.ua.events.Event;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.UserMediaQuery;
import ois.cc.gravity.entities.util.AppUtil;
import ois.cc.gravity.framework.events.xs.EventUserMediaFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.objects.OAgentMediaMap;
import ois.cc.gravity.objects.OTerminal;
import ois.cc.gravity.objects.OUserMedia;
import ois.cc.gravity.objects.OXServer;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AgentMediaMap;
import ois.radius.cc.entities.tenant.cc.Terminal;
import ois.radius.cc.entities.tenant.cc.UserMedia;
import ois.radius.cc.entities.tenant.cc.XServer;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RequestUserMediaFetchService extends ARequestEntityService {
    public RequestUserMediaFetchService(UAClient uac) {
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
            UserMediaQuery enQry = new UserMediaQuery();
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


            ArrayList<UserMedia> userMedias = _tctx.getDB().Select(getClass(), ctq);
            ArrayList<OUserMedia> oUserMedia = new ArrayList<>();
            for (UserMedia usermedia : userMedias) {

                oUserMedia.add(BuildUserMedia(usermedia));
            }


            EventUserMediaFetched ev = new EventUserMediaFetched(request);
            ev.setEntities(oUserMedia);
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


    private OUserMedia BuildUserMedia(UserMedia um) throws Exception {
        OUserMedia omedia = new OUserMedia();
        omedia.setUser(um.getUser());
        omedia.setAutoRegister(um.getAutoRegister());
        omedia.setAgentMediaMaps(BuildOAgentMediaMaps(um.getAgentMediaMaps()));
        omedia.setAutoRegAgentMedia(BuildOAgentMediaMap(um.getAutoRegAgentMedia()));
        omedia.setId(um.getId());
        omedia.setChannel(um.getChannel());
        return omedia;
    }

    private ArrayList<OAgentMediaMap> BuildOAgentMediaMaps(List<AgentMediaMap> mediamaplists) throws Exception {
        if (mediamaplists != null || !mediamaplists.isEmpty()) {
            ArrayList<OAgentMediaMap> oAgentMediaMaps = new ArrayList<>();
            for (AgentMediaMap map : mediamaplists) {
                OAgentMediaMap omap = new OAgentMediaMap();
                omap.setXServer(BuildOXserver(map.getXServer()));
                omap.setTerminal(BuildOTerminal(map.getTerminal()));
                omap.setAuthParams(BuildAuthParams(map.getAuthParams()));
                omap.setId(map.getId());
                omap.setEndPointType(map.getEndPointType());
                oAgentMediaMaps.add(omap);
            }
            return oAgentMediaMaps;
        } else {
            return null;
        }
    }

    private OAgentMediaMap BuildOAgentMediaMap(AgentMediaMap mediamaplist) throws Exception {
        if (mediamaplist != null) {
            OAgentMediaMap oAgentMediaMaps = new OAgentMediaMap();

            OAgentMediaMap omap = new OAgentMediaMap();
            omap.setXServer(BuildOXserver(mediamaplist.getXServer()));
            omap.setTerminal(BuildOTerminal(mediamaplist.getTerminal()));
            omap.setAuthParams(BuildAuthParams(mediamaplist.getAuthParams()));
            omap.setId(mediamaplist.getId());
            omap.setEndPointType(mediamaplist.getEndPointType());


            return oAgentMediaMaps;
        } else {
            return null;
        }
    }

    private OTerminal BuildOTerminal(Terminal term) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (term != null) {
            OTerminal oterm = new OTerminal();
            oterm.setAddress(term.getAddress());
            oterm.setChannel(term.getChannel());
            oterm.setCode(term.getCode());
            oterm.setPassword(AppUtil.Encrypt(term.getPassword()));
            oterm.setLoginId(term.getLoginId());
            oterm.setId(term.getId());
            return oterm;
        } else {
            return null;
        }
    }

    private OXServer BuildOXserver(XServer xs) throws Exception {

        if (xs != null) {
            OXServer oxs = new OXServer();
            oxs.setProviderID(xs.getProviderID());
            oxs.setCode(xs.getCode());
            oxs.setDescription(xs.getDescription());
            oxs.setName(xs.getName());
            oxs.setAuthParams(encryptauthparampassword(xs.getAuthParams()));
            oxs.setId(xs.getId());
            oxs.setEndPointTypeProps(xs.GetAllEndpointProps());
            return oxs;
        } else {
            return null;
        }
    }

    private String BuildAuthParams(String props) throws Exception {
        Properties authprops = JSONUtil.FromJSON(props, Properties.class);
        if (authprops != null || !authprops.isEmpty()) {
            String key = "Password";
            if (authprops.containsKey(key)) {

                String pwd = authprops.getProperty(key);
                if (!pwd.isEmpty() && !pwd.startsWith("*>_")) {
                    authprops.put(key, AppUtil.Encrypt(pwd));
                }
            }

        }
        return new JSONObject(authprops).toString();

    }

    private String encryptauthparampassword(Properties authparams) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {


        if (authparams != null || !authparams.isEmpty()) {

            String[] passwordKeys =
                    {
                            "CTRLPassword", "Cx_CTRL_Passwd","Password"
                    };

            for (String key : passwordKeys) {
                if (authparams.containsKey(key)) {
                    String pwd = authparams.getProperty(key);
                    if (pwd != null && !pwd.isEmpty()) {
                        if (!pwd.startsWith("*>_")) {
                            authparams.put(key, AppUtil.Encrypt(pwd));
                        }
                        return  authparams.toString();
                    }
                }
                return  authparams.toString();
            }
        }
        return null;
    }
}
