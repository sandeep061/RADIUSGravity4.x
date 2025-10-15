package ois.cc.gravity.framework.requests.user;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;


public class RequestProfileAdd extends Request
{

    @Param(Optional = false ,Regex = "^[a-zA-Z](?!.*__)[a-zA-Z0-9_]+$", Length = 32)
    private String Code;

    @Param(Optional = false ,Regex = "^[a-zA-Z0-9](?!.*[\\s\\-_]{2})[a-zA-Z0-9\\s\\-_]+$", Length = 48)
    private String Name;

    @Param(Optional = true, Length = 512)
    private String Description;

    private String Policy;

    public RequestProfileAdd(String requestid)
    {
        super(requestid, GReqType.Control, GReqCode.ProfileAdd);
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

    public String getPolicy()
    {
        return Policy;
    }

    public void setPolicy(String Policy)
    {
        this.Policy = Policy;
    }


}
