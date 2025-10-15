/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.exceptions;

/**
 *
 * @author Deepak
 */
public class GravityUniqueConstraintViolationException extends GravityException
{

    private String _entityName;
    private String _condition;

    public GravityUniqueConstraintViolationException(Throwable cause, String entityanme, String condition)
    {
        super(cause);

        this._entityName = entityanme;
        this._condition = condition;
    }

    public String getEntityName()
    {
        return _entityName;
    }

    public void setEntityName(String EntityName)
    {
        this._entityName = EntityName;
    }

    public String getCondition()
    {
        return _condition;
    }

}
