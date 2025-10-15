package ois.cc.gravity.framework.requests.common;

import CrsCde.CODE.Common.Utils.TypeUtil;
import code.ua.requests.Param;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.requests.RequestAbase;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestEntityEdit extends RequestAbase
{
    /**
     * Name of the entity to be added.
     */
    @Param(Optional = false)
    private EN EntityName;

    /**
     * Id of the entity to be edited.
     */
    @Param(Optional = false)
    protected Long EntityId;

    /**
     * Name-Value pair for attributes. <br>
     * Required for Add and Edit commands.
     */
    @Param(Optional = true)
    private HashMap<String, Object> Attributes;

    /**
     * Collection Attribute Name - Values to be appended in the collection.
     */
    private HashMap<String, ArrayList<String>> AttributeCollectionAppend;

    /**
     * Collection Attribute Name - Values to be removed from the collection.
     */
    private HashMap<String, ArrayList<String>> AttributeCollectionRemove;

    /**
     *
     * @param requestid Unique Id of the request
     * @param entityname Name of the entity to be edited
     * @param entityid Id of the entity to be edited
     */
    public RequestEntityEdit(String requestid, EN entityname, Long entityid)
    {
        super(requestid, GReqType.Config, GReqCode.EntityEdit);

        this.EntityName = entityname;
        this.EntityId = entityid;
        this.Attributes = new HashMap<>();
        this.AttributeCollectionAppend = new HashMap<>();
        this.AttributeCollectionRemove = new HashMap<>();
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

    public HashMap<String, Object> getAttributes()
    {
        return Attributes;
    }

    public void setAttributes(HashMap<String, Object> Attributes)
    {
        this.Attributes = Attributes;
    }

    public <T> T getAttributeValueOf(Class type, String name) throws Exception
    {
        return TypeUtil.ValueOf(type, String.valueOf(this.Attributes.get(name)));
    }

    public Object getAttribute(String name)
    {
        return this.Attributes.get(name);
    }

    public void setAttribute(String name, Object value)
    {
        this.Attributes.put(name, value);
    }

    public HashMap<String, ArrayList<String>> getAttributeCollectionAppend()
    {
        return AttributeCollectionAppend;
    }

    public void setAttributeCollectionAppend(HashMap<String, ArrayList<String>> AttributeCollectionAppend)
    {
        this.AttributeCollectionAppend = AttributeCollectionAppend;
    }

    public HashMap<String, ArrayList<String>> getAttributeCollectionRemove()
    {
        return AttributeCollectionRemove;
    }

    public void setAttributeCollectionRemove(HashMap<String, ArrayList<String>> AttributeCollectionRemove)
    {
        this.AttributeCollectionRemove = AttributeCollectionRemove;
    }

}
