package ois.cc.gravity.framework.requests.survey;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.ca.enums.Channel;
import ois.radius.cc.entities.tenant.cc.XAlertDR;

import java.util.Date;

public class RequestXAlertDRAdd extends Request {

    @Param(Optional = false)
    private String USUID;

    private String UXID;

    private Long AOPs;

    private Channel Channel;

    @Param(Optional = false)
    private String UAltID;

    private String Message;

    private String Response;

    private Date SendAt;

    private XAlertDR.Status Status;

    private Long XAlertID;

    public RequestXAlertDRAdd(String requestid) {
        super(requestid, GReqType.Config, GReqCode.XAlertDRAdd);
    }

    public String getUSUID() {
        return USUID;
    }

    public void setUSUID(String USUID) {
        this.USUID = USUID;
    }

    public String getUXID() {
        return UXID;
    }

    public void setUXID(String UXID) {
        this.UXID = UXID;
    }

    public Long getAOPs() {
        return AOPs;
    }

    public void setAOPs(Long AOPs) {
        this.AOPs = AOPs;
    }

    public Channel getChannel() {
        return Channel;
    }

    public void setChannel(Channel channel) {
        Channel = channel;
    }

    public String getUAltID() {
        return UAltID;
    }

    public void setUAltID(String UAltID) {
        this.UAltID = UAltID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public Date getSendAt() {
        return SendAt;
    }

    public void setSendAt(Date sendAt) {
        SendAt = sendAt;
    }

    public XAlertDR.Status getStatus() {
        return Status;
    }

    public void setStatus(XAlertDR.Status status) {
        Status = status;
    }

    public Long getXAlertID() {
        return XAlertID;
    }

    public void setXAlertID(Long XAlertID) {
        this.XAlertID = XAlertID;
    }
}
