package ois.cc.gravity.framework.requests.common;

import code.ua.requests.Param;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.requests.RequestAbase;

public class RequestEntityDelete extends RequestAbase
{

    /**
     * Name of the entity to be deleted.
     */
    @Param(Optional = false)
    private EN EntityName;
    /**
     * Id of the entity to be deleted.
     */
    @Param(Optional = false)
    protected Long EntityId;

    /**
     * @param requestid  Unique Id of the request
     * @param entityname Name of the entity to be deleted
     * @param entityid   Id of the entity to be deleted
     */
    public RequestEntityDelete(String requestid, EN entityname, Long entityid)
    {
        super(requestid,
                GReqType.Config,
                GReqCode.EntityDelete);

        this.EntityName = entityname;
        this.EntityId = entityid;
    }

    public EN getEntityName()
    {

        return EntityName;
    }

    public void setEntityName(EN EntityName)
    {

        this.EntityName = EntityName;
    }

    public Long getEntityId()
    {

        return EntityId;
    }

    public void setEntityId(Long EntityId)
    {

        this.EntityId = EntityId;
    }

}
