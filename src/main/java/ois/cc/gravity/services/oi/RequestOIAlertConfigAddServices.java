package ois.cc.gravity.services.oi;

import code.common.exceptions.CODEException;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.requests.Request;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.XAlertID;
import ois.radius.cc.entities.tenant.oi.OIAlertConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RequestOIAlertConfigAddServices extends ARequestEntityService {
    public RequestOIAlertConfigAddServices(UAClient uac) {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable {

        RequestEntityAdd req= (RequestEntityAdd) request;

        OIAlertConfig oiAlertConfig = BuildOIAlerts(req);

        _tctx.getDB().Insert(_uac.getUserSession().getUser(),oiAlertConfig);

        EventEntityAdded ev=new EventEntityAdded(req,oiAlertConfig);

        return ev;
    }

    private OIAlertConfig BuildOIAlerts(RequestEntityAdd req) throws Exception, CODEException, GravityException {

        XAlertID xAlertID=null;
        Boolean InApp=null;


        if(req.getAttributes().containsKey("XAlertID")){
            xAlertID= _tctx.getDB().FindAssert(EN.XAlertID.getEntityClass(),req.getAttributeValueOf(Long.class,"XAlertID"));
        }
        if(req.getAttributes().containsKey("InApp")){
         InApp=req.getAttributeValueOf(Boolean.class,"XAlertID");
        }
        OIAlertConfig allerts=new OIAlertConfig();
//        allerts.setXAlertID(xAlertID);
        if (req.getAttributes().containsKey("Users"))
        {
           allerts.setUsers(getUsers(req));
        }
        allerts.setInApp(InApp);
        return allerts;
    }

    private ArrayList<User> getUsers(RequestEntityAdd req) throws Exception, CODEException, GravityException {
      ArrayList<User>userlist=new ArrayList<>();

        String ids=req.getAttributeValueOf(String.class,"Users");
        ArrayList<String> userids = getusersId(ids);
        for (String userid:userids){
         User user=_tctx.getDB().FindAssert(new UserQuery().filterByUserId(userid));
        userlist.add(user);
        }
         return userlist;
    }
    private ArrayList<String> getusersId(String ids) {
        String[] metricsids = ids.split(",");
        return (ArrayList<String>) Arrays.asList(metricsids).stream().collect(Collectors.toList());
    }
}
