package ois.cc.gravity.framework.objects;

import ois.radius.cc.entities.tenant.cc.XServerEndpointProperties;

import java.util.ArrayList;
import java.util.Date;
import ois.radius.ca.enums.Channel;
import org.vn.radius.cc.platform.xspi.ProviderID;
import org.vn.radius.cc.platform.xspi.ProviderState;

public class OXServer extends AObject
{

    private String Code;

    private String Name;

    private ProviderID ProviderID;

    private Channel Channel;
    //    /**
//     * Additional authentication parameter may be required by provider. e.g TLink etc.. The exact format and usage of this value will be defined by
//     * implementation packages. <br>
//     * Will allow only String values.
//     */
//    private Properties AuthParams;
    private ProviderState ProviderState;

    private Date StartAt;

    private ArrayList<XServerEndpointProperties> EndPointTypeProps;

    public OXServer()
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

    public ProviderID getProviderID()
    {
        return ProviderID;
    }

    public void setProviderID(ProviderID ProviderID)
    {
        this.ProviderID = ProviderID;
    }

    public ProviderState getProviderState()
    {
        return ProviderState;
    }

    public void setProviderState(ProviderState ProviderState)
    {
        this.ProviderState = ProviderState;
    }

    public Date getStartAt()
    {
        return StartAt;
    }

    public void setStartAt(Date StartAt)
    {
        this.StartAt = StartAt;
    }

    public ArrayList<XServerEndpointProperties> getEndPointTypeProps()
    {
        return EndPointTypeProps;
    }

    public void setEndPointTypeProps(ArrayList<XServerEndpointProperties> EndPointTypeProps)
    {
        this.EndPointTypeProps = EndPointTypeProps;
    }

    public Channel getChannel()
    {
        return Channel;
    }

    public void setChannel(Channel Channel)
    {
        this.Channel = Channel;
    }

}
