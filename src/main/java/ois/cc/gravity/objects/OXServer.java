package ois.cc.gravity.objects;

import CrsCde.CODE.Common.Utils.JSONUtil;
import code.entities.annotations.AnAttribute;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.tenant.cc.XServerEndpointProperties;
import org.vn.radius.cc.platform.xspi.ProviderID;

import java.util.ArrayList;
import java.util.Properties;

public class OXServer extends AObject{

    private String Code;

    private String Name;

    private ProviderID ProviderID;

    private Channel Channel;

    private String Description;

    private ArrayList<XServerEndpointProperties> EndPointTypeProps;

    private String AuthParams;

    public ArrayList<XServerEndpointProperties> getEndPointTypeProps() {
        return EndPointTypeProps;
    }

    public void setEndPointTypeProps(ArrayList<XServerEndpointProperties> endPointTypeProps) {
        EndPointTypeProps = endPointTypeProps;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Channel getChannel() {
        return Channel;
    }

    public void setChannel(Channel channel) {
        Channel = channel;
    }

    public ProviderID getProviderID() {
        return ProviderID;
    }

    public void setProviderID(ProviderID providerID) {
        ProviderID = providerID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
    public Properties getAuthParams() throws Exception
    {
        if (AuthParams != null)
        {
            return JSONUtil.FromJSON(AuthParams, Properties.class);
        }
        return new Properties();
    }

    public void setAuthParams(String authParams)
    {
        AuthParams = authParams;
    }
}
