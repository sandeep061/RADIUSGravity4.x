package ois.cc.gravity.services.exceptions;
import org.vn.radius.cc.platform.exceptions.RADException;

import java.util.Objects;

public class GravityIllegalObjectTypeException extends RADException
{

    /**
     * Simple name of the Class to which this Object is type of.
     */
    private String ObjectType;

    private String ObjectId;

    /**
     * Current state of the Object.
     */
    private String Found;

    /**
     * The legal valid states required for the request to be performed.
     */
    private String Expected;

    public GravityIllegalObjectTypeException(String type, String id, String found)
    {
        this.ObjectId = id;
        this.ObjectType = type;
        this.Found = found;
        //Expected - excpected state is not defined, but the requested operation can not be performed on the current state of the object.
    }

    public GravityIllegalObjectTypeException(String type, String id, String found, String expected)
    {
        this.ObjectId = id;
        this.ObjectType = type;
        this.Found = found;
        this.Expected = expected;
    }

    public GravityIllegalObjectTypeException(Class type, String id, String found, String expected)
    {
        this(type.getSimpleName(), id, found, expected);
    }

    public GravityIllegalObjectTypeException(Class type, Object id, String found, String expected)
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

    public String getFound()
    {
        return Found;
    }

    public void setFound(String Found)
    {
        this.Found = Found;
    }

    public String getExpected()
    {
        return Expected;
    }

    public void setExpected(String Expected)
    {
        this.Expected = Expected;
    }

}


