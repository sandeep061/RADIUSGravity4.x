package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.JSONUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.db.jpa.JPAQuery;
import code.entities.EntityState;
import code.ua.events.Event;
import code.ua.events.EventFailedCause;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.Limits;
import ois.cc.gravity.db.queries.*;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.aops.RequestAOPsPropertiesConfig;
import ois.cc.gravity.services.common.RequestEntityAddService;
import ois.cc.gravity.services.exceptions.*;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.Channel;
import ois.radius.ca.enums.aops.AOPsType;
import ois.radius.ca.enums.aops.CallbackType;
import ois.radius.ca.enums.aops.DialMode;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.Process;
import ois.radius.cc.entities.tenant.cc.*;
import org.json.JSONArray;
import org.vn.radius.cc.platform.exceptions.RADException;

import java.util.*;
import java.util.stream.Collectors;

public class RequestAOPsPropertiesConfigService extends RequestEntityAddService
{

    public RequestAOPsPropertiesConfigService(UAClient uac)
    {
        super(uac);
    }

    private ArrayList<NameValuePair> entities = new ArrayList<>();

    private AOPs _aops = null;

    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestAOPsPropertiesConfig req = (RequestAOPsPropertiesConfig) request;

        _aops = _tctx.getDB().FindAssert(new AOPsQuery().filterById(req.getCampaign()));

        HashMap<String, String> hmAttr = req.getAttributes();

        //Validation of Request Attributes.
        Event ev = IsValidRequestAtributes(req, _aops, hmAttr);
        if (ev != null)
        {
            return ev;
        }

        /**
         * Find the AOPsProperties entity for this Campaign, if not found create new.
         */
        AOPsProperties aopsProp = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()));
        if (aopsProp == null)
        {
            InitAOPsProperties(hmAttr);
        }

        /**
         * Ensure AOPsProperties is set with basis required attributes for each channel of this campaign. This is required as we may get request for channel
         * specific properties in this request.
         */
        InitChannelProperties(_aops, hmAttr);

        getCampProps(hmAttr);

        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        EventSuccess evs = new EventSuccess(request);
        return evs;
    }

    /**
     * This method will create a CampaignProperty and return with the default values.
     *
     * @param
     * @return
     */
    private void InitAOPsProperties(HashMap<String, String> hmattr) throws Exception, GravityException, CODEException
    {

        AOPsProperties aopGlobalAutoStart = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AutoStart));
        if (aopGlobalAutoStart == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_AutoStart.name()))
            {
                aopGlobalAutoStart = new AOPsProperties();
                aopGlobalAutoStart.setAOPs(_aops);
                aopGlobalAutoStart.setConfKey(AOPsProperties.Keys.Global_AutoStart.name());
                aopGlobalAutoStart.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobalAutoStart));
            }
        }

        AOPsProperties aopGlobalAgentReadyOn = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AgentReadyOn));
        if (aopGlobalAgentReadyOn == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_AgentReadyOn.name()))
            {
                aopGlobalAgentReadyOn = new AOPsProperties();
                aopGlobalAgentReadyOn.setAOPs(_aops);
                aopGlobalAgentReadyOn.setConfKey(AOPsProperties.Keys.Global_AgentReadyOn.name());
                aopGlobalAgentReadyOn.setConfValue(AOPsProperties.AgentReadyOn.Never.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobalAgentReadyOn));
            }
        }
        AOPsProperties aopgGlobalAgentContactAddressAccesses = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AgentContactAddressAccesses));
        if (aopgGlobalAgentContactAddressAccesses == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_AgentContactAddressAccesses.name()))
            {
                aopgGlobalAgentContactAddressAccesses = new AOPsProperties();
                aopgGlobalAgentContactAddressAccesses.setAOPs(_aops);
                aopgGlobalAgentContactAddressAccesses.setConfKey(AOPsProperties.Keys.Global_AgentContactAddressAccesses.name());
                List<String> alAgCon = Arrays.asList(AOPsProperties.AgentContactAddressAccess.CanView.name());
                JSONArray alarray = new JSONArray(alAgCon);
                aopgGlobalAgentContactAddressAccesses.setConfValue(alarray.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgGlobalAgentContactAddressAccesses));
            }
        }
        AOPsProperties aopgGlobal_ContactScheduledMaxDuration = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_ContactScheduledMaxDuration));
        if (aopgGlobal_ContactScheduledMaxDuration == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_ContactScheduledMaxDuration.name()))
            {
                aopgGlobal_ContactScheduledMaxDuration = new AOPsProperties();
                aopgGlobal_ContactScheduledMaxDuration.setAOPs(_aops);
                aopgGlobal_ContactScheduledMaxDuration.setConfKey(AOPsProperties.Keys.Global_ContactScheduledMaxDuration.name());
                aopgGlobal_ContactScheduledMaxDuration.setConfValue(Limits.ContactScheduledOnAfter_MAX.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgGlobal_ContactScheduledMaxDuration));
            }
        }
        AOPsProperties aopgGlobal_ContactScheduledExpiryTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_ContactScheduledExpiryTimeout));
        if (aopgGlobal_ContactScheduledExpiryTimeout == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_ContactScheduledExpiryTimeout.name()))
            {
                aopgGlobal_ContactScheduledExpiryTimeout = new AOPsProperties();
                aopgGlobal_ContactScheduledExpiryTimeout.setAOPs(_aops);
                aopgGlobal_ContactScheduledExpiryTimeout.setConfKey(AOPsProperties.Keys.Global_ContactScheduledExpiryTimeout.name());
                aopgGlobal_ContactScheduledExpiryTimeout.setConfValue(Limits.ContactScheduled_AutoExpiry_Hour.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgGlobal_ContactScheduledExpiryTimeout));
            }
        }
        AOPsProperties aopgGlobal_RedialExpiryTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_RedialExpiryTimeout));
        if (aopgGlobal_RedialExpiryTimeout == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_RedialExpiryTimeout.name()))
            {
                aopgGlobal_RedialExpiryTimeout = new AOPsProperties();
                aopgGlobal_RedialExpiryTimeout.setAOPs(_aops);
                aopgGlobal_RedialExpiryTimeout.setConfKey(AOPsProperties.Keys.Global_RedialExpiryTimeout.name());
                aopgGlobal_RedialExpiryTimeout.setConfValue(Limits.Global_RedialExpiryTimeout.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgGlobal_RedialExpiryTimeout));
            }
        }
        AOPsProperties aopgGlobalCallbackTypes = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_CallbackTypes));
        if (aopgGlobalCallbackTypes == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_CallbackTypes.name()))
            {

                aopgGlobalCallbackTypes = new AOPsProperties();
                aopgGlobalCallbackTypes.setAOPs(_aops);
                aopgGlobalCallbackTypes.setConfKey(AOPsProperties.Keys.Global_CallbackTypes.name());
                List<String> alcltype = Arrays.asList(CallbackType.Agent.name(), CallbackType.Campaign.name(), CallbackType.Personal.name(), CallbackType.Skill.name());
                JSONArray alclarray = new JSONArray(alcltype);
                aopgGlobalCallbackTypes.setConfValue(alclarray.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgGlobalCallbackTypes));
            }
        }
        AOPsProperties aopPropsSsoen = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SSOEnabled));
        if (aopPropsSsoen == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_SSOEnabled.name()))
            {
                aopPropsSsoen = new AOPsProperties();
                aopPropsSsoen.setAOPs(_aops);
                aopPropsSsoen.setConfKey(AOPsProperties.Keys.Global_SSOEnabled.name());
                aopPropsSsoen.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopPropsSsoen));
            }
        }

        AOPsProperties aopGlobal_StickyAgent = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_StickyAgent));
        if (aopGlobal_StickyAgent == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_StickyAgent.name()))
            {
                aopGlobal_StickyAgent = new AOPsProperties();
                aopGlobal_StickyAgent.setAOPs(_aops);
                aopGlobal_StickyAgent.setConfKey(AOPsProperties.Keys.Global_StickyAgent.name());
                aopGlobal_StickyAgent.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_StickyAgent));
            }
        }

        AOPsProperties aopGlobal_StrictlyStickyAgent = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_StrictlyStickyAgent));

        if (aopGlobal_StrictlyStickyAgent == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_StrictlyStickyAgent.name()))
            {

                aopGlobal_StrictlyStickyAgent = new AOPsProperties();
                aopGlobal_StrictlyStickyAgent.setAOPs(_aops);
                aopGlobal_StrictlyStickyAgent.setConfKey(AOPsProperties.Keys.Global_StrictlyStickyAgent.name());
                aopGlobal_StrictlyStickyAgent.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_StrictlyStickyAgent));
            }
        }

        AOPsProperties aopGlobal_AutoPreview = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AutoPreview));
        if (aopGlobal_AutoPreview == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_AutoPreview.name()))
            {
                aopGlobal_AutoPreview = new AOPsProperties();
                aopGlobal_AutoPreview.setAOPs(_aops);
                aopGlobal_AutoPreview.setConfKey(AOPsProperties.Keys.Global_AutoPreview.name());
                aopGlobal_AutoPreview.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_AutoPreview));
            }
        }

        AOPsProperties aopGlobal_SessionDoneTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SessionDoneTimeout));
        if (aopGlobal_SessionDoneTimeout == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_SessionDoneTimeout.name()))
            {

                aopGlobal_SessionDoneTimeout = new AOPsProperties();
                aopGlobal_SessionDoneTimeout.setAOPs(_aops);
                aopGlobal_SessionDoneTimeout.setConfKey(AOPsProperties.Keys.Global_SessionDoneTimeout.name());
                aopGlobal_SessionDoneTimeout.setConfValue(Integer.valueOf(0).toString());
                entities.add(new NameValuePair("Insert", aopGlobal_SessionDoneTimeout));
            }
        }
        AOPsProperties aopGlobal_SessionDoneIsAuto = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SessionDoneIsAuto));
        if (aopGlobal_SessionDoneIsAuto == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_SessionDoneIsAuto.name()))
            {

                aopGlobal_SessionDoneIsAuto = new AOPsProperties();
                aopGlobal_SessionDoneIsAuto.setAOPs(_aops);
                aopGlobal_SessionDoneIsAuto.setConfKey(AOPsProperties.Keys.Global_SessionDoneIsAuto.name());
                aopGlobal_SessionDoneIsAuto.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_SessionDoneIsAuto));
            }
        }

        AOPsProperties aopGlobal_DisposeTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_DisposeTimeout));
        if (aopGlobal_DisposeTimeout == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_DisposeTimeout.name()))
            {

                aopGlobal_DisposeTimeout = new AOPsProperties();
                aopGlobal_DisposeTimeout.setAOPs(_aops);
                aopGlobal_DisposeTimeout.setConfKey(AOPsProperties.Keys.Global_DisposeTimeout.name());
                aopGlobal_DisposeTimeout.setConfValue(Integer.valueOf(0).toString());
                entities.add(new NameValuePair("Insert", aopGlobal_DisposeTimeout));
            }
        }

        AOPsProperties aopGlobal_DIsposeIsAuto = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_DisposeIsAuto));
        if (aopGlobal_DIsposeIsAuto == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_DisposeIsAuto.name()))
            {

                aopGlobal_DIsposeIsAuto = new AOPsProperties();
                aopGlobal_DIsposeIsAuto.setAOPs(_aops);
                aopGlobal_DIsposeIsAuto.setConfKey(AOPsProperties.Keys.Global_DisposeIsAuto.name());
                aopGlobal_DIsposeIsAuto.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_DIsposeIsAuto));
            }
        }

        AOPsProperties aopGlobal_DisposeExtend = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_DisposeExtend));
        if (aopGlobal_DisposeExtend == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_DisposeExtend.name()))
            {

                aopGlobal_DisposeExtend = new AOPsProperties();
                aopGlobal_DisposeExtend.setAOPs(_aops);
                aopGlobal_DisposeExtend.setConfKey(AOPsProperties.Keys.Global_DisposeExtend.name());
                aopGlobal_DisposeExtend.setConfValue(Limits.DisposeExtend.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_DisposeExtend));
            }
        }
        AOPsProperties aopGlobal_SessionExtend = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SessionExtend));
        if (aopGlobal_SessionExtend == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_SessionExtend.name()))
            {
                aopGlobal_SessionExtend = new AOPsProperties();
                aopGlobal_SessionExtend.setAOPs(_aops);
                aopGlobal_SessionExtend.setConfKey(AOPsProperties.Keys.Global_SessionExtend.name());
                aopGlobal_SessionExtend.setConfValue(Limits.SessionExtend.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_SessionExtend));
            }
        }

        AOPsProperties aopGlobal_AIDisposeEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AIDisposeEnabled));
        if (aopGlobal_AIDisposeEnabled == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_AIDisposeEnabled.name()))
            {
                aopGlobal_AIDisposeEnabled = new AOPsProperties();
                aopGlobal_AIDisposeEnabled.setAOPs(_aops);
                aopGlobal_AIDisposeEnabled.setConfKey(AOPsProperties.Keys.Global_AIDisposeEnabled.name());
                aopGlobal_AIDisposeEnabled.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_AIDisposeEnabled));
            }
        }
        AOPsProperties aopGlobal_AIDisposeOverride = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AIDisposeOverride));
        if (aopGlobal_AIDisposeOverride == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_AIDisposeOverride.name()))
            {
                aopGlobal_AIDisposeOverride = new AOPsProperties();
                aopGlobal_AIDisposeOverride.setAOPs(_aops);
                aopGlobal_AIDisposeOverride.setConfKey(AOPsProperties.Keys.Global_AIDisposeOverride.name());
                aopGlobal_AIDisposeOverride.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_AIDisposeEnabled));
            }
        }

        AOPsProperties aopGlobalCanRejectPreview = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_CanRejectPreview));
        if (aopGlobalCanRejectPreview == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_CanRejectPreview.name()))
            {
                aopGlobalCanRejectPreview = new AOPsProperties();
                aopGlobalCanRejectPreview.setAOPs(_aops);
                aopGlobalCanRejectPreview.setConfKey(AOPsProperties.Keys.Global_CanRejectPreview.name());
                aopGlobalCanRejectPreview.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobalCanRejectPreview));
            }
        }
        AOPsProperties aopGlobalPreviewTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_PreviewTimeout));
        if (aopGlobalPreviewTimeout == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_PreviewTimeout.name()))
            {
                aopGlobalPreviewTimeout = new AOPsProperties();
                aopGlobalPreviewTimeout.setAOPs(_aops);
                aopGlobalPreviewTimeout.setConfKey(AOPsProperties.Keys.Global_PreviewTimeout.name());
                aopGlobalPreviewTimeout.setConfValue(Limits.Global_PreviewTimeout.toString());
                entities.add(new NameValuePair("Insert", aopGlobalPreviewTimeout));
            }
        }

        AOPsProperties aopGlobal_EnableAbandonTreatment = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_EnableAbandonTreatment));
        if (aopGlobal_EnableAbandonTreatment == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_EnableAbandonTreatment.name()))
            {
                aopGlobal_EnableAbandonTreatment = new AOPsProperties();
                aopGlobal_EnableAbandonTreatment.setAOPs(_aops);
                aopGlobal_EnableAbandonTreatment.setConfKey(AOPsProperties.Keys.Global_EnableAbandonTreatment.name());
                aopGlobal_EnableAbandonTreatment.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_EnableAbandonTreatment));
            }
        }
        AOPsProperties aopGlobal_Global_MaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_MaxAttemptCount));
        if (aopGlobal_Global_MaxAttemptCount == null)
        {
            if (!hmattr.containsKey(AOPsProperties.Keys.Global_MaxAttemptCount.name()))
            {
                aopGlobal_Global_MaxAttemptCount = new AOPsProperties();
                aopGlobal_Global_MaxAttemptCount.setAOPs(_aops);
                aopGlobal_Global_MaxAttemptCount.setConfKey(AOPsProperties.Keys.Global_MaxAttemptCount.name());
                aopGlobal_Global_MaxAttemptCount.setConfValue(Limits.AttemptCount_MAX.toString());
                entities.add(new NameValuePair("Insert", aopGlobal_Global_MaxAttemptCount));
            }
        }
    }

    /**
     * Ensure the default campaign properties are set as required. <br>
     * - this method will be called every time any property config request sent, so that if any changes done to campaign, those respective properties should
     * also be set.
     *
     * @param hmattr
     * @return
     * @throws RADException
     */
    private void InitChannelProperties(AOPs aops, HashMap<String, String> hmattr) throws RADException, GravityException, CODEException, Exception
    {

        if (hmattr.isEmpty())
        {
            return;
        }
        JPAQuery dbq = new JPAQuery("Select cm.Channel from AOPsMedia cm " + " Where cm.AOPs.Id =: campid");
        dbq.setParam("campid", aops.getId());

        ArrayList<Channel> chnList = _tctx.getDB().SelectList(dbq);
        if (chnList == null)
        {

            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.NoCampaignMediaConfigured);

        }
        for (Channel chn : chnList)
        {
            if (chn.equals(Channel.Call))
            {

                AOPsProperties aopsprop = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_AllowReject));
                if (aopsprop == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_AllowReject.name()))
                    {
                        AOPsProperties xtProps = new AOPsProperties();
                        xtProps.setAOPs(_aops);
                        xtProps.setConfKey(AOPsProperties.Keys.XT_AllowReject.name());
                        xtProps.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), xtProps));
                    }

                }
                AOPsProperties aopXT_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_AutoAnswer));
                if (aopXT_AutoAnswer == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_AutoAnswer.name()))
                    {
                        AOPsProperties xtPropsAutoAns = new AOPsProperties();
                        xtPropsAutoAns.setAOPs(_aops);
                        xtPropsAutoAns.setConfKey(AOPsProperties.Keys.XT_AutoAnswer.name());
                        xtPropsAutoAns.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), xtPropsAutoAns));
                    }

                }
                AOPsProperties aopXT_DialTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_DialTimeout));
                if (aopXT_DialTimeout == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_DialTimeout.name()))
                    {
                        aopXT_DialTimeout = new AOPsProperties();
                        aopXT_DialTimeout.setAOPs(_aops);
                        aopXT_DialTimeout.setConfKey(AOPsProperties.Keys.XT_DialTimeout.name());
                        aopXT_DialTimeout.setConfValue(Limits.Default_DialTimeOut.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXT_DialTimeout));
                    }

                }
                AOPsProperties aopXTAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_AutoAnswerDelay));
                if (aopXTAutoAnswerDelay == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_AutoAnswerDelay.name()))
                    {
                        aopXTAutoAnswerDelay = new AOPsProperties();
                        aopXTAutoAnswerDelay.setAOPs(_aops);
                        aopXTAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XT_AutoAnswerDelay.name());
                        aopXTAutoAnswerDelay.setConfValue(Limits.AutoAnsDelay.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTAutoAnswerDelay));
                    }

                }
                AOPsProperties aopXT_RecordingAlert = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_RecordingAlert));
                if (aopXT_RecordingAlert == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_RecordingAlert.name()))
                    {
                        aopXT_RecordingAlert = new AOPsProperties();
                        aopXT_RecordingAlert.setAOPs(_aops);
                        aopXT_RecordingAlert.setConfKey(AOPsProperties.Keys.XT_RecordingAlert.name());
                        aopXT_RecordingAlert.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXT_RecordingAlert));
                    }

                }

                AOPsProperties aopXTPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_PostSessRecEnabled));
                if (aopXTPostSessRecEnabled == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_PostSessRecEnabled.name()))
                    {
                        aopXTPostSessRecEnabled = new AOPsProperties();
                        aopXTPostSessRecEnabled.setAOPs(_aops);
                        aopXTPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XT_PostSessRecEnabled.name());
                        aopXTPostSessRecEnabled.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTPostSessRecEnabled));
                    }

                }

                AOPsProperties aopXTEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_EncryptRecordingFile));
                if (aopXTEncryptRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_EncryptRecordingFile.name()))
                    {
                        aopXTEncryptRecordingFile = new AOPsProperties();
                        aopXTEncryptRecordingFile.setAOPs(_aops);
                        aopXTEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XT_EncryptRecordingFile.name());
                        aopXTEncryptRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTEncryptRecordingFile));
                    }

                }

                AOPsProperties aopXTCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_CompressRecordingFile));
                if (aopXTCompressRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_CompressRecordingFile.name()))
                    {
                        aopXTCompressRecordingFile = new AOPsProperties();
                        aopXTCompressRecordingFile.setAOPs(_aops);
                        aopXTCompressRecordingFile.setConfKey(AOPsProperties.Keys.XT_CompressRecordingFile.name());
                        aopXTCompressRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTCompressRecordingFile));
                    }

                }
                AOPsProperties aopXT_RecordingBeepDuration = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_RecordingBeepDuration));
                if (aopXT_RecordingBeepDuration == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_RecordingBeepDuration.name()))
                    {
                        aopXT_RecordingBeepDuration = new AOPsProperties();
                        aopXT_RecordingBeepDuration.setAOPs(_aops);
                        aopXT_RecordingBeepDuration.setConfKey(AOPsProperties.Keys.XT_RecordingBeepDuration.name());
                        aopXT_RecordingBeepDuration.setConfValue(Limits.XT_RecordingBeepDuration.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXT_RecordingBeepDuration));
                    }

                }
                AOPsProperties aopXTEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_EnableScreenRecording));
                if (aopXTEnableScreenRecording == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XT_EnableScreenRecording.name()))
                    {
                        aopXTEnableScreenRecording = new AOPsProperties();
                        aopXTEnableScreenRecording.setAOPs(_aops);
                        aopXTEnableScreenRecording.setConfKey(AOPsProperties.Keys.XT_EnableScreenRecording.name());
                        aopXTEnableScreenRecording.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTEnableScreenRecording));
                    }

                }

                if (aops.getAOPsType().equals(AOPsType.Process))
                {
                    Process pro = (Process) aops;
                    switch (pro.getProcessType())
                    {
                        case Inbound:
                            build_XT_IB(hmattr);
                            continue;
                        case Blended:
                            build_XT_OB(hmattr);
                            build_XT_IB(hmattr);
                            continue;
                    }
                }
                else
                {
                    build_XT_OB(hmattr);
                    continue;
                }
            }

            if (chn.equals(Channel.Email))
            {

                AOPsProperties aopgXEM_AllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_AllowReject));
                if (aopgXEM_AllowReject == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XEM_AllowReject.name()))
                    {
                        AOPsProperties aopsXEMAllowReject = new AOPsProperties();
                        aopsXEMAllowReject.setAOPs(_aops);
                        aopsXEMAllowReject.setConfKey(AOPsProperties.Keys.XEM_AllowReject.name());
                        aopsXEMAllowReject.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXEMAllowReject));

                    }
                }
                AOPsProperties aopgXEM_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_AutoAnswer));
                if (aopgXEM_AutoAnswer == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XEM_AutoAnswer.name()))
                    {
                        AOPsProperties aopsXEMAutoAnswer = new AOPsProperties();
                        aopsXEMAutoAnswer.setAOPs(_aops);
                        aopsXEMAutoAnswer.setConfKey(AOPsProperties.Keys.XEM_AutoAnswer.name());
                        aopsXEMAutoAnswer.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXEMAutoAnswer));
                    }
                }
                AOPsProperties aopXEMAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_AutoAnswerDelay));
                if (aopXEMAutoAnswerDelay == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XEM_AutoAnswerDelay.name()))
                    {
                        aopXEMAutoAnswerDelay = new AOPsProperties();
                        aopXEMAutoAnswerDelay.setAOPs(_aops);
                        aopXEMAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XEM_AutoAnswerDelay.name());
                        aopXEMAutoAnswerDelay.setConfValue(Limits.AutoAnsDelay.toString().toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMAutoAnswerDelay));
                    }

                }
                AOPsProperties aopXEMDialTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_DialTimeout));
                if (aopXEMDialTimeout == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XEM_DialTimeout.name()))
                    {
                        aopXEMDialTimeout = new AOPsProperties();
                        aopXEMDialTimeout.setAOPs(_aops);
                        aopXEMDialTimeout.setConfKey(AOPsProperties.Keys.XEM_DialTimeout.name());
                        aopXEMDialTimeout.setConfValue(Limits.Default_DialTimeOut.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMDialTimeout));
                    }

                }
                AOPsProperties aopXEMPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_PostSessRecEnabled));
                if (aopXEMPostSessRecEnabled == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XEM_PostSessRecEnabled.name()))
                    {
                        aopXEMPostSessRecEnabled = new AOPsProperties();
                        aopXEMPostSessRecEnabled.setAOPs(_aops);
                        aopXEMPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XEM_PostSessRecEnabled.name());
                        aopXEMPostSessRecEnabled.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMPostSessRecEnabled));
                    }

                }
                AOPsProperties aopXEMEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_EncryptRecordingFile));
                if (aopXEMEncryptRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XEM_EncryptRecordingFile.name()))
                    {
                        aopXEMEncryptRecordingFile = new AOPsProperties();
                        aopXEMEncryptRecordingFile.setAOPs(_aops);
                        aopXEMEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XEM_EncryptRecordingFile.name());
                        aopXEMEncryptRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMEncryptRecordingFile));
                    }

                }

                AOPsProperties aopXEMCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_CompressRecordingFile));
                if (aopXEMCompressRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XEM_CompressRecordingFile.name()))
                    {
                        aopXEMCompressRecordingFile = new AOPsProperties();
                        aopXEMCompressRecordingFile.setAOPs(_aops);
                        aopXEMCompressRecordingFile.setConfKey(AOPsProperties.Keys.XEM_CompressRecordingFile.name());
                        aopXEMCompressRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMCompressRecordingFile));
                    }

                }
                AOPsProperties aopXEMEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_EnableScreenRecording));
                if (aopXEMEnableScreenRecording == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XEM_EnableScreenRecording.name()))
                    {
                        aopXEMEnableScreenRecording = new AOPsProperties();
                        aopXEMEnableScreenRecording.setAOPs(_aops);
                        aopXEMEnableScreenRecording.setConfKey(AOPsProperties.Keys.XEM_EnableScreenRecording.name());
                        aopXEMEnableScreenRecording.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMEnableScreenRecording));
                    }

                }

                if (aops.getAOPsType().equals(AOPsType.Process))
                {
                    Process pro = (Process) aops;
                    switch (pro.getProcessType())
                    {
                        case Inbound:
                            build_XEM_IB(hmattr);
                            continue;

                        case Blended:
                            build_XEM_OB(hmattr);
                            continue;
                    }
                }
                else
                {
                    build_XEM_OB(hmattr);

                    continue;
                }
            }

            if (chn.equals(Channel.Chat))
            {
                AOPsProperties aopgXCH_AllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_AllowReject));
                if (aopgXCH_AllowReject == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_AllowReject.name()))
                    {
                        AOPsProperties aopsXCAllowReject = new AOPsProperties();
                        aopsXCAllowReject.setAOPs(_aops);
                        aopsXCAllowReject.setConfKey(AOPsProperties.Keys.XCH_AllowReject.name());
                        aopsXCAllowReject.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXCAllowReject));
                    }
                }
                AOPsProperties aopgXCH_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_AutoAnswer));
                if (aopgXCH_AutoAnswer == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_AutoAnswer.name()))
                    {
                        AOPsProperties aopsXCAutoAns = new AOPsProperties();
                        aopsXCAutoAns.setAOPs(_aops);
                        aopsXCAutoAns.setConfKey(AOPsProperties.Keys.XCH_AutoAnswer.name());
                        aopsXCAutoAns.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXCAutoAns));
                    }
                }
                AOPsProperties aopXCHAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_AutoAnswerDelay));
                if (aopXCHAutoAnswerDelay == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_AutoAnswerDelay.name()))
                    {
                        aopXCHAutoAnswerDelay = new AOPsProperties();
                        aopXCHAutoAnswerDelay.setAOPs(_aops);
                        aopXCHAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XCH_AutoAnswerDelay.name());
                        aopXCHAutoAnswerDelay.setConfValue(Limits.AutoAnsDelay.toString().toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHAutoAnswerDelay));
                    }

                }
                AOPsProperties aopXCH_KBEnable = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_KBEnable));
                if (aopXCH_KBEnable == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_KBEnable.name()))
                    {
                        aopXCH_KBEnable = new AOPsProperties();
                        aopXCH_KBEnable.setAOPs(_aops);
                        aopXCH_KBEnable.setConfKey(AOPsProperties.Keys.XCH_KBEnable.name());
                        aopXCH_KBEnable.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_KBEnable));
                    }

                }

                AOPsProperties aopXCH_KBAutoAssist = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_KBAutoAssist));
                if (aopXCH_KBAutoAssist == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_KBAutoAssist.name()))
                    {
                        aopXCH_KBAutoAssist = new AOPsProperties();
                        aopXCH_KBAutoAssist.setAOPs(_aops);
                        aopXCH_KBAutoAssist.setConfKey(AOPsProperties.Keys.XCH_KBAutoAssist.name());
                        aopXCH_KBAutoAssist.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_KBAutoAssist));
                    }

                }
                AOPsProperties aopsXCH_KBAutoReply = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_KBAutoReply));
                if (aopsXCH_KBAutoReply == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_KBAutoReply.name()))
                    {
                        aopsXCH_KBAutoReply = new AOPsProperties();
                        aopsXCH_KBAutoReply.setAOPs(_aops);
                        aopsXCH_KBAutoReply.setConfKey(AOPsProperties.Keys.XCH_KBAutoReply.name());
                        aopsXCH_KBAutoReply.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXCH_KBAutoReply));
                    }

                }
                AOPsProperties aopsXCH_KBVerifyNReply = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_KBVerifyNReply));
                if (aopsXCH_KBVerifyNReply == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_KBVerifyNReply.name()))
                    {
                        aopsXCH_KBVerifyNReply = new AOPsProperties();
                        aopsXCH_KBVerifyNReply.setAOPs(_aops);
                        aopsXCH_KBVerifyNReply.setConfKey(AOPsProperties.Keys.XCH_KBVerifyNReply.name());
                        aopsXCH_KBVerifyNReply.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXCH_KBVerifyNReply));
                    }

                }

                AOPsProperties aopXCHPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_PostSessRecEnabled));
                if (aopXCHPostSessRecEnabled == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_PostSessRecEnabled.name()))
                    {
                        aopXCHPostSessRecEnabled = new AOPsProperties();
                        aopXCHPostSessRecEnabled.setAOPs(_aops);
                        aopXCHPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XCH_PostSessRecEnabled.name());
                        aopXCHPostSessRecEnabled.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHPostSessRecEnabled));
                    }

                }

                AOPsProperties aopXCHEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_EncryptRecordingFile));
                if (aopXCHEncryptRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_EncryptRecordingFile.name()))
                    {
                        aopXCHEncryptRecordingFile = new AOPsProperties();
                        aopXCHEncryptRecordingFile.setAOPs(_aops);
                        aopXCHEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XCH_EncryptRecordingFile.name());
                        aopXCHEncryptRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHEncryptRecordingFile));
                    }

                }

                AOPsProperties aopXCHCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_CompressRecordingFile));
                if (aopXCHCompressRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_CompressRecordingFile.name()))
                    {
                        aopXCHCompressRecordingFile = new AOPsProperties();
                        aopXCHCompressRecordingFile.setAOPs(_aops);
                        aopXCHCompressRecordingFile.setConfKey(AOPsProperties.Keys.XCH_CompressRecordingFile.name());
                        aopXCHCompressRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHCompressRecordingFile));
                    }

                }

                AOPsProperties aopXCHEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_EnableScreenRecording));
                if (aopXCHEnableScreenRecording == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XCH_EnableScreenRecording.name()))
                    {
                        aopXCHEnableScreenRecording = new AOPsProperties();
                        aopXCHEnableScreenRecording.setAOPs(_aops);
                        aopXCHEnableScreenRecording.setConfKey(AOPsProperties.Keys.XCH_EnableScreenRecording.name());
                        aopXCHEnableScreenRecording.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHEnableScreenRecording));
                    }

                }
                if (aops.getAOPsType().equals(AOPsType.Process))
                {
                    Process pro = (Process) aops;
                    switch (pro.getProcessType())
                    {
                        case Inbound:
                            build_XCH_IB(hmattr);
                            continue;

                        case Blended:
                            build_XCH_OB(hmattr);
                            continue;
                    }
                }
                else
                {
                    build_XCH_OB(hmattr);
                    continue;
                }

            }

            if (chn.equals(Channel.Social))
            {

                AOPsProperties aopgXSO_AllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_AllowReject));
                if (aopgXSO_AllowReject == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XSO_AllowReject.name()))
                    {
                        AOPsProperties aopsXSOAllowReject = new AOPsProperties();
                        aopsXSOAllowReject.setAOPs(_aops);
                        aopsXSOAllowReject.setConfKey(AOPsProperties.Keys.XSO_AllowReject.name());
                        aopsXSOAllowReject.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXSOAllowReject));
                    }
                }
                AOPsProperties aopgXSO_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_AutoAnswer));
                if (aopgXSO_AutoAnswer == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XSO_AutoAnswer.name()))
                    {
                        AOPsProperties aopsXSOAutoAns = new AOPsProperties();
                        aopsXSOAutoAns.setAOPs(_aops);
                        aopsXSOAutoAns.setConfKey(AOPsProperties.Keys.XSO_AutoAnswer.name());
                        aopsXSOAutoAns.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXSOAutoAns));
                    }
                }
                AOPsProperties aopXSOAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_AutoAnswerDelay));
                if (aopXSOAutoAnswerDelay == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XSO_AutoAnswerDelay.name()))
                    {
                        aopXSOAutoAnswerDelay = new AOPsProperties();
                        aopXSOAutoAnswerDelay.setAOPs(_aops);
                        aopXSOAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XSO_AutoAnswerDelay.name());
                        aopXSOAutoAnswerDelay.setConfValue(Limits.AutoAnsDelay.toString().toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOAutoAnswerDelay));
                    }

                }

                AOPsProperties aopXSOPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_PostSessRecEnabled));
                if (aopXSOPostSessRecEnabled == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XSO_PostSessRecEnabled.name()))
                    {
                        aopXSOPostSessRecEnabled = new AOPsProperties();
                        aopXSOPostSessRecEnabled.setAOPs(_aops);
                        aopXSOPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XSO_PostSessRecEnabled.name());
                        aopXSOPostSessRecEnabled.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOPostSessRecEnabled));
                    }

                }

                AOPsProperties aopXSOEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_EncryptRecordingFile));
                if (aopXSOEncryptRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XSO_EncryptRecordingFile.name()))
                    {
                        aopXSOEncryptRecordingFile = new AOPsProperties();
                        aopXSOEncryptRecordingFile.setAOPs(_aops);
                        aopXSOEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XSO_EncryptRecordingFile.name());
                        aopXSOEncryptRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOEncryptRecordingFile));
                    }

                }

                AOPsProperties aopXSOCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_CompressRecordingFile));
                if (aopXSOCompressRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XSO_CompressRecordingFile.name()))
                    {
                        aopXSOCompressRecordingFile = new AOPsProperties();
                        aopXSOCompressRecordingFile.setAOPs(_aops);
                        aopXSOCompressRecordingFile.setConfKey(AOPsProperties.Keys.XSO_CompressRecordingFile.name());
                        aopXSOCompressRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOCompressRecordingFile));
                    }

                }

                AOPsProperties aopXSOEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_EnableScreenRecording));
                if (aopXSOEnableScreenRecording == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XSO_EnableScreenRecording.name()))
                    {
                        aopXSOEnableScreenRecording = new AOPsProperties();
                        aopXSOEnableScreenRecording.setAOPs(_aops);
                        aopXSOEnableScreenRecording.setConfKey(AOPsProperties.Keys.XSO_EnableScreenRecording.name());
                        aopXSOEnableScreenRecording.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOEnableScreenRecording));
                    }

                }

                if (aops.getAOPsType().equals(AOPsType.Process))
                {
                    Process pro = (Process) aops;
                    switch (pro.getProcessType())
                    {
                        case Inbound:
                            build_XSO_IB(hmattr);
                            continue;

                        case Blended:
                            build_XSO_OB(hmattr);
                            continue;
                    }
                }
                else
                {
                    build_XSO_OB(hmattr);

                    continue;
                }

            }

            if (chn.equals(Channel.Video))
            {

                AOPsProperties aopgXVD_AllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_AllowReject));
                if (aopgXVD_AllowReject == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XVD_AllowReject.name()))
                    {
                        AOPsProperties aopsXVDAllowReject = new AOPsProperties();
                        aopsXVDAllowReject.setAOPs(_aops);
                        aopsXVDAllowReject.setConfKey(AOPsProperties.Keys.XVD_AllowReject.name());
                        aopsXVDAllowReject.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVDAllowReject));
                    }
                }
                AOPsProperties aopgXVD_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_AutoAnswer));
                if (aopgXVD_AutoAnswer == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XVD_AutoAnswer.name()))
                    {
                        AOPsProperties aopsXVDAutoAns = new AOPsProperties();
                        aopsXVDAutoAns.setAOPs(_aops);
                        aopsXVDAutoAns.setConfKey(AOPsProperties.Keys.XVD_AutoAnswer.name());
                        aopsXVDAutoAns.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVDAutoAns));
                    }
                }
                AOPsProperties aopXVDAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_AutoAnswerDelay));
                if (aopXVDAutoAnswerDelay == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XVD_AutoAnswerDelay.name()))
                    {
                        aopXVDAutoAnswerDelay = new AOPsProperties();
                        aopXVDAutoAnswerDelay.setAOPs(_aops);
                        aopXVDAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XVD_AutoAnswerDelay.name());
                        aopXVDAutoAnswerDelay.setConfValue(Limits.AutoAnsDelay.toString().toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDAutoAnswerDelay));
                    }

                }

                AOPsProperties aopXVDPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_PostSessRecEnabled));
                if (aopXVDPostSessRecEnabled == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XVD_PostSessRecEnabled.name()))
                    {
                        aopXVDPostSessRecEnabled = new AOPsProperties();
                        aopXVDPostSessRecEnabled.setAOPs(_aops);
                        aopXVDPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XVD_PostSessRecEnabled.name());
                        aopXVDPostSessRecEnabled.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDPostSessRecEnabled));
                    }

                }

                AOPsProperties aopXVDEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_EncryptRecordingFile));
                if (aopXVDEncryptRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XVD_EncryptRecordingFile.name()))
                    {
                        aopXVDEncryptRecordingFile = new AOPsProperties();
                        aopXVDEncryptRecordingFile.setAOPs(_aops);
                        aopXVDEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XVD_EncryptRecordingFile.name());
                        aopXVDEncryptRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDEncryptRecordingFile));
                    }

                }

                AOPsProperties aopXVDCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_CompressRecordingFile));
                if (aopXVDCompressRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XVD_CompressRecordingFile.name()))
                    {
                        aopXVDCompressRecordingFile = new AOPsProperties();
                        aopXVDCompressRecordingFile.setAOPs(_aops);
                        aopXVDCompressRecordingFile.setConfKey(AOPsProperties.Keys.XVD_CompressRecordingFile.name());
                        aopXVDCompressRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDCompressRecordingFile));
                    }

                }

                AOPsProperties aopXVDEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_EnableScreenRecording));
                if (aopXVDEnableScreenRecording == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XVD_EnableScreenRecording.name()))
                    {
                        aopXVDEnableScreenRecording = new AOPsProperties();
                        aopXVDEnableScreenRecording.setAOPs(_aops);
                        aopXVDEnableScreenRecording.setConfKey(AOPsProperties.Keys.XVD_EnableScreenRecording.name());
                        aopXVDEnableScreenRecording.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDEnableScreenRecording));
                    }

                }

                if (aops.getAOPsType().equals(AOPsType.Process))
                {
                    Process pro = (Process) aops;
                    switch (pro.getProcessType())
                    {
                        case Inbound:
                            build_XVD_IB(hmattr);
                            continue;

                        case Blended:
                            build_XVD_OB(hmattr);
                            continue;
                    }
                }
                else
                {
                    build_XVD_OB(hmattr);

                    continue;
                }

            }

            if (chn.equals(Channel.RAWB))
            {

                AOPsProperties aopsXWAllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_AllowReject));
                if (aopsXWAllowReject == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XW_AllowReject.name()))
                    {
                        aopsXWAllowReject = new AOPsProperties();
                        aopsXWAllowReject.setAOPs(_aops);
                        aopsXWAllowReject.setConfKey(AOPsProperties.Keys.XW_AllowReject.name());
                        aopsXWAllowReject.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXWAllowReject));
                    }
                }
                AOPsProperties aopgXW_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_AutoAnswer));
                if (aopgXW_AutoAnswer == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XW_AutoAnswer.name()))
                    {
                        AOPsProperties aopsXVDAutoAns = new AOPsProperties();
                        aopsXVDAutoAns.setAOPs(_aops);
                        aopsXVDAutoAns.setConfKey(AOPsProperties.Keys.XW_AutoAnswer.name());
                        aopsXVDAutoAns.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVDAutoAns));
                    }
                }
                AOPsProperties aopXWAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_AutoAnswerDelay));
                if (aopXWAutoAnswerDelay == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XW_AutoAnswerDelay.name()))
                    {
                        aopXWAutoAnswerDelay = new AOPsProperties();
                        aopXWAutoAnswerDelay.setAOPs(_aops);
                        aopXWAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XW_AutoAnswerDelay.name());
                        aopXWAutoAnswerDelay.setConfValue(Limits.AutoAnsDelay.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXWAutoAnswerDelay));
                    }

                }
                AOPsProperties aopXWEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_EnableScreenRecording));
                if (aopXWEnableScreenRecording == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XW_EnableScreenRecording.name()))
                    {
                        aopXWEnableScreenRecording = new AOPsProperties();
                        aopXWEnableScreenRecording.setAOPs(_aops);
                        aopXWEnableScreenRecording.setConfKey(AOPsProperties.Keys.XW_EnableScreenRecording.name());
                        aopXWEnableScreenRecording.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXWEnableScreenRecording));
                    }

                }
                if (aops.getAOPsType().equals(AOPsType.Process))
                {
                    Process pro = (Process) aops;
                    switch (pro.getProcessType())
                    {
                        case Inbound:
                            build_XW_IB(hmattr);
                            continue;

                    }
                }

            }

            if (chn.equals(Channel.Visit))
            {
                AOPsProperties aopXVTEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_EnableScreenRecording));
                if (aopXVTEnableScreenRecording == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XVT_EnableScreenRecording.name()))
                    {
                        aopXVTEnableScreenRecording = new AOPsProperties();
                        aopXVTEnableScreenRecording.setAOPs(_aops);
                        aopXVTEnableScreenRecording.setConfKey(AOPsProperties.Keys.XVT_EnableScreenRecording.name());
                        aopXVTEnableScreenRecording.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVTEnableScreenRecording));
                    }

                }

                if (aops.getAOPsType().equals(AOPsType.Process))
                {
                    Process pro = (Process) aops;
                    switch (pro.getProcessType())
                    {
                        case Inbound:
                            build_XW_IB(hmattr);
                            continue;

//                        case Blended:
//                            build_XVD_OB(hmattr);
//                            continue;
                    }
                }
