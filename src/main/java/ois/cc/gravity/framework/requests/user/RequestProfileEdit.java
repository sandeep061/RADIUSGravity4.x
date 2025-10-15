package ois.cc.gravity.framework.requests.user;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

public class RequestProfileEdit extends Request
{

    @Param(Optional = false)
    private Long ProfileId;

    @Param(Optional = true ,Regex = "^[a-zA-Z0-9](?!.*[\\s\\-_]{2})[a-zA-Z0-9\\s\\-_]+$", Length = 48)
    private String Name;

    @Param(Optional = true, Length = 512)
    private String Description;

    private String Policy;

    public RequestProfileEdit(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.ProfileEdit);
    }

    public Long getProfileId()
    {
        return ProfileId;
    }

    public void setProfileId(Long ProfileId)
    {
        this.ProfileId = ProfileId;
    }


    public String getName()
    {
        return Name;
    }

    public void setName(String Name)
    {
        this.Name = Name;
    }

    public String getPolicy()
    {
        return Policy;
    }

    public void setPolicy(String Policy)
    {
        this.Policy = Policy;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String Description)
    {
        this.Description = Description;
    }


}
