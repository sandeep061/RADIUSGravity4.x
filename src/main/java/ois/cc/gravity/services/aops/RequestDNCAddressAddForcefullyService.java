//package ois.cc.gravity.services.aops;
//
//import code.ua.events.Event;
//import code.ua.events.EventOK;
//import code.ua.requests.Request;
//import ois.cc.gravity.db.MySQLDB;
//import ois.cc.gravity.entities.util.AddressUtill;
//import ois.cc.gravity.framework.requests.aops.RequestDNCAddressAddForcefully;
//import ois.cc.gravity.services.ARequestService;
//import ois.cc.gravity.ua.UAClient;
//import ois.radius.cc.entities.EN;
//import ois.radius.cc.entities.tenant.cc.Campaign;
//import ois.radius.cc.entities.tenant.cc.DNCAddress;
//import ois.radius.cc.entities.tenant.cc.DNCList;
//import org.vn.radius.cc.platform.exceptions.RADException;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//
//public class RequestDNCAddressAddForcefullyService extends ARequestService
//{
//
//    public RequestDNCAddressAddForcefullyService(UAClient uac)
//    {
//        super(uac);
//    }
//
//    @Override
//    protected Event DoProcessRequest(Request request) throws Throwable
//    {
//        RequestDNCAddressAddForcefully req = (RequestDNCAddressAddForcefully) request;
//        MySQLDB db = _tctx.getDB();
//
//        Campaign camp = db.FindAssert(EN.Campaign.getEntityClass(), req.getCampaignId());
//        DNCList dncList = db.FindAssert(EN.DNCList.getEntityClass(), req.getDNCListId());
//
//        ArrayList<DNCAddress> alDncs = new ArrayList<>();
//
//        //validate address format for the channel
//        StringBuilder regxSb = new StringBuilder();
//        if (!AddressUtill.IsValidAddress(req.getChannel(), req.getAddress(), regxSb))
//        {
//            return new EventAddressValidationFailed(request, req.getChannel(), req.getAddress(), regxSb.toString());
//        }
//
//        /**
//         * If any contact found with this address then check in ContactPuller if not used now(in Buffered state) then remove that from Loaded list so that it
//         * can't take part in further contact activities.
//         */
//        ALMClientContext alm = _cctx.getALMCtx();
//        HashMap<String, ArrayList<String>> filter = new HashMap<>();
//        filter.put("byaddress", new ArrayList<>(Arrays.asList(req.getAddress())));
//        filter.put("bychannel", new ArrayList<>(Arrays.asList(req.getChannel().toString())));
//        ArrayList<OContactAddress> ContactAddresses = alm.GetCampContactAddresses(camp, filter);
//        OContactAddress oconaddr = null;
//        if (!ContactAddresses.isEmpty())
//        {
//            oconaddr = ContactAddresses.get(0);
//        }
//
////        ContactAddressQueury qry = new ContactAddressQueury().filterByAddress(req.getAddress()).filterByAops(camp.getId()).filterByChannel(req.getChannel());
////        ContactAddress conaddr = _cctx.getCoreDB().Find(qry);
//        if (oconaddr != null)
//        {
//            if (oconaddr.getIsDNC())
//            {
//                AICampaign aicm = _cctx.getCampaignStore().GetById(camp.getId());
//                if (aicm != null)
//                {
////                    aicm.getContactPuller().checkAndRemoveInUseContact(oconaddr.getContact());
//                }
//            }
//        }
//
//        //create entity and put to list.
//        DNCAddress dncAdd = new DNCAddress();
//        dncAdd.setDNCList(dncList);
//        dncAdd.setChannel(req.getChannel());
//        dncAdd.setAddress(req.getAddress());
//
//        alDncs.add(dncAdd);
//
//        try
//        {
//            /**
//             * Here we have to check DNCAddress should not be duplicate in a list. so in DNCAddress we are make unique (DNCList,Channel and Address) fields. We
//             * are trying to use The concept Insert Ignore (if any duplicate row found then that should be ignored)of SQL but ObjectDB does not support it.
//             *
//             * When ever exception arises regarding Unique key constraints we have handled this in catch block.
//             */
//            db.Insert(_uac.getUserSession().getUser(), alDncs);
//        }
//        catch (RADException rbex)
//        {
//            //We can ignore this.
//        }
//
//        //Add and Apply DNC.
//        if (oconaddr != null)
//        {
//            OContact ocon = alm.GetContactByIdAssert(camp, oconaddr.getContactId());
//            UtilDNC.ApplyDNC(_cctx, _uac.getUserSession().getUser(),camp, ocon.getContactListId(), oconaddr);
//        }
//
//        EventOK ev = new EventOK(request);
//        return ev;
//    }
//
//}
