package ois.cc.gravity.objects;

import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.tenant.cc.AOPsCSATConf;
import ois.radius.cc.entities.tenant.cc.XPlatform;
import ois.radius.cc.entities.tenant.cc.XPlatformUA;

import java.util.List;

public class OXAlertID extends AObject{

    private Channel Channel;

    private String Template;

    private XPlatform XPlatform;

    private XPlatformUA XPlatformUA;

    private List<OAOPsCSATConf> AOPsCSATConf;

    public Channel getChannel() {
        return Channel;
    }

    public void setChannel(Channel channel) {
        Channel = channel;
    }

    public String getTemplate() {
        return Template;
    }

    public void setTemplate(String template) {
        Template = template;
    }

    public XPlatform getXPlatform() {
        return XPlatform;
    }

    public void setXPlatform(XPlatform XPlatform) {
        this.XPlatform = XPlatform;
    }

    public XPlatformUA getXPlatformUA() {
        return XPlatformUA;
    }

    public void setXPlatformUA(XPlatformUA XPlatformUA) {
        this.XPlatformUA = XPlatformUA;
    }

    public List<OAOPsCSATConf> getAOPsCSATConf() {
        return AOPsCSATConf;
    }

    public void setAOPsCSATConf(List<OAOPsCSATConf> AOPsCSATConf) {
        this.AOPsCSATConf = AOPsCSATConf;
    }
}
