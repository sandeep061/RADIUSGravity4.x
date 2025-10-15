package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.radius.cc.entities.EN;

public class RequestAOPsPropertiesFetch extends RequestEntityFetch
{

    @Param(Optional = false)
    private Long AOPsId;

    public RequestAOPsPropertiesFetch(String requestid)
    {
        super(requestid,EN.AOPsProperties);
    }

    public Long getAOPsId()
    {
        return AOPsId;
    }

    public void setAOPsId(Long AOPsId)
    {
        this.AOPsId = AOPsId;
    }
}
