package ois.cc.gravity.framework.requests.sys;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;

import java.util.Date;

public class RequestXSPIClientConfig extends Request
{

    private String NodeId;
    
    @Param(Optional = false)
    private String TenantCode;

    @Param(Optional = false)
    private String XServerCode;

    private String CCurl;

    private String PIUAC;

    private Date PILastPingAt;

    public RequestXSPIClientConfig(String requestid)
    {
        super(requestid, GReqType.Control, GReqCode.XSPIClientConfig);
    }

    public String getNodeId()
    {
        return NodeId;
    }

    public void setNodeId(String nodeId)
    {
        NodeId = nodeId;
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

    public String getCCurl()
    {
        return CCurl;
    }

    public void setCCurl(String CCurl)
    {
        this.CCurl = CCurl;
    }

    public String getPIUAC()
    {
        return PIUAC;
    }

    public void setPIUAC(String PIUAC)
    {
        this.PIUAC = PIUAC;
    }

    public Date getPILastPingAt()
    {
        return PILastPingAt;
    }

    public void setPILastPingAt(Date PILastPingAt)
    {
        this.PILastPingAt = PILastPingAt;
    }

}
