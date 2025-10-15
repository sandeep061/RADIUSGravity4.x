package ois.cc.gravity.entities.util;

import ois.radius.ca.enums.Channel;

public class AddressUtill
{

    /**
     * Validate an address string as per regular expression.
     *
     * @param channel
     * @param address
     * @param regx
     * @return
     */
    public static boolean IsValidAddress(Channel channel, String address, StringBuilder regx)
    {
        switch (channel)
        {
            case Call:
            case SMS:
                /**
                 * Telephone address length is 3 to 14 and only numbers. <br>
                 *
                 * EPP-style phone numbers use the format +CCC.NNNNNNNNNNxEEEE, where C is the 1–3 digit country code, N is up to 14 digits, and E is the
                 * (optional) extension. The leading plus sign and the dot following the country code are required. The literal “x” character is required only
                 * if an extension is provided. <br>
                 *
                 * @see
                 * https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch04s03.html#:~:text=EPP%2Dstyle%20phone%20numbers%20use,if%20an%20extension%20is%20provided
                 * <br>
                 * -We have refer the above link for validating international phone numbers, but we ignore "." and changed minimum range to 3 as the internal
                 * extensions may be 3 digit(AES).<br>
                 * -This regex is common for Telephone and SMS.
                 */
                regx.append("^(\\+[0-9]{1,3})?[0-9]{3,14}(?:x.+)?$");
                if (address.matches(regx.toString()))
                {
                    return true;
                }
                break;
            case Email:
                /**
                 * This regex is for email ..etc eg:- abc@ef.in.
                 */
                regx.append("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
                if (address.matches(regx.toString()))
                {
                    return true;
                }
                break;

            case Chat:
            case Social:
            case Video:
                regx.append("^(?!\\+$)\\S.*\\S$");
                if (address.matches(regx.toString()))
                {
                    return true;
                }
                break;
            //A chat address can be of PhoneNo, EmailId or simpley any name. Hence we need not do any validationa.
            //TBD:if required we can implement validation client specific by confgiuring some regex for chat addres..
            default:
                break;
        }
        return false;
    }

    public static boolean IsValidTerminalAddress(Channel channel, String address, StringBuilder regx)
    {
        switch (channel)
        {
            case Call:
                regx.append("^[0-9]+$");
                if (address.matches(regx.toString()))
                {
                    return true;
                }
                break;
            case Email:
            case Chat:
            case Video:
            case Social:
            case SMS:
                regx.append("^[a-zA-Z0-9_\\-+.%]+$");
                if (address.matches(regx.toString()))
                {
                    return true;
                }
                break;
            default:
                break;

        }
        return false;
    }

    /**
     * This method will validate the contact address is a valid external address or not and the contact address must be of Telephone channel.
     *
     * @param chn
     * @param conaddr
     * @param extregx
     * @return
     */
    public static Boolean IsValidExternalAddress(Channel chn, String conaddr, String extregx)
    {
        if (!Channel.Call.equals(chn))
        {
            return false;
        }

        return conaddr.matches(extregx);
    }

    public static String GetMaskAddress(String addr, Channel chn)
    {
        switch (chn)
        {
            case Call:
            case Chat:
            case SMS:
                addr = "#**********";
                break;
            case Email:
            case Social:
            case Video:
                addr = "*****@***";
                break;
        }
        return addr;
    }

//    /**
//     * Return False if there is already an address exists ( across entire campaign).<br>
//     *
//     * @param db
//     * @param camp
//     * @param conadr
//     * @return
//     */
//    private static boolean isAddressUniqueInCampaign(MySQLDB db, Campaign camp, ContactAddress conadr) throws REALMException
//    {
//        HashMap<Channel, Boolean> hmVals = camp.getCampaignContact().getIsAddressUniqueInCampaign();
//        if (hmVals.isEmpty())
//        {
//            return true;
//        }
//        else if (hmVals.get(conadr.getChannel()) != null && !hmVals.get(conadr.getChannel()))
//        {
//            return true;
//        }
//        else
//        {
//            StringBuilder sbqrystr = new StringBuilder();
//            sbqrystr.append("Select COUNT(ca) from ContactAddress ca "
//                    + " Where ca.Address =: address "
//                    + " And ca.Contact.ContactList.Campaign.Id =: campid"
//                    + " And ca.Deleted =: es");
//
//            /**
//             * For edit request, the address must already be exists. Hence we can ignore checking current ContactAddress object.
//             */
//            if (conadr.getId() != null)
//            {
//                sbqrystr.append(" And ca.Id <>: caid");
//            }
//
//            JPAQuery dbq = new JPAQuery(sbqrystr.toString());
//            dbq.setParam("campid", camp.getId());
//            dbq.setParam("address", conadr.getAddress());
//            dbq.setParam("es", false);
//
//            if (conadr.getId() != null)
//            {
//                dbq.setParam("caid", conadr.getId());
//            }
//
//            Long value = (Long) db.SelectScalar(dbq);
//            return value.equals(0L);
//        }
//
//    }

//    /**
//     * Return False if there is already an address exists ( across entire Contact List).<br>
//     *
//     * @param db
//     * @param camp
//     * @param conlist
//     * @param conadr
//     * @return
//     */
//    private static boolean isAddressUniqueInList(CoreDB db, Campaign camp, ContactList conlist, ContactAddress conadr) throws RADException
//    {
//        HashMap<Channel, Boolean> hmVals = camp.getCampaignContact().getIsAddressUniqueInList();
//        if (hmVals.isEmpty())
//        {
//            return true;
//        }
//        else if (hmVals.get(conadr.getChannel()) != null && !hmVals.get(conadr.getChannel()))
//        {
//            return true;
//        }
//        else
//        {
//            StringBuilder sbqrystr = new StringBuilder();
//            sbqrystr.append("Select COUNT(ca) from ContactAddress ca "
//                    + " Where ca.Address =: address "
//                    + " And ca.Contact.ContactList.Id =: listid "
//                    + " And ca.Deleted =: es ");
//
//            /**
//             * For edit request, the address must already be exists.Hence we can ignore checking current ContactAddress object.
//             */
//            if (conadr.getId() != null) //for edit scenario
//            {
//                sbqrystr.append(" And ca.Id <>: caid");
//            }
//
//            JPAQuery dbq = new JPAQuery(sbqrystr.toString());
//            dbq.setParam("listid", conlist.getId());
//            dbq.setParam("address", conadr.getAddress());
//            dbq.setParam("es", false);
//
//            if (conadr.getId() != null) //for edit scenario
//            {
//                dbq.setParam("caid", conadr.getId());
//            }
//
//            Long value = (Long) db.SelectScalar(dbq);
//            return value.equals(0L);
//        }
//    }
}
