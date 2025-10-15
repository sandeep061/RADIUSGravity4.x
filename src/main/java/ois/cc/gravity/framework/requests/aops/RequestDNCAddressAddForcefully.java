//package ois.cc.gravity.framework.requests.aops;
//
//import code.ua.requests.Param;
//import code.ua.requests.Request;
//import ois.cc.gravity.framework.requests.GReqCode;
//import ois.cc.gravity.framework.requests.GReqType;
//import ois.radius.ca.enums.Channel;
//
//public class RequestDNCAddressAddForcefully extends Request
//{
//
//    @Param(Optional = false)
//    private Long CampaignId;
//
//    @Param(Optional = false)
//    private Long DNCListId;
//
//    @Param(Optional = false)
//    private ois.radius.ca.enums.Channel Channel;
//
//    private String Address;
//
//    public RequestDNCAddressAddForcefully(String requestid)
//    {
//        super(requestid, GReqType.Config, GReqCode.DNCAddressAddForcefully);
//    }
//
//    public Long getCampaignId()
//    {
//        return CampaignId;
//    }
//
//    public void setCampaignId(Long CampaignId)
//    {
//        this.CampaignId = CampaignId;
//    }
//
//    public Long getDNCListId()
//    {
//        return DNCListId;
//    }
//
//    public void setDNCListId(Long DNCListId)
//    {
//        this.DNCListId = DNCListId;
//    }
//
//    public Channel getChannel()
//    {
//        return Channel;
//    }
//
//    public void setChannel(Channel Channel)
//    {
//        this.Channel = Channel;
//    }
//
//    public String getAddress()
//    {
//        return Address;
//    }
//
//    public void setAddress(String addrs)
//    {
//        this.Address = addrs;
//    }
//
//}
//
