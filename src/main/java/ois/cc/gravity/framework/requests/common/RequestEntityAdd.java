package ois.cc.gravity.framework.requests.common;

import CrsCde.CODE.Common.Utils.TypeUtil;
import code.entities.EntityState;
import code.ua.requests.Param;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.requests.RequestAbase;

import java.util.HashMap;

public class RequestEntityAdd extends RequestAbase
{

    /**
     * ChangeLog. <br>
     * V.28032018.1 and V.28032018.1/1 <br>
     * - multi argument constructors
     */
    /**
     * Name of the entity to be added.
     */
    @Param(Optional = false)
    private EN EntityName;
    /**
     * Name-Value pair for attributes. <br>
     * Required for Add and Edit commands.
     */
    @Param(Optional = false)
    protected HashMap<String, Object> Attributes;

    /**
     *
     * @param requestid Unique Id of the request
     * @param entityname Name of the entity to be edited
     */
    public RequestEntityAdd(String requestid, EN entityname)
    {
        super(requestid, GReqType.Config, GReqCode.EntityAdd);

        this.EntityName = entityname;
        this.Attributes = new HashMap<>();
    }

    public EN getEntityName()
    {
        return EntityName;
    }

    public void setEntityName(EN EntityName)
    {
        this.EntityName = EntityName;
    }

    public HashMap<String, Object> getAttributes()
    {
        return Attributes;
    }

    public void setAttributes(HashMap<String, Object> Attributes)
    {
        this.Attributes = Attributes;
    }

    public Object getAttribute(String name)
    {
        return this.Attributes.get(name);
    }

    public <T> T getAttributeValueOf(Class type, String name) throws Exception
    {
        return TypeUtil.ValueOf(type, String.valueOf(this.Attributes.get(name)));
    }

    public void setAttribute(String name, Object value)
    {
        this.Attributes.put(name, value);
    }

    public void setEntityState(EntityState es)
    {
        setAttribute("EntityState", es);
    }
}


