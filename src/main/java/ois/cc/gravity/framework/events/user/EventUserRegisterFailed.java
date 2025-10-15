package ois.cc.gravity.framework.events.user;




import code.ua.events.EventFailed;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.EventCode;
import ois.radius.ca.enums.AgentState;


public class EventUserRegisterFailed extends EventFailed
{

    private String IP;

    private AgentState CurrentAgentState;

//    private Map<Channel, OTerminal> Extensions;

    private Cause Cause;

    public enum Cause
    {
        AgentAlreadyLoggedin,
        UnAuthorizedApplicationAccess,
        LoggedOutProcessNotCompleted,
        InvalidToken;
    }

    public EventUserRegisterFailed(Request request)
    {
        super(request, EventCode.UserRegisterFailed);
    }

    public String getIP()
    {
        return IP;
    }

    public void setIP(String IP)
    {
        this.IP = IP;
    }

    public AgentState getCurrentAgentState()
    {
        return CurrentAgentState;
    }

    public void setCurrentAgentState(AgentState CurrentAgentState)
    {
        this.CurrentAgentState = CurrentAgentState;
    }

//    public Map<Channel, OTerminal> getExtensions()
//    {
//        return Extensions;
//    }
//
//    public void setExtensions(Map<Channel, OTerminal> extsn)
//    {
//        this.Extensions = extsn;
//    }

    public Cause getCause()
    {
        return Cause;
    }

    public void setCause(Cause Cause)
    {
        this.Cause = Cause;
    }

}
