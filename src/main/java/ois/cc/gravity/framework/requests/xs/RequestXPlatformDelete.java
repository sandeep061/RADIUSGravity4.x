package ois.cc.gravity.framework.requests.xs;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestXPlatformDelete extends Request
{
    @Param(Optional = false)
    protected Long EntityId;

    private Boolean ForceDelete;

    public RequestXPlatformDelete(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.XPlatformDelete);
    }

    public Boolean getForceDelete()
    {
        if(ForceDelete==null){
            return Boolean.FALSE;
        }
        return ForceDelete;
    }

    public void setForceDelete(Boolean forceDelete)
    {
        ForceDelete = forceDelete;
    }

    public Long getEntityId()
    {
        return EntityId;
    }

    public void setEntityId(Long entityId)
    {
        EntityId = entityId;
    }
}
