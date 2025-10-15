package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.radius.cc.entities.EN;

public class RequestAOPsBFPropertiesFetch extends RequestEntityFetch
{

    @Param(Optional = false)
    private Long AOPsBFId;

    public RequestAOPsBFPropertiesFetch(String requestid)
    {
        super(requestid, EN.AOPsBFProperties);
    }

    public Long getAOPsBFId()
    {
        return AOPsBFId;
    }

    public void setAOPsBFId(Long AOPsBFId)
    {
        this.AOPsBFId = AOPsBFId;
    }
}