//                else
//                {
//                    build_XVD_OB(hmattr);
//
//                    continue;
//                }

            }

            if (chn.equals(Channel.SMS))
            {
                AOPsProperties aopgXM_AllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_AllowReject));
                if (aopgXM_AllowReject == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XM_AllowReject.name()))
                    {
                        AOPsProperties aopsXMAllowReject = new AOPsProperties();
                        aopsXMAllowReject.setAOPs(_aops);
                        aopsXMAllowReject.setConfKey(AOPsProperties.Keys.XM_AllowReject.name());
                        aopsXMAllowReject.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXMAllowReject));
                    }
                }
                AOPsProperties aopgXM_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_AutoAnswer));
                if (aopgXM_AutoAnswer == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XM_AutoAnswer.name()))
                    {
                        AOPsProperties aopsXMAutoAns = new AOPsProperties();
                        aopsXMAutoAns.setAOPs(_aops);
                        aopsXMAutoAns.setConfKey(AOPsProperties.Keys.XM_AutoAnswer.name());
                        aopsXMAutoAns.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXMAutoAns));
                    }
                }
                AOPsProperties aopXMAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_AutoAnswerDelay));
                if (aopXMAutoAnswerDelay == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XM_AutoAnswerDelay.name()))
                    {
                        aopXMAutoAnswerDelay = new AOPsProperties();
                        aopXMAutoAnswerDelay.setAOPs(_aops);
                        aopXMAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XM_AutoAnswerDelay.name());
                        aopXMAutoAnswerDelay.setConfValue(Limits.AutoAnsDelay.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMAutoAnswerDelay));
                    }

                }
                AOPsProperties aopXMPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_PostSessRecEnabled));
                if (aopXMPostSessRecEnabled == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XM_PostSessRecEnabled.name()))
                    {
                        aopXMPostSessRecEnabled = new AOPsProperties();
                        aopXMPostSessRecEnabled.setAOPs(_aops);
                        aopXMPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XM_PostSessRecEnabled.name());
                        aopXMPostSessRecEnabled.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMPostSessRecEnabled));
                    }

                }

                AOPsProperties aopXMEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_EncryptRecordingFile));
                if (aopXMEncryptRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XM_EncryptRecordingFile.name()))
                    {
                        aopXMEncryptRecordingFile = new AOPsProperties();
                        aopXMEncryptRecordingFile.setAOPs(_aops);
                        aopXMEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XM_EncryptRecordingFile.name());
                        aopXMEncryptRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMEncryptRecordingFile));
                    }

                }

                AOPsProperties aopXMCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_CompressRecordingFile));
                if (aopXMCompressRecordingFile == null)
                {
                    if (!hmattr.containsKey(AOPsProperties.Keys.XM_CompressRecordingFile.name()))
                    {
                        aopXMCompressRecordingFile = new AOPsProperties();
                        aopXMCompressRecordingFile.setAOPs(_aops);
                        aopXMCompressRecordingFile.setConfKey(AOPsProperties.Keys.XM_CompressRecordingFile.name());
                        aopXMCompressRecordingFile.setConfValue(Boolean.FALSE.toString());
                        entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMCompressRecordingFile));
                    }

                }
                if (aops.getAOPsType().equals(AOPsType.Process))
                {
                    Process pro = (Process) aops;
                    switch (pro.getProcessType())
                    {
                        case Inbound:
                            build_XM_IB(hmattr);
                            continue;

//                        case Blended:
//                            build_XSO_OB(hmattr);
//                            continue;
                    }
                }
                else
                {
//                    build_XSO_OB(hmattr);
//
//                    continue;
                }

            }
        }

    }

    //Note:- This methods need to create for other channels also but for now we are supporting OB and Blended only for Telephone.
    private void build_XT_IB(HashMap<String, String> hmAttr) throws GravityException, CODEException, Exception
    {

        AOPsProperties aopgXTIBAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_IB_AllowDialBack));
        if (aopgXTIBAllowDialBack == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XT_IB_AllowDialBack.name()))
            {
                AOPsProperties aopDBack = new AOPsProperties();
                aopDBack.setAOPs(_aops);
                aopDBack.setConfKey(AOPsProperties.Keys.XT_IB_AllowDialBack.name());
                aopDBack.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopDBack));
            }

        }

        AOPsProperties aopgXT_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_IB_EnableBlockFilter));
        if (aopgXT_IB_EnableBlockFilter == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XT_IB_EnableBlockFilter.name()))
            {
                AOPsProperties aopBlockCall = new AOPsProperties();
                aopBlockCall.setAOPs(_aops);
                aopBlockCall.setConfKey(AOPsProperties.Keys.XT_IB_EnableBlockFilter.name());
                aopBlockCall.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopBlockCall));
            }

        }

    }

    public void build_XT_OB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopgXTOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_EnableDialChain));
        if (aopgXTOBEnableDialChain == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XT_OB_EnableDialChain.name()))
            {
                AOPsProperties aopsEnableCh = new AOPsProperties();
                aopsEnableCh.setAOPs(_aops);
                aopsEnableCh.setConfKey(AOPsProperties.Keys.XT_OB_EnableDialChain.name());
                aopsEnableCh.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsEnableCh));
            }

        }
        AOPsProperties aopgXTOBAllowManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_AllowManualDial));
        if (aopgXTOBAllowManualDial == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XT_OB_AllowManualDial.name()))
            {
                AOPsProperties aopsManualDial = new AOPsProperties();
                aopsManualDial.setAOPs(_aops);
                aopsManualDial.setConfKey(AOPsProperties.Keys.XT_OB_AllowManualDial.name());
                aopsManualDial.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsManualDial));
            }

        }
        AOPsProperties aopgXTOBCheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_CheckDNC));
        if (aopgXTOBCheckDNC == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XT_OB_CheckDNC.name()))
            {
                AOPsProperties aopsCheckDNC = new AOPsProperties();
                aopsCheckDNC.setAOPs(_aops);
                aopsCheckDNC.setConfKey(AOPsProperties.Keys.XT_OB_CheckDNC.name());
                aopsCheckDNC.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsCheckDNC));
            }

        }
        AOPsProperties aopgXTOBOverrideDefCallerId = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_OverrideDefCallerId));
        if (aopgXTOBOverrideDefCallerId == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XT_OB_OverrideDefCallerId.name()))
            {
                AOPsProperties aopsDefClId = new AOPsProperties();
                aopsDefClId.setAOPs(_aops);
                aopsDefClId.setConfKey(AOPsProperties.Keys.XT_OB_OverrideDefCallerId.name());
                aopsDefClId.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsDefClId));
            }

        }
        AOPsProperties aopgXTOBMaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_MaxAttemptCount));
        if (aopgXTOBMaxAttemptCount == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XT_OB_MaxAttemptCount.name()))
            {
                AOPsProperties aopsMaxAtCount = new AOPsProperties();
                aopsMaxAtCount.setAOPs(_aops);
                aopsMaxAtCount.setConfKey(AOPsProperties.Keys.XT_OB_MaxAttemptCount.name());
                aopsMaxAtCount.setConfValue(Limits.AttemptCount_MAX.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsMaxAtCount));
            }

        }
        AOPsProperties aopgXTOBPaceLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_PaceLimit));
        if (aopgXTOBPaceLimit == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XT_OB_PaceLimit.name()))
            {
                AOPsProperties aopsPaceLimit = new AOPsProperties();
                aopsPaceLimit.setAOPs(_aops);
                aopsPaceLimit.setConfKey(AOPsProperties.Keys.XT_OB_PaceLimit.name());
                aopsPaceLimit.setConfValue("1");
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsPaceLimit));
            }

        }

    }

    public void build_XCH_OB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXCH_OB_CheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_CheckDNC));
        if (aopXCH_OB_CheckDNC == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XCH_OB_CheckDNC.name()))
            {
                aopXCH_OB_CheckDNC = new AOPsProperties();
                aopXCH_OB_CheckDNC.setAOPs(_aops);
                aopXCH_OB_CheckDNC.setConfKey(AOPsProperties.Keys.XCH_OB_CheckDNC.name());
                aopXCH_OB_CheckDNC.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_OB_CheckDNC));
            }

        }

        AOPsProperties aopXCHOBOverrideDefFromAddress = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_OverrideDefFromAddress));

        if (aopXCHOBOverrideDefFromAddress == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XCH_OB_OverrideDefFromAddress.name()))
            {
                aopXCHOBOverrideDefFromAddress = new AOPsProperties();
                aopXCHOBOverrideDefFromAddress.setAOPs(_aops);
                aopXCHOBOverrideDefFromAddress.setConfKey(AOPsProperties.Keys.XCH_OB_OverrideDefFromAddress.name());
                aopXCHOBOverrideDefFromAddress.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBOverrideDefFromAddress));
            }

        }
        AOPsProperties aopXCHOBMaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_MaxAttemptCount));

        if (aopXCHOBMaxAttemptCount == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XCH_OB_MaxAttemptCount.name()))
            {
                aopXCHOBMaxAttemptCount = new AOPsProperties();
                aopXCHOBMaxAttemptCount.setAOPs(_aops);
                aopXCHOBMaxAttemptCount.setConfKey(AOPsProperties.Keys.XCH_OB_MaxAttemptCount.name());
                aopXCHOBMaxAttemptCount.setConfValue(Limits.AttemptCount_MAX.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBMaxAttemptCount));

            }
        }
        AOPsProperties aopgXCHOBAllowManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_AllowManualDial));
        if (aopgXCHOBAllowManualDial == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XCH_OB_AllowManualDial.name()))
            {
                aopgXCHOBAllowManualDial = new AOPsProperties();
                aopgXCHOBAllowManualDial.setAOPs(_aops);
                aopgXCHOBAllowManualDial.setConfKey(AOPsProperties.Keys.XCH_OB_AllowManualDial.name());
                aopgXCHOBAllowManualDial.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXCHOBAllowManualDial));
            }

        }

        AOPsProperties aopgXCHOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_EnableDialChain));
        if (aopgXCHOBEnableDialChain == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XCH_OB_EnableDialChain.name()))
            {
                aopgXCHOBEnableDialChain = new AOPsProperties();
                aopgXCHOBEnableDialChain.setAOPs(_aops);
                aopgXCHOBEnableDialChain.setConfKey(AOPsProperties.Keys.XCH_OB_EnableDialChain.name());
                aopgXCHOBEnableDialChain.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXCHOBEnableDialChain));
            }

        }

    }

    public void build_XSO_OB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXSO_OB_CheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_CheckDNC));
        if (aopXSO_OB_CheckDNC == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XSO_OB_CheckDNC.name()))
            {
                aopXSO_OB_CheckDNC = new AOPsProperties();
                aopXSO_OB_CheckDNC.setAOPs(_aops);
                aopXSO_OB_CheckDNC.setConfKey(AOPsProperties.Keys.XSO_OB_CheckDNC.name());
                aopXSO_OB_CheckDNC.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSO_OB_CheckDNC));
            }

        }
        AOPsProperties aopgXSOOBAllowManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_AllowManualDial));
        if (aopgXSOOBAllowManualDial == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XSO_OB_AllowManualDial.name()))
            {
                aopgXSOOBAllowManualDial = new AOPsProperties();
                aopgXSOOBAllowManualDial.setAOPs(_aops);
                aopgXSOOBAllowManualDial.setConfKey(AOPsProperties.Keys.XSO_OB_AllowManualDial.name());
                aopgXSOOBAllowManualDial.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXSOOBAllowManualDial));
            }
        }
        AOPsProperties aopgXSOOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_EnableDialChain));
        if (aopgXSOOBEnableDialChain == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XSO_OB_EnableDialChain.name()))
            {
                aopgXSOOBEnableDialChain = new AOPsProperties();
                aopgXSOOBEnableDialChain.setAOPs(_aops);
                aopgXSOOBEnableDialChain.setConfKey(AOPsProperties.Keys.XSO_OB_EnableDialChain.name());
                aopgXSOOBEnableDialChain.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXSOOBEnableDialChain));
            }

        }

    }

    public void build_XVD_OB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXVD_OB_CheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_CheckDNC));
        if (aopXVD_OB_CheckDNC == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XVD_OB_CheckDNC.name()))
            {
                aopXVD_OB_CheckDNC = new AOPsProperties();
                aopXVD_OB_CheckDNC.setAOPs(_aops);
                aopXVD_OB_CheckDNC.setConfKey(AOPsProperties.Keys.XVD_OB_CheckDNC.name());
                aopXVD_OB_CheckDNC.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVD_OB_CheckDNC));
            }

        }
        AOPsProperties aopXVDMOBAllowManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_AllowManualDial));
        if (aopXVDMOBAllowManualDial == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XVD_OB_AllowManualDial.name()))
            {
                aopXVDMOBAllowManualDial = new AOPsProperties();
                aopXVDMOBAllowManualDial.setAOPs(_aops);
                aopXVDMOBAllowManualDial.setConfKey(AOPsProperties.Keys.XVD_OB_AllowManualDial.name());
                aopXVDMOBAllowManualDial.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDMOBAllowManualDial));

            }
        }
        AOPsProperties aopgXVDMOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_EnableDialChain));
        if (aopgXVDMOBEnableDialChain == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XVD_OB_EnableDialChain.name()))
            {
                aopgXVDMOBEnableDialChain = new AOPsProperties();
                aopgXVDMOBEnableDialChain.setAOPs(_aops);
                aopgXVDMOBEnableDialChain.setConfKey(AOPsProperties.Keys.XVD_OB_EnableDialChain.name());
                aopgXVDMOBEnableDialChain.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXVDMOBEnableDialChain));
            }

        }

    }

    public void build_XVT_OB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXVT_OB_CheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_OB_CheckDNC));
        if (aopXVT_OB_CheckDNC == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XVT_OB_CheckDNC.name()))
            {
                aopXVT_OB_CheckDNC = new AOPsProperties();
                aopXVT_OB_CheckDNC.setAOPs(_aops);
                aopXVT_OB_CheckDNC.setConfKey(AOPsProperties.Keys.XVT_OB_CheckDNC.name());
                aopXVT_OB_CheckDNC.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVT_OB_CheckDNC));
            }

        }

        AOPsProperties aopgXVTOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_OB_EnableDialChain));
        if (aopgXVTOBEnableDialChain == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XVT_OB_EnableDialChain.name()))
            {
                aopgXVTOBEnableDialChain = new AOPsProperties();
                aopgXVTOBEnableDialChain.setAOPs(_aops);
                aopgXVTOBEnableDialChain.setConfKey(AOPsProperties.Keys.XVT_OB_EnableDialChain.name());
                aopgXVTOBEnableDialChain.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXVTOBEnableDialChain));
            }

        }

    }

    private void build_XEM_IB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopgXEM_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_IB_EnableBlockFilter));
        if (aopgXEM_IB_EnableBlockFilter == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XEM_IB_EnableBlockFilter.name()))
            {
                aopgXEM_IB_EnableBlockFilter = new AOPsProperties();
                aopgXEM_IB_EnableBlockFilter.setAOPs(_aops);
                aopgXEM_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XEM_IB_EnableBlockFilter.name());
                aopgXEM_IB_EnableBlockFilter.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXEM_IB_EnableBlockFilter));
            }

        }
        AOPsProperties aopgXEMAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_IB_AllowDialBack));
        if (aopgXEMAllowDialBack == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XEM_IB_AllowDialBack.name()))
            {
                aopgXEMAllowDialBack = new AOPsProperties();
                aopgXEMAllowDialBack.setAOPs(_aops);
                aopgXEMAllowDialBack.setConfKey(AOPsProperties.Keys.XEM_IB_AllowDialBack.name());
                aopgXEMAllowDialBack.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXEMAllowDialBack));
            }

        }

    }

    private void build_XCH_IB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopgXCH_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_IB_EnableBlockFilter));
        if (aopgXCH_IB_EnableBlockFilter == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XCH_IB_EnableBlockFilter.name()))
            {
                aopgXCH_IB_EnableBlockFilter = new AOPsProperties();
                aopgXCH_IB_EnableBlockFilter.setAOPs(_aops);
                aopgXCH_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XCH_IB_EnableBlockFilter.name());
                aopgXCH_IB_EnableBlockFilter.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXCH_IB_EnableBlockFilter));
            }

        }
        AOPsProperties aopXCH_IB_EnableWebChat = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_IB_EnableWebChat));
        if (aopXCH_IB_EnableWebChat == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XCH_IB_EnableWebChat.name()))
            {
                aopXCH_IB_EnableWebChat = new AOPsProperties();
                aopXCH_IB_EnableWebChat.setAOPs(_aops);
                aopXCH_IB_EnableWebChat.setConfKey(AOPsProperties.Keys.XCH_IB_EnableWebChat.name());
                aopXCH_IB_EnableWebChat.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_IB_EnableWebChat));
            }

        }
        AOPsProperties aopgXCHAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_IB_AllowDialBack));
        if (aopgXCHAllowDialBack == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XCH_IB_AllowDialBack.name()))
            {
                aopgXCHAllowDialBack = new AOPsProperties();
                aopgXCHAllowDialBack.setAOPs(_aops);
                aopgXCHAllowDialBack.setConfKey(AOPsProperties.Keys.XCH_IB_AllowDialBack.name());
                aopgXCHAllowDialBack.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXCHAllowDialBack));
            }

        }

    }

    private void build_XSO_IB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXSO_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_IB_EnableBlockFilter));
        if (aopXSO_IB_EnableBlockFilter == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XSO_IB_EnableBlockFilter.name()))
            {
                aopXSO_IB_EnableBlockFilter = new AOPsProperties();
                aopXSO_IB_EnableBlockFilter.setAOPs(_aops);
                aopXSO_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XSO_IB_EnableBlockFilter.name());
                aopXSO_IB_EnableBlockFilter.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSO_IB_EnableBlockFilter));
            }

        }
        AOPsProperties aopgXSOAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_IB_AllowDialBack));
        if (aopgXSOAllowDialBack == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XSO_IB_AllowDialBack.name()))
            {
                aopgXSOAllowDialBack = new AOPsProperties();
                aopgXSOAllowDialBack.setAOPs(_aops);
                aopgXSOAllowDialBack.setConfKey(AOPsProperties.Keys.XSO_IB_AllowDialBack.name());
                aopgXSOAllowDialBack.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXSOAllowDialBack));
            }

        }
    }

    private void build_XM_IB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXM_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_IB_EnableBlockFilter));
        if (aopXM_IB_EnableBlockFilter == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XM_IB_EnableBlockFilter.name()))
            {
                aopXM_IB_EnableBlockFilter = new AOPsProperties();
                aopXM_IB_EnableBlockFilter.setAOPs(_aops);
                aopXM_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XM_IB_EnableBlockFilter.name());
                aopXM_IB_EnableBlockFilter.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXM_IB_EnableBlockFilter));
            }

        }
        AOPsProperties aopgXMAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_IB_AllowDialBack));
        if (aopgXMAllowDialBack == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XM_IB_AllowDialBack.name()))
            {
                aopgXMAllowDialBack = new AOPsProperties();
                aopgXMAllowDialBack.setAOPs(_aops);
                aopgXMAllowDialBack.setConfKey(AOPsProperties.Keys.XM_IB_AllowDialBack.name());
                aopgXMAllowDialBack.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXMAllowDialBack));
            }

        }
    }

    private void build_XVD_IB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXVD_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_IB_EnableBlockFilter));
        if (aopXVD_IB_EnableBlockFilter == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XVD_IB_EnableBlockFilter.name()))
            {
                aopXVD_IB_EnableBlockFilter = new AOPsProperties();
                aopXVD_IB_EnableBlockFilter.setAOPs(_aops);
                aopXVD_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XVD_IB_EnableBlockFilter.name());
                aopXVD_IB_EnableBlockFilter.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVD_IB_EnableBlockFilter));
            }

        }
        AOPsProperties aopXVD_IB_EnableWebVideo = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_IB_EnableWebVideo));
        if (aopXVD_IB_EnableWebVideo == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XVD_IB_EnableWebVideo.name()))
            {
                aopXVD_IB_EnableWebVideo = new AOPsProperties();
                aopXVD_IB_EnableWebVideo.setAOPs(_aops);
                aopXVD_IB_EnableWebVideo.setConfKey(AOPsProperties.Keys.XVD_IB_EnableWebVideo.name());
                aopXVD_IB_EnableWebVideo.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVD_IB_EnableWebVideo));
            }

        }
        AOPsProperties aopgXVDAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_IB_AllowDialBack));
        if (aopgXVDAllowDialBack == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XVD_IB_AllowDialBack.name()))
            {
                aopgXVDAllowDialBack = new AOPsProperties();
                aopgXVDAllowDialBack.setAOPs(_aops);
                aopgXVDAllowDialBack.setConfKey(AOPsProperties.Keys.XVD_IB_AllowDialBack.name());
                aopgXVDAllowDialBack.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXVDAllowDialBack));
            }

        }

    }

    private void build_XW_IB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXW_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_IB_EnableBlockFilter));
        if (aopXW_IB_EnableBlockFilter == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XW_IB_EnableBlockFilter.name()))
            {
                aopXW_IB_EnableBlockFilter = new AOPsProperties();
                aopXW_IB_EnableBlockFilter.setAOPs(_aops);
                aopXW_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XW_IB_EnableBlockFilter.name());
                aopXW_IB_EnableBlockFilter.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXW_IB_EnableBlockFilter));
            }

        }
        AOPsProperties aopXW_IB_EnableWebRAWB = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_IB_EnableWebRAWB));
        if (aopXW_IB_EnableWebRAWB == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XW_IB_EnableWebRAWB.name()))
            {
                aopXW_IB_EnableWebRAWB = new AOPsProperties();
                aopXW_IB_EnableWebRAWB.setAOPs(_aops);
                aopXW_IB_EnableWebRAWB.setConfKey(AOPsProperties.Keys.XW_IB_EnableWebRAWB.name());
                aopXW_IB_EnableWebRAWB.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXW_IB_EnableWebRAWB));
            }

        }

    }

    public void build_XEM_OB(HashMap<String, String> hmAttr) throws GravityException, CODEException
    {

        AOPsProperties aopXEMOBCheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_CheckDNC));

        if (aopXEMOBCheckDNC == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XEM_OB_CheckDNC.name()))
            {
                AOPsProperties aopsXEMCheckDnc = new AOPsProperties();
                aopsXEMCheckDnc.setAOPs(_aops);
                aopsXEMCheckDnc.setConfKey(AOPsProperties.Keys.XEM_OB_CheckDNC.name());
                aopsXEMCheckDnc.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXEMCheckDnc));
            }

        }

        AOPsProperties aopXEMOBOverrideDefFromAddress = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_OverrideDefFromAddress));

        if (aopXEMOBOverrideDefFromAddress == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XEM_OB_OverrideDefFromAddress.name()))
            {
                AOPsProperties aopsXEMOverrideDefFromAddress = new AOPsProperties();
                aopsXEMOverrideDefFromAddress.setAOPs(_aops);
                aopsXEMOverrideDefFromAddress.setConfKey(AOPsProperties.Keys.XEM_OB_OverrideDefFromAddress.name());
                aopsXEMOverrideDefFromAddress.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXEMOverrideDefFromAddress));
            }

        }
        AOPsProperties aopXEMOBMaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_MaxAttemptCount));
        if (aopXEMOBMaxAttemptCount == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XEM_OB_MaxAttemptCount.name()))
            {
                aopXEMOBMaxAttemptCount = new AOPsProperties();
                aopXEMOBMaxAttemptCount.setAOPs(_aops);
                aopXEMOBMaxAttemptCount.setConfKey(AOPsProperties.Keys.XEM_OB_MaxAttemptCount.name());
                aopXEMOBMaxAttemptCount.setConfValue(Limits.AttemptCount_MAX.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBMaxAttemptCount));

            }
        }

        AOPsProperties aopXEMOBAllowManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_AllowManualDial));
        if (aopXEMOBAllowManualDial == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XEM_OB_AllowManualDial.name()))
            {
                aopXEMOBAllowManualDial = new AOPsProperties();
                aopXEMOBAllowManualDial.setAOPs(_aops);
                aopXEMOBAllowManualDial.setConfKey(AOPsProperties.Keys.XEM_OB_AllowManualDial.name());
                aopXEMOBAllowManualDial.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBAllowManualDial));

            }
        }
        AOPsProperties aopgXEMOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_EnableDialChain));
        if (aopgXEMOBEnableDialChain == null)
        {
            if (!hmAttr.containsKey(AOPsProperties.Keys.XEM_OB_EnableDialChain.name()))
            {
                aopgXEMOBEnableDialChain = new AOPsProperties();
                aopgXEMOBEnableDialChain.setAOPs(_aops);
                aopgXEMOBEnableDialChain.setConfKey(AOPsProperties.Keys.XEM_OB_EnableDialChain.name());
                aopgXEMOBEnableDialChain.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopgXEMOBEnableDialChain));
            }

        }

    }

    private Event IsValidRequestAtributes(RequestAOPsPropertiesConfig req, AOPs aops, HashMap<String, String> hmattr) throws GravityException, GravityRuntimeCheckFailedException, CODEException
    {
        for (String key : hmattr.keySet())
        {
            String[] arrKeys = key.split("_");
            switch (arrKeys[0])
            {

                case "XT":
                {
                    if (!IsValidCampaignChannel(aops, Channel.Call))
                    {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.IllegalCampaignMedia, "Excepted channel Telephone");
                    }
                }
                break;
                case "XEM":
                {
                    if (!IsValidCampaignChannel(aops, Channel.Email))
                    {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.IllegalCampaignMedia, "Excepted channel EMail");
                    }
                }
                break;
                case "XCH":
                {
                    if (!IsValidCampaignChannel(aops, Channel.Chat))
                    {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.IllegalCampaignMedia, "Excepted channel Chat");

                    }
                }
                break;
                case "XSO":
                {
                    if (!IsValidCampaignChannel(aops, Channel.Social))
                    {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.IllegalCampaignMedia, "Excepted channel Social");

                    }
                }
                break;
                case "XVD":
                {
                    if (!IsValidCampaignChannel(aops, Channel.Video))
                    {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.IllegalCampaignMedia, "Excepted channel Video");
                    }
                }
                break;

                case "XM":
                {
                    if (!IsValidCampaignChannel(aops, Channel.SMS))
                    {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.IllegalCampaignMedia, "Excepted channel SMS");
                    }
                }
                break;

            }

            switch (arrKeys[1])
            {
                case "IB":
                {
                    //check campaign type.
                    if (aops.getAOPsType().equals(AOPsType.Campaign))
                    {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.AOPsIllegalType, "Excepted CampaignType Outbound");
                    }
                }
                break;

            }

        }

        return null;
    }

    private Boolean IsValidCampaignChannel(AOPs aops, Channel chn) throws GravityException, CODEException
    {
        AOPsMedia cm = _tctx.getDB().Find(new AOPsMediaQuery().filterByAOPs(aops.getId()).filterByChannel(chn));
        return cm != null;
    }

    private void getCampProps(HashMap<String, String> hmattr) throws GravityException, Exception, CODEException
    {

        for (String key : hmattr.keySet())
        {
            AOPsProperties.Keys propKey = AOPsProperties.Keys.valueOf(key);
            switch (propKey)
            {
                case Global_AutoStart:
                    setAutoStart(hmattr.get(key));
                    break;
                case Global_AutoStartDialMode:
                    setAutoStartDialMode(hmattr.get(key));
                    break;
                case Global_CallbackTypes:
                    setCallbackTypes(hmattr.get(key));
                    break;
                case Global_AgentReadyOn:
                    setAgentReadyOn(hmattr.get(key));
                    break;
                case Global_MOH_Code_Agent:
                    setMOHCodeAgent(hmattr.get(key));
                    break;
                case Global_AgentContactAddressAccesses:
                    setAgentContactAddressAccesses(hmattr.get(key));
                    break;
                case Global_DefRecordListCode:
                    setGlobalDefRecordListCode(hmattr.get(key));
                    break;
                case Global_Home_CRM_URL:
                    setHomeCRMURL(hmattr.get(key));
                    break;
                case Global_Home_CRM_Param:
                    setHomeCRMParam(hmattr.get(key));
                    break;
                case Global_Popup_CRM_URL:
                    setPopupCRMURL(hmattr.get(key));
                    break;
                case Global_Popup_CRM_Param:
                    setPopupCRMParam(hmattr.get(key));
                    break;
                case Global_Popup_Script_URL:
                    setPopupScriptURL(hmattr.get(key));
                    break;
                case Global_Popup_Script_Param:
                    setPopupScriptParam(hmattr.get(key));
                    break;
                case Global_SSOAuthParams:
                    setSSOAuthParams(hmattr.get(key));
                    break;
                case Global_SSOEnabled:
                    setSSOEnabled(hmattr.get(key));
                    break;
                case Global_ContactScheduledExpiryTimeout:
                    setContactScheduledExpiryTimeout(hmattr.get(key));
                    break;
                case Global_ContactScheduledMaxDuration:
                    setContactScheduledMaxDuration(hmattr.get(key));
                    break;
                case Global_ContactSessionLimit:
                    setAgentTaskLimit(hmattr.get(key));
                    break;
                case Global_AutoAOPsJoin:
                    setAutoCampaignJoin(hmattr.get(key));
                    break;
                case Global_AllowManualAOPsJoin:
                    setAllowManualCampaignJoin(hmattr.get(key));
                    break;
                case Global_AllowManualAOPsLeave:
                    setAllowManualCampaignLeave(hmattr.get(key));
                    break;
                case Global_StickyAgent:
                    setaopsStickyAgent(hmattr.get(key));
                    break;
                case Global_StrictlyStickyAgent:
                    setaopsStrictlyStickyAgent(hmattr.get(key), hmattr);
                    break;
                case Global_DisposeTimeout:
                    setGlobalDisposeTimeout(hmattr.get(key));
                    break;
                case Global_DisposeIsAuto:
                    setGlobalDIsposeIsAuto(hmattr.get(key));
                    break;
                case Global_SessionDoneIsAuto:
                    setGlobalSessionDoneIsAuto(hmattr.get(key));
                    break;
                case Global_SessionDoneTimeout:
                    setGlobalSessionDoneTimeout(hmattr.get(key));
                    break;
                case Global_RedialExpiryTimeout:
                    setaopGlobalRedialExpiryTimeout(hmattr.get(key));
                    break;
                case Global_DisposeExtend:
                    setGlobalDisposeExtend(hmattr.get(key));
                    break;
                case Global_SessionExtend:
                    setGlobalSessionExtend(hmattr.get(key));
                    break;
                case Global_AutoPreview:
                    setGlobalAutoPreview(hmattr.get(key));
                    break;
                case Global_AIDisposeEnabled:
                    setGlobalAIDisposeEnabled(hmattr.get(key));
                    break;
                case Global_AIDisposeOverride:
                    setGlobalAIDisposeOverride(hmattr.get(key));
                    break;
                case Global_EnableAbandonTreatment:
                    setGlobal_EnableAbandonTreatment(hmattr.get(key));
                    break;
                case Global_AIDisposeAllowedChannels:
                    setGlobalAIDisposeAllowedChannels(hmattr.get(key));
                    break;
                case Global_AIDisposeXPlatform:
                    setGlobalAIDisposeXPlatform(hmattr.get(key));
                    break;
                case Global_AIDisposeXPlatformUA:
                    setGlobalAIDisposeXPlatformUA(hmattr.get(key));
                    break;
                case Global_CanRejectPreview:
                    setGlobalCanRejectPreview(hmattr.get(key));
                    break;
                case Global_PreviewTimeout:
                    setGlobalPreviewTimeout(hmattr.get(key));
                    break;
                case Global_MaxAttemptCount:
                    setGlobalMaxAttemptCount(hmattr.get(key));
                    break;
                case Global_SurveyId:
                    setGlobalSurveyId(hmattr.get(key));
                    break;
                case XEM_EnableRecording:
                    setXEMEnableRecordingMode(hmattr.get(key));
                    break;
                case XEM_DialTimeout:
                    setXEMDialTimeOut(hmattr.get(key));
                    break;
                case XEM_PostSessRecEnabled:
                    setXEMPostSessRecEnabled(hmattr.get(key));
                    break;
                case XEM_ScreenRecordingMode:
                    setXEMScreenRecordingMode(hmattr.get(key));
                    break;
                case XEM_IB_AuthParams:
                    setXEMIBAuthParams(hmattr.get(key));
                    break;
                case XEM_IB_AllowDialBack:
                    setXEMIBAllowDialBack(hmattr.get(key));
                    break;

                case XEM_AutoAnswer:
                    setXEMAutoAnswer(hmattr.get(key));
                    break;
                case XEM_AutoAnswerDelay:
                    setXEMAutoAnswerDelay(hmattr.get(key));
                    break;
                case XEM_AllowReject:
                    setXEMAllowReject(hmattr.get(key));
                    break;
                case XEM_CompressRecordingFile:
                    setXEMCompressRecordingFile(hmattr.get(key));
                    break;
                case XEM_EncryptRecordingFile:
                    setXEMEncryptRecordingFile(hmattr.get(key));
                    break;
                case XEM_OB_OverrideDefFromAddress:
                    setXEMOBOverrideDefFromAddress(hmattr.get(key));
                    break;
                case XEM_OB_MaxAttemptCount:
                    setXEMOBMaxAttemptCount(hmattr.get(key));
                    break;
                case XEM_OB_CheckDNC:
                    setXEMOBCheckDNC(hmattr.get(key));
                    break;

                case XEM_OB_Mecha_TemplateCode:
                    setXEMOBMechaTemplateCode(hmattr.get(key), hmattr);
                    break;
                case XEM_OB_EnableDialChain:
                    setXEMOBEnableDialChain(hmattr.get(key));
                    break;
                case XEM_OB_AllowManualDial:
                    setXEMOBManualDial(hmattr.get(key));
                    break;
                case XEM_OB_AllowedDialModes:
                    setXEMOBAllowedDialModes(hmattr.get(key));
                    break;
                case XEM_OB_DialLimit:
                    setXEMOBDialLimit(hmattr.get(key));
                    break;
                case XT_ExternalAddressRegx:
                    setXTExternalAddressRegx(hmattr.get(key));
                    break;
                case XT_InternalAddressRegx:
                    setXTInternalAddressRegx(hmattr.get(key));
                    break;
                case XT_RecordingMode:
                    setXTRecordingMode(hmattr.get(key));
                    break;
                case XT_ScreenRecordingMode:
                    setXTScreenRecordingMode(hmattr.get(key));
                    break;
                case XT_PostSessRecEnabled:
                    setXTPostSessRecEnabled(hmattr.get(key));
                    break;
                case XT_IB_EnableBlockFilter:
                    setXTIBEnableBlockCall(hmattr.get(key));
                    break;
                case XT_IB_AllowDialBack:
                    setXTIBAllowDialBack(hmattr.get(key));
                    break;

                case XT_AutoAnswer:
                    setXTAutoAnswer(hmattr.get(key));
                    break;
                case XT_AutoAnswerDelay:
                    setXTAutoAnswerDelay(hmattr.get(key));
                    break;
                case XT_RecordingAlert:
                    setXT_RecordingAlert(hmattr.get(key));
                    break;
                case XT_AllowReject:
                    setXTAllowReject(hmattr.get(key));
                    break;
                case XT_DialTimeout:
                    setXTDialTimeout(hmattr.get(key));
                    break;
                case XT_CompressRecordingFile:
                    setXTCompressRecordingFile(hmattr.get(key));
                    break;
                case XT_EnableScreenRecording:
                    setXTEnableScreenRecording(hmattr.get(key));
                    break;
                case XT_EncryptRecordingFile:
                    setXTEncryptRecordingFile(hmattr.get(key));
                    break;
                case XT_OB_OverrideDefCallerId:
                    setXTOBOverrideDefCallerId(hmattr.get(key));
                    break;
                case XT_OBDialTrunk:
                    setXTOBDialTrunk(hmattr.get(key));
                    break;

                case XT_RecordingMediaType:
                    setXTRecordingMediaType(hmattr.get(key));
                    break;
                case XT_RecordingBeepDuration:
                    setXT_RecordingBeepDuration(hmattr.get(key));
                    break;
                case XT_OB_MaxAttemptCount:
                    setXTOBMaxAttemptCount(hmattr.get(key));
                    break;
                case XT_OB_AllowedDialModes:
                    setXTOBAllowedDialModes(hmattr.get(key));
                    break;
                case XT_OB_AllowManualDial:
                    setXTOBManualDialAllowed(hmattr.get(key));
                    break;
                case XT_OB_CanRejectPreview:
                    setXTOBCanRejectPreview(hmattr.get(key));
                    break;
                case XT_OB_CheckDNC:
                    setXTOBCheckDNC(hmattr.get(key));
                    break;
                case XT_OB_PreviewTimeout:
                    setXTOBPreviewTimeout(hmattr.get(key));
                    break;
                case XT_OB_EnableDialChain:
                    setXTOBEnableDialChain(hmattr.get(key));
                    break;
                case XT_OB_DialLimit:
                    setXTOBDialLimit(hmattr.get(key));
                    break;
                case XT_OB_RouteToAddress:
                    setXTOBRouteToAddress(hmattr.get(key));
                    break;
                case XT_OB_PaceLimit:
                    setXTOBPaceLimit(hmattr.get(key));
                    break;
                case XT_OB_RouteToCDN:
                    setXTOBRouteToCDN(hmattr.get(key));
                    break;
                case XT_OB_RouteOnAlert:
                    setXTOBRouteOnAlert(hmattr.get(key));
                    break;
                case XT_OB_PredQueue:
                    setXTOBPredQueue(hmattr.get(key));
                    break;
                case XCH_SessionTimeout:
                    setXCHSessionTimeout(hmattr.get(key));
                    break;
                case XCH_CompressRecordingFile:
                    setXCHCompressRecordingFile(hmattr.get(key));
                    break;
                case XCH_ScreenRecordingMode:
                    setXCHScreenRecordingMode(hmattr.get(key));
                    break;
                case XCH_EncryptRecordingFile:
                    setXCHEncryptRecordingFile(hmattr.get(key));
                    break;
                case XCH_EnableRecording:
                    setXCHRecordingMode(hmattr.get(key));
                    break;
                case XCH_EnableScreenRecording:
                    setXCHEnableScreenRecording(hmattr.get(key));
                    break;
                case XCH_IB_AuthParams:
                    setXCHIBAuthParams(hmattr.get(key));
                    break;

                case XCH_AutoAnswer:
                    setXCHAutoAnswer(hmattr.get(key));
                    break;
                case XCH_DialTimeout:
                    setXCHDialTimeout(hmattr.get(key));
                    break;
                case XCH_AutoAnswerDelay:
                    setXCHAutoAnswerDelay(hmattr.get(key));
                    break;
                case XCH_PostSessRecEnabled:
                    setXCHPostSessRecEnabled(hmattr.get(key));
                    break;
                case XCH_IB_EnableBlockFilter:
                    setXCH_IB_EnableBlockCall(hmattr.get(key));
                    break;
                case XCH_IB_AllowDialBack:
                    setXCHIBAllowDialBack(hmattr.get(key));
                    break;
                case XCH_OB_CheckDNC:
                    setXCHOBCheckDNC(hmattr.get(key));
                    break;
                case XCH_IB_EnableWebChat:
                    setXCHIBEnableWebChat(hmattr.get(key));
                    break;
                case XCH_OB_MaxAttemptCount:
                    setXCHOBMaxAttemptCount(hmattr.get(key));
                    break;
                case XCH_OB_OverrideDefFromAddress:
                    setXCHOBOverrideDefFromAddress(hmattr.get(key));
                    break;
                case XCH_OB_AllowManualDial:
                    setXCHOBAllowManualDial(hmattr.get(key));
                    break;
                case XCH_OB_EnableDialChain:
                    setXCHOBEnableDialChain(hmattr.get(key));
                    break;
                case XCH_OB_AllowedDialModes:
                    setXCHOBAllowedDialModes(hmattr.get(key));
                    break;
                case XCH_OB_DialLimit:
                    setXCHOBDialLimit(hmattr.get(key));
                    break;

                case XCH_KBEnable:
                    setXCHKBEnable(hmattr.get(key));
                    break;
                case XCH_KBAutoAssist:
                    setXCHKBAutoAssist(hmattr.get(key));
                    break;
                case XCH_KBAutoReply:
                    setXCHKBAutoReply(hmattr.get(key));
                    break;
                case XCH_OB_Mecha_TemplateCode:
                    setXCHOBMechaTemplateCode(hmattr.get(key), hmattr);
                    break;
                case XCH_KBVerifyNReply:
                    setXCHKBVerifyNReply(hmattr.get(key));
                    break;
                case XCH_AllowReject:
                    setXCHAllowReject(hmattr.get(key));
                    break;
                case XVD_SessionTimeout:
                    setXVDSessionTimeout(hmattr.get(key));
                    break;
                case XVD_RecordingMode:
                    setXVDRecordingMode(hmattr.get(key));
                    break;
                case XVD_ScreenRecordingMode:
                    setXVDScreenRecordingMode(hmattr.get(key));
                    break;
                case XVD_CompressRecordingFile:
                    setXVDCompressRecordingFile(hmattr.get(key));
                    break;
                case XVD_EncryptRecordingFile:
                    setXVDEncryptRecordingFile(hmattr.get(key));
                    break;
                case XVD_PostSessRecEnabled:
                    setXVDPostSessRecEnabled(hmattr.get(key));
                    break;
                case XVD_IB_AuthParams:
                    setXVDIBAuthParams(hmattr.get(key));
                    break;
                case XVD_EnableScreenRecording:
                    setXVDEnableScreenRecording(hmattr.get(key));
                    break;
                case XVD_IB_EnableWebVideo:
                    setXVDIBEnableWebVideo(hmattr.get(key));
                    break;
                case XVD_IB_AllowDialBack:
                    setXVDIBAllowDialBack(hmattr.get(key));
                    break;

                case XVD_AutoAnswer:
                    setXVDAutoAnswer(hmattr.get(key));
                    break;
                case XVD_DialTimeout:
                    setXVDDialTimeout(hmattr.get(key));
                    break;
                case XVD_AutoAnswerDelay:
                    setXVDAutoAnswerDelay(hmattr.get(key));
                    break;
                case XVD_AllowReject:
                    setXVD_AllowReject(hmattr.get(key));
                    break;
                case XVD_OB_OverrideDefFromAddress:
                    setXVDOBOverrideDefFromAddress(hmattr.get(key));
                    break;
                case XVD_OB_MaxAttemptCount:
                    setXVDOBMaxAttemptCount(hmattr.get(key));
                    break;
                case XVD_OB_CheckDNC:
                    setXVDOBCheckDNC(hmattr.get(key));
                    break;
                case XVD_OB_AllowManualDial:
                    setXVDOBAllowManualDial(hmattr.get(key));
                    break;
                case XVD_OB_EnableDialChain:
                    setXVDOBEnableDialChain(hmattr.get(key));
                    break;

                case XVD_OB_AllowedDialModes:
                    setXVDOBAllowedDialModes(hmattr.get(key));
                    break;
                case XVD_OB_DialLimit:
                    setXVDOBDialLimit(hmattr.get(key));
                    break;
                case XM_SessionTimeout:
                    setXMSessionTimeout(hmattr.get(key));
                    break;
                case XM_PostSessRecEnabled:
                    setXMPostSessRecEnabled(hmattr.get(key));
                    break;
                case XM_IB_AuthParams:
                    setXMIBAuthParams(hmattr.get(key));
                    break;
                case XM_ScreenRecordingMode:
                    setXMScreenRecordingMode(hmattr.get(key));
                    break;
                case XM_IB_AllowDialBack:
                    setXMIBAllowDialBack(hmattr.get(key));
                    break;
                case XM_CompressRecordingFile:
                    setXMCompressRecordingFile(hmattr.get(key));
                    break;
                case XM_EncryptRecordingFile:
                    setXMEncryptRecordingFile(hmattr.get(key));
                    break;
                case XM_AutoAnswer:
                    setXMAutoAnswer(hmattr.get(key));
                    break;
                case XM_EnableScreenRecording:
                    setXMEnableScreenRecording(hmattr.get(key));
                    break;
                case XM_AutoAnswerDelay:
                    setXMAutoAnswerDelay(hmattr.get(key));
                    break;
                case XEM_IB_EnableBlockFilter:
                    setXEMIBEnableBlockCall(hmattr.get(key));
                    break;
                case XM_AllowReject:
                    setXMAllowReject(hmattr.get(key));
                    break;
                case XM_DialTimeout:
                    setXMDialTimeout(hmattr.get(key));
                    break;
                case XM_EnableRecording:
                    setXMRecordingMode(hmattr.get(key));
                    break;
                case XSO_EnableRecording:
                    setXSORecordingMode(hmattr.get(key));
                    break;
                case XSO_CompressRecordingFile:
                    setXSOCompressRecordingFile(hmattr.get(key));
                    break;
                case XSO_EncryptRecordingFile:
                    setXSOEncryptRecordingFile(hmattr.get(key));
                    break;
                case XSO_ScreenRecordingMode:
                    setXSOScreenRecordingMode(hmattr.get(key));
                    break;
                case XSO_EnableScreenRecording:
                    setXSOEnableScreenRecording(hmattr.get(key));
                    break;
                case XSO_PostSessRecEnabled:
                    setXSOPostSessRecEnabled(hmattr.get(key));
                    break;
                case XM_IB_EnableBlockFilter:
                    setXMIBEnableBlockCall(hmattr.get(key));
                    break;
                case XSO_IB_AuthParams:
                    setXSOIBAuthParams(hmattr.get(key));
                    break;
                case XSO_IB_EnableBlockFilter:
                    setXSOIBEnableBlockCall(hmattr.get(key));
                    break;
                case XVD_IB_EnableBlockFilter:
                    setXVDIBEnableBlockCall(hmattr.get(key));
                    break;
                case XSO_IB_AllowDialBack:
                    setXSOIBAllowDialBack(hmattr.get(key));
                    break;
                case XSO_OB_CheckDNC:
                    setXSOOBCheckDNC(hmattr.get(key));
                    break;
                case XSO_OB_MaxAttemptCount:
                    setXSOOBMaxAttemptCount(hmattr.get(key));
                    break;
                case XSO_OB_OverrideDefFromAddress:
                    setXSOOBOverrideDefFromAddress(hmattr.get(key));
                    break;
                case XSO_OB_DialLimit:
                    setXSOOBDialLimit(hmattr.get(key));
                    break;
                case XSO_OB_EnableDialChain:
                    setXSOOBEnableDialChain(hmattr.get(key));
                    break;
                case XSO_OB_AllowedDialModes:
                    setXSOOBAllowedDialModes(hmattr.get(key));
                    break;
                case XSO_DialTimeout:
                    setXSODialTimeout(hmattr.get(key));
                    break;
                case XSO_AllowReject:
                    setXSOAllowReject(hmattr.get(key));
                    break;
                case XSO_AutoAnswer:
                    setXSOAutoAnswer(hmattr.get(key));
                    break;
                case XSO_AutoAnswerDelay:
                    setXSOAutoAnswerDelay(hmattr.get(key));
                    break;
                case XW_AllowReject:
                    setXW_AllowReject(hmattr.get(key));
                    break;
                case XW_ScreenRecordingMode:
                    setXWScreenRecordingMode(hmattr.get(key));
                    break;
                case XW_EnableScreenRecording:
                    setXWEnableScreenRecording(hmattr.get(key));
                    break;
                case XW_SessionTimeout:
                    setXWSessionTimeout(hmattr.get(key));
                    break;
                case XW_AutoAnswer:
                    setXWAutoAnswer(hmattr.get(key));
                    break;
                case XW_AutoAnswerDelay:
                    setXWAutoAnswerDelay(hmattr.get(key));
                    break;
                case XW_IB_AuthParams:
                    setXWIBAuthParams(hmattr.get(key));
                    break;
                case XW_IB_EnableBlockFilter:
                    setXWIBEnableBlockCall(hmattr.get(key));
                    break;
                case XW_IB_EnableWebRAWB:
                    setXWIBEnableWebRAWB(hmattr.get(key));
                    break;
                case XVT_DialTimeout:
                    setXVTDialTimeout(hmattr.get(key));
                    break;
                case XVT_SessionTimeout:
                    setXVTSessionTimeout(hmattr.get(key));
                    break;
                case XVT_OB_CheckDNC:
                    setXVTOBCheckDNC(hmattr.get(key));
                    break;
                case XVT_EnableScreenRecording:
                    setXVTEnableScreenRecording(hmattr.get(key));
                    break;
                case XVT_ScreenRecordingMode:
                    setXVTScreenRecordingMode(hmattr.get(key));
                    break;
                case XVT_OB_DialLimit:
                    setXVTOBDialLimit(hmattr.get(key));
                    break;
                case XVT_OB_EnableDialChain:
                    setXVTOBEnableDialChain(hmattr.get(key));
                    break;
                case XVT_OB_MaxAttemptCount:
                    setXVTOBMaxAttemptCount(hmattr.get(key));
                    break;

            }
        }
    }

    private void setAutoStart(String value) throws GravityException, CODEException
    {

        Boolean isAuto = value.isEmpty() ? false : Boolean.valueOf(value);
        if (isAuto)
        {
            /**
             * Logic to set AutoStart. <br>
             * - Check campaign scheduled exist or not. <br>
             * -- If exist check scheduled is enabled or not?<br>
             * -- else throw exception.
             */
            AOPsSchedule aopsSH = _tctx.getDB().FindAssert(new AOPsScheduleQuery().filterByAops(_aops.getId()));

            if (!aopsSH.getIsScheduleEnable() && isAuto)
            {
                throw new GravityIllegalArgumentException("Scheduled is not enabled for Campaign : " + _aops.getCode(), AOPsProperties.Keys.Global_AutoStart.name(), EventFailedCause.ValueOutOfRange);
            }

        }
        AOPsProperties aopsGlobal = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AutoStart));
        if (aopsGlobal == null)
        {
            aopsGlobal = new AOPsProperties();
            aopsGlobal.setAOPs(_aops);
            aopsGlobal.setConfKey(AOPsProperties.Keys.Global_AutoStart.name());
            aopsGlobal.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsGlobal));
        }
        else
        {
            aopsGlobal.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsGlobal));
        }
    }

    private void setGlobalSessionExtend(String ACWExtend) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopGlobal_SessionExtend = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SessionExtend));
            if (aopGlobal_SessionExtend == null)
            {
                aopGlobal_SessionExtend = new AOPsProperties();
                aopGlobal_SessionExtend.setAOPs(_aops);
                aopGlobal_SessionExtend.setConfKey(AOPsProperties.Keys.Global_SessionExtend.name());
                aopGlobal_SessionExtend.setConfValue(Integer.valueOf(ACWExtend).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_SessionExtend));
            }
            else
            {
                aopGlobal_SessionExtend.setConfValue(Integer.valueOf(ACWExtend).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_SessionExtend));
            }

        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Intger type", AOPsProperties.Keys.Global_SessionExtend.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setGlobalDisposeExtend(String ACWExtend) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopGlobal_DisposeExtend = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_DisposeExtend));
            if (aopGlobal_DisposeExtend == null)
            {
                aopGlobal_DisposeExtend = new AOPsProperties();
                aopGlobal_DisposeExtend.setAOPs(_aops);
                aopGlobal_DisposeExtend.setConfKey(AOPsProperties.Keys.Global_DisposeExtend.name());
                aopGlobal_DisposeExtend.setConfValue(Integer.valueOf(ACWExtend).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_DisposeExtend));
            }
            else
            {
                aopGlobal_DisposeExtend.setConfValue(Integer.valueOf(ACWExtend).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_DisposeExtend));
            }

        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Intger type", AOPsProperties.Keys.Global_DisposeExtend.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setAgentReadyOn(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.AgentReadyOn agRdyOn = (value == null || value.isEmpty() ? null : AOPsProperties.AgentReadyOn.valueOf(value));

            AOPsProperties aopAgentReadyOn = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AgentReadyOn));
            if (aopAgentReadyOn == null)
            {
                aopAgentReadyOn = new AOPsProperties();
                aopAgentReadyOn.setAOPs(_aops);
                aopAgentReadyOn.setConfKey(AOPsProperties.Keys.Global_AgentReadyOn.name());
                aopAgentReadyOn.setConfValue(agRdyOn.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopAgentReadyOn));
            }
            else
            {
                aopAgentReadyOn.setConfValue(agRdyOn.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopAgentReadyOn));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.Global_AgentReadyOn.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setMOHCodeAgent(String mohcode) throws GravityException, CODEException
    {
        String value = (mohcode == null || mohcode.isEmpty()) ? null : mohcode;

        AOPsProperties aopMOHCodeAgent = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_MOH_Code_Agent));
        if (aopMOHCodeAgent == null)
        {
            aopMOHCodeAgent = new AOPsProperties();
            aopMOHCodeAgent.setAOPs(_aops);
            aopMOHCodeAgent.setConfKey(AOPsProperties.Keys.Global_MOH_Code_Agent.name());
            aopMOHCodeAgent.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopMOHCodeAgent));
        }
        else
        {
            aopMOHCodeAgent.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopMOHCodeAgent));
        }
    }

    private void setAgentContactAddressAccesses(String agcon) throws GravityException, Exception, CODEException
    {
        if (agcon == null)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.Global_AgentContactAddressAccesses.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        try
        {
            List<AOPsProperties.AgentContactAddressAccess> accessList = new ArrayList<>();
            if (!agcon.trim().isEmpty())
            {
                accessList = Arrays.stream(agcon.split(",")).map(String::trim).map(AOPsProperties.AgentContactAddressAccess::valueOf).collect(Collectors.toList());
            }
            JSONArray accessArray = new JSONArray(accessList);

            AOPsProperties aopConAddAcc = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AgentContactAddressAccesses));
            if (aopConAddAcc == null)
            {
                aopConAddAcc = new AOPsProperties();
                aopConAddAcc.setAOPs(_aops);
                aopConAddAcc.setConfKey(AOPsProperties.Keys.Global_AgentContactAddressAccesses.name());
                aopConAddAcc.setConfValue(accessArray.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopConAddAcc));
            }
            else
            {
                aopConAddAcc.setConfValue(accessArray.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopConAddAcc));
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.Global_AgentContactAddressAccesses.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    private void setCallbackTypes(String callbacktypes) throws GravityException, Exception, CODEException
    {
        try
        {
            if (callbacktypes == null || callbacktypes.trim().isEmpty())
            {

                throw new GravityIllegalArgumentException("Invalid callbacktype", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
            }
            String[] arrCl = callbacktypes.split(",");
            ArrayList<CallbackType> alClbkTy = (ArrayList<CallbackType>) Arrays.asList(arrCl).stream().map(cl -> CallbackType.valueOf(cl.trim())).collect(Collectors.toList());
            ArrayList<String> al = JSONUtil.FromJSON(alClbkTy.toString(), ArrayList.class);
            JSONArray alclarray = new JSONArray(al);
            AOPsProperties aopCallbackTypes = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_CallbackTypes));
            if (aopCallbackTypes == null)
            {
                aopCallbackTypes = new AOPsProperties();
                aopCallbackTypes.setAOPs(_aops);
                aopCallbackTypes.setConfKey(AOPsProperties.Keys.Global_CallbackTypes.name());
                aopCallbackTypes.setConfValue(alclarray.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCallbackTypes));
            }
            else
            {
                aopCallbackTypes.setConfValue(alclarray.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCallbackTypes));
            }

        }
        catch (IllegalArgumentException e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.Global_CallbackTypes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setHomeCRMURL(String home_crm_url) throws GravityException, CODEException
    {
        String value = (home_crm_url == null || home_crm_url.isEmpty()) ? null : home_crm_url;
        AOPsProperties aopHomeCRMURL = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_Home_CRM_URL));
        if (aopHomeCRMURL == null)
        {
            aopHomeCRMURL = new AOPsProperties();
            aopHomeCRMURL.setAOPs(_aops);
            aopHomeCRMURL.setConfKey(AOPsProperties.Keys.Global_Home_CRM_URL.name());
            aopHomeCRMURL.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopHomeCRMURL));
        }
        else
        {
            aopHomeCRMURL.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopHomeCRMURL));
        }

    }

    private void setHomeCRMParam(String home_crm_param) throws GravityException, CODEException
    {
        String value = (home_crm_param == null || home_crm_param.isEmpty()) ? null : home_crm_param;
        AOPsProperties aopHomeCRMParam = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_Home_CRM_Param));
        if (aopHomeCRMParam == null)
        {
            aopHomeCRMParam = new AOPsProperties();
            aopHomeCRMParam.setAOPs(_aops);
            aopHomeCRMParam.setConfKey(AOPsProperties.Keys.Global_Home_CRM_Param.name());
            aopHomeCRMParam.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopHomeCRMParam));
        }
        else
        {
            aopHomeCRMParam.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopHomeCRMParam));
        }

    }

    private void setPopupCRMURL(String popup_crm_url) throws GravityException, CODEException
    {
        String value = (popup_crm_url == null || popup_crm_url.isEmpty()) ? null : popup_crm_url;
        AOPsProperties aopPopupCRMURL = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_Popup_CRM_URL));
        if (aopPopupCRMURL == null)
        {
            aopPopupCRMURL = new AOPsProperties();
            aopPopupCRMURL.setAOPs(_aops);
            aopPopupCRMURL.setConfKey(AOPsProperties.Keys.Global_Popup_CRM_URL.name());
            aopPopupCRMURL.setConfValue(value);

            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopPopupCRMURL));
        }
        else
        {
            aopPopupCRMURL.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopPopupCRMURL));
        }
    }

    private void setPopupCRMParam(String popup_crm_param) throws GravityException, CODEException
    {
        String value = (popup_crm_param == null || popup_crm_param.isEmpty()) ? null : popup_crm_param;
        AOPsProperties aopPopupCRMParam = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_Popup_CRM_Param));
        if (aopPopupCRMParam == null)
        {
            aopPopupCRMParam.setAOPs(_aops);
            aopPopupCRMParam = new AOPsProperties();
            aopPopupCRMParam.setConfKey(AOPsProperties.Keys.Global_Popup_CRM_Param.name());
            aopPopupCRMParam.setConfValue(value);

            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopPopupCRMParam));
        }
        else
        {
            aopPopupCRMParam.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopPopupCRMParam));
        }

    }

    private void setPopupScriptURL(String popup_crm_url) throws GravityException, CODEException
    {
        String value = (popup_crm_url == null || popup_crm_url.isEmpty()) ? null : popup_crm_url;
        AOPsProperties aopPopupScriptURL = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_Popup_Script_URL));
        if (aopPopupScriptURL == null)
        {
            aopPopupScriptURL = new AOPsProperties();
            aopPopupScriptURL.setAOPs(_aops);
            aopPopupScriptURL.setConfKey(AOPsProperties.Keys.Global_Popup_Script_URL.name());
            aopPopupScriptURL.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopPopupScriptURL));
        }
        else
        {
            aopPopupScriptURL.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopPopupScriptURL));
        }

    }

    private void setPopupScriptParam(String popup_crm_param) throws GravityException, CODEException
    {
        String value = (popup_crm_param == null || popup_crm_param.isEmpty()) ? null : popup_crm_param;
        AOPsProperties aopPopupScriptParam = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_Popup_Script_Param));
        if (aopPopupScriptParam == null)
        {
            aopPopupScriptParam = new AOPsProperties();
            aopPopupScriptParam.setAOPs(_aops);
            aopPopupScriptParam.setConfKey(AOPsProperties.Keys.Global_Popup_Script_Param.name());
            aopPopupScriptParam.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopPopupScriptParam));
        }
        else
        {
            aopPopupScriptParam.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopPopupScriptParam));
        }

    }

    private void setContactScheduledExpiryTimeout(String get) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopscextime = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_ContactScheduledExpiryTimeout));
            if (aopscextime == null)
            {
                aopscextime = new AOPsProperties();
                aopscextime.setAOPs(_aops);
                aopscextime.setConfKey(AOPsProperties.Keys.Global_ContactScheduledExpiryTimeout.name());
                aopscextime.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopscextime));
            }
            else
            {
                aopscextime.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopscextime));
            }
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Intger type", AOPsProperties.Keys.Global_ContactScheduledExpiryTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setContactScheduledMaxDuration(String get) throws GravityException, CODEException
    {
        Integer duration = null;
        try
        {
            duration = Integer.valueOf(get);
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Intger type", AOPsProperties.Keys.Global_ContactScheduledMaxDuration.name(), EventFailedCause.ValueOutOfRange);
        }

        if (duration > Limits.ContactScheduledOnAfter_MAX)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.Global_ContactScheduledMaxDuration.name(), EventFailedCause.DataBoundaryLimitViolation, EvCauseRequestValidationFail.InvalidParamName);
        }

        AOPsProperties aopscmaxduration = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_ContactScheduledMaxDuration));
        if (aopscmaxduration == null)
        {
            aopscmaxduration = new AOPsProperties();
            aopscmaxduration.setAOPs(_aops);
            aopscmaxduration.setConfKey(AOPsProperties.Keys.Global_ContactScheduledMaxDuration.name());
            aopscmaxduration.setConfValue(duration.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopscmaxduration));
        }
        else
        {
            aopscmaxduration.setConfValue(duration.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopscmaxduration));
        }
        aopscmaxduration.setConfValue(duration.toString());
    }

    private void setSSOEnabled(String value) throws GravityException, CODEException
    {

        Boolean ssoenble = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopSSOEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SSOEnabled));
        if (aopSSOEnabled == null)
        {
            aopSSOEnabled = new AOPsProperties();
            aopSSOEnabled.setAOPs(_aops);
            aopSSOEnabled.setConfKey(AOPsProperties.Keys.Global_SSOEnabled.name());
            aopSSOEnabled.setConfValue(ssoenble.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopSSOEnabled));
        }
        else
        {
            aopSSOEnabled.setConfValue(ssoenble.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopSSOEnabled));
        }
    }

    private void setSSOAuthParams(String SSOAuthParams) throws Exception, GravityException, CODEException
    {
        Properties prop = JSONUtil.FromJSON(SSOAuthParams, Properties.class);
        AOPsProperties aopSSOAuthParams = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SSOAuthParams));
        if (aopSSOAuthParams == null)
        {
            aopSSOAuthParams = new AOPsProperties();
            aopSSOAuthParams.setAOPs(_aops);
            aopSSOAuthParams.setConfKey(AOPsProperties.Keys.Global_SSOAuthParams.name());
            aopSSOAuthParams.setConfValue(SSOAuthParams);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopSSOAuthParams));
        }
        else
        {
            aopSSOAuthParams.setConfValue(SSOAuthParams);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopSSOAuthParams));
        }

    }

    private void setXTRecordingMediaType(String value) throws GravityIllegalArgumentException, CODEException
    {
        try
        {
            AOPsProperties.RecordingMediaType recMd = (value == null || value.isEmpty() ? null : AOPsProperties.RecordingMediaType.valueOf(value));

            AOPsProperties aopXTRecordingMediaType = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_RecordingMediaType));
            if (aopXTRecordingMediaType == null)
            {
                aopXTRecordingMediaType = new AOPsProperties();
                aopXTRecordingMediaType.setAOPs(_aops);
                aopXTRecordingMediaType.setConfKey(AOPsProperties.Keys.XT_RecordingMediaType.name());
                aopXTRecordingMediaType.setConfValue(recMd.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTRecordingMediaType));
            }
            else
            {
                aopXTRecordingMediaType.setConfValue(recMd.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTRecordingMediaType));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XT_RecordingMediaType.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        catch (GravityException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void setXTRecordingMode(String value) throws GravityIllegalArgumentException, CODEException
    {
        try
        {
            AOPsProperties.RecordingMode recMd = (value == null || value.isEmpty() ? null : AOPsProperties.RecordingMode.valueOf(value));

            AOPsProperties aopXTRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_RecordingMode));
            if (aopXTRecordingMode == null)
            {
                aopXTRecordingMode = new AOPsProperties();
                aopXTRecordingMode.setAOPs(_aops);
                aopXTRecordingMode.setConfKey(AOPsProperties.Keys.XT_RecordingMode.name());
                aopXTRecordingMode.setConfValue(recMd.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTRecordingMode));
            }
            else
            {
                aopXTRecordingMode.setConfValue(recMd.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XT_RecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);

        }
        catch (GravityException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void setGlobalDefRecordListCode(String conlistcode) throws GravityException, Exception, CODEException
    {
        try
        {
            //Check supplied recordList is available in ALM or not.
            switch (_aops.getAOPsType())
            {
                case Campaign ->
                {
                    Boolean isListExist = _tctx.getALMCtx().IsRecordListExist((Campaign) _aops, conlistcode);
                    if (!isListExist)
                    {
                        throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ContactNotFoundFromALM, "[Campaign,RecordList ==" + _aops.getId() + "," + conlistcode + " ]");
                    }
                }
                case Process ->
                    throw new GravityIllegalArgumentException(_aops.toString(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
            }

            AOPsProperties aopdecontact = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_DefRecordListCode));
            if (aopdecontact == null)
            {
                aopdecontact = new AOPsProperties();
                aopdecontact.setAOPs(_aops);
                aopdecontact.setConfKey(AOPsProperties.Keys.Global_DefRecordListCode.name());
                aopdecontact.setConfValue(conlistcode);

                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopdecontact));
            }
            else
            {
                aopdecontact.setConfValue(conlistcode);
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopdecontact));
            }
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Long type", AOPsProperties.Keys.Global_DefRecordListCode.name(), EventFailedCause.ValueOutOfRange);
        }

    }

    private void setXTExternalAddressRegx(String extregx) throws GravityException, CODEException
    {
        //TBD: need to validate the value is a correct format or not.
        String value = (extregx == null || extregx.isEmpty()) ? null : extregx;
        AOPsProperties aopXT_ExtAddRegx = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_ExternalAddressRegx));
        if (aopXT_ExtAddRegx == null)
        {
            aopXT_ExtAddRegx = new AOPsProperties();
            aopXT_ExtAddRegx.setAOPs(_aops);
            aopXT_ExtAddRegx.setConfKey(AOPsProperties.Keys.XT_ExternalAddressRegx.name());
            aopXT_ExtAddRegx.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXT_ExtAddRegx));
        }
        else
        {
            aopXT_ExtAddRegx.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXT_ExtAddRegx));
        }

