/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.exceptions;

import java.util.ArrayList;

import CrsCde.CODE.Common.Utils.TypeUtil;

/**
 *
 * @author Deepak
 */
public class GravityAttributeConstraintFailedException extends GravityException
{
       /**
     * About. <br>
     * This exception is used at two different use cases. <br>
     * 1. to validate input value of an attribute while add/edit an entity. <br>
     * 2. validating value of an attribute while processing. <br>
     * At first we have named this exception as "RADAttributeValidationFailedException" but then changed to "RADAttributeConstraintFailedException" to signify
     * this exception is not only validating user input but for a generic purpose. <br>
     * Any soft of attribute data violation must be thrown by using this exception.
     */
    /**
     *
     */
    public enum FailedCause
    {
        IsNotNullable,
        IsNotEditable,
        IsUniqueInCampaign,
        IsUniqueInList,
        SizeLimitExceeds,
        ValueOutOfRange,
        TenantNotStarted
    }

    private String _entityName;
    private String _attributeName;
    private FailedCause _failedCause;

    //Additonal informartion about the exception (specific to the cause).
    /**
     * Specifies the size limit for this attribute. <br>
     */
    private Integer _sizeLimit;

    /**
     * Specifies the allowed range of values for this attribute, failure of which will case 'ValueOutOfRange'.
     */
    private ArrayList<String> _valueRange;

    public GravityAttributeConstraintFailedException(String _entityName, String _attributeName, FailedCause _failCause)
    {
        this._entityName = _entityName;
        this._attributeName = _attributeName;
        this._failedCause = _failCause;
    }

    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Value of '").append(_entityName).append(".").append(_attributeName).append("'")
                .append(" is not valid as the attribute is configured as ")
                .append(_failedCause.name());

        return sb.toString();
    }

    public String getEntityName()
    {
        return _entityName;
    }

    public String getAttributeName()
    {
        return _attributeName;
    }

    public FailedCause getFailedCause()
    {
        return _failedCause;
    }

    public Integer getSizeLimit()
    {
        return _sizeLimit;
    }

    public void setSizeLimit(Integer _sizeLimit)
    {
        this._sizeLimit = _sizeLimit;
    }

    public ArrayList<String> getValueRange()
    {
        return _valueRange;
    }

    public void setValueRange(Object... range)
    {
        if (_valueRange == null)
        {
            _valueRange = new ArrayList<>();
        }
        for (Object obj : range)
        {
            if (TypeUtil.IsPrimeType(obj.getClass()))
            {
                this._valueRange.add(TypeUtil.ToString(obj));
            }
        }
    }

}
