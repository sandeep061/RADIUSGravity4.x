/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ois.cc.gravity.framework.events.common;

import code.ua.events.EventFailed;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;

/**
 *
 * @author Manoj-PC
 * @since Sep 1, 2024
 */
public class EventAttributeValidationFailed extends EventFailed
{
    /**
     * About. <br>
     * This event is mapped with RADAttributeConstraintFailedException.
     */

    private String EntityName;
    private String AttributeName;
    private String FailedCause;

    public EventAttributeValidationFailed(Request request, String entityname, String attributename, String failcause)
    {
        super(request, EventCode.AttributeValidationFailed);
        this.EntityName = entityname;
        this.AttributeName = attributename;
        this.FailedCause = failcause;
    }

    public String getEntityName()
    {
        return EntityName;
    }

    public String getAttributeName()
    {
        return AttributeName;
    }

    public String getFailedCause()
    {
        return FailedCause;
    }

}