//        camprop.getXT().setExternalAddressRegx(value);
    }

    private void setXTInternalAddressRegx(String intregx) throws GravityException, CODEException
    {
        //TBD: need to validate the value is a correct format or not.
        String value = (intregx == null || intregx.isEmpty()) ? null : intregx;

        AOPsProperties aopXT_intAddRegx = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_InternalAddressRegx));
        if (aopXT_intAddRegx == null)
        {
            aopXT_intAddRegx = new AOPsProperties();
            aopXT_intAddRegx.setAOPs(_aops);
            aopXT_intAddRegx.setConfKey(AOPsProperties.Keys.XT_InternalAddressRegx.name());
            aopXT_intAddRegx.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXT_intAddRegx));
        }
        else
        {
            aopXT_intAddRegx.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXT_intAddRegx));
        }
    }

    private void setXTIBEnableBlockCall(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTIBEnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_IB_EnableBlockFilter));
        if (aopXTIBEnableBlockFilter == null)
        {
            aopXTIBEnableBlockFilter = new AOPsProperties();
            aopXTIBEnableBlockFilter.setAOPs(_aops);
            aopXTIBEnableBlockFilter.setConfKey(AOPsProperties.Keys.XT_IB_EnableBlockFilter.name());
            aopXTIBEnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTIBEnableBlockFilter));
        }
        else
        {
            aopXTIBEnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTIBEnableBlockFilter));
        }
    }

    private void setXEMIBEnableBlockCall(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEM_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_IB_EnableBlockFilter));
        if (aopXEM_IB_EnableBlockFilter == null)
        {
            aopXEM_IB_EnableBlockFilter = new AOPsProperties();
            aopXEM_IB_EnableBlockFilter.setAOPs(_aops);
            aopXEM_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XEM_IB_EnableBlockFilter.name());
            aopXEM_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEM_IB_EnableBlockFilter));
        }
        else
        {
            aopXEM_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEM_IB_EnableBlockFilter));
        }
    }

    private void setXCH_IB_EnableBlockCall(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCH_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_IB_EnableBlockFilter));
        if (aopXCH_IB_EnableBlockFilter == null)
        {
            aopXCH_IB_EnableBlockFilter = new AOPsProperties();
            aopXCH_IB_EnableBlockFilter.setAOPs(_aops);
            aopXCH_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XCH_IB_EnableBlockFilter.name());
            aopXCH_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_IB_EnableBlockFilter));
        }
        else
        {
            aopXCH_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCH_IB_EnableBlockFilter));
        }
    }

    private void setXVDIBEnableBlockCall(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXVD_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_IB_EnableBlockFilter));
        if (aopXVD_IB_EnableBlockFilter == null)
        {
            aopXVD_IB_EnableBlockFilter = new AOPsProperties();
            aopXVD_IB_EnableBlockFilter.setAOPs(_aops);
            aopXVD_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XVD_IB_EnableBlockFilter.name());
            aopXVD_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVD_IB_EnableBlockFilter));
        }
        else
        {
            aopXVD_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVD_IB_EnableBlockFilter));
        }
    }

    private void setXSOIBEnableBlockCall(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXSO_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_IB_EnableBlockFilter));
        if (aopXSO_IB_EnableBlockFilter == null)
        {
            aopXSO_IB_EnableBlockFilter = new AOPsProperties();
            aopXSO_IB_EnableBlockFilter.setAOPs(_aops);
            aopXSO_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XSO_IB_EnableBlockFilter.name());
            aopXSO_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSO_IB_EnableBlockFilter));
        }
        else
        {
            aopXSO_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSO_IB_EnableBlockFilter));
        }
    }

    private void setXMIBEnableBlockCall(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXM_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_IB_EnableBlockFilter));
        if (aopXM_IB_EnableBlockFilter == null)
        {
            aopXM_IB_EnableBlockFilter = new AOPsProperties();
            aopXM_IB_EnableBlockFilter.setAOPs(_aops);
            aopXM_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XM_IB_EnableBlockFilter.name());
            aopXM_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXM_IB_EnableBlockFilter));
        }
        else
        {
            aopXM_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXM_IB_EnableBlockFilter));
        }
    }

    private void setXTIBAllowDialBack(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTIBAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_IB_AllowDialBack));
        if (aopXTIBAllowDialBack == null)
        {
            aopXTIBAllowDialBack = new AOPsProperties();
            aopXTIBAllowDialBack.setAOPs(_aops);
            aopXTIBAllowDialBack.setConfKey(AOPsProperties.Keys.XT_IB_AllowDialBack.name());
            aopXTIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTIBAllowDialBack));
        }
        else
        {
            aopXTIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTIBAllowDialBack));
        }
    }

    private void setXTOBAllowedDialModes(String campmodes) throws GravityException, RADException, CODEException
    {
        if (campmodes == null || campmodes.trim().isEmpty())
        {

            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XT_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        //TBD:need to check for IllegalArgumentException for campaign mode.
        try
        {
            List<DialMode> listCampModes = Arrays.asList(campmodes.split(",")).stream().map(cm -> DialMode.valueOf(cm)).collect(Collectors.toList());
            ArrayList<String> aldialmode = JSONUtil.FromJSON(listCampModes.toString(), ArrayList.class);
            JSONArray dialmodejsonarr = new JSONArray(aldialmode.toArray());

            //If Predictive dialmode removed form the alloweDialed modes then we have to unmpa the PredQueue also as there are no use of Predictive queue now.And if we are not removing it then it will not mapped with any other predictive campaign also.
            if (!aldialmode.contains(DialMode.Predictive.name()))
            {
                AOPsProperties aopPreedQueue = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_PredQueue));
                if (aopPreedQueue != null)
                {
                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreedQueue));
                }

            }
            if (!aldialmode.contains(DialMode.Preview.name()))
            {
                AOPsProperties aopPreviewLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_PreviewTimeout));
                if (aopPreviewLimit != null)
                {
                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreviewLimit));
                }
            }
            AOPsProperties aopXTOBAllowedDialModes = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_AllowedDialModes));
            if (aopXTOBAllowedDialModes == null)
            {
                aopXTOBAllowedDialModes = new AOPsProperties();
                aopXTOBAllowedDialModes.setAOPs(_aops);
                aopXTOBAllowedDialModes.setConfKey(AOPsProperties.Keys.XT_OB_AllowedDialModes.name());
                aopXTOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBAllowedDialModes));
            }
            else
            {
                aopXTOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBAllowedDialModes));
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XT_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    private void setXTOBCheckDNC(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        if (val)
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
        }

        AOPsProperties aopXTOBCheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_CheckDNC));
        if (aopXTOBCheckDNC == null)
        {
            aopXTOBCheckDNC = new AOPsProperties();
            aopXTOBCheckDNC.setAOPs(_aops);
            aopXTOBCheckDNC.setConfKey(AOPsProperties.Keys.XT_OB_CheckDNC.name());
            aopXTOBCheckDNC.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBCheckDNC));
        }
        else
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
            aopXTOBCheckDNC.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBCheckDNC));
        }

    }

    private void setXTOBPreviewTimeout(String timeout) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopXTOBPertime = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_PreviewTimeout));
            if (aopXTOBPertime == null)
            {
                aopXTOBPertime = new AOPsProperties();
                aopXTOBPertime.setAOPs(_aops);
                aopXTOBPertime.setConfKey(AOPsProperties.Keys.XT_OB_PreviewTimeout.name());
                aopXTOBPertime.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBPertime));
            }
            else
            {
                aopXTOBPertime.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBPertime));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XT_OB_PreviewTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXTOBCanRejectPreview(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTOBCanRejectPreview = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_CanRejectPreview));
        if (aopXTOBCanRejectPreview == null)
        {
            aopXTOBCanRejectPreview = new AOPsProperties();
            aopXTOBCanRejectPreview.setAOPs(_aops);
            aopXTOBCanRejectPreview.setConfKey(AOPsProperties.Keys.XT_OB_CanRejectPreview.name());
            aopXTOBCanRejectPreview.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBCanRejectPreview));
        }
        else
        {
            aopXTOBCanRejectPreview.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBCanRejectPreview));
        }
    }

    private void setXTOBMaxAttemptCount(String attempt) throws GravityException, CODEException
    {
        try
        {
            Integer maxattempt = Integer.valueOf(attempt);
            if (maxattempt < 1 || maxattempt > 1024)
            {
                throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XT_OB_MaxAttemptCount.name(), EventFailedCause.DataBoundaryLimitViolation);
            }
            AOPsProperties aopXT_OB_MaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_MaxAttemptCount));
            if (aopXT_OB_MaxAttemptCount == null)
            {
                aopXT_OB_MaxAttemptCount = new AOPsProperties();
                aopXT_OB_MaxAttemptCount.setAOPs(_aops);
                aopXT_OB_MaxAttemptCount.setConfKey(AOPsProperties.Keys.XT_OB_MaxAttemptCount.name());
                aopXT_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXT_OB_MaxAttemptCount));
            }
            else
            {
                aopXT_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXT_OB_MaxAttemptCount));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XT_OB_MaxAttemptCount.name(), EventFailedCause.ValueOutOfRange);
        }

    }

    private void setXEMIBAuthParams(String authprms) throws Exception, GravityException, CODEException
    {
        Properties props = JSONUtil.FromJSON(authprms, Properties.class);
        AOPsProperties aopXEMIBAuthParams = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_IB_AuthParams));
        if (aopXEMIBAuthParams == null)
        {
            aopXEMIBAuthParams = new AOPsProperties();
            aopXEMIBAuthParams.setAOPs(_aops);
            aopXEMIBAuthParams.setConfKey(AOPsProperties.Keys.XEM_IB_AuthParams.name());
            aopXEMIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMIBAuthParams));
        }
        else
        {
            aopXEMIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMIBAuthParams));
        }
    }

    private void setXTOBEnableDialChain(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_EnableDialChain));
        if (aopXTOBEnableDialChain == null)
        {
            aopXTOBEnableDialChain = new AOPsProperties();
            aopXTOBEnableDialChain.setAOPs(_aops);
            aopXTOBEnableDialChain.setConfKey(AOPsProperties.Keys.XT_OB_EnableDialChain.name());
            aopXTOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBEnableDialChain));
        }
        else
        {
            aopXTOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBEnableDialChain));
        }
    }

    private void setXTOBDialLimit(String value) throws GravityException, CODEException
    {
        Integer dialLimit = null;
        try
        {
            dialLimit = Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XT_OB_DialLimit.name(), EventFailedCause.ValueOutOfRange);
        }
        if (dialLimit < Limits.DialLimit_MIN || dialLimit > Limits.DialLimit_MAX)
        {
            throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XT_OB_DialLimit.name(), EventFailedCause.DataBoundaryLimitViolation);
        }

        AOPsProperties aopXTOBDialLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_DialLimit));
        if (aopXTOBDialLimit == null)
        {
            aopXTOBDialLimit = new AOPsProperties();
            aopXTOBDialLimit.setAOPs(_aops);
            aopXTOBDialLimit.setConfKey(AOPsProperties.Keys.XT_OB_DialLimit.name());
            aopXTOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBDialLimit));
        }
        else
        {
            aopXTOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBDialLimit));
        }
    }

    private void setXTOBRouteToAddress(String value) throws GravityException, CODEException
    {



        AOPsProperties aopXTOBRouteToAddress = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_RouteToAddress));
        if (aopXTOBRouteToAddress == null)
        {
            aopXTOBRouteToAddress = new AOPsProperties();
            aopXTOBRouteToAddress.setAOPs(_aops);
            aopXTOBRouteToAddress.setConfKey(AOPsProperties.Keys.XT_OB_RouteToAddress.name());
            aopXTOBRouteToAddress.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBRouteToAddress));
        }
        else
        {
            aopXTOBRouteToAddress.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBRouteToAddress));
        }
        AOPsProperties cdnprops = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_RouteToCDN));

        if (cdnprops != null)
        {
            entities.add(new NameValuePair(ENActionList.Action.Delete.name(), cdnprops));
        }
    }

    private void setXTOBRouteOnAlert(String value) throws GravityException, CODEException
    {
        Boolean val = value == null || value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXTOBRouteOnAlert = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_RouteOnAlert));
        if (aopXTOBRouteOnAlert == null)
        {
            aopXTOBRouteOnAlert = new AOPsProperties();
            aopXTOBRouteOnAlert.setAOPs(_aops);
            aopXTOBRouteOnAlert.setConfKey(AOPsProperties.Keys.XT_OB_RouteOnAlert.name());
            aopXTOBRouteOnAlert.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBRouteOnAlert));
        }
        else
        {
            aopXTOBRouteOnAlert.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBRouteOnAlert));
        }
    }
    private void setXTOBManualDialAllowed(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTOBManualDialAllowed = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_AllowManualDial));
        if (aopXTOBManualDialAllowed == null)
        {
            aopXTOBManualDialAllowed = new AOPsProperties();
            aopXTOBManualDialAllowed.setAOPs(_aops);
            aopXTOBManualDialAllowed.setConfKey(AOPsProperties.Keys.XT_OB_AllowManualDial.name());
            aopXTOBManualDialAllowed.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBManualDialAllowed));
        }
        else
        {
            aopXTOBManualDialAllowed.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBManualDialAllowed));
        }
    }

    private void setXTOBPredQueue(String predq) throws GravityException, GravityEntityExistsException, CODEException
    {

        //Check the predictive queue should not already mapped with any other campaign as predictive queue.
//        //Check this queue should not mapped with any other campaign.
        JPAQuery skQry = new JPAQuery("select s from Skill s " + " where s.Queue.Address=:address And s.AOPs.Id <>: campid And s.EntityState=:isdel");
        skQry.setParam("address", predq);
        skQry.setParam("campid", _aops.getId());
        skQry.setParam("isdel", EntityState.Active);

        List<Skill> mappedSkills = _tctx.getDB().Select(EN.Skill, skQry);
        if (!mappedSkills.isEmpty())
        {
            GravityEntityExistsException ex = new GravityEntityExistsException(EN.Queue.name(), "Campaign,Skill,Queue", OPRelational.Eq, mappedSkills.get(0).getAOPs().getName() + "," + mappedSkills.get(0).getName() + "," + predq);
            throw ex;
        }
        AOPsProperties aopXTOBPredQueue = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_PredQueue));
        if (aopXTOBPredQueue == null)
        {
            aopXTOBPredQueue = new AOPsProperties();
            aopXTOBPredQueue.setAOPs(_aops);
            aopXTOBPredQueue.setConfKey(AOPsProperties.Keys.XT_OB_PredQueue.name());
            aopXTOBPredQueue.setConfValue(predq);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBPredQueue));
        }
        else
        {
            aopXTOBPredQueue.setConfValue(predq);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBPredQueue));
        }
    }

    private void setXCHSessionTimeout(String timeout) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopXCHSessionTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_SessionTimeout));
            if (aopXCHSessionTimeout == null)
            {
                aopXCHSessionTimeout = new AOPsProperties();
                aopXCHSessionTimeout.setAOPs(_aops);
                aopXCHSessionTimeout.setConfKey(AOPsProperties.Keys.XCH_SessionTimeout.name());
                aopXCHSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHSessionTimeout));
            }
            else
            {
                aopXCHSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHSessionTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XCH_SessionTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXCHRecordingMode(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopsXCH_EnableRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_EnableRecording));
        if (aopsXCH_EnableRecording == null)
        {
            aopsXCH_EnableRecording = new AOPsProperties();
            aopsXCH_EnableRecording.setAOPs(_aops);
            aopsXCH_EnableRecording.setConfKey(AOPsProperties.Keys.XCH_EnableRecording.name());
            aopsXCH_EnableRecording.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXCH_EnableRecording));
        }
        else
        {
            aopsXCH_EnableRecording.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXCH_EnableRecording));
        }
    }

    private void setXCHIBAuthParams(String authprms) throws Exception, GravityException, CODEException
    {

        Properties props = JSONUtil.FromJSON(authprms, Properties.class);

        AOPsProperties aopXCHIBAuthParams = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_IB_AuthParams));
        if (aopXCHIBAuthParams == null)
        {
            aopXCHIBAuthParams = new AOPsProperties();
            aopXCHIBAuthParams.setConfKey(AOPsProperties.Keys.XCH_IB_AuthParams.name());
            aopXCHIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHIBAuthParams));
        }
        else
        {
            aopXCHIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHIBAuthParams));
        }

    }

    private Set<String> assertCDNValues(String attrname, String cdnstr) throws GravityException, CODEException
    {
        try
        {
            /**
             * Inputs may be a alfa numeric string or range.<br>
             * Ranges must be numeric. <br>
             * case-1:123-345 -> valid. <br>
             * case-2:123abc-123cde -> Invalid. <br>
             * eg:- 123ABC,123-123,123,abc,...etc
             */
            //CDN string can be empty when user remove all cdns from a campaign.
            Set<String> hscdn = new HashSet<>();
            if (cdnstr == null || cdnstr.trim().isEmpty())
            {
                return hscdn;
            }

            //CDN string can be empty when user remove all cdns from a campaign.
            if (cdnstr.trim().isEmpty())
            {
                return hscdn;
            }
            if (!cdnstr.matches("^[0-9a-zA-Z]+(?:-[0-9a-zA-Z]+)?(?:,[0-9a-zA-Z]+(?:-[0-9a-zA-Z]+)?)*$"))
            {
                throw new GravityIllegalArgumentException("Invalid CDN values('^[0-9a-zA-Z]+(?:-[0-9a-zA-Z]+)?(?:,[0-9a-zA-Z]+(?:-[0-9a-zA-Z]+)?)*$')");
            }

            String[] cdns = cdnstr.split(",");
            for (String cdn : cdns)
            {
                if (cdn.contains("-"))
                {
                    String[] arrCDN = cdn.split("-");
                    int lb = 0, ub = 0;
                    if (arrCDN.length != 2)
                    {
                        throw new GravityIllegalArgumentException("Invalid CDN values");
                    }

                    try
                    {
                        lb = Integer.parseInt(arrCDN[0]);
                        ub = Integer.parseInt(arrCDN[1]);
                    }
                    catch (NumberFormatException ex)
                    {
                        throw new GravityIllegalArgumentException("value must be an Integer type", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);

                    }
                    if (lb > ub)
                    {
                        throw new GravityIllegalArgumentException("Invalid range lb :" + lb + " ub: =" + ub);
                    }
                    for (int i = lb; i <= ub; i++)
                    {
                        hscdn.add(i + "");
                    }
                }
                else
                {
                    hscdn.add(cdn);
                }

            }
//        return hscdn;
            //V:140721
            StringBuilder qryStr = new StringBuilder("Select c.ConfValue from AOPsProperties c ").append("where c.ConfKey =: confkey");

            if (_aops.getId() != null)
            {
                qryStr.append(" And c.AOPs.Id <>: curraopid ");
            }

            JPAQuery qry = new JPAQuery(qryStr.toString());
            qry.setParam("confkey", attrname);
            if (_aops.getId() != null)
            {
                qry.setParam("curraopid", _aops.getId());
            }
            ArrayList<String> cdnsdbs = (ArrayList<String>) _tctx.getDB().Select(qry);
            for (String cdndb : cdnsdbs)
            {
                HashSet<String> tempcdns = JSONUtil.FromJSON(cdndb, HashSet.class);
                if (tempcdns.isEmpty())
                {
                    //CDN can be empty array when all cdns ar unmapped from the Campaign , so we are allowing the cdn with empty array.
                    continue;
                }
                if (hscdn.containsAll(tempcdns))
                {
                    GravityEntityExistsException rex = new GravityEntityExistsException(EN.AOPsProperties.name(), "Campaign, " + _aops.getId() + attrname, OPRelational.Eq, String.join(", ", tempcdns));
                    throw rex;
                }

            }

            return hscdn;
        }
        catch (Exception ex)
        {
            throw new CODEException(ex);
        }
    }

    private void setAutoStartDialMode(String dialmode) throws GravityException, CODEException
    {
        if (dialmode.trim().isEmpty())
        {
            return;
        }

        //TBD: need to cehck DialMode is subset of Allowed dialed mode or not but now it's in XT_OB.
        DialMode dialMode = DialMode.valueOf(dialmode);
        //TBD:Need to check.
//        if (!UtilCampaign.IsValidModeForType(camprop.getCampaign().getCampaignType(), campMode))
//        {
//            throw new GravityIllegalArgumentException(camprop.getCampaign().getId(), campMode, (DialMode[]) UtilCampaign.GetValidModesForTypeForOB().toArray());
//        }

        AOPsProperties aopsAutoStartDialMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AutoStartDialMode));
        if (aopsAutoStartDialMode == null)
        {
            aopsAutoStartDialMode = new AOPsProperties();
            aopsAutoStartDialMode.setAOPs(_aops);
            aopsAutoStartDialMode.setConfKey(AOPsProperties.Keys.Global_AutoStartDialMode.name());
            aopsAutoStartDialMode.setConfValue(dialmode);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsAutoStartDialMode));
        }
        else
        {
            aopsAutoStartDialMode.setConfValue(dialmode);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsAutoStartDialMode));
        }
    }

    private void setXVDSessionTimeout(String timeout) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopsXVDSessionTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_SessionTimeout));
            if (aopsXVDSessionTimeout == null)
            {
                aopsXVDSessionTimeout = new AOPsProperties();
                aopsXVDSessionTimeout.setAOPs(_aops);
                aopsXVDSessionTimeout.setConfKey(AOPsProperties.Keys.XVD_SessionTimeout.name());
                aopsXVDSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVDSessionTimeout));
            }
            else
            {
                aopsXVDSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXVDSessionTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_SessionTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXVDRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.RecordingMode recMd = (value == null || value.isEmpty() ? null : AOPsProperties.RecordingMode.valueOf(value));

            AOPsProperties aopsXVDRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_RecordingMode));
            if (aopsXVDRecordingMode == null)
            {
                aopsXVDRecordingMode = new AOPsProperties();
                aopsXVDRecordingMode.setAOPs(_aops);
                aopsXVDRecordingMode.setConfKey(AOPsProperties.Keys.XVD_RecordingMode.name());
                aopsXVDRecordingMode.setConfValue(recMd.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVDRecordingMode));
            }
            else
            {
                aopsXVDRecordingMode.setConfValue(recMd.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXVDRecordingMode));
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XVD_RecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    private void setXVDIBAuthParams(String authprms) throws Exception, GravityException, CODEException
    {
        Properties props = JSONUtil.FromJSON(authprms, Properties.class);
        AOPsProperties aopsXVDIBAuthParams = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_IB_AuthParams));
        if (aopsXVDIBAuthParams == null)
        {
            aopsXVDIBAuthParams = new AOPsProperties();
            aopsXVDIBAuthParams.setAOPs(_aops);
            aopsXVDIBAuthParams.setConfKey(AOPsProperties.Keys.XVD_IB_AuthParams.name());
            aopsXVDIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVDIBAuthParams));
        }
        else
        {
            aopsXVDIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXVDIBAuthParams));
        }
    }

    private void setXVDOBOverrideDefFromAddress(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXVDOBOverrideDefFromAdd = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_OverrideDefFromAddress));
        if (aopXVDOBOverrideDefFromAdd == null)
        {
            aopXVDOBOverrideDefFromAdd = new AOPsProperties();
            aopXVDOBOverrideDefFromAdd.setAOPs(_aops);
            aopXVDOBOverrideDefFromAdd.setConfKey(AOPsProperties.Keys.XVD_OB_OverrideDefFromAddress.name());
            aopXVDOBOverrideDefFromAdd.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDOBOverrideDefFromAdd));
        }
        else
        {
            aopXVDOBOverrideDefFromAdd.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDOBOverrideDefFromAdd));
        }
    }

    private void setXVDOBMaxAttemptCount(String attempt) throws GravityException, CODEException
    {
        try
        {
            Integer maxattempt = Integer.valueOf(attempt);
            if (maxattempt < 1 || maxattempt > 1024)
            {
                throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XVD_OB_MaxAttemptCount.name(), EventFailedCause.DataBoundaryLimitViolation);
            }

            AOPsProperties aopsXVD_OB_MaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_MaxAttemptCount));
            if (aopsXVD_OB_MaxAttemptCount == null)
            {
                aopsXVD_OB_MaxAttemptCount = new AOPsProperties();
                aopsXVD_OB_MaxAttemptCount.setAOPs(_aops);
                aopsXVD_OB_MaxAttemptCount.setConfKey(AOPsProperties.Keys.XVD_OB_MaxAttemptCount.name());
                aopsXVD_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVD_OB_MaxAttemptCount));
            }
            else
            {
                aopsXVD_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXVD_OB_MaxAttemptCount));
            }

        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_OB_MaxAttemptCount.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXVDOBCheckDNC(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        if (val)
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
        }

        AOPsProperties aopXVDOBCheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_CheckDNC));
        if (aopXVDOBCheckDNC == null)
        {
            aopXVDOBCheckDNC = new AOPsProperties();
            aopXVDOBCheckDNC.setAOPs(_aops);
            aopXVDOBCheckDNC.setConfKey(AOPsProperties.Keys.XVD_OB_CheckDNC.name());
            aopXVDOBCheckDNC.setConfValue(value.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDOBCheckDNC));
        }
        else
        {
            aopXVDOBCheckDNC.setConfValue(value.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDOBCheckDNC));
        }
    }

    private void setXMSessionTimeout(String timeout) throws GravityException, CODEException
    {
        try
        {

            AOPsProperties aopsXMSessionTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_SessionTimeout));
            if (aopsXMSessionTimeout == null)
            {
                aopsXMSessionTimeout = new AOPsProperties();
                aopsXMSessionTimeout.setAOPs(_aops);
                aopsXMSessionTimeout.setConfKey(AOPsProperties.Keys.XM_SessionTimeout.name());
                aopsXMSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXMSessionTimeout));
            }
            else
            {
                aopsXMSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXMSessionTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_SessionTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXMRecordingMode(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopsXM_EnableRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_EnableRecording));
        if (aopsXM_EnableRecording == null)
        {
            aopsXM_EnableRecording = new AOPsProperties();
            aopsXM_EnableRecording.setAOPs(_aops);
            aopsXM_EnableRecording.setConfKey(AOPsProperties.Keys.XM_EnableRecording.name());
            aopsXM_EnableRecording.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXM_EnableRecording));
        }
        else
        {
            aopsXM_EnableRecording.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXM_EnableRecording));
        }
    }

    private void setXMIBAuthParams(String authprms) throws Exception, GravityException, CODEException
    {
        Properties props = JSONUtil.FromJSON(authprms, Properties.class);
        AOPsProperties aopXMIBAuthParams = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_IB_AuthParams));
        if (aopXMIBAuthParams == null)
        {
            aopXMIBAuthParams = new AOPsProperties();
            aopXMIBAuthParams.setAOPs(_aops);
            aopXMIBAuthParams.setConfKey(AOPsProperties.Keys.XM_IB_AuthParams.name());
            aopXMIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMIBAuthParams));
        }
        else
        {
            aopXMIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMIBAuthParams));
        }

    }

    private void setXTAutoAnswer(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTAutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_AutoAnswer));
        if (aopXTAutoAnswer == null)
        {
            aopXTAutoAnswer = new AOPsProperties();
            aopXTAutoAnswer.setAOPs(_aops);
            aopXTAutoAnswer.setConfKey(AOPsProperties.Keys.XT_AutoAnswer.name());
            aopXTAutoAnswer.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTAutoAnswer));
        }
        else
        {
            aopXTAutoAnswer.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTAutoAnswer));
        }
    }

    private void setXT_RecordingAlert(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXT_RecordingAlert = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_RecordingAlert));
        if (aopXT_RecordingAlert == null)
        {
            aopXT_RecordingAlert = new AOPsProperties();
            aopXT_RecordingAlert.setAOPs(_aops);
            aopXT_RecordingAlert.setConfKey(AOPsProperties.Keys.XT_RecordingAlert.name());
            aopXT_RecordingAlert.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXT_RecordingAlert));
        }
        else
        {
            aopXT_RecordingAlert.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXT_RecordingAlert));
        }
    }

    private void setXTAllowReject(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTAllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_AllowReject));
        if (aopXTAllowReject == null)
        {
            aopXTAllowReject = new AOPsProperties();
            aopXTAllowReject.setAOPs(_aops);
            aopXTAllowReject.setConfKey(AOPsProperties.Keys.XT_AllowReject.name());
            aopXTAllowReject.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTAllowReject));
        }
        else
        {
            aopXTAllowReject.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTAllowReject));
        }
    }

    private void setXCHAutoAnswer(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHAutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_AutoAnswer));
        if (aopXCHAutoAnswer == null)
        {
            aopXCHAutoAnswer = new AOPsProperties();
            aopXCHAutoAnswer.setAOPs(_aops);
            aopXCHAutoAnswer.setConfKey(AOPsProperties.Keys.XCH_AutoAnswer.name());
            aopXCHAutoAnswer.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHAutoAnswer));
        }
        else
        {
            aopXCHAutoAnswer.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHAutoAnswer));
        }
    }

    private void setXCH_AutoAnswerDelay(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_AutoAnswerDelay));
        if (aopXCHAutoAnswerDelay == null)
        {
            aopXCHAutoAnswerDelay = new AOPsProperties();
            aopXCHAutoAnswerDelay.setAOPs(_aops);
            aopXCHAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XCH_AutoAnswerDelay.name());
            aopXCHAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHAutoAnswerDelay));
        }
        else
        {
            aopXCHAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHAutoAnswerDelay));
        }
    }

    private void setXCHAllowReject(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHAllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_AllowReject));
        if (aopXCHAllowReject == null)
        {
            aopXCHAllowReject = new AOPsProperties();
            aopXCHAllowReject.setAOPs(_aops);
            aopXCHAllowReject.setConfKey(AOPsProperties.Keys.XCH_AllowReject.name());
            aopXCHAllowReject.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHAllowReject));
        }
        else
        {
            aopXCHAllowReject.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHAllowReject));
        }

    }

    private void setXEMAutoAnswer(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEM_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_AutoAnswer));
        if (aopXEM_AutoAnswer == null)
        {
            aopXEM_AutoAnswer = new AOPsProperties();
            aopXEM_AutoAnswer.setAOPs(_aops);
            aopXEM_AutoAnswer.setConfKey(AOPsProperties.Keys.XEM_AutoAnswer.name());
            aopXEM_AutoAnswer.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEM_AutoAnswer));
        }
        else
        {
            aopXEM_AutoAnswer.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEM_AutoAnswer));
        }
    }

    private void setXEM_AutoAnswerDelay(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEM_AutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_AutoAnswerDelay));
        if (aopXEM_AutoAnswerDelay == null)
        {
            aopXEM_AutoAnswerDelay = new AOPsProperties();
            aopXEM_AutoAnswerDelay.setAOPs(_aops);
            aopXEM_AutoAnswerDelay.setConfKey(AOPsProperties.Keys.XEM_AutoAnswerDelay.name());
            aopXEM_AutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEM_AutoAnswerDelay));
        }
        else
        {
            aopXEM_AutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEM_AutoAnswerDelay));
        }
    }

    private void setXEMEnableRecordingMode(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopsXEM_EnableRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_EnableRecording));
        if (aopsXEM_EnableRecording == null)
        {
            aopsXEM_EnableRecording = new AOPsProperties();
            aopsXEM_EnableRecording.setAOPs(_aops);
            aopsXEM_EnableRecording.setConfKey(AOPsProperties.Keys.XEM_EnableRecording.name());
            aopsXEM_EnableRecording.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXEM_EnableRecording));
        }
        else
        {
            aopsXEM_EnableRecording.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXEM_EnableRecording));
        }
    }

    private void setXEMAllowReject(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEM_AllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_AllowReject));
        if (aopXEM_AllowReject == null)
        {
            aopXEM_AllowReject = new AOPsProperties();
            aopXEM_AllowReject.setAOPs(_aops);
            aopXEM_AllowReject.setConfKey(AOPsProperties.Keys.XEM_AllowReject.name());
            aopXEM_AllowReject.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEM_AllowReject));
        }
        else
        {
            aopXEM_AllowReject.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEM_AllowReject));
        }
    }

    private void setXVDAutoAnswer(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXVDAutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_AutoAnswer));
        if (aopXVDAutoAnswer == null)
        {
            aopXVDAutoAnswer = new AOPsProperties();
            aopXVDAutoAnswer.setAOPs(_aops);
            aopXVDAutoAnswer.setConfKey(AOPsProperties.Keys.XVD_AutoAnswer.name());
            aopXVDAutoAnswer.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDAutoAnswer));
        }
        else
        {
            aopXVDAutoAnswer.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDAutoAnswer));
        }
    }

    private void setXVD_AutoAnswerDelay(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXVDAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_AutoAnswerDelay));
        if (aopXVDAutoAnswerDelay == null)
        {
            aopXVDAutoAnswerDelay = new AOPsProperties();
            aopXVDAutoAnswerDelay.setAOPs(_aops);
            aopXVDAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XVD_AutoAnswerDelay.name());
            aopXVDAutoAnswerDelay.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDAutoAnswerDelay));
        }
        else
        {
            aopXVDAutoAnswerDelay.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDAutoAnswerDelay));
        }
    }

    private void setXVD_AllowReject(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXVDAllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_AllowReject));
        if (aopXVDAllowReject == null)
        {
            aopXVDAllowReject = new AOPsProperties();
            aopXVDAllowReject.setAOPs(_aops);
            aopXVDAllowReject.setConfKey(AOPsProperties.Keys.XVD_AllowReject.name());
            aopXVDAllowReject.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDAllowReject));
        }
        else
        {
            aopXVDAllowReject.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDAllowReject));
        }
    }

    private void setXMAutoAnswer(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXMAutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_AutoAnswer));
        if (aopXMAutoAnswer == null)
        {
            aopXMAutoAnswer = new AOPsProperties();
            aopXMAutoAnswer.setAOPs(_aops);
            aopXMAutoAnswer.setConfKey(AOPsProperties.Keys.XM_AutoAnswer.name());
            aopXMAutoAnswer.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMAutoAnswer));
        }
        else
        {
            aopXMAutoAnswer.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMAutoAnswer));
        }
    }

    private void setXM_AutoAnswerDelay(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXMAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_AutoAnswerDelay));
        if (aopXMAutoAnswerDelay == null)
        {
            aopXMAutoAnswerDelay = new AOPsProperties();
            aopXMAutoAnswerDelay.setAOPs(_aops);
            aopXMAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XM_AutoAnswerDelay.name());
            aopXMAutoAnswerDelay.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMAutoAnswerDelay));
        }
        else
        {
            aopXMAutoAnswerDelay.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMAutoAnswerDelay));
        }
    }

    private void setXMAllowReject(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXMAllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_AllowReject));
        if (aopXMAllowReject == null)
        {
            aopXMAllowReject = new AOPsProperties();
            aopXMAllowReject.setAOPs(_aops);
            aopXMAllowReject.setConfKey(AOPsProperties.Keys.XM_AllowReject.name());
            aopXMAllowReject.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMAllowReject));
        }
        else
        {
            aopXMAllowReject.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMAllowReject));
        }
    }

    private void setAgentTaskLimit(String get) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopsagtasklimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_ContactSessionLimit));
            if (aopsagtasklimit == null)
            {
                aopsagtasklimit = new AOPsProperties();
                aopsagtasklimit.setAOPs(_aops);
                aopsagtasklimit.setConfKey(AOPsProperties.Keys.Global_ContactSessionLimit.name());
                aopsagtasklimit.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsagtasklimit));
            }
            else
            {
                aopsagtasklimit.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsagtasklimit));
            }
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be an Intger type", AOPsProperties.Keys.Global_ContactSessionLimit.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXTDialTimeout(String timeout) throws GravityException, CODEException
    {

        try
        {

            AOPsProperties aopXTDialTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_DialTimeout));
            if (aopXTDialTimeout == null)
            {
                aopXTDialTimeout = new AOPsProperties();
                aopXTDialTimeout.setAOPs(_aops);
                aopXTDialTimeout.setConfKey(AOPsProperties.Keys.XT_DialTimeout.name());
                aopXTDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTDialTimeout));
            }
            else
            {
                aopXTDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTDialTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XT_DialTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXTOBDialTrunk(String dialTrunk) throws GravityException, CODEException
    {
        if (dialTrunk == null || dialTrunk.isEmpty())
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XT_OBDialTrunk.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        AOPsProperties aopsXTOBDialTrunk = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OBDialTrunk));
        if (aopsXTOBDialTrunk == null)
        {
            aopsXTOBDialTrunk = new AOPsProperties();
            aopsXTOBDialTrunk.setAOPs(_aops);
            aopsXTOBDialTrunk.setConfKey(AOPsProperties.Keys.XT_OBDialTrunk.name());
            aopsXTOBDialTrunk.setConfValue(dialTrunk);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXTOBDialTrunk));
        }
        else
        {
            aopsXTOBDialTrunk.setConfValue(dialTrunk);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXTOBDialTrunk));
        }

    }

    private void setXTOBOverrideDefCallerId(String value) throws GravityException, CODEException
    {
        //Check DefCallerId is there or not.

        if (value.trim().isEmpty())
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XT_OB_OverrideDefCallerId.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);

        }
        AOPsProperties aopsXTOBOverrideDefC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_OverrideDefCallerId));
        if (aopsXTOBOverrideDefC == null)
        {
            aopsXTOBOverrideDefC = new AOPsProperties();
            aopsXTOBOverrideDefC.setAOPs(_aops);
            aopsXTOBOverrideDefC.setConfKey(AOPsProperties.Keys.XT_OB_OverrideDefCallerId.name());
            aopsXTOBOverrideDefC.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXTOBOverrideDefC));
        }
        else
        {
            aopsXTOBOverrideDefC.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXTOBOverrideDefC));
        }

    }

    private void setXTOBPaceLimit(String pacelimit) throws GravityException, CODEException
    {
        try
        {
            Integer pace = Integer.valueOf(pacelimit);

            if (pace < 1 || pace > 128)
            {
                throw new GravityIllegalArgumentException("Value Must be In Range Of [1,128]", AOPsProperties.Keys.XT_OB_PaceLimit.name(), EventFailedCause.DataBoundaryLimitViolation);
            }
            AOPsProperties aopsPaceLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_PaceLimit));
            if (aopsPaceLimit == null)
            {
                aopsPaceLimit = new AOPsProperties();
                aopsPaceLimit.setAOPs(_aops);
                aopsPaceLimit.setConfKey(AOPsProperties.Keys.XT_OB_PaceLimit.name());
                aopsPaceLimit.setConfValue(pace.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsPaceLimit));
            }
            else
            {
                aopsPaceLimit.setConfValue(pace.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsPaceLimit));
            }

        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be an Intger type", AOPsProperties.Keys.XT_OB_PaceLimit.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setAutoCampaignJoin(String value) throws GravityException, CODEException
    {
        Boolean isAuto = value.isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopsAutoCampaignJoin = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AutoAOPsJoin));
        if (aopsAutoCampaignJoin == null)
        {
            aopsAutoCampaignJoin = new AOPsProperties();
            aopsAutoCampaignJoin.setAOPs(_aops);
            aopsAutoCampaignJoin.setConfKey(AOPsProperties.Keys.Global_AutoAOPsJoin.name());
            aopsAutoCampaignJoin.setConfValue(isAuto.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsAutoCampaignJoin));
        }
        else
        {
            aopsAutoCampaignJoin.setConfValue(isAuto.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsAutoCampaignJoin));
        }

    }

    private void setaopsStickyAgent(String value) throws GravityException, CODEException
    {
        Boolean isAuto = value.isEmpty() ? false : Boolean.valueOf(value);
        if (!isAuto)
        {
            AOPsProperties aopsStrictlyStickyAgent = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_StrictlyStickyAgent));
            if (aopsStrictlyStickyAgent != null && aopsStrictlyStickyAgent.getConfValue().equals("true"))
            {
                aopsStrictlyStickyAgent.setConfValue(Boolean.FALSE.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsStrictlyStickyAgent));
            }
        }

        AOPsProperties aopsStickyAgent = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_StickyAgent));
        if (aopsStickyAgent == null)
        {
            aopsStickyAgent = new AOPsProperties();
            aopsStickyAgent.setAOPs(_aops);
            aopsStickyAgent.setConfKey(AOPsProperties.Keys.Global_StickyAgent.name());
            aopsStickyAgent.setConfValue(isAuto.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsStickyAgent));
        }
        else
        {
            aopsStickyAgent.setConfValue(isAuto.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsStickyAgent));
        }

    }

    private void setaopsStrictlyStickyAgent(String value, HashMap<String, String> hmAttr) throws GravityException, CODEException
    {
        Boolean isAuto = value.isEmpty() ? false : Boolean.valueOf(value);
        if (isAuto)
        {
            Boolean isSticky = false;
            if (hmAttr.containsKey(AOPsProperties.Keys.Global_StickyAgent.name()))
            {
                String s = hmAttr.get(AOPsProperties.Keys.Global_StickyAgent.name());
                isSticky = Boolean.valueOf(s);
            }
            else
            {
                //db
                AOPsProperties aopsStickyAgentdb = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_StickyAgent));
                isSticky = Boolean.valueOf(aopsStickyAgentdb.getConfValue());
            }
            if (isSticky)
            {
                AOPsProperties aopsStrictlyStickyAgent = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_StrictlyStickyAgent));
                if (aopsStrictlyStickyAgent == null)
                {

                    aopsStrictlyStickyAgent = new AOPsProperties();
                    aopsStrictlyStickyAgent.setAOPs(_aops);
                    aopsStrictlyStickyAgent.setConfKey(AOPsProperties.Keys.Global_StrictlyStickyAgent.name());
                    aopsStrictlyStickyAgent.setConfValue(isAuto.toString());
                    entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsStrictlyStickyAgent));
                }
                else
                {
                    aopsStrictlyStickyAgent.setConfValue(isAuto.toString());
                    entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsStrictlyStickyAgent));
                }
            }
            else
            {
                //TBD Throw Exception
                throw new GravityIllegalArgumentException("sticky agent must be enable ", AOPsProperties.Keys.Global_StickyAgent.name(), EventFailedCause.ValueOutOfRange);
            }
        }
        else
        {
            AOPsProperties aopsStrictlyStickyAgent = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_StrictlyStickyAgent));
            if (aopsStrictlyStickyAgent == null)
            {
                aopsStrictlyStickyAgent = new AOPsProperties();
                aopsStrictlyStickyAgent.setAOPs(_aops);
                aopsStrictlyStickyAgent.setConfKey(AOPsProperties.Keys.Global_StrictlyStickyAgent.name());
                aopsStrictlyStickyAgent.setConfValue(isAuto.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsStrictlyStickyAgent));
            }
            else
            {
                aopsStrictlyStickyAgent.setConfValue(isAuto.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsStrictlyStickyAgent));
            }
        }
    }

    private void setAllowManualCampaignJoin(String value) throws GravityException, CODEException
    {
        Boolean val = value.isEmpty() ? true : Boolean.valueOf(value);
        AOPsProperties aopsAllowManualCampaignJoin = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AllowManualAOPsJoin));
        if (aopsAllowManualCampaignJoin == null)
        {
            aopsAllowManualCampaignJoin = new AOPsProperties();
            aopsAllowManualCampaignJoin.setAOPs(_aops);
            aopsAllowManualCampaignJoin.setConfKey(AOPsProperties.Keys.Global_AllowManualAOPsJoin.name());
            aopsAllowManualCampaignJoin.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsAllowManualCampaignJoin));
        }
        else
        {
            aopsAllowManualCampaignJoin.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsAllowManualCampaignJoin));
        }
    }

    private void setAllowManualCampaignLeave(String value) throws GravityException, CODEException
    {
        Boolean val = value.isEmpty() ? true : Boolean.valueOf(value);
        AOPsProperties aopsAllowManualCampaignLeave = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AllowManualAOPsLeave));
        if (aopsAllowManualCampaignLeave == null)
        {
            aopsAllowManualCampaignLeave = new AOPsProperties();
            aopsAllowManualCampaignLeave.setAOPs(_aops);
            aopsAllowManualCampaignLeave.setConfKey(AOPsProperties.Keys.Global_AllowManualAOPsLeave.name());
            aopsAllowManualCampaignLeave.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsAllowManualCampaignLeave));
        }
        else
        {
            aopsAllowManualCampaignLeave.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsAllowManualCampaignLeave));
        }

    }

    private void setXEMOBOverrideDefFromAddress(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEMOBOverrideDefFromAdd = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_OverrideDefFromAddress));
        if (aopXEMOBOverrideDefFromAdd == null)
        {
            aopXEMOBOverrideDefFromAdd = new AOPsProperties();
            aopXEMOBOverrideDefFromAdd.setAOPs(_aops);
            aopXEMOBOverrideDefFromAdd.setConfKey(AOPsProperties.Keys.XEM_OB_OverrideDefFromAddress.name());
            aopXEMOBOverrideDefFromAdd.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBOverrideDefFromAdd));
        }
        else
        {
            aopXEMOBOverrideDefFromAdd.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMOBOverrideDefFromAdd));
        }
    }

    private void setXEMOBMaxAttemptCount(String attempt) throws GravityException, CODEException
    {
        try
        {
            Integer maxattempt = Integer.valueOf(attempt);
            if (maxattempt < 1 || maxattempt > 1024)
            {
                throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XEM_OB_MaxAttemptCount.name(), EventFailedCause.DataBoundaryLimitViolation);
            }

            AOPsProperties aopsXEM_OB_MaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_MaxAttemptCount));
            if (aopsXEM_OB_MaxAttemptCount == null)
            {
                aopsXEM_OB_MaxAttemptCount = new AOPsProperties();
                aopsXEM_OB_MaxAttemptCount.setAOPs(_aops);
                aopsXEM_OB_MaxAttemptCount.setConfKey(AOPsProperties.Keys.XEM_OB_MaxAttemptCount.name());
                aopsXEM_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXEM_OB_MaxAttemptCount));
            }
            else
            {
                aopsXEM_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXEM_OB_MaxAttemptCount));
            }

        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XEM_OB_MaxAttemptCount.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXCHOBOverrideDefFromAddress(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHOBOverrideDefFromAdd = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_OverrideDefFromAddress));
        if (aopXCHOBOverrideDefFromAdd == null)
        {
            aopXCHOBOverrideDefFromAdd = new AOPsProperties();
            aopXCHOBOverrideDefFromAdd.setAOPs(_aops);
            aopXCHOBOverrideDefFromAdd.setConfKey(AOPsProperties.Keys.XCH_OB_OverrideDefFromAddress.name());
            aopXCHOBOverrideDefFromAdd.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBOverrideDefFromAdd));
        }
        else
        {
            aopXCHOBOverrideDefFromAdd.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHOBOverrideDefFromAdd));
        }
    }

    private void setXCHOBMaxAttemptCount(String attempt) throws GravityException, CODEException
    {
        try
        {
            Integer maxattempt = Integer.valueOf(attempt);
            if (maxattempt < 1 || maxattempt > 1024)
            {
                throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XCH_OB_MaxAttemptCount.name(), EventFailedCause.DataBoundaryLimitViolation);
            }

            AOPsProperties aopsXCH_OB_MaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_MaxAttemptCount));
            if (aopsXCH_OB_MaxAttemptCount == null)
            {
                aopsXCH_OB_MaxAttemptCount = new AOPsProperties();
                aopsXCH_OB_MaxAttemptCount.setAOPs(_aops);
                aopsXCH_OB_MaxAttemptCount.setConfKey(AOPsProperties.Keys.XCH_OB_MaxAttemptCount.name());
                aopsXCH_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXCH_OB_MaxAttemptCount));
            }
            else
            {
                aopsXCH_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXCH_OB_MaxAttemptCount));
            }

        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XCH_OB_MaxAttemptCount.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXEMOBCheckDNC(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        if (val)
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
        }
        AOPsProperties aopXEMOBCheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_CheckDNC));
        if (aopXEMOBCheckDNC == null)
        {
            aopXEMOBCheckDNC = new AOPsProperties();
            aopXEMOBCheckDNC.setAOPs(_aops);
            aopXEMOBCheckDNC.setConfKey(AOPsProperties.Keys.XEM_OB_CheckDNC.name());
            aopXEMOBCheckDNC.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBCheckDNC));
        }
        else
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
            aopXEMOBCheckDNC.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMOBCheckDNC));
        }
    }

    private void setXCHOBCheckDNC(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        if (val)
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
        }

        AOPsProperties aopXCH_OB_CheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_CheckDNC));
        if (aopXCH_OB_CheckDNC == null)
        {
            aopXCH_OB_CheckDNC = new AOPsProperties();
            aopXCH_OB_CheckDNC.setAOPs(_aops);
            aopXCH_OB_CheckDNC.setConfKey(AOPsProperties.Keys.XCH_OB_CheckDNC.name());
            aopXCH_OB_CheckDNC.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_OB_CheckDNC));
        }
        else
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
            aopXCH_OB_CheckDNC.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCH_OB_CheckDNC));
        }
    }

    private void setXEMDialTimeOut(String timeout) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopXEMDialTimeOut = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_DialTimeout));
            if (aopXEMDialTimeOut == null)
            {
                aopXEMDialTimeOut = new AOPsProperties();
                aopXEMDialTimeOut.setConfKey(AOPsProperties.Keys.XEM_DialTimeout.name());
                aopXEMDialTimeOut.setConfValue(Integer.valueOf(timeout).toString());
                aopXEMDialTimeOut.setAOPs(_aops);
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMDialTimeOut));
            }
            else
            {
                aopXEMDialTimeOut.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMDialTimeOut));
            }

        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XEM_DialTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXSORecordingMode(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopsXSO_EnableRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_EnableRecording));
        if (aopsXSO_EnableRecording == null)
        {
            aopsXSO_EnableRecording = new AOPsProperties();
            aopsXSO_EnableRecording.setAOPs(_aops);
            aopsXSO_EnableRecording.setConfKey(AOPsProperties.Keys.XSO_EnableRecording.name());
            aopsXSO_EnableRecording.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXSO_EnableRecording));
        }
        else
        {
            aopsXSO_EnableRecording.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXSO_EnableRecording));
        }
    }

    private void setXSOIBAuthParams(String authprms) throws Exception, GravityException, CODEException
    {

        Properties props = JSONUtil.FromJSON(authprms, Properties.class);

        AOPsProperties aopXSOIBAuthParams = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_IB_AuthParams));
        if (aopXSOIBAuthParams == null)
        {
            aopXSOIBAuthParams = new AOPsProperties();
            aopXSOIBAuthParams.setAOPs(_aops);
            aopXSOIBAuthParams.setConfKey(AOPsProperties.Keys.XSO_IB_AuthParams.name());
            aopXSOIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOIBAuthParams));
        }
        else
        {
            aopXSOIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOIBAuthParams));
        }

    }

    private void setXSOOBCheckDNC(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        if (val)
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
        }
        AOPsProperties aopXSOOBCheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_CheckDNC));
        if (aopXSOOBCheckDNC == null)
        {
            aopXSOOBCheckDNC = new AOPsProperties();
            aopXSOOBCheckDNC.setAOPs(_aops);
            aopXSOOBCheckDNC.setConfKey(AOPsProperties.Keys.XSO_OB_CheckDNC.name());
            aopXSOOBCheckDNC.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOOBCheckDNC));
        }
        else
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
            aopXSOOBCheckDNC.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOOBCheckDNC));
        }

    }

    private void setXSOOBMaxAttemptCount(String attempt) throws GravityException, CODEException
    {
        try
        {
            Integer maxattempt = Integer.valueOf(attempt);
            if (maxattempt < 1 || maxattempt > 1024)
            {
                throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XSO_OB_MaxAttemptCount.name(), EventFailedCause.DataBoundaryLimitViolation);
            }
            AOPsProperties aopXSOOBMaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_MaxAttemptCount));
            if (aopXSOOBMaxAttemptCount == null)
            {
                aopXSOOBMaxAttemptCount = new AOPsProperties();
                aopXSOOBMaxAttemptCount.setAOPs(_aops);
                aopXSOOBMaxAttemptCount.setConfKey(AOPsProperties.Keys.XSO_OB_MaxAttemptCount.name());
                aopXSOOBMaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOOBMaxAttemptCount));
            }
            else
            {
                aopXSOOBMaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOOBMaxAttemptCount));
            }

        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XSO_OB_MaxAttemptCount.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXSOOBOverrideDefFromAddress(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXSOOBOverrideDefFromAddress = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_OverrideDefFromAddress));
        if (aopXSOOBOverrideDefFromAddress == null)
        {
            aopXSOOBOverrideDefFromAddress = new AOPsProperties();
            aopXSOOBOverrideDefFromAddress.setAOPs(_aops);
            aopXSOOBOverrideDefFromAddress.setConfKey(AOPsProperties.Keys.XEM_OB_OverrideDefFromAddress.name());
            aopXSOOBOverrideDefFromAddress.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOOBOverrideDefFromAddress));
        }
        else
        {
            aopXSOOBOverrideDefFromAddress.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOOBOverrideDefFromAddress));
        }

    }

    private void setXSODialTimeout(String timeout) throws GravityException, CODEException
    {

        try
        {

            AOPsProperties aopXSODialTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_DialTimeout));
            if (aopXSODialTimeout == null)
            {
                aopXSODialTimeout = new AOPsProperties();
                aopXSODialTimeout.setAOPs(_aops);
                aopXSODialTimeout.setConfKey(AOPsProperties.Keys.XSO_DialTimeout.name());
                aopXSODialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSODialTimeout));
            }
            else
            {
                aopXSODialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSODialTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XSO_DialTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXSOAllowReject(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXSOAllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_AllowReject));
        if (aopXSOAllowReject == null)
        {
            aopXSOAllowReject = new AOPsProperties();
            aopXSOAllowReject.setAOPs(_aops);
            aopXSOAllowReject.setConfKey(AOPsProperties.Keys.XSO_AllowReject.name());
            aopXSOAllowReject.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOAllowReject));
        }
        else
        {
            aopXSOAllowReject.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOAllowReject));
        }
    }

    private void setXSOAutoAnswer(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXSO_AutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_AutoAnswer));
        if (aopXSO_AutoAnswer == null)
        {
            aopXSO_AutoAnswer = new AOPsProperties();
            aopXSO_AutoAnswer.setAOPs(_aops);
            aopXSO_AutoAnswer.setConfKey(AOPsProperties.Keys.XSO_AutoAnswer.name());
            aopXSO_AutoAnswer.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSO_AutoAnswer));
        }
        else
        {
            aopXSO_AutoAnswer.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSO_AutoAnswer));
        }
    }

    private void setXSO_AutoAnswerDelay(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXSO_AutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_AutoAnswerDelay));
        if (aopXSO_AutoAnswerDelay == null)
        {
            aopXSO_AutoAnswerDelay = new AOPsProperties();
            aopXSO_AutoAnswerDelay.setAOPs(_aops);
            aopXSO_AutoAnswerDelay.setConfKey(AOPsProperties.Keys.XSO_AutoAnswerDelay.name());
            aopXSO_AutoAnswerDelay.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSO_AutoAnswerDelay));
        }
        else
        {
            aopXSO_AutoAnswerDelay.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSO_AutoAnswerDelay));
        }
    }

    private void setXSOAutoAnswerDelay(String value) throws GravityException, CODEException
    {

        AOPsProperties aopXSO_AutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_AutoAnswerDelay));
        if (aopXSO_AutoAnswerDelay == null)
        {
            aopXSO_AutoAnswerDelay = new AOPsProperties();
            aopXSO_AutoAnswerDelay.setAOPs(_aops);
            aopXSO_AutoAnswerDelay.setConfKey(AOPsProperties.Keys.XSO_AutoAnswerDelay.name());
            aopXSO_AutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSO_AutoAnswerDelay));
        }
        else
        {
            aopXSO_AutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSO_AutoAnswerDelay));
        }
    }

    private void setGlobalSessionDoneTimeout(String value) throws GravityException, CODEException
    {

        AOPsProperties aopGlobal_SessionDoneTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SessionDoneTimeout));
        if (aopGlobal_SessionDoneTimeout == null)
        {
            aopGlobal_SessionDoneTimeout = new AOPsProperties();
            aopGlobal_SessionDoneTimeout.setAOPs(_aops);
            aopGlobal_SessionDoneTimeout.setConfKey(AOPsProperties.Keys.Global_SessionDoneTimeout.name());
            aopGlobal_SessionDoneTimeout.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_SessionDoneTimeout));
        }
        else
        {
            aopGlobal_SessionDoneTimeout.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_SessionDoneTimeout));
        }
    }

    private void setGlobalDisposeTimeout(String value) throws GravityException, CODEException
    {

        AOPsProperties aopGlobal_DisposeTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_DisposeTimeout));
        if (aopGlobal_DisposeTimeout == null)
        {
            aopGlobal_DisposeTimeout = new AOPsProperties();
            aopGlobal_DisposeTimeout.setAOPs(_aops);
            aopGlobal_DisposeTimeout.setConfKey(AOPsProperties.Keys.Global_DisposeTimeout.name());
            aopGlobal_DisposeTimeout.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_DisposeTimeout));
        }
        else
        {
            aopGlobal_DisposeTimeout.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_DisposeTimeout));
        }
    }

    private void setGlobalSessionDoneIsAuto(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties Global_SessionDoneIsAuto = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SessionDoneIsAuto));
        if (Global_SessionDoneIsAuto == null)
        {
            Global_SessionDoneIsAuto = new AOPsProperties();
            Global_SessionDoneIsAuto.setAOPs(_aops);
            Global_SessionDoneIsAuto.setConfKey(AOPsProperties.Keys.Global_SessionDoneIsAuto.name());
            Global_SessionDoneIsAuto.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), Global_SessionDoneIsAuto));
        }
        else
        {
            Global_SessionDoneIsAuto.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), Global_SessionDoneIsAuto));
        }
    }

    private void setGlobalDIsposeIsAuto(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopGlobal_DIsposeIsAuto = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_DisposeIsAuto));
        if (aopGlobal_DIsposeIsAuto == null)
        {
            aopGlobal_DIsposeIsAuto = new AOPsProperties();
            aopGlobal_DIsposeIsAuto.setAOPs(_aops);
            aopGlobal_DIsposeIsAuto.setConfKey(AOPsProperties.Keys.Global_DisposeIsAuto.name());
            aopGlobal_DIsposeIsAuto.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_DIsposeIsAuto));
        }
        else
        {
            aopGlobal_DIsposeIsAuto.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_DIsposeIsAuto));
        }
    }

    private void setGlobalAutoPreview(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopGlobal_AutoPreview = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AutoPreview));
        if (aopGlobal_AutoPreview == null)
        {
            aopGlobal_AutoPreview = new AOPsProperties();
            aopGlobal_AutoPreview.setAOPs(_aops);
            aopGlobal_AutoPreview.setConfKey(AOPsProperties.Keys.Global_AutoPreview.name());
            aopGlobal_AutoPreview.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_AutoPreview));
        }
        else
        {
            aopGlobal_AutoPreview.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_AutoPreview));
        }
    }

    private void setaopGlobalRedialExpiryTimeout(String value) throws GravityException, CODEException
    {

        AOPsProperties aopGlobal_RedialExpiryTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_RedialExpiryTimeout));
        if (aopGlobal_RedialExpiryTimeout == null)
        {
            aopGlobal_RedialExpiryTimeout = new AOPsProperties();
            aopGlobal_RedialExpiryTimeout.setAOPs(_aops);
            aopGlobal_RedialExpiryTimeout.setConfKey(AOPsProperties.Keys.Global_RedialExpiryTimeout.name());
            aopGlobal_RedialExpiryTimeout.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_RedialExpiryTimeout));
        }
        else
        {
            aopGlobal_RedialExpiryTimeout.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_RedialExpiryTimeout));
        }
    }

    private void setXMAutoAnswerDelay(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXMAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_AutoAnswerDelay));
        if (aopXMAutoAnswerDelay == null)
        {
            aopXMAutoAnswerDelay = new AOPsProperties();
            aopXMAutoAnswerDelay.setAOPs(_aops);
            aopXMAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XM_AutoAnswerDelay.name());
            aopXMAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMAutoAnswerDelay));
        }
        else
        {
            aopXMAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMAutoAnswerDelay));
        }
    }

    private void setXVDAutoAnswerDelay(String value) throws GravityException, CODEException
    {

        AOPsProperties aopXVDAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_AutoAnswerDelay));
        if (aopXVDAutoAnswerDelay == null)
        {
            aopXVDAutoAnswerDelay = new AOPsProperties();
            aopXVDAutoAnswerDelay.setAOPs(_aops);
            aopXVDAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XVD_AutoAnswerDelay.name());
            aopXVDAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDAutoAnswerDelay));
        }
        else
        {
            aopXVDAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDAutoAnswerDelay));
        }
    }

    private void setXCHAutoAnswerDelay(String value) throws GravityException, CODEException
    {

        AOPsProperties aopXCHAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_AutoAnswerDelay));
        if (aopXCHAutoAnswerDelay == null)
        {
            aopXCHAutoAnswerDelay = new AOPsProperties();
            aopXCHAutoAnswerDelay.setAOPs(_aops);
            aopXCHAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XCH_AutoAnswerDelay.name());
            aopXCHAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHAutoAnswerDelay));
        }
        else
        {
            aopXCHAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHAutoAnswerDelay));
        }
    }

    private void setXTAutoAnswerDelay(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXTAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_AutoAnswerDelay));
        if (aopXTAutoAnswerDelay == null)
        {
            aopXTAutoAnswerDelay = new AOPsProperties();
            aopXTAutoAnswerDelay.setAOPs(_aops);
            aopXTAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XT_AutoAnswerDelay.name());
            aopXTAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTAutoAnswerDelay));
        }
        else
        {
            aopXTAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTAutoAnswerDelay));
        }
    }

    private void setXEMAutoAnswerDelay(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXEM_AutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_AutoAnswerDelay));
        if (aopXEM_AutoAnswerDelay == null)
        {
            aopXEM_AutoAnswerDelay = new AOPsProperties();
            aopXEM_AutoAnswerDelay.setAOPs(_aops);
            aopXEM_AutoAnswerDelay.setConfKey(AOPsProperties.Keys.XEM_AutoAnswerDelay.name());
            aopXEM_AutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEM_AutoAnswerDelay));
        }
        else
        {
            aopXEM_AutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEM_AutoAnswerDelay));
        }
    }

    private void setXCHIBEnableWebChat(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXCH_IB_EnableWebChat = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_IB_EnableWebChat));
        if (aopXCH_IB_EnableWebChat == null)
        {
            aopXCH_IB_EnableWebChat = new AOPsProperties();
            aopXCH_IB_EnableWebChat.setAOPs(_aops);
            aopXCH_IB_EnableWebChat.setConfKey(AOPsProperties.Keys.XCH_IB_EnableWebChat.name());
            aopXCH_IB_EnableWebChat.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_IB_EnableWebChat));
        }
        else
        {
            aopXCH_IB_EnableWebChat.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCH_IB_EnableWebChat));
        }
    }

    private void setXVDIBEnableWebVideo(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXVD_IB_EnableWebVideo = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_IB_EnableWebVideo));
        if (aopXVD_IB_EnableWebVideo == null)
        {
            aopXVD_IB_EnableWebVideo = new AOPsProperties();
            aopXVD_IB_EnableWebVideo.setAOPs(_aops);
            aopXVD_IB_EnableWebVideo.setConfKey(AOPsProperties.Keys.XVD_IB_EnableWebVideo.name());
            aopXVD_IB_EnableWebVideo.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVD_IB_EnableWebVideo));
        }
        else
        {
            aopXVD_IB_EnableWebVideo.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVD_IB_EnableWebVideo));
        }
    }

    private void setXCHOBAllowManualDial(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXCHOBAllowManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_AllowManualDial));
        if (aopXCHOBAllowManualDial == null)
        {
            aopXCHOBAllowManualDial = new AOPsProperties();
            aopXCHOBAllowManualDial.setAOPs(_aops);
            aopXCHOBAllowManualDial.setConfKey(AOPsProperties.Keys.XCH_OB_AllowManualDial.name());
            aopXCHOBAllowManualDial.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBAllowManualDial));
        }
        else
        {
            aopXCHOBAllowManualDial.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHOBAllowManualDial));
        }
    }

