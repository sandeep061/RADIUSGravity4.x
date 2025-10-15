/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.exceptions;

/**
 *
 * @author Deepak
 */
public class GravityNoSuchFieldException extends GravityException
{

    private String _className;
    private String _fieldName;
    
    public GravityNoSuchFieldException(Throwable ex, String classname, String fieldname)
    {
        super(ex);

        this._className = classname;
        this._fieldName = fieldname;
    }

    public String getClassName()
    {
        return _className;
    }

    public String getFlieldName()
    {
        return _fieldName;
    }

}
