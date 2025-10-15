/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.services.exceptions;


import code.ua.events.EventFailedCause;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;

/**
 *
 * @author Deepak
 */
public class GravityIllegalArgumentException extends GravityException
{ 
     private String ArgName;

    private EventFailedCause EvCause;
    
    private EvCauseRequestValidationFail EvAppCause;



    public GravityIllegalArgumentException(String string)
    {
        super(string);
    }

    public GravityIllegalArgumentException(String argname, EventFailedCause EvCause, EvCauseRequestValidationFail EvAppCause)
    {
        this.ArgName = argname;
       this.EvCause = EvCause;
       this.EvAppCause = EvAppCause;
    }

    public GravityIllegalArgumentException(String message, String argname, EventFailedCause EvCause)
    {
        super(message);
        this.ArgName = argname;
        this.EvCause = EvCause;
    }

    @Override
    public String getMessage()
    {
        return super.getMessage();
    }

    public String getArgName()
    {
        return ArgName;
    }

    public void setArgName(String ArgName)
    {
        this.ArgName = ArgName;
    }

    public EventFailedCause getEvCause()
    {
        return EvCause;
    }

    public void setEvCause(EventFailedCause EvCause)
    {
        this.EvCause = EvCause;
    }

    public EvCauseRequestValidationFail getEvAppCause()
    {
        return EvAppCause;
    }
    
    
}
