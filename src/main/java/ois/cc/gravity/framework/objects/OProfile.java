/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.framework.objects;

import code.uaap.service.common.entities.app.Policy;



/**
 *
 * @author rumana.begum
 * @since 25 Oct, 2023
 */
public class OProfile extends AObject
{

    private String Code;

    private String Name;

    private String Description;

    private Policy Policy;

    public OProfile()
    {
    }

    public String getCode()
    {
        return Code;
    }

    public void setCode(String Code)
    {
        this.Code = Code;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String Name)
    {
        this.Name = Name;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String Description)
    {
        this.Description = Description;
    }

    public Policy getPolicy()
    {
        return Policy;
    }

    public void setPolicy(Policy Policy)
    {
        this.Policy = Policy;
    }

}
