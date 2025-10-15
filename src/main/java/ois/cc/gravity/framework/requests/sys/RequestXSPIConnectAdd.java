package ois.cc.gravity.framework.requests.sys;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import org.vn.radius.cc.platform.xspi.ProviderState;

import java.util.Date;

 public class RequestXSPIConnectAdd extends Request
{

    private String NodeId;

    private String XSPIConnId;

    @Param(Optional = false)
    protected String TenantCode;

    @Param(Optional = false)
    protected String XServerCode;

    private String PIUAC;

    private String CCUAC;

    private Date MapAt;

    private Date UnmapAt;

    private ProviderState CCUACStatus;

    private ProviderState PIUACStatus;

    public RequestXSPIConnectAdd(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.XSPIConnectAdd);
    }

    public String getTenantCode()
    {
        return TenantCode;
    }

    public void setTenantCode(String tenantCode)
    {
        TenantCode = tenantCode;
    }

    public String getXServerCode()
    {
        return XServerCode;
    }

    public void setXServerCode(String XServerCode)
    {
        this.XServerCode = XServerCode;
    }

    public String getNodeId()
    {
        return NodeId;
    }

    public void setNodeId(String nodeId)
    {
        NodeId = nodeId;
    }

    public String getXSPIConnId()
    {
        return XSPIConnId;
    }

    public void setXSPIConnId(String XSPIConnId)
    {
        this.XSPIConnId = XSPIConnId;
    }

    public String getPIUAC()
    {
        return PIUAC;
    }

    public void setPIUAC(String PIUAC)
    {
        this.PIUAC = PIUAC;
    }

    public String getCCUAC()
    {
        return CCUAC;
    }

    public void setCCUAC(String CCUAC)
    {
        this.CCUAC = CCUAC;
    }

    public Date getMapAt()
    {
        return MapAt;
    }

    public void setMapAt(Date mapAt)
    {
        MapAt = mapAt;
    }

    public Date getUnmapAt()
    {
        return UnmapAt;
    }

    public void setUnmapAt(Date unmapAt)
    {
        UnmapAt = unmapAt;
    }

    public ProviderState getCCUACStatus()
    {
        return CCUACStatus;
    }

    public void setCCUACStatus(ProviderState CCUACStatus)
    {
        this.CCUACStatus = CCUACStatus;
    }

    public ProviderState getPIUACStatus()
    {
        return PIUACStatus;
    }

    public void setPIUACStatus(ProviderState PIUACStatus)
    {
        this.PIUACStatus = PIUACStatus;
    }
}