//    private void setXCHOBAllowedDialModes(String campmodes) throws GravityException, CODEException
//    {
//        if (campmodes == null || campmodes.trim().isEmpty())
//        {
//
//            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XT_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
//        }
//        //TBD:need to check for IllegalArgumentException for campaign mode.
//        try
//        {
//            List<DialMode> listCampModes = Arrays.asList(campmodes.split(",")).stream().map(cm -> DialMode.valueOf(cm)).collect(Collectors.toList());
//            ArrayList<String> aldialmode = JSONUtil.FromJSON(listCampModes.toString(), ArrayList.class);
//            JSONArray dialmodejsonarr = new JSONArray(aldialmode.toArray());
//            //If Predictive dialmode removed form the alloweDialed modes then we have to unmpa the PredQueue also as there are no use of Predictive queue now.And if we are not removing it then it will not mapped with any other predictive campaign also.
    /// /            if(!aldialmode.contains(DialMode.Predictive.name())){
    /// /                AOPsProperties aopPreedQueue = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_PredQueue));
    /// /                if(aopPreedQueue!=null){
    /// /                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreedQueue));
    /// /                }
    /// /            }
//            AOPsProperties aopXCHOBAllowedDialModes = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_AllowedDialModes));
//
//            if (aopXCHOBAllowedDialModes == null)
//            {
//                aopXCHOBAllowedDialModes = new AOPsProperties();
//                aopXCHOBAllowedDialModes.setAOPs(_aops);
//                aopXCHOBAllowedDialModes.setConfKey(AOPsProperties.Keys.XCH_OB_AllowedDialModes.name());
//                aopXCHOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
//                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBAllowedDialModes));
//            }
//            else
//            {
//                aopXCHOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
//                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHOBAllowedDialModes));
//            }
//        }
//        catch (Exception e)
//        {
//            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XCH_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
//        }
//    }
    private void setGlobalAIDisposeEnabled(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopGlobal_AIDisposeEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AIDisposeEnabled));
        if (aopGlobal_AIDisposeEnabled == null)
        {
            aopGlobal_AIDisposeEnabled = new AOPsProperties();
            aopGlobal_AIDisposeEnabled.setAOPs(_aops);
            aopGlobal_AIDisposeEnabled.setConfKey(AOPsProperties.Keys.Global_AIDisposeEnabled.name());
            aopGlobal_AIDisposeEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_AIDisposeEnabled));
        }
        else
        {
            aopGlobal_AIDisposeEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_AIDisposeEnabled));
        }
    }

    private void setGlobalAIDisposeOverride(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopGlobal_AIDisposeOverride = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AIDisposeOverride));
        if (aopGlobal_AIDisposeOverride == null)
        {
            aopGlobal_AIDisposeOverride = new AOPsProperties();
            aopGlobal_AIDisposeOverride.setAOPs(_aops);
            aopGlobal_AIDisposeOverride.setConfKey(AOPsProperties.Keys.Global_AIDisposeOverride.name());
            aopGlobal_AIDisposeOverride.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_AIDisposeOverride));
        }
        else
        {
            aopGlobal_AIDisposeOverride.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_AIDisposeOverride));
        }
    }

    private void setGlobalAIDisposeXPlatform(String value) throws GravityException, CODEException
    {
        long id = Long.valueOf(value);
        _tctx.getDB().FindAssert(EN.XPlatform.getEntityClass(), id);
        AOPsProperties aopGlobal_AIDisposeXPlatform = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AIDisposeXPlatform));
        if (aopGlobal_AIDisposeXPlatform == null)
        {
            aopGlobal_AIDisposeXPlatform = new AOPsProperties();
            aopGlobal_AIDisposeXPlatform.setAOPs(_aops);
            aopGlobal_AIDisposeXPlatform.setConfKey(AOPsProperties.Keys.Global_AIDisposeXPlatform.name());
            aopGlobal_AIDisposeXPlatform.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_AIDisposeXPlatform));
        }
        else
        {
            aopGlobal_AIDisposeXPlatform.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_AIDisposeXPlatform));
        }
    }

    private void setGlobalAIDisposeXPlatformUA(String value) throws GravityException, CODEException
    {
        long id = Long.valueOf(value);
        _tctx.getDB().FindAssert(EN.XPlatformUA.getEntityClass(), id);
        AOPsProperties aopGlobal_AIDisposeXPlatformua = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AIDisposeXPlatformUA));
        if (aopGlobal_AIDisposeXPlatformua == null)
        {
            aopGlobal_AIDisposeXPlatformua = new AOPsProperties();
            aopGlobal_AIDisposeXPlatformua.setAOPs(_aops);
            aopGlobal_AIDisposeXPlatformua.setConfKey(AOPsProperties.Keys.Global_AIDisposeXPlatformUA.name());
            aopGlobal_AIDisposeXPlatformua.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_AIDisposeXPlatformua));
        }
        else
        {
            aopGlobal_AIDisposeXPlatformua.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_AIDisposeXPlatformua));
        }
    }

    private void setGlobalAIDisposeAllowedChannels(String inchannels) throws GravityException, CODEException
    {
        if (inchannels == null || inchannels.trim().isEmpty())
        {

            throw new GravityIllegalArgumentException(AOPsProperties.Keys.Global_AIDisposeAllowedChannels.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        //TBD:need to check for IllegalArgumentException for campaign mode.
        try
        {
            List<Channel> listChannels = Arrays.asList(inchannels.split(",")).stream().map(cm -> Channel.valueOf(cm)).collect(Collectors.toList());
            ArrayList<String> channels = JSONUtil.FromJSON(listChannels.toString(), ArrayList.class);
            JSONArray channelsjsonarr = new JSONArray(channels.toArray());

            AOPsProperties aopGlobal_AIDisposeAllowedChannels = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_AIDisposeAllowedChannels));

            if (aopGlobal_AIDisposeAllowedChannels == null)
            {
                aopGlobal_AIDisposeAllowedChannels = new AOPsProperties();
                aopGlobal_AIDisposeAllowedChannels.setAOPs(_aops);
                aopGlobal_AIDisposeAllowedChannels.setConfKey(AOPsProperties.Keys.Global_AIDisposeAllowedChannels.name());
                aopGlobal_AIDisposeAllowedChannels.setConfValue(channelsjsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_AIDisposeAllowedChannels));
            }
            else
            {
                aopGlobal_AIDisposeAllowedChannels.setConfValue(channelsjsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_AIDisposeAllowedChannels));
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.Global_AIDisposeAllowedChannels.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    private void setXCHKBAutoReply(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCH_KBAutoReply = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_KBAutoReply));
        if (aopXCH_KBAutoReply == null)
        {
            aopXCH_KBAutoReply = new AOPsProperties();
            aopXCH_KBAutoReply.setAOPs(_aops);
            aopXCH_KBAutoReply.setConfKey(AOPsProperties.Keys.XCH_KBAutoReply.name());
            aopXCH_KBAutoReply.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_KBAutoReply));
        }
        else
        {
            aopXCH_KBAutoReply.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCH_KBAutoReply));
        }
    }

    private void setXCHKBAutoAssist(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCH_KBAutoAssist = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_KBAutoAssist));
        if (aopXCH_KBAutoAssist == null)
        {
            aopXCH_KBAutoAssist = new AOPsProperties();
            aopXCH_KBAutoAssist.setAOPs(_aops);
            aopXCH_KBAutoAssist.setConfKey(AOPsProperties.Keys.XCH_KBAutoAssist.name());
            aopXCH_KBAutoAssist.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_KBAutoAssist));
        }
        else
        {
            aopXCH_KBAutoAssist.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCH_KBAutoAssist));
        }
    }

    private void setXCHKBEnable(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCH_KBEnable = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_KBEnable));
        if (aopXCH_KBEnable == null)
        {
            aopXCH_KBEnable = new AOPsProperties();
            aopXCH_KBEnable.setAOPs(_aops);
            aopXCH_KBEnable.setConfKey(AOPsProperties.Keys.XCH_KBEnable.name());
            aopXCH_KBEnable.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_KBEnable));
        }
        else
        {
            aopXCH_KBEnable.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCH_KBEnable));
        }
    }

    private void setXCHKBVerifyNReply(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCH_KBVerifyNReply = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_KBVerifyNReply));
        if (aopXCH_KBVerifyNReply == null)
        {
            aopXCH_KBVerifyNReply = new AOPsProperties();
            aopXCH_KBVerifyNReply.setAOPs(_aops);
            aopXCH_KBVerifyNReply.setConfKey(AOPsProperties.Keys.XCH_KBVerifyNReply.name());
            aopXCH_KBVerifyNReply.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCH_KBVerifyNReply));
        }
        else
        {
            aopXCH_KBVerifyNReply.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCH_KBVerifyNReply));
        }
    }

    private void setXEMOBAllowedDialModes(String campmodes) throws GravityException, CODEException
    {
        if (campmodes == null || campmodes.trim().isEmpty())
        {

            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XEM_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        //TBD:need to check for IllegalArgumentException for campaign mode.
        try
        {
            List<DialMode> listCampModes = Arrays.asList(campmodes.split(",")).stream().map(cm -> DialMode.valueOf(cm)).collect(Collectors.toList());
            ArrayList<String> aldialmode = JSONUtil.FromJSON(listCampModes.toString(), ArrayList.class);
            JSONArray dialmodejsonarr = new JSONArray(aldialmode.toArray());

            //If Predictive dialmode removed form the alloweDialed modes then we have to unmpa the PredQueue also as there are no use of Predictive queue now.And if we are not removing it then it will not mapped with any other predictive campaign also.
//            if(!aldialmode.contains(DialMode.Predictive.name())){
//                AOPsProperties aopPreedQueue = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_PredQueue));
//                if(aopPreedQueue!=null){
//                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreedQueue));
//                }
//
//            }
//            if(!aldialmode.contains(DialMode.Preview.name())){
//                AOPsProperties aopPreviewLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_));
//                if (aopPreviewLimit != null)
//                {
//                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreviewLimit));
//                }
//            }
            AOPsProperties aopXEMOBAllowedDialModes = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_AllowedDialModes));
            if (aopXEMOBAllowedDialModes == null)
            {
                aopXEMOBAllowedDialModes = new AOPsProperties();
                aopXEMOBAllowedDialModes.setAOPs(_aops);
                aopXEMOBAllowedDialModes.setConfKey(AOPsProperties.Keys.XEM_OB_AllowedDialModes.name());
                aopXEMOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBAllowedDialModes));
            }
            else
            {
                aopXEMOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMOBAllowedDialModes));
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XEM_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    private void setXEMOBManualDial(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEMOBManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_AllowManualDial));
        if (aopXEMOBManualDial == null)
        {
            aopXEMOBManualDial = new AOPsProperties();
            aopXEMOBManualDial.setAOPs(_aops);
            aopXEMOBManualDial.setConfKey(AOPsProperties.Keys.XEM_OB_AllowManualDial.name());
            aopXEMOBManualDial.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBManualDial));
        }
        else
        {
            aopXEMOBManualDial.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMOBManualDial));
        }
    }

    private void setGlobalPreviewTimeout(String timeout) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopGlobalPreviewTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_PreviewTimeout));
            if (aopGlobalPreviewTimeout == null)
            {
                aopGlobalPreviewTimeout = new AOPsProperties();
                aopGlobalPreviewTimeout.setAOPs(_aops);
                aopGlobalPreviewTimeout.setConfKey(AOPsProperties.Keys.Global_PreviewTimeout.name());
                aopGlobalPreviewTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobalPreviewTimeout));
            }
            else
            {
                aopGlobalPreviewTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobalPreviewTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.Global_PreviewTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXEMOBDialLimit(String value) throws GravityException, CODEException
    {
        Integer dialLimit = null;
        try
        {
            dialLimit = Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XEM_OB_DialLimit.name(), EventFailedCause.ValueOutOfRange);
        }
        if (dialLimit < Limits.DialLimit_MIN || dialLimit > Limits.DialLimit_MAX)
        {
            throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XEM_OB_DialLimit.name(), EventFailedCause.DataBoundaryLimitViolation);
        }

        AOPsProperties aopXEMOBDialLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_DialLimit));
        if (aopXEMOBDialLimit == null)
        {
            aopXEMOBDialLimit = new AOPsProperties();
            aopXEMOBDialLimit.setAOPs(_aops);
            aopXEMOBDialLimit.setConfKey(AOPsProperties.Keys.XEM_OB_DialLimit.name());
            aopXEMOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBDialLimit));
        }
        else
        {
            aopXEMOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMOBDialLimit));
        }
    }

    private void setXEMOBEnableDialChain(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEMOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_EnableDialChain));
        if (aopXEMOBEnableDialChain == null)
        {
            aopXEMOBEnableDialChain = new AOPsProperties();
            aopXEMOBEnableDialChain.setAOPs(_aops);
            aopXEMOBEnableDialChain.setConfKey(AOPsProperties.Keys.XEM_OB_EnableDialChain.name());
            aopXEMOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBEnableDialChain));
        }
        else
        {
            aopXEMOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMOBEnableDialChain));
        }
    }

    private void setGlobalCanRejectPreview(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopGlobalCanRejectPreview = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_CanRejectPreview));
        if (aopGlobalCanRejectPreview == null)
        {
            aopGlobalCanRejectPreview = new AOPsProperties();
            aopGlobalCanRejectPreview.setAOPs(_aops);
            aopGlobalCanRejectPreview.setConfKey(AOPsProperties.Keys.Global_CanRejectPreview.name());
            aopGlobalCanRejectPreview.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobalCanRejectPreview));
        }
        else
        {
            aopGlobalCanRejectPreview.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobalCanRejectPreview));
        }
    }

    private void setXCHOBDialLimit(String value) throws GravityException, CODEException
    {
        Integer dialLimit = null;
        try
        {
            dialLimit = Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XCH_OB_DialLimit.name(), EventFailedCause.ValueOutOfRange);
        }
        if (dialLimit < Limits.DialLimit_MIN || dialLimit > Limits.DialLimit_MAX)
        {
            throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XCH_OB_DialLimit.name(), EventFailedCause.DataBoundaryLimitViolation);
        }

        AOPsProperties aopXCHOBDialLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_DialLimit));
        if (aopXCHOBDialLimit == null)
        {
            aopXCHOBDialLimit = new AOPsProperties();
            aopXCHOBDialLimit.setAOPs(_aops);
            aopXCHOBDialLimit.setConfKey(AOPsProperties.Keys.XCH_OB_DialLimit.name());
            aopXCHOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBDialLimit));
        }
        else
        {
            aopXCHOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHOBDialLimit));
        }
    }

    private void setXCHOBEnableDialChain(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_EnableDialChain));
        if (aopXCHOBEnableDialChain == null)
        {
            aopXCHOBEnableDialChain = new AOPsProperties();
            aopXCHOBEnableDialChain.setAOPs(_aops);
            aopXCHOBEnableDialChain.setConfKey(AOPsProperties.Keys.XCH_OB_EnableDialChain.name());
            aopXCHOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBEnableDialChain));
        }
        else
        {
            aopXCHOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHOBEnableDialChain));
        }
    }

    private void setXCHOBAllowedDialModes(String campmodes) throws GravityException, CODEException
    {
        if (campmodes == null || campmodes.trim().isEmpty())
        {

            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XCH_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        //TBD:need to check for IllegalArgumentException for campaign mode.
        try
        {
            List<DialMode> listCampModes = Arrays.asList(campmodes.split(",")).stream().map(cm -> DialMode.valueOf(cm)).collect(Collectors.toList());
            ArrayList<String> aldialmode = JSONUtil.FromJSON(listCampModes.toString(), ArrayList.class);
            JSONArray dialmodejsonarr = new JSONArray(aldialmode.toArray());

            //If Predictive dialmode removed form the alloweDialed modes then we have to unmpa the PredQueue also as there are no use of Predictive queue now.And if we are not removing it then it will not mapped with any other predictive campaign also.
//            if(!aldialmode.contains(DialMode.Predictive.name())){
//                AOPsProperties aopPreedQueue = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_PredQueue));
//                if(aopPreedQueue!=null){
//                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreedQueue));
//                }
//
//            }
//            if(!aldialmode.contains(DialMode.Preview.name())){
//                AOPsProperties aopPreviewLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_PreviewTimeout));
//                if (aopPreviewLimit != null)
//                {
//                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreviewLimit));
//                }
//            }
            AOPsProperties aopXCHOBAllowedDialModes = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_AllowedDialModes));
            if (aopXCHOBAllowedDialModes == null)
            {
                aopXCHOBAllowedDialModes = new AOPsProperties();
                aopXCHOBAllowedDialModes.setAOPs(_aops);
                aopXCHOBAllowedDialModes.setConfKey(AOPsProperties.Keys.XCH_OB_AllowedDialModes.name());
                aopXCHOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBAllowedDialModes));
            }
            else
            {
                aopXCHOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHOBAllowedDialModes));
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XCH_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    private void setXCHIBAllowDialBack(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHIBAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_IB_AllowDialBack));
        if (aopXCHIBAllowDialBack == null)
        {
            aopXCHIBAllowDialBack = new AOPsProperties();
            aopXCHIBAllowDialBack.setAOPs(_aops);
            aopXCHIBAllowDialBack.setConfKey(AOPsProperties.Keys.XCH_IB_AllowDialBack.name());
            aopXCHIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHIBAllowDialBack));
        }
        else
        {
            aopXCHIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHIBAllowDialBack));
        }
    }

    private void setXEMIBAllowDialBack(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEMIBAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_IB_AllowDialBack));
        if (aopXEMIBAllowDialBack == null)
        {
            aopXEMIBAllowDialBack = new AOPsProperties();
            aopXEMIBAllowDialBack.setAOPs(_aops);
            aopXEMIBAllowDialBack.setConfKey(AOPsProperties.Keys.XEM_IB_AllowDialBack.name());
            aopXEMIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMIBAllowDialBack));
        }
        else
        {
            aopXEMIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMIBAllowDialBack));
        }
    }

    private void setXVDIBAllowDialBack(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXVDIBAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_IB_AllowDialBack));
        if (aopXVDIBAllowDialBack == null)
        {
            aopXVDIBAllowDialBack = new AOPsProperties();
            aopXVDIBAllowDialBack.setAOPs(_aops);
            aopXVDIBAllowDialBack.setConfKey(AOPsProperties.Keys.XVD_IB_AllowDialBack.name());
            aopXVDIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDIBAllowDialBack));
        }
        else
        {
            aopXVDIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDIBAllowDialBack));
        }
    }

    private void setXSOIBAllowDialBack(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXSOIBAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_IB_AllowDialBack));
        if (aopXSOIBAllowDialBack == null)
        {
            aopXSOIBAllowDialBack = new AOPsProperties();
            aopXSOIBAllowDialBack.setAOPs(_aops);
            aopXSOIBAllowDialBack.setConfKey(AOPsProperties.Keys.XSO_IB_AllowDialBack.name());
            aopXSOIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOIBAllowDialBack));
        }
        else
        {
            aopXSOIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOIBAllowDialBack));
        }

    }

    private void setXMIBAllowDialBack(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXMIBAllowDialBack = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_IB_AllowDialBack));
        if (aopXMIBAllowDialBack == null)
        {
            aopXMIBAllowDialBack = new AOPsProperties();
            aopXMIBAllowDialBack.setAOPs(_aops);
            aopXMIBAllowDialBack.setConfKey(AOPsProperties.Keys.XM_IB_AllowDialBack.name());
            aopXMIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMIBAllowDialBack));
        }
        else
        {
            aopXMIBAllowDialBack.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMIBAllowDialBack));
        }
    }

    private void setXSOOBAllowedDialModes(String campmodes) throws GravityException, CODEException
    {
        if (campmodes == null || campmodes.trim().isEmpty())
        {

            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XSO_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        //TBD:need to check for IllegalArgumentException for campaign mode.
        try
        {
            List<DialMode> listCampModes = Arrays.asList(campmodes.split(",")).stream().map(cm -> DialMode.valueOf(cm)).collect(Collectors.toList());
            ArrayList<String> aldialmode = JSONUtil.FromJSON(listCampModes.toString(), ArrayList.class);
            JSONArray dialmodejsonarr = new JSONArray(aldialmode.toArray());

            //If Predictive dialmode removed form the alloweDialed modes then we have to unmpa the PredQueue also as there are no use of Predictive queue now.And if we are not removing it then it will not mapped with any other predictive campaign also.
//            if(!aldialmode.contains(DialMode.Predictive.name())){
//                AOPsProperties aopPreedQueue = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_PredQueue));
//                if(aopPreedQueue!=null){
//                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreedQueue));
//                }
//
//            }
//            if(!aldialmode.contains(DialMode.Preview.name())){
//                AOPsProperties aopPreviewLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_PreviewTimeout));
//                if (aopPreviewLimit != null)
//                {
//                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreviewLimit));
//                }
//            }
            AOPsProperties aopXSOOBAllowedDialModes = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_AllowedDialModes));
            if (aopXSOOBAllowedDialModes == null)
            {
                aopXSOOBAllowedDialModes = new AOPsProperties();
                aopXSOOBAllowedDialModes.setAOPs(_aops);
                aopXSOOBAllowedDialModes.setConfKey(AOPsProperties.Keys.XSO_OB_AllowedDialModes.name());
                aopXSOOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOOBAllowedDialModes));
            }
            else
            {
                aopXSOOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOOBAllowedDialModes));
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XSO_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    private void setXSOOBAllowManualDial(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXSOOBAllowManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_AllowManualDial));
        if (aopXSOOBAllowManualDial == null)
        {
            aopXSOOBAllowManualDial = new AOPsProperties();
            aopXSOOBAllowManualDial.setAOPs(_aops);
            aopXSOOBAllowManualDial.setConfKey(AOPsProperties.Keys.XSO_OB_AllowManualDial.name());
            aopXSOOBAllowManualDial.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOOBAllowManualDial));
        }
        else
        {
            aopXSOOBAllowManualDial.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOOBAllowManualDial));
        }
    }

    private void setXSOOBDialLimit(String value) throws GravityException, CODEException
    {
        Integer dialLimit = null;
        try
        {
            dialLimit = Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XSO_OB_DialLimit.name(), EventFailedCause.ValueOutOfRange);
        }
        if (dialLimit < Limits.DialLimit_MIN || dialLimit > Limits.DialLimit_MAX)
        {
            throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XSO_OB_DialLimit.name(), EventFailedCause.DataBoundaryLimitViolation);
        }

        AOPsProperties aopXSOOBDialLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_DialLimit));
        if (aopXSOOBDialLimit == null)
        {
            aopXSOOBDialLimit = new AOPsProperties();
            aopXSOOBDialLimit.setAOPs(_aops);
            aopXSOOBDialLimit.setConfKey(AOPsProperties.Keys.XSO_OB_DialLimit.name());
            aopXSOOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOOBDialLimit));
        }
        else
        {
            aopXSOOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOOBDialLimit));
        }
    }

    private void setXSOOBEnableDialChain(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXSOOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_OB_EnableDialChain));
        if (aopXSOOBEnableDialChain == null)
        {
            aopXSOOBEnableDialChain = new AOPsProperties();
            aopXSOOBEnableDialChain.setAOPs(_aops);
            aopXSOOBEnableDialChain.setConfKey(AOPsProperties.Keys.XSO_OB_EnableDialChain.name());
            aopXSOOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOOBEnableDialChain));
        }
        else
        {
            aopXSOOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOOBEnableDialChain));
        }
    }

    private void setXVDOBAllowedDialModes(String campmodes) throws GravityException, CODEException
    {
        if (campmodes == null || campmodes.trim().isEmpty())
        {

            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XVD_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        //TBD:need to check for IllegalArgumentException for campaign mode.
        try
        {
            List<DialMode> listCampModes = Arrays.asList(campmodes.split(",")).stream().map(cm -> DialMode.valueOf(cm)).collect(Collectors.toList());
            ArrayList<String> aldialmode = JSONUtil.FromJSON(listCampModes.toString(), ArrayList.class);
            JSONArray dialmodejsonarr = new JSONArray(aldialmode.toArray());

            //If Predictive dialmode removed form the alloweDialed modes then we have to unmpa the PredQueue also as there are no use of Predictive queue now.And if we are not removing it then it will not mapped with any other predictive campaign also.
//            if(!aldialmode.contains(DialMode.Predictive.name())){
//                AOPsProperties aopPreedQueue = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_PredQueue));
//                if(aopPreedQueue!=null){
//                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreedQueue));
//                }
//
//            }
//            if(!aldialmode.contains(DialMode.Preview.name())){
//                AOPsProperties aopPreviewLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_PreviewTimeout));
//                if (aopPreviewLimit != null)
//                {
//                    entities.add(new NameValuePair(ENActionList.Action.Delete.name(), aopPreviewLimit));
//                }
//            }
            AOPsProperties aopXVDOBAllowedDialModes = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_AllowedDialModes));
            if (aopXVDOBAllowedDialModes == null)
            {
                aopXVDOBAllowedDialModes = new AOPsProperties();
                aopXVDOBAllowedDialModes.setAOPs(_aops);
                aopXVDOBAllowedDialModes.setConfKey(AOPsProperties.Keys.XVD_OB_AllowedDialModes.name());
                aopXVDOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDOBAllowedDialModes));
            }
            else
            {
                aopXVDOBAllowedDialModes.setConfValue(dialmodejsonarr.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDOBAllowedDialModes));
            }
        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XVD_OB_AllowedDialModes.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
    }

    private void setXVDOBAllowManualDial(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXVDOBAllowManualDial = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_AllowManualDial));
        if (aopXVDOBAllowManualDial == null)
        {
            aopXVDOBAllowManualDial = new AOPsProperties();
            aopXVDOBAllowManualDial.setAOPs(_aops);
            aopXVDOBAllowManualDial.setConfKey(AOPsProperties.Keys.XVD_OB_AllowManualDial.name());
            aopXVDOBAllowManualDial.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDOBAllowManualDial));
        }
        else
        {
            aopXVDOBAllowManualDial.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDOBAllowManualDial));
        }
    }

    private void setXVDOBDialLimit(String value) throws GravityException, CODEException
    {
        Integer dialLimit = null;
        try
        {
            dialLimit = Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_OB_DialLimit.name(), EventFailedCause.ValueOutOfRange);
        }
        if (dialLimit < Limits.DialLimit_MIN || dialLimit > Limits.DialLimit_MAX)
        {
            throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XVD_OB_DialLimit.name(), EventFailedCause.DataBoundaryLimitViolation);
        }

        AOPsProperties aopXVDOBDialLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_DialLimit));
        if (aopXVDOBDialLimit == null)
        {
            aopXVDOBDialLimit = new AOPsProperties();
            aopXVDOBDialLimit.setAOPs(_aops);
            aopXVDOBDialLimit.setConfKey(AOPsProperties.Keys.XVD_OB_DialLimit.name());
            aopXVDOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDOBDialLimit));
        }
        else
        {
            aopXVDOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDOBDialLimit));
        }
    }

    private void setXVDOBEnableDialChain(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXVDOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_OB_EnableDialChain));
        if (aopXVDOBEnableDialChain == null)
        {
            aopXVDOBEnableDialChain = new AOPsProperties();
            aopXVDOBEnableDialChain.setAOPs(_aops);
            aopXVDOBEnableDialChain.setConfKey(AOPsProperties.Keys.XVD_OB_EnableDialChain.name());
            aopXVDOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDOBEnableDialChain));
        }
        else
        {
            aopXVDOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDOBEnableDialChain));
        }
    }

    private void setXVDDialTimeout(String timeout) throws GravityException, CODEException
    {

        try
        {

            AOPsProperties aopXVDDialTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_DialTimeout));
            if (aopXVDDialTimeout == null)
            {
                aopXVDDialTimeout = new AOPsProperties();
                aopXVDDialTimeout.setAOPs(_aops);
                aopXVDDialTimeout.setConfKey(AOPsProperties.Keys.XVD_DialTimeout.name());
                aopXVDDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDDialTimeout));
            }
            else
            {
                aopXVDDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDDialTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_DialTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXCHDialTimeout(String timeout) throws GravityException, CODEException
    {

        try
        {

            AOPsProperties aopXCHDialTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_DialTimeout));
            if (aopXCHDialTimeout == null)
            {
                aopXCHDialTimeout = new AOPsProperties();
                aopXCHDialTimeout.setAOPs(_aops);
                aopXCHDialTimeout.setConfKey(AOPsProperties.Keys.XCH_DialTimeout.name());
                aopXCHDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHDialTimeout));
            }
            else
            {
                aopXCHDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHDialTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XCH_DialTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXMDialTimeout(String timeout) throws GravityException, CODEException
    {

        try
        {

            AOPsProperties aopXMDialTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_DialTimeout));
            if (aopXMDialTimeout == null)
            {
                aopXMDialTimeout = new AOPsProperties();
                aopXMDialTimeout.setAOPs(_aops);
                aopXMDialTimeout.setConfKey(AOPsProperties.Keys.XM_DialTimeout.name());
                aopXMDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMDialTimeout));
            }
            else
            {
                aopXMDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMDialTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XM_DialTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXTPostSessRecEnabled(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_PostSessRecEnabled));
        if (aopXTPostSessRecEnabled == null)
        {
            aopXTPostSessRecEnabled = new AOPsProperties();
            aopXTPostSessRecEnabled.setAOPs(_aops);
            aopXTPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XT_PostSessRecEnabled.name());
            aopXTPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTPostSessRecEnabled));
        }
        else
        {
            aopXTPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTPostSessRecEnabled));
        }
    }

    private void setXCHPostSessRecEnabled(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_PostSessRecEnabled));
        if (aopXCHPostSessRecEnabled == null)
        {
            aopXCHPostSessRecEnabled = new AOPsProperties();
            aopXCHPostSessRecEnabled.setAOPs(_aops);
            aopXCHPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XCH_PostSessRecEnabled.name());
            aopXCHPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHPostSessRecEnabled));
        }
        else
        {
            aopXCHPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHPostSessRecEnabled));
        }
    }

    private void setXVDPostSessRecEnabled(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXVDPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_PostSessRecEnabled));
        if (aopXVDPostSessRecEnabled == null)
        {
            aopXVDPostSessRecEnabled = new AOPsProperties();
            aopXVDPostSessRecEnabled.setAOPs(_aops);
            aopXVDPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XVD_PostSessRecEnabled.name());
            aopXVDPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDPostSessRecEnabled));
        }
        else
        {
            aopXVDPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDPostSessRecEnabled));
        }
    }

    private void setXEMPostSessRecEnabled(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEMPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_PostSessRecEnabled));
        if (aopXEMPostSessRecEnabled == null)
        {
            aopXEMPostSessRecEnabled = new AOPsProperties();
            aopXEMPostSessRecEnabled.setAOPs(_aops);
            aopXEMPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XEM_PostSessRecEnabled.name());
            aopXEMPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMPostSessRecEnabled));
        }
        else
        {
            aopXEMPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMPostSessRecEnabled));
        }
    }

    private void setXSOPostSessRecEnabled(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_PostSessRecEnabled));
        if (aopXCHPostSessRecEnabled == null)
        {
            aopXCHPostSessRecEnabled = new AOPsProperties();
            aopXCHPostSessRecEnabled.setAOPs(_aops);
            aopXCHPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XSO_PostSessRecEnabled.name());
            aopXCHPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHPostSessRecEnabled));
        }
        else
        {
            aopXCHPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHPostSessRecEnabled));
        }
    }

    private void setXMPostSessRecEnabled(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXMPostSessRecEnabled = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_PostSessRecEnabled));
        if (aopXMPostSessRecEnabled == null)
        {
            aopXMPostSessRecEnabled = new AOPsProperties();
            aopXMPostSessRecEnabled.setAOPs(_aops);
            aopXMPostSessRecEnabled.setConfKey(AOPsProperties.Keys.XM_PostSessRecEnabled.name());
            aopXMPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMPostSessRecEnabled));
        }
        else
        {
            aopXMPostSessRecEnabled.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMPostSessRecEnabled));
        }
    }

    private void setGlobal_EnableAbandonTreatment(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopGlobal_EnableAbandonTreatment = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_EnableAbandonTreatment));
        if (aopGlobal_EnableAbandonTreatment == null)
        {
            aopGlobal_EnableAbandonTreatment = new AOPsProperties();
            aopGlobal_EnableAbandonTreatment.setAOPs(_aops);
            aopGlobal_EnableAbandonTreatment.setConfKey(AOPsProperties.Keys.Global_EnableAbandonTreatment.name());
            aopGlobal_EnableAbandonTreatment.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopGlobal_EnableAbandonTreatment));
        }
        else
        {
            aopGlobal_EnableAbandonTreatment.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopGlobal_EnableAbandonTreatment));
        }
    }

    private void setXTCompressRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXTCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_CompressRecordingFile));
        if (aopXTCompressRecordingFile == null)
        {
            aopXTCompressRecordingFile = new AOPsProperties();
            aopXTCompressRecordingFile.setAOPs(_aops);
            aopXTCompressRecordingFile.setConfKey(AOPsProperties.Keys.XT_CompressRecordingFile.name());
            aopXTCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTCompressRecordingFile));
        }
        else
        {
            aopXTCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTCompressRecordingFile));
        }

    }

    private void setXTEncryptRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXTEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_EncryptRecordingFile));
        if (aopXTEncryptRecordingFile == null)
        {
            aopXTEncryptRecordingFile = new AOPsProperties();
            aopXTEncryptRecordingFile.setAOPs(_aops);
            aopXTEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XT_EncryptRecordingFile.name());
            aopXTEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTEncryptRecordingFile));
        }
        else
        {
            aopXTEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTEncryptRecordingFile));
        }

    }

    private void setXCHCompressRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXCHCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_CompressRecordingFile));
        if (aopXCHCompressRecordingFile == null)
        {
            aopXCHCompressRecordingFile = new AOPsProperties();
            aopXCHCompressRecordingFile.setAOPs(_aops);
            aopXCHCompressRecordingFile.setConfKey(AOPsProperties.Keys.XCH_CompressRecordingFile.name());
            aopXCHCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHCompressRecordingFile));
        }
        else
        {
            aopXCHCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHCompressRecordingFile));
        }

    }

    private void setXCHEncryptRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXCHEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_EncryptRecordingFile));
        if (aopXCHEncryptRecordingFile == null)
        {
            aopXCHEncryptRecordingFile = new AOPsProperties();
            aopXCHEncryptRecordingFile.setAOPs(_aops);
            aopXCHEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XCH_EncryptRecordingFile.name());
            aopXCHEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHEncryptRecordingFile));
        }
        else
        {
            aopXCHEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHEncryptRecordingFile));
        }

    }

    private void setXVDCompressRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXVDCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_CompressRecordingFile));
        if (aopXVDCompressRecordingFile == null)
        {
            aopXVDCompressRecordingFile = new AOPsProperties();
            aopXVDCompressRecordingFile.setAOPs(_aops);
            aopXVDCompressRecordingFile.setConfKey(AOPsProperties.Keys.XVD_CompressRecordingFile.name());
            aopXVDCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDCompressRecordingFile));
        }
        else
        {
            aopXVDCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDCompressRecordingFile));
        }

    }

    private void setXVDEncryptRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXVDEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_EncryptRecordingFile));
        if (aopXVDEncryptRecordingFile == null)
        {
            aopXVDEncryptRecordingFile = new AOPsProperties();
            aopXVDEncryptRecordingFile.setAOPs(_aops);
            aopXVDEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XVD_EncryptRecordingFile.name());
            aopXVDEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDEncryptRecordingFile));
        }
        else
        {
            aopXVDEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDEncryptRecordingFile));
        }

    }

    private void setXEMCompressRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXEMCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_CompressRecordingFile));
        if (aopXEMCompressRecordingFile == null)
        {
            aopXEMCompressRecordingFile = new AOPsProperties();
            aopXEMCompressRecordingFile.setAOPs(_aops);
            aopXEMCompressRecordingFile.setConfKey(AOPsProperties.Keys.XEM_CompressRecordingFile.name());
            aopXEMCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMCompressRecordingFile));
        }
        else
        {
            aopXEMCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMCompressRecordingFile));
        }

    }

    private void setXEMEncryptRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXEMEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_EncryptRecordingFile));
        if (aopXEMEncryptRecordingFile == null)
        {
            aopXEMEncryptRecordingFile = new AOPsProperties();
            aopXEMEncryptRecordingFile.setAOPs(_aops);
            aopXEMEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XEM_EncryptRecordingFile.name());
            aopXEMEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMEncryptRecordingFile));
        }
        else
        {
            aopXEMEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMEncryptRecordingFile));
        }

    }

    private void setXSOCompressRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXSOCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_CompressRecordingFile));
        if (aopXSOCompressRecordingFile == null)
        {
            aopXSOCompressRecordingFile = new AOPsProperties();
            aopXSOCompressRecordingFile.setAOPs(_aops);
            aopXSOCompressRecordingFile.setConfKey(AOPsProperties.Keys.XSO_CompressRecordingFile.name());
            aopXSOCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOCompressRecordingFile));
        }
        else
        {
            aopXSOCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOCompressRecordingFile));
        }

    }

    private void setXSOEncryptRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXSOEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_EncryptRecordingFile));
        if (aopXSOEncryptRecordingFile == null)
        {
            aopXSOEncryptRecordingFile = new AOPsProperties();
            aopXSOEncryptRecordingFile.setAOPs(_aops);
            aopXSOEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XSO_EncryptRecordingFile.name());
            aopXSOEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOEncryptRecordingFile));
        }
        else
        {
            aopXSOEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOEncryptRecordingFile));
        }

    }

    private void setXMCompressRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXMCompressRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_CompressRecordingFile));
        if (aopXMCompressRecordingFile == null)
        {
            aopXMCompressRecordingFile = new AOPsProperties();
            aopXMCompressRecordingFile.setAOPs(_aops);
            aopXMCompressRecordingFile.setConfKey(AOPsProperties.Keys.XM_CompressRecordingFile.name());
            aopXMCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMCompressRecordingFile));
        }
        else
        {
            aopXMCompressRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMCompressRecordingFile));
        }

    }

    private void setXMEncryptRecordingFile(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXMEncryptRecordingFile = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_EncryptRecordingFile));
        if (aopXMEncryptRecordingFile == null)
        {
            aopXMEncryptRecordingFile = new AOPsProperties();
            aopXMEncryptRecordingFile.setAOPs(_aops);
            aopXMEncryptRecordingFile.setConfKey(AOPsProperties.Keys.XM_EncryptRecordingFile.name());
            aopXMEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMEncryptRecordingFile));
        }
        else
        {
            aopXMEncryptRecordingFile.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMEncryptRecordingFile));
        }

    }

    private void setXT_RecordingBeepDuration(String value) throws GravityException, CODEException
    {

        try {
            Integer beep = Integer.valueOf(value);

            if (beep < Limits.XT_RecordingBeepDuration || beep > Limits.XT_MAXRecordingBeepDuration) {
                if (beep < Limits.XT_RecordingBeepDuration || beep > Limits.XT_MAXRecordingBeepDuration) {
                    throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.LimitExceed,AOPsProperties.Keys.XT_RecordingBeepDuration.name()+" value must between 4 to 128 ");
                }
            }
            AOPsProperties aopXT_RecordingBeepDuration = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_RecordingBeepDuration));
            if (aopXT_RecordingBeepDuration == null) {
                aopXT_RecordingBeepDuration = new AOPsProperties();
                aopXT_RecordingBeepDuration.setAOPs(_aops);
                aopXT_RecordingBeepDuration.setConfKey(AOPsProperties.Keys.XT_RecordingBeepDuration.name());
                aopXT_RecordingBeepDuration.setConfValue(value);
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXT_RecordingBeepDuration));
            } else {
                aopXT_RecordingBeepDuration.setConfValue(value);
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXT_RecordingBeepDuration));
            }
        } catch (NumberFormatException e) {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XT_RecordingBeepDuration.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXCHOBMechaTemplateCode(String value, HashMap<String, String> hmAttr) throws GravityException, CODEException, Exception
    {

        if (hmAttr.containsKey(AOPsProperties.Keys.XCH_OB_AllowedDialModes.name()))
        {
            String reqtempcode = hmAttr.get(AOPsProperties.Keys.XCH_OB_AllowedDialModes.name());
            List<String> listreqtempcode = Arrays.asList(reqtempcode.split(","));
            if (!listreqtempcode.contains(DialMode.Mecha.name()))
            {
                //throw an exception
                throw new GravityIllegalArgumentException("Property Only Allowed For Mecha Dial Mode");
            }
        }
        else
        {
            AOPsProperties aopsallowdialmode = _tctx.getDB().FindAssert(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_AllowedDialModes));
            String Dialmode = aopsallowdialmode.getConfValue();
            ArrayList<String> dbdialmodes = JSONUtil.FromJSON(Dialmode, ArrayList.class);
            if (!dbdialmodes.contains(DialMode.Mecha.name()))
            {
                //throw an exception
                throw new GravityIllegalArgumentException("Property Only Allowed For Mecha Dial Mode");
            }
        }
        AOPsProperties aopXCHOBMechaTemplateCode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_OB_Mecha_TemplateCode));
        if (aopXCHOBMechaTemplateCode == null)
        {
            aopXCHOBMechaTemplateCode = new AOPsProperties();
            aopXCHOBMechaTemplateCode.setAOPs(_aops);
            aopXCHOBMechaTemplateCode.setConfKey(AOPsProperties.Keys.XCH_OB_Mecha_TemplateCode.name());
            aopXCHOBMechaTemplateCode.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHOBMechaTemplateCode));
        }
        else
        {
            aopXCHOBMechaTemplateCode.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHOBMechaTemplateCode));
        }

    }

    private void setXEMOBMechaTemplateCode(String value, HashMap<String, String> hmAttr) throws GravityException, CODEException, Exception
    {

        if (hmAttr.containsKey(AOPsProperties.Keys.XEM_OB_AllowedDialModes.name()))
        {
            String reqtempcode = hmAttr.get(AOPsProperties.Keys.XEM_OB_AllowedDialModes.name());
            List<String> listreqtempcode = Arrays.asList(reqtempcode.split(","));
            if (!listreqtempcode.contains(DialMode.Mecha.name()))
            {
                //throw an exception
                throw new GravityIllegalArgumentException("Property Only Allowed For Mecha Dial Mode");
            }
        }
        else
        {
            AOPsProperties aopsallowdialmode = _tctx.getDB().FindAssert(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_AllowedDialModes));
            String Dialmode = aopsallowdialmode.getConfValue();
            ArrayList<String> dbdialmodes = JSONUtil.FromJSON(Dialmode, ArrayList.class);
            if (!dbdialmodes.contains(DialMode.Mecha.name()))
            {
                //throw an exception
                throw new GravityIllegalArgumentException("Property Only Allowed For Mecha Dial Mode");
            }
        }
        AOPsProperties aopXEMOBMechaTemplateCode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_OB_Mecha_TemplateCode));
        if (aopXEMOBMechaTemplateCode == null)
        {
            aopXEMOBMechaTemplateCode = new AOPsProperties();
            aopXEMOBMechaTemplateCode.setAOPs(_aops);
            aopXEMOBMechaTemplateCode.setConfKey(AOPsProperties.Keys.XEM_OB_Mecha_TemplateCode.name());
            aopXEMOBMechaTemplateCode.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMOBMechaTemplateCode));
        }
        else
        {
            aopXEMOBMechaTemplateCode.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMOBMechaTemplateCode));
        }

    }

    private void setXWSessionTimeout(String timeout) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopsXWSessionTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_SessionTimeout));
            if (aopsXWSessionTimeout == null)
            {
                aopsXWSessionTimeout = new AOPsProperties();
                aopsXWSessionTimeout.setAOPs(_aops);
                aopsXWSessionTimeout.setConfKey(AOPsProperties.Keys.XW_SessionTimeout.name());
                aopsXWSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXWSessionTimeout));
            }
            else
            {
                aopsXWSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXWSessionTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_SessionTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXWAutoAnswer(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXWAutoAnswer = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_AutoAnswer));
        if (aopXWAutoAnswer == null)
        {
            aopXWAutoAnswer = new AOPsProperties();
            aopXWAutoAnswer.setAOPs(_aops);
            aopXWAutoAnswer.setConfKey(AOPsProperties.Keys.XW_AutoAnswer.name());
            aopXWAutoAnswer.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXWAutoAnswer));
        }
        else
        {
            aopXWAutoAnswer.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXWAutoAnswer));
        }
    }

    private void setXWAutoAnswerDelay(String value) throws GravityException, CODEException
    {

        AOPsProperties aopXWAutoAnswerDelay = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_AutoAnswerDelay));
        if (aopXWAutoAnswerDelay == null)
        {
            aopXWAutoAnswerDelay = new AOPsProperties();
            aopXWAutoAnswerDelay.setAOPs(_aops);
            aopXWAutoAnswerDelay.setConfKey(AOPsProperties.Keys.XW_AutoAnswerDelay.name());
            aopXWAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXWAutoAnswerDelay));
        }
        else
        {
            aopXWAutoAnswerDelay.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXWAutoAnswerDelay));
        }
    }

    private void setXW_AllowReject(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        AOPsProperties aopXVDAllowReject = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_AllowReject));
        if (aopXVDAllowReject == null)
        {
            aopXVDAllowReject = new AOPsProperties();
            aopXVDAllowReject.setAOPs(_aops);
            aopXVDAllowReject.setConfKey(AOPsProperties.Keys.XW_AllowReject.name());
            aopXVDAllowReject.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDAllowReject));
        }
        else
        {
            aopXVDAllowReject.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDAllowReject));
        }
    }

    private void setXWIBAuthParams(String authprms) throws Exception, GravityException, CODEException
    {
        Properties props = JSONUtil.FromJSON(authprms, Properties.class);
        AOPsProperties aopsXWIBAuthParams = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_IB_AuthParams));
        if (aopsXWIBAuthParams == null)
        {
            aopsXWIBAuthParams = new AOPsProperties();
            aopsXWIBAuthParams.setAOPs(_aops);
            aopsXWIBAuthParams.setConfKey(AOPsProperties.Keys.XW_IB_AuthParams.name());
            aopsXWIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXWIBAuthParams));
        }
        else
        {
            aopsXWIBAuthParams.setConfValue(authprms);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXWIBAuthParams));
        }
    }

    private void setXWIBEnableBlockCall(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXW_IB_EnableBlockFilter = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_IB_EnableBlockFilter));
        if (aopXW_IB_EnableBlockFilter == null)
        {
            aopXW_IB_EnableBlockFilter = new AOPsProperties();
            aopXW_IB_EnableBlockFilter.setAOPs(_aops);
            aopXW_IB_EnableBlockFilter.setConfKey(AOPsProperties.Keys.XW_IB_EnableBlockFilter.name());
            aopXW_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXW_IB_EnableBlockFilter));
        }
        else
        {
            aopXW_IB_EnableBlockFilter.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXW_IB_EnableBlockFilter));
        }
    }

    private void setXWIBEnableWebRAWB(String value) throws GravityException, CODEException
    {
        AOPsProperties aopXWB_IB_EnableWebRAWB = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_IB_EnableWebRAWB));
        if (aopXWB_IB_EnableWebRAWB == null)
        {
            aopXWB_IB_EnableWebRAWB = new AOPsProperties();
            aopXWB_IB_EnableWebRAWB.setAOPs(_aops);
            aopXWB_IB_EnableWebRAWB.setConfKey(AOPsProperties.Keys.XW_IB_EnableWebRAWB.name());
            aopXWB_IB_EnableWebRAWB.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXWB_IB_EnableWebRAWB));
        }
        else
        {
            aopXWB_IB_EnableWebRAWB.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXWB_IB_EnableWebRAWB));
        }
    }

    private void setXVTSessionTimeout(String timeout) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties aopsXVTSessionTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_SessionTimeout));
            if (aopsXVTSessionTimeout == null)
            {
                aopsXVTSessionTimeout = new AOPsProperties();
                aopsXVTSessionTimeout.setAOPs(_aops);
                aopsXVTSessionTimeout.setConfKey(AOPsProperties.Keys.XVT_SessionTimeout.name());
                aopsXVTSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVTSessionTimeout));
            }
            else
            {
                aopsXVTSessionTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXVTSessionTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_SessionTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXVTDialTimeout(String timeout) throws GravityException, CODEException
    {

        try
        {

            AOPsProperties aopXVTDialTimeout = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_DialTimeout));
            if (aopXVTDialTimeout == null)
            {
                aopXVTDialTimeout = new AOPsProperties();
                aopXVTDialTimeout.setAOPs(_aops);
                aopXVTDialTimeout.setConfKey(AOPsProperties.Keys.XVT_DialTimeout.name());
                aopXVTDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVTDialTimeout));
            }
            else
            {
                aopXVTDialTimeout.setConfValue(Integer.valueOf(timeout).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVTDialTimeout));
            }
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_DialTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXVTOBDialLimit(String value) throws GravityException, CODEException
    {
        Integer dialLimit = null;
        try
        {
            dialLimit = Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_OB_DialLimit.name(), EventFailedCause.ValueOutOfRange);
        }
        if (dialLimit < Limits.DialLimit_MIN || dialLimit > Limits.DialLimit_MAX)
        {
            throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XVD_OB_DialLimit.name(), EventFailedCause.DataBoundaryLimitViolation);
        }

        AOPsProperties aopXVTOBDialLimit = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_OB_DialLimit));
        if (aopXVTOBDialLimit == null)
        {
            aopXVTOBDialLimit = new AOPsProperties();
            aopXVTOBDialLimit.setAOPs(_aops);
            aopXVTOBDialLimit.setConfKey(AOPsProperties.Keys.XVT_OB_DialLimit.name());
            aopXVTOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVTOBDialLimit));
        }
        else
        {
            aopXVTOBDialLimit.setConfValue(dialLimit.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVTOBDialLimit));
        }
    }

    private void setXVTOBMaxAttemptCount(String attempt) throws GravityException, CODEException
    {
        try
        {
            Integer maxattempt = Integer.valueOf(attempt);
            if (maxattempt < 1 || maxattempt > 1024)
            {
                throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.XVD_OB_MaxAttemptCount.name(), EventFailedCause.DataBoundaryLimitViolation);
            }

            AOPsProperties aopsXVT_OB_MaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_OB_MaxAttemptCount));
            if (aopsXVT_OB_MaxAttemptCount == null)
            {
                aopsXVT_OB_MaxAttemptCount = new AOPsProperties();
                aopsXVT_OB_MaxAttemptCount.setAOPs(_aops);
                aopsXVT_OB_MaxAttemptCount.setConfKey(AOPsProperties.Keys.XVT_OB_MaxAttemptCount.name());
                aopsXVT_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsXVT_OB_MaxAttemptCount));
            }
            else
            {
                aopsXVT_OB_MaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsXVT_OB_MaxAttemptCount));
            }

        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.XVD_OB_MaxAttemptCount.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXVTOBCheckDNC(String value) throws GravityException, CODEException
    {

        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);

        if (val)
        {
            _tctx.getUCOSCtx().GetProcess(_aops);
        }

        AOPsProperties aopXVTOBCheckDNC = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_OB_CheckDNC));
        if (aopXVTOBCheckDNC == null)
        {
            aopXVTOBCheckDNC = new AOPsProperties();
            aopXVTOBCheckDNC.setAOPs(_aops);
            aopXVTOBCheckDNC.setConfKey(AOPsProperties.Keys.XVT_OB_CheckDNC.name());
            aopXVTOBCheckDNC.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVTOBCheckDNC));
        }
        else
        {
            aopXVTOBCheckDNC.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVTOBCheckDNC));
        }

    }

    private void setXVTOBEnableDialChain(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXVTOBEnableDialChain = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_OB_EnableDialChain));
        if (aopXVTOBEnableDialChain == null)
        {
            aopXVTOBEnableDialChain = new AOPsProperties();
            aopXVTOBEnableDialChain.setAOPs(_aops);
            aopXVTOBEnableDialChain.setConfKey(AOPsProperties.Keys.XVT_OB_EnableDialChain.name());
            aopXVTOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVTOBEnableDialChain));
        }
        else
        {
            aopXVTOBEnableDialChain.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVTOBEnableDialChain));
        }
    }

    private void setGlobalMaxAttemptCount(String attempt) throws GravityException, CODEException
    {
        try
        {
            Integer maxattempt = Integer.valueOf(attempt);
            if (maxattempt < 1 || maxattempt > 1024)
            {
                throw new GravityIllegalArgumentException("Value Must be In Range Of [1,1024]", AOPsProperties.Keys.Global_MaxAttemptCount.name(), EventFailedCause.DataBoundaryLimitViolation);
            }

            AOPsProperties aopsMaxAttemptCount = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_MaxAttemptCount));
            if (aopsMaxAttemptCount == null)
            {
                aopsMaxAttemptCount = new AOPsProperties();
                aopsMaxAttemptCount.setAOPs(_aops);
                aopsMaxAttemptCount.setConfKey(AOPsProperties.Keys.Global_MaxAttemptCount.name());
                aopsMaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsMaxAttemptCount));
            }
            else
            {
                aopsMaxAttemptCount.setConfValue(maxattempt.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsMaxAttemptCount));
            }

        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Integer type", AOPsProperties.Keys.Global_MaxAttemptCount.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setGlobalSurveyId(String id) throws GravityException, CODEException
    {
        try
        {
            Long surveyId = Long.valueOf(id);
            //check survey present or not

            Survey survey = _tctx.getDB().Find(EN.Survey.getEntityClass(), surveyId);
            if (survey == null)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.EntityNotCreatedYet, "Survey");
            }
            AOPsProperties aopsGlobal_SurveyId = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.Global_SurveyId));
            if (aopsGlobal_SurveyId == null)
            {
                aopsGlobal_SurveyId = new AOPsProperties();
                aopsGlobal_SurveyId.setAOPs(_aops);
                aopsGlobal_SurveyId.setConfKey(AOPsProperties.Keys.Global_SurveyId.name());
                aopsGlobal_SurveyId.setConfValue(surveyId.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopsGlobal_SurveyId));
            }
            else
            {
                aopsGlobal_SurveyId.setConfValue(surveyId.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopsGlobal_SurveyId));
            }

        }
        catch (NumberFormatException e)
        {
            throw new GravityIllegalArgumentException("value must be an Long type", AOPsProperties.Keys.Global_SurveyId.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setXTEnableScreenRecording(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXTEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_EnableScreenRecording));
        if (aopXTEnableScreenRecording == null)
        {
            aopXTEnableScreenRecording = new AOPsProperties();
            aopXTEnableScreenRecording.setAOPs(_aops);
            aopXTEnableScreenRecording.setConfKey(AOPsProperties.Keys.XT_EnableScreenRecording.name());
            aopXTEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTEnableScreenRecording));
        }
        else
        {
            aopXTEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTEnableScreenRecording));
        }
    }

    private void setXCHEnableScreenRecording(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXCHEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_EnableScreenRecording));
        if (aopXCHEnableScreenRecording == null)
        {
            aopXCHEnableScreenRecording = new AOPsProperties();
            aopXCHEnableScreenRecording.setAOPs(_aops);
            aopXCHEnableScreenRecording.setConfKey(AOPsProperties.Keys.XCH_EnableScreenRecording.name());
            aopXCHEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHEnableScreenRecording));
        }
        else
        {
            aopXCHEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHEnableScreenRecording));
        }
    }

    private void setXEMEnableScreenRecording(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXEMEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_EnableScreenRecording));
        if (aopXEMEnableScreenRecording == null)
        {
            aopXEMEnableScreenRecording = new AOPsProperties();
            aopXEMEnableScreenRecording.setAOPs(_aops);
            aopXEMEnableScreenRecording.setConfKey(AOPsProperties.Keys.XEM_EnableScreenRecording.name());
            aopXEMEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMEnableScreenRecording));
        }
        else
        {
            aopXEMEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMEnableScreenRecording));
        }
    }

    private void setXSOEnableScreenRecording(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXSOEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_EnableScreenRecording));
        if (aopXSOEnableScreenRecording == null)
        {
            aopXSOEnableScreenRecording = new AOPsProperties();
            aopXSOEnableScreenRecording.setAOPs(_aops);
            aopXSOEnableScreenRecording.setConfKey(AOPsProperties.Keys.XSO_EnableScreenRecording.name());
            aopXSOEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOEnableScreenRecording));
        }
        else
        {
            aopXSOEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOEnableScreenRecording));
        }
    }

    private void setXVDEnableScreenRecording(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXVDEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_EnableScreenRecording));
        if (aopXVDEnableScreenRecording == null)
        {
            aopXVDEnableScreenRecording = new AOPsProperties();
            aopXVDEnableScreenRecording.setAOPs(_aops);
            aopXVDEnableScreenRecording.setConfKey(AOPsProperties.Keys.XVD_EnableScreenRecording.name());
            aopXVDEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDEnableScreenRecording));
        }
        else
        {
            aopXVDEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDEnableScreenRecording));
        }
    }

    private void setXWEnableScreenRecording(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXWEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_EnableScreenRecording));
        if (aopXWEnableScreenRecording == null)
        {
            aopXWEnableScreenRecording = new AOPsProperties();
            aopXWEnableScreenRecording.setAOPs(_aops);
            aopXWEnableScreenRecording.setConfKey(AOPsProperties.Keys.XW_EnableScreenRecording.name());
            aopXWEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXWEnableScreenRecording));
        }
        else
        {
            aopXWEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXWEnableScreenRecording));
        }
    }

    private void setXVTEnableScreenRecording(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXVTEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_EnableScreenRecording));
        if (aopXVTEnableScreenRecording == null)
        {
            aopXVTEnableScreenRecording = new AOPsProperties();
            aopXVTEnableScreenRecording.setAOPs(_aops);
            aopXVTEnableScreenRecording.setConfKey(AOPsProperties.Keys.XVT_EnableScreenRecording.name());
            aopXVTEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVTEnableScreenRecording));
        }
        else
        {
            aopXVTEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVTEnableScreenRecording));
        }
    }

    private void setXMEnableScreenRecording(String value) throws GravityException, CODEException
    {
        Boolean val = value.trim().isEmpty() ? false : Boolean.valueOf(value);
        AOPsProperties aopXMEnableScreenRecording = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_EnableScreenRecording));
        if (aopXMEnableScreenRecording == null)
        {
            aopXMEnableScreenRecording = new AOPsProperties();
            aopXMEnableScreenRecording.setAOPs(_aops);
            aopXMEnableScreenRecording.setConfKey(AOPsProperties.Keys.XM_EnableScreenRecording.name());
            aopXMEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMEnableScreenRecording));
        }
        else
        {
            aopXMEnableScreenRecording.setConfValue(val.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMEnableScreenRecording));
        }
    }

    private void setXTScreenRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.ScreenRecordingMode recmode = (value == null || value.isEmpty() ? null : AOPsProperties.ScreenRecordingMode.valueOf(value));

            AOPsProperties aopXTEnableScreenRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_ScreenRecordingMode));
            if (aopXTEnableScreenRecordingMode == null)
            {
                aopXTEnableScreenRecordingMode = new AOPsProperties();
                aopXTEnableScreenRecordingMode.setAOPs(_aops);
                aopXTEnableScreenRecordingMode.setConfKey(AOPsProperties.Keys.XT_ScreenRecordingMode.name());
                aopXTEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTEnableScreenRecordingMode));
            }
            else
            {
                aopXTEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTEnableScreenRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XT_ScreenRecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setXCHScreenRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.ScreenRecordingMode recmode = (value == null || value.isEmpty() ? null : AOPsProperties.ScreenRecordingMode.valueOf(value));

            AOPsProperties aopXCHEnableScreenRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XCH_ScreenRecordingMode));
            if (aopXCHEnableScreenRecordingMode == null)
            {
                aopXCHEnableScreenRecordingMode = new AOPsProperties();
                aopXCHEnableScreenRecordingMode.setAOPs(_aops);
                aopXCHEnableScreenRecordingMode.setConfKey(AOPsProperties.Keys.XCH_ScreenRecordingMode.name());
                aopXCHEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXCHEnableScreenRecordingMode));
            }
            else
            {
                aopXCHEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXCHEnableScreenRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XCH_ScreenRecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setXVDScreenRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.ScreenRecordingMode recmode = (value == null || value.isEmpty() ? null : AOPsProperties.ScreenRecordingMode.valueOf(value));

            AOPsProperties aopXVDEnableScreenRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVD_ScreenRecordingMode));
            if (aopXVDEnableScreenRecordingMode == null)
            {
                aopXVDEnableScreenRecordingMode = new AOPsProperties();
                aopXVDEnableScreenRecordingMode.setAOPs(_aops);
                aopXVDEnableScreenRecordingMode.setConfKey(AOPsProperties.Keys.XVD_ScreenRecordingMode.name());
                aopXVDEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVDEnableScreenRecordingMode));
            }
            else
            {
                aopXVDEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVDEnableScreenRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XVD_ScreenRecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setXEMScreenRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.ScreenRecordingMode recmode = (value == null || value.isEmpty() ? null : AOPsProperties.ScreenRecordingMode.valueOf(value));

            AOPsProperties aopXEMEnableScreenRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XEM_ScreenRecordingMode));
            if (aopXEMEnableScreenRecordingMode == null)
            {
                aopXEMEnableScreenRecordingMode = new AOPsProperties();
                aopXEMEnableScreenRecordingMode.setAOPs(_aops);
                aopXEMEnableScreenRecordingMode.setConfKey(AOPsProperties.Keys.XEM_ScreenRecordingMode.name());
                aopXEMEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXEMEnableScreenRecordingMode));
            }
            else
            {
                aopXEMEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXEMEnableScreenRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XEM_ScreenRecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setXSOScreenRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.ScreenRecordingMode recmode = (value == null || value.isEmpty() ? null : AOPsProperties.ScreenRecordingMode.valueOf(value));

            AOPsProperties aopXSOEnableScreenRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XSO_ScreenRecordingMode));
            if (aopXSOEnableScreenRecordingMode == null)
            {
                aopXSOEnableScreenRecordingMode = new AOPsProperties();
                aopXSOEnableScreenRecordingMode.setAOPs(_aops);
                aopXSOEnableScreenRecordingMode.setConfKey(AOPsProperties.Keys.XSO_ScreenRecordingMode.name());
                aopXSOEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXSOEnableScreenRecordingMode));
            }
            else
            {
                aopXSOEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXSOEnableScreenRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XSO_ScreenRecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setXMScreenRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.ScreenRecordingMode recmode = (value == null || value.isEmpty() ? null : AOPsProperties.ScreenRecordingMode.valueOf(value));

            AOPsProperties aopXMEnableScreenRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XM_ScreenRecordingMode));
            if (aopXMEnableScreenRecordingMode == null)
            {
                aopXMEnableScreenRecordingMode = new AOPsProperties();
                aopXMEnableScreenRecordingMode.setAOPs(_aops);
                aopXMEnableScreenRecordingMode.setConfKey(AOPsProperties.Keys.XM_ScreenRecordingMode.name());
                aopXMEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXMEnableScreenRecordingMode));
            }
            else
            {
                aopXMEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXMEnableScreenRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XM_ScreenRecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setXWScreenRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.ScreenRecordingMode recmode = (value == null || value.isEmpty() ? null : AOPsProperties.ScreenRecordingMode.valueOf(value));

            AOPsProperties aopXWEnableScreenRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XW_ScreenRecordingMode));
            if (aopXWEnableScreenRecordingMode == null)
            {
                aopXWEnableScreenRecordingMode = new AOPsProperties();
                aopXWEnableScreenRecordingMode.setAOPs(_aops);
                aopXWEnableScreenRecordingMode.setConfKey(AOPsProperties.Keys.XW_ScreenRecordingMode.name());
                aopXWEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXWEnableScreenRecordingMode));
            }
            else
            {
                aopXWEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXWEnableScreenRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XW_ScreenRecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setXVTScreenRecordingMode(String value) throws GravityException, CODEException
    {
        try
        {
            AOPsProperties.ScreenRecordingMode recmode = (value == null || value.isEmpty() ? null : AOPsProperties.ScreenRecordingMode.valueOf(value));

            AOPsProperties aopXVTEnableScreenRecordingMode = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XVT_ScreenRecordingMode));
            if (aopXVTEnableScreenRecordingMode == null)
            {
                aopXVTEnableScreenRecordingMode = new AOPsProperties();
                aopXVTEnableScreenRecordingMode.setAOPs(_aops);
                aopXVTEnableScreenRecordingMode.setConfKey(AOPsProperties.Keys.XVT_ScreenRecordingMode.name());
                aopXVTEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXVTEnableScreenRecordingMode));
            }
            else
            {
                aopXVTEnableScreenRecordingMode.setConfValue(recmode.name());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXVTEnableScreenRecordingMode));
            }

        }
        catch (Exception e)
        {
            throw new GravityIllegalArgumentException(AOPsProperties.Keys.XVT_ScreenRecordingMode.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setXTOBRouteToCDN(String value) throws GravityException, CODEException
    {

        //change aops to Campaign
        if (_aops.getAOPsType().equals(AOPsType.Process))
        {
            throw new GravityIllegalArgumentException("Found " + _aops.getAOPsType().name() + " But Expected " + AOPsType.Campaign);
        }

        Campaign camp = (Campaign) _aops;
        //get Process from this campaign
        Process process = camp.getProcess();
        //fetch cdn
        _tctx.getDB().FindAssert(new AOPsCDNQuery().filterByCode(value));

        if (process == null)
        {

            throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ProcessNotMappedWithCampaign, "Campaign id " + camp.getId() );
        }

        AOPsCDN cdn = _tctx.getDB().Find(new AOPsCDNQuery().filterByAOPs(process.getId()).filterByCode(value));

        if (cdn == null)
        {
            throw new GravityEntityNotFoundException(EN.AOPsCDN.name(), "Campaign.Process.AOPsCDN = " + value);
        }
        AOPsProperties aopXTOBRouteToCDN = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_RouteToCDN));
        if (aopXTOBRouteToCDN == null)
        {
            aopXTOBRouteToCDN = new AOPsProperties();
            aopXTOBRouteToCDN.setAOPs(_aops);
            aopXTOBRouteToCDN.setConfKey(AOPsProperties.Keys.XT_OB_RouteToCDN.name());
            aopXTOBRouteToCDN.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopXTOBRouteToCDN));
        }
        else
        {
            aopXTOBRouteToCDN.setConfValue(value);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopXTOBRouteToCDN));
        }
        /*
        * we can add XT_OB_RouteToCDN or XT_OB_RouteToAddress at a time one so if add cdn we can remove address  
        */
        AOPsProperties addressproperties = _tctx.getDB().Find(new AOPsPropertiesQuery().filterByAOPs(_aops.getId()).filterByConfKey(AOPsProperties.Keys.XT_OB_RouteToAddress));
        if (addressproperties != null)
        {
            entities.add(new NameValuePair(ENActionList.Action.Delete.name(), addressproperties));
        }
    }
}
