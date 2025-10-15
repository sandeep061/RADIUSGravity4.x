package ois.cc.gravity.ua.rest;

import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * DTO object used to transfer data from Interceptor to Controller.
 *
 * @author suman
 */
@Component
public class RESTRequestDTO
{

    private EN EN;
    private UAClient UAClient;
    private JSONObject ReqJson;
    private String ReqId;

    public ois.radius.cc.entities.EN getEN()
    {
        return EN;
    }

    public void setEN(ois.radius.cc.entities.EN EN)
    {
        this.EN = EN;
    }

    public ois.cc.gravity.ua.UAClient getUAClient()
    {
        return UAClient;
    }

    public void setUAClient(ois.cc.gravity.ua.UAClient UAClient)
    {
        this.UAClient = UAClient;
    }

    public JSONObject getReqJson()
    {
        return ReqJson;
    }

    public void setReqJson(JSONObject reqJson)
    {
        ReqJson = reqJson;
    }

    public String getReqId() {
        return ReqId;
    }

    public void setReqId(String reqId) {
        ReqId = reqId;
    }
}
