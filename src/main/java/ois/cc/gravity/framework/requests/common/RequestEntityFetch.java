package ois.cc.gravity.framework.requests.common;
import code.ua.requests.Param;
import ois.radius.cc.entities.EN;

public class RequestEntityFetch extends RequestAbaseFetch
{
    /**
     * Name of the entity to be fetched.
     */
    @Param(Optional = false)
    protected EN EntityName;

    /**
     *
     * @param requestid
     * @param entityname Name of the entity to be fetched
     */
    public RequestEntityFetch(String requestid, EN entityname)
    {
        super(requestid);
        this.EntityName = entityname;
    }

    public EN getEntityName()
    {

        return EntityName;
    }

    public void setEntityName(EN entityname)
    {

        this.EntityName = entityname;
    }

}
