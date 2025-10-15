package ois.cc.gravity.framework.requests.user;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

import java.util.ArrayList;

public class RequestAgentSkillDelete extends Request
{

    /**
     * If Id is available is request then direct delete the specified AgentSkill. This will be a optional parameter. User can delete by Id or by specified
     * skills.
     */
    @Param(Optional = true)
    private Long Id;

    /**
     * Delete the agent skills where these skills are mapped.
     */
    @Param(Optional = true)
    private ArrayList<Long> BySkill;

    public RequestAgentSkillDelete(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.AgentSkillDelete);
    }


    public Long getId()
    {
        return Id;
    }

    public void setId(Long id)
    {
        this.Id = id;
    }

    public ArrayList<Long> getBySkill()
    {
        return BySkill;
    }

    public void setByskill(ArrayList<Long> bySkill)
    {
        this.BySkill = bySkill;
    }

}

