/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.exceptions;

import CrsCde.CODE.Common.Enums.OPRelational;

/**
 *
 * @author Deepak
 */
public class GravityEntityNotFoundException extends GravityException
{

    private String _entityName;

    private String _condition;

    public GravityEntityNotFoundException(String entityname)
    {
        this._entityName = entityname;
    }

    public GravityEntityNotFoundException(String en, String condition)
    {
        this(en);
        setCondition(condition);
    }

    public GravityEntityNotFoundException(String en, String attrname, OPRelational ops, Object value)
    {
        this(en);
        setCondition(attrname, ops, value);
    }

    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("No entity '").append(_entityName).append("'").append(" found in Core DB, for condition ")
                .append(_condition);

        return sb.toString();
    }

    public String getEntityName()
    {
        return _entityName;
    }

    public void setEntityName(String _entityName)
    {
        this._entityName = _entityName;
    }

    public String getCondition()
    {
        return _condition;
    }

    public void setCondition(String Condition)
    {
        this._condition = Condition;
    }

    public void setCondition(String attrname, OPRelational ops, String value)
    {
        this._condition = "[ " + attrname + ops.Symbol() + value + " ]";
    }

    public void setCondition(String attrname, OPRelational ops, Object value)
    {
        setCondition(attrname, ops, String.valueOf(value));
    }
}
