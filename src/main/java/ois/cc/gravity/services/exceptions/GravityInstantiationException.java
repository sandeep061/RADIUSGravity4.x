/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.exceptions;

/**
 *
 * @author Deepak
 */
public class GravityInstantiationException extends GravityException
{

    private String _entityName;

    public GravityInstantiationException(Throwable cause, String entityname)
    {
        super(cause);
        this._entityName = entityname;
    }

    public String getEntityName()
    {
        return _entityName;
    }

    public void setEntityName(String EntityName)
    {
        this._entityName = EntityName;
    }
}
