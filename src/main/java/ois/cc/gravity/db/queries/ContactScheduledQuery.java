/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ois.cc.gravity.db.queries;

import CrsCde.CODE.Common.Utils.DATEUtil;
import code.ua.events.EventFailedCause;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.ca.enums.aops.CallbackType;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.ContactScheduled;
import ois.radius.cc.entities.tenant.cc.ContactScheduled.State;

/**
 *
 * @author Manoj-PC
 * @since Sep 2, 2024
 */
public class ContactScheduledQuery extends EntityQuery
{

    public ContactScheduledQuery()
    {
        super(EN.ContactScheduled);
    }

    public ContactScheduledQuery filterByContactId(String conid)
    {
        AppendWhere("And ContactScheduled.ContactId=:conid");
        _params.put("conid", conid);

        return this;
    }

    public ContactScheduledQuery filterByContactAddressId(Long conaddrid)
    {
        AppendWhere("And ContactScheduled.ContactAddressId=:conaddrid");
        _params.put("conaddrid", conaddrid);

        return this;
    }

    public ContactScheduledQuery filterByContactAddress(String conaddr)
    {
        AppendWhere("And ContactScheduled.ContactAddress=:conaddr");
        _params.put("conaddr", conaddr);

        return this;
    }

    public ContactScheduledQuery filterByAgent(Long agid)
    {
        AppendWhere("And ContactScheduled.Agent.Id=:agid");
        _params.put("agid", agid);

        return this;
    }

    public ContactScheduledQuery filterBySkill(Long skid)
    {
        AppendWhere("And ContactScheduled.Skill.Id=:skid");
        _params.put("skid", skid);

        return this;
    }

    public ContactScheduledQuery filterByScheduledOn(Date schon)
    {
        AppendWhere("And ContactScheduled.ScheduledOn=:schon");
        _params.put("schon", schon);

        return this;
    }

    public ContactScheduledQuery filterByCampaign(Long campid)
    {
        AppendWhere("And ContactScheduled.AOPs.Id =:cmpid");
        _params.put("cmpid", campid);

        return this;
    }

    public ContactScheduledQuery filterByScheduledOnBetween(Date frmdt, Date todt)
    {
        AppendWhere("And ContactScheduled.ScheduledOn BETWEEN :frmdt AND :todt");
        _params.put("frmdt", frmdt);
        _params.put("todt", todt);

        return this;
    }

    public ContactScheduledQuery filterByCreatedBy(Long userid)
    {
        AppendWhere("And ContactScheduled.CreatedBy =:userid");
        _params.put("userid", userid);

        return this;
    }

    public ContactScheduledQuery filterByScheduledStates(ContactScheduled.State... states)
    {
        AppendWhere("And ContactScheduled.State in (:states)");
        _params.put("states", Arrays.asList(states));

        return this;
    }

    public ContactScheduledQuery filterByCallBackTypes(CallbackType... clbcktyp)
    {
        AppendWhere("And ContactScheduled.CallbackType in (:clbcktyp)");
        _params.put("clbcktyp", Arrays.asList(clbcktyp));

        return this;
    }

    public ContactScheduledQuery filterByContactAddressLike(String address)
    {
        AppendWhere("And Lower(ContactScheduled.ContactAddress.Address) Like : address");
        _params.put("address",  "%" + address + "%");

        return this;
    }

    @Override
    public void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws GravityIllegalArgumentException, Exception
    {
        for (String name : filters.keySet())
        {
            switch (name.toLowerCase())
            {
                case "byid":
                    filterById(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bycontact":
                    filterByContactId(filters.get(name).get(0));
                    break;
                case "bycontactaddress":
                    filterByContactAddress(filters.get(name).get(0));
                    break;
                case "bycontactaddresslike":
                    filterByContactAddressLike(filters.get(name).get(0).toLowerCase());
                    break;
                case "bycontactaddressid":
                    filterByContactAddressId(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byagent":
                    filterByAgent(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byskill":
                    filterBySkill(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byscheduledon":
                    Date date = DATEUtil.ValueOf(filters.get(name).get(0));
                    filterByScheduledOn(date);
                    break;
                case "byscheduledonbetween":
                    List<Date> alDates = new ArrayList<>();
                    Iterator<String> itr = filters.get(name).iterator();
                    while (itr.hasNext())
                    {
                        Object next = itr.next();
                        alDates.add(DATEUtil.ValueOf(next.toString()));

                    }
                    filterByScheduledOnBetween(alDates.get(0), alDates.get(1));
                    break;
                case "bycreatedby":
                    filterByCreatedBy(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "byscheduledstates":
                    List<State> state = filters.get(name).stream()
                            .map((c) -> State.valueOf(c)).collect(Collectors.toList());
                    filterByScheduledStates(state.toArray(new State[state.size()]));
                    break;
                case "byaops":
                    filterByCampaign(Long.valueOf(filters.get(name).get(0)));
                    break;
                case "bycallbacktypes":
                    List<CallbackType> clbck = filters.get(name).stream()
                            .map((c) -> CallbackType.valueOf(c)).collect(Collectors.toList());
                    filterByCallBackTypes(clbck.toArray(new CallbackType[clbck.size()]));
                    break;
                default:
                    throw new GravityIllegalArgumentException("filter{" + name + "}",EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
            }
        }

    }

    private ContactScheduledQuery orderByPriority(Boolean asc)
    {
        setOrederBy("Priority", asc);
        return this;
    }

    private ContactScheduledQuery orderByDialSeq(Boolean asc)
    {
        setOrederBy("DialSeq", asc);
        return this;
    }

    private ContactScheduledQuery orderByScheduledOn(Boolean asc)
    {
        setOrederBy("ScheduledOn", asc);
        return this;
    }

    @Override
    protected void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws GravityIllegalArgumentException
    {
        for (HashMap<String, Boolean> hm : orderby)
        {
            for (String name : hm.keySet())
            {
                Boolean isAsc = hm.get(name);
                switch (name.toLowerCase())
                {
                    case "id":
                        orderById(isAsc);
                        break;
                    case "createdon":
                        orderByCreatedOn(isAsc);
                        break;
                    case "priority":
                        orderByPriority(isAsc);
                        break;
                    case "dialseq":
                        orderByDialSeq(isAsc);
                        break;
                    case "scheduledon":
                        orderByScheduledOn(isAsc);
                        break;
                    default:
                        throw new GravityIllegalArgumentException("orderby{" + name + "}",EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.InvalidParamName);
                }
            }
        }
    }

}
