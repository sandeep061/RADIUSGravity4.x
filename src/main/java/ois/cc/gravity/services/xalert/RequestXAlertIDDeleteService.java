package ois.cc.gravity.services.xalert;

import CrsCde.CODE.Common.Classes.NameValuePair;
import code.db.jpa.ENActionList;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityDeleted;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.OIAlertConfigQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityDelete;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XAlertID;
import ois.radius.cc.entities.tenant.oi.OIAlertConfig;

import java.util.ArrayList;
import java.util.HashSet;

public class RequestXAlertIDDeleteService extends ARequestEntityService
{

    public RequestXAlertIDDeleteService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        ArrayList<NameValuePair> entities = new ArrayList<>();
        RequestEntityDelete req = (RequestEntityDelete) request;
        //find the xalertid
        XAlertID xAlertID = _tctx.getDB().FindAssert(EN.XAlertID.getEntityClass(), req.getEntityId());

        //get all oiAlertConfig mapped with xalertid
        ArrayList<OIAlertConfig> oiConfigsalerts = (ArrayList<OIAlertConfig>) _tctx.getDB().Select(new OIAlertConfigQuery().filterByXALertsJPA(xAlertID.getId()));

        for (OIAlertConfig oiAlertConfig : oiConfigsalerts)
        {
            if (oiAlertConfig.getXAlertIDs() != null)
            {
                oiAlertConfig.getXAlertIDs().remove(xAlertID);
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), oiAlertConfig));
            }
        }
        entities.add(new NameValuePair(ENActionList.Action.Delete.name(), xAlertID));

        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);
        EventEntityDeleted ev = new EventEntityDeleted(req, xAlertID);
        return ev;
    }
}
