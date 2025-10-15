package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import org.vn.radius.cc.platform.xspi.ProviderID;
import org.vn.radius.cc.platform.xspi.ProviderState;
import org.vn.radius.cc.platform.xspi.XSPIConnStatus;

import java.util.Date;
import java.util.HashMap;

public class RequestXSPIConnectionConfig extends Request
{

    private String NodeId;
    private String XSPIConnId;
    private String PIUAC;

    @Param(Optional = false)
    private String XServerCode;
    @Param(Optional = false)
    private String TenantCode;

    private String CCUAC;
    private Date MapAt;
    private Date UnmapAt;
    private ProviderState CCUACStatus;
    private ProviderState PIUACStatus;


    public RequestXSPIConnectionConfig(String requestid)
    {
        super(requestid, GReqType.Config, GReqCode.XSPIConnectConfig);


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

    public String getXServerCode()
    {
        return XServerCode;
    }

    public void setXServerCode(String XServerCode)
    {
        this.XServerCode = XServerCode;
    }

    public String getTenantCode()
    {
        return TenantCode;
    }

    public void setTenantCode(String tenantCode)
    {
        TenantCode = tenantCode;
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
