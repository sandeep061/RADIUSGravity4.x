package ois.cc.gravity.objects;

import jakarta.persistence.*;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.tenant.cc.AgentMediaMap;
import ois.radius.cc.entities.tenant.cc.User;


import java.util.List;

public class OUserMedia extends AObject{

    private User User;

    private Channel Channel;

    private Boolean AutoRegister;

    private OAgentMediaMap AutoRegAgentMedia;

    private List<OAgentMediaMap> AgentMediaMaps;

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }

    public Channel getChannel() {
        return Channel;
    }

    public void setChannel(Channel channel) {
        Channel = channel;
    }

    public Boolean getAutoRegister() {
        return AutoRegister;
    }

    public void setAutoRegister(Boolean autoRegister) {
        AutoRegister = autoRegister;
    }

    public OAgentMediaMap getAutoRegAgentMedia() {
        return AutoRegAgentMedia;
    }

    public void setAutoRegAgentMedia(OAgentMediaMap autoRegAgentMedia) {
        AutoRegAgentMedia = autoRegAgentMedia;
    }

    public List<OAgentMediaMap> getAgentMediaMaps() {
        return AgentMediaMaps;
    }

    public void setAgentMediaMaps(List<OAgentMediaMap> agentMediaMaps) {
        AgentMediaMaps = agentMediaMaps;
    }
}
