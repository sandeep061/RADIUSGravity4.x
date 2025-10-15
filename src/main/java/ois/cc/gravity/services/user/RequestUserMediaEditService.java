package ois.cc.gravity.services.user;

import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.ua.events.EventFailedCause;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.tenant.cc.AgentMediaMap;
import ois.radius.cc.entities.tenant.cc.UserMedia;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RequestUserMediaEditService extends RequestEntityEditService
{
    private static Logger logger = LoggerFactory.getLogger(RequestUserMediaEditService.class);


    public RequestUserMediaEditService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected void DoPostBuildProcess(RequestEntityEdit req, AEntity thisentity) throws Throwable
    {
        UserMedia agMd = (UserMedia) thisentity;

        List<AgentMediaMap> agentMediaMaps = agMd.getAgentMediaMaps();
        if (agentMediaMaps.isEmpty())
        {
            return;
        }

        if(req.getAttributes()!=null&&req.getAttributes().containsKey("AutoRegAgentMedia"))
        {
            Long agMdMapId = req.getAttributeValueOf(Long.class, "AutoRegAgentMedia");
            if (agMdMapId != null)
            {
                if (agentMediaMaps.stream().noneMatch(mp -> mp.getId().equals(agMdMapId))) {
                    throw new GravityIllegalArgumentException("AgentMediaMap is not mapped yet...", "AutoRegAgentMedia", EventFailedCause.ValueOutOfRange);
                }
                if (agMd.getAutoRegister() && agMd.getAutoRegAgentMedia() == null) {
                    throw new GravityIllegalArgumentException("AutoRegAgentMedia can't be Null as AutoRegister is enabled...");
                }
            }
        }

    }

    @Override
    protected void DoPostUpdateProcess(RequestEntityEdit reqenedit, AEntity entity) throws Throwable
    {
        UserMedia agMd = (UserMedia) entity;
        _tctx.getDB().Update(_uac.getUserSession().getUser(),agMd);
    }

    @Override
    protected void appendAttribute(RequestEntityEdit req, AEntity entity)throws CODEException, GravityException
    {
        UserMedia am = (UserMedia) entity;
        ArrayList<AgentMediaMap> amms = new ArrayList<>();
        ArrayList<String> AgentMediaMapsId = req.getAttributeCollectionAppend().get("AgentMediaMaps");
        for (String id : AgentMediaMapsId)
        {
            Long amid = Long.valueOf(id);

            AgentMediaMap amp = _tctx.getDB().FindAssert(AgentMediaMap.class, amid);
            amms.add(amp);
        }
        am.setAgentMediaMaps(amms);
    }

    @Override
    protected void removeAttribute(RequestEntityEdit req, AEntity entity) throws NoSuchFieldException, Exception, CODEException, GravityException
    {
        UserMedia am = (UserMedia) entity;
        ArrayList<String> AgentMediaMapsId = req.getAttributeCollectionRemove().get("AgentMediaMaps");
        for (String id : AgentMediaMapsId)
        {
            Long amid = Long.valueOf(id);

            AgentMediaMap amp = _tctx.getDB().FindAssert(AgentMediaMap.class, amid);
            am.getAgentMediaMaps().remove(amp);
        }

    }
}
