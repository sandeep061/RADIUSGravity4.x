package ois.cc.gravity.services.exceptions;

import java.util.Objects;

import org.vn.radius.cc.platform.exceptions.RADException;

import code.common.IObjectState;

public class GravityIllegalObjectStateException extends RADException
{

    /**
     * Simple name of the Class to which this Object is type of.
     */
    private String ObjectType;

    private String ObjectId;

    /**
     * Current state of the Object.
     */
    private IObjectState Found;

    /**
     * The legal valid states required for the request to be performed.
     */
    private IObjectState[] Expected;

    public GravityIllegalObjectStateException(String type, String id, IObjectState found)
    {
        this.ObjectId = id;
        this.ObjectType = type;
        this.Found = found;
        //Expected - excpected state is not defined, but the requested operation can not be performed on the current state of the object.
    }

    public GravityIllegalObjectStateException(String type, String id, IObjectState found, IObjectState... expected)
    {
        this.ObjectId = id;
        this.ObjectType = type;
        this.Found = found;
        this.Expected = expected;
    }

    public GravityIllegalObjectStateException(Class type, String id, IObjectState found, IObjectState... expected)
    {
        this(type.getSimpleName(), id, found, expected);
    }

    public GravityIllegalObjectStateException(Class type, Object id, IObjectState found, IObjectState... expected)
    {
        this(type.getSimpleName(), Objects.toString(id), found, expected);
    }

    public String getObjectId()
    {
        return ObjectId;
    }

    public void setObjectId(String ObjectId)
    {
        this.ObjectId = ObjectId;
    }

    public String getObjectType()
    {
        return ObjectType;
    }

    public void setObjectType(String ObjectType)
    {
        this.ObjectType = ObjectType;
    }

    public IObjectState getFound()
    {
        return Found;
    }

    public void setFound(IObjectState Found)
    {
        this.Found = Found;
    }

    public IObjectState[] getExpected()
    {
        return Expected;
    }

    public void setExpected(IObjectState[] Expected)
    {
        this.Expected = Expected;
    }

}

