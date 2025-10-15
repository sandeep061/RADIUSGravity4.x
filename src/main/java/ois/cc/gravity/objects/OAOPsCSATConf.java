package ois.cc.gravity.objects;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.xsess.XSessType;
import ois.radius.cc.entities.tenant.cc.AOPs;

import java.util.List;

public class OAOPsCSATConf extends AObject{

    private AOPs AOPs;

    private Channel Channel;

    private XSessType XSessType;

    private Boolean IsEnable;

    private Boolean IsAuto;

    private String DispositionCodes;

    private List<OXAlertID>XAlerts;

    public AOPs getAOPs() {
        return AOPs;
    }

    public void setAOPs(AOPs AOPs) {
        this.AOPs = AOPs;
    }

    public Channel getChannel() {
        return Channel;
    }

    public void setChannel(Channel channel) {
        Channel = channel;
    }

    public XSessType getXSessType() {
        return XSessType;
    }

    public void setXSessType(XSessType XSessType) {
        this.XSessType = XSessType;
    }

    public Boolean getEnable() {
        return IsEnable;
    }

    public void setEnable(Boolean enable) {
        IsEnable = enable;
    }

    public Boolean getAuto() {
        return IsAuto;
    }

    public void setAuto(Boolean auto) {
        IsAuto = auto;
    }

    public String getDispositionCodes() {
        return DispositionCodes;
    }

    public void setDispositionCodes(String dispositionCodes) {
        DispositionCodes = dispositionCodes;
    }

    public List<OXAlertID> getXAlerts() {
        return XAlerts;
    }

    public void setXAlerts(List<OXAlertID> XAlerts) {
        this.XAlerts = XAlerts;
    }
}
