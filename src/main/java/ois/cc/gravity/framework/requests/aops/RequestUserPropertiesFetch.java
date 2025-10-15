package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.radius.cc.entities.EN;

public class RequestUserPropertiesFetch extends RequestEntityFetch
{

    @Param(Optional = false)
    private String User;


    public RequestUserPropertiesFetch(String requestid)
    {
        super(requestid, EN.UserProperties);
    }

    public String getUser()
    {
        return User;
    }

    public void setUser(String user)
    {
        User = user;
    }


}
