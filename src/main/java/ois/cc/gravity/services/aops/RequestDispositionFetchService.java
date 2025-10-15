package ois.cc.gravity.services.aops;

import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityNotFound;
import code.ua.events.EventFailedCause;
import code.ua.requests.Request;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPs;
import ois.cc.gravity.db.queries.AOPsMediaQuery;
import ois.cc.gravity.db.queries.DispositionQuery;
import ois.cc.gravity.framework.events.common.EventEntitiesFetched;
import ois.cc.gravity.framework.requests.common.RequestEntityFetch;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.radius.ca.enums.Channel;

import java.util.ArrayList;

public class RequestDispositionFetchService extends ARequestEntityService
{

    public RequestDispositionFetchService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        // filter is requied for dispositionFetch need to know will fetch by super or byonlyparent.
        RequestEntityFetch req = (RequestEntityFetch) request;

        if (req.getFilters() == null)
        {
            throw new GravityIllegalArgumentException("Filters", EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.NonOptionalConstraintViolation);
        }

        Channel reqChn = req.getFilterValue("bychannel", Channel.class);
        Long campid = req.getFilterValue("byaops", Long.class);

        AOPs camp = null;
        if (campid != null)
        {
            camp = _tctx.getDB().FindAssert(AOPs.class, campid);

            if (reqChn != null && !isValidChannel(camp, reqChn))
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.IllegalCampaignMedia);
//                EventAttributeInvalid ev = new EventAttributeInvalid(req, EN.Disposition.name(), "Channel");
//                return ev;
            }
        }

        /**
         * Based on filter parameter EN will be chose. <br>
         * - If filter contains 'byparentonly' and its value is ture then we are sure the entity must be super disposition (Disposition0). <br>
         * - If filter contains 'bysuper' then we are sure entity will be sub-disposition(Disposition1). <br>
         * - Else entity will be Disposition.
         */
        EN en = EN.Disposition;
        if (req.getFilters().containsKey("byonlyparent")
                && (Boolean.parseBoolean(req.getFilters().get("byonlyparent").get(0))))
        {
            en = EN.Disposition0;
        }
        else if (req.getFilters().containsKey("bysuper"))
        {
            en = EN.Disposition1;
        }

        DispositionQuery dispQry = new DispositionQuery(en);

        if (req.getFilters() != null)
        {
            dispQry.ApplyFilters(req.getFilters());
        }

        dispQry.ApplyOrderBy(req.getOrderBy());

        /**
         * We are filtering explicitly here as if there is no campaign filter the instate of sending all we need to send Non-campaign dispositions.
         */
        {   //add campaign filter
            if (camp != null)
            {
                dispQry.filterByAops(camp.getId());
            }
            else
            {
                dispQry.forNonAOPs();
            }
        }

        Integer recCount = null;

        Boolean reqcnt = req.getIncludeCount() == null ? false : req.getIncludeCount();
        if (reqcnt)
        {
            recCount = _tctx.getDB().SelectCount(dispQry);
        }

        dispQry.setLimit(req.getLimit());
        dispQry.setOffset(req.getOffset());

        ArrayList<AEntity> alDisp = _tctx.getDB().Select(dispQry);

        if (alDisp == null || alDisp.isEmpty())
        {
            EventEntityNotFound ev = new EventEntityNotFound(request, "Disposition");
            return ev;
        }
        EventEntitiesFetched ev = new EventEntitiesFetched(req, new ArrayList<>(alDisp));
        ev.setRecordCount(recCount);
        return ev;
    }

    /**
     * Return true if the supplied campaign have given channel.
     *
     * @param camp
     * @param chn
     * @return
     */
    private Boolean isValidChannel(AOPs camp, Channel chn) throws CODEException, CODEException, GravityException {

        AOPsMediaQuery cmq = new AOPsMediaQuery().filterByAOPs(camp.getId()).filterByChannel(chn);
        return !_tctx.getDB().SelectCount(cmq).equals(0l);
    }
}
