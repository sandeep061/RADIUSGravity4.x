package ois.cc.gravity.framework.events.user;

import code.ua.events.EventOK;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.radius.cc.entities.tenant.cc.UserProperties;

import java.util.HashMap;

public class EventUserPropertiesFetched extends EventOK
{
    private HashMap<UserProperties.Keys,String> UserProperties ;
    public EventUserPropertiesFetched(Request request)
    {
        super(request, EventCode.UserPropertiesFetched);
        this.UserProperties = new HashMap<>();
    }

    public HashMap<UserProperties.Keys, String> getUserProperties()
    {
        return UserProperties;
    }

    public void setUserProperties(HashMap<UserProperties.Keys, String> userProperties)
    {
        UserProperties = userProperties;
    }
}
