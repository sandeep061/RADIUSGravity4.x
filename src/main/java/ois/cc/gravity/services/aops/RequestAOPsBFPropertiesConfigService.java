package ois.cc.gravity.services.aops;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Utils.DATEUtil;
import CrsCde.CODE.Common.Utils.JSONUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.ua.events.Event;
import code.ua.events.EventFailedCause;
import code.ua.events.EventSuccess;
import code.ua.requests.Request;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.db.queries.AOPsBFPropertiesQuery;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.framework.requests.aops.RequestAOPsBFPropertiesConfig;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.ca.enums.CrossCXMapType;
import ois.radius.ca.enums.aops.CallbackCause;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.AOPsBF;
import ois.radius.cc.entities.tenant.cc.AOPsBFProperties;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import ois.cc.gravity.Limits;
import ois.cc.gravity.db.queries.CrossCXContactMapQuery;
import ois.cc.gravity.services.exceptions.EvCauseRuntimeCheckFailed;
import ois.cc.gravity.services.exceptions.GravityRuntimeCheckFailedException;
import ois.radius.cc.entities.tenant.cc.CrossCXContactMap;

public class RequestAOPsBFPropertiesConfigService extends ARequestEntityService
{

    private AOPsBF _aopsbf;
    private final ArrayList<NameValuePair> entities = new ArrayList<>();

    public RequestAOPsBFPropertiesConfigService(UAClient uac)
    {
        super(uac);
    }

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestAOPsBFPropertiesConfig req = (RequestAOPsBFPropertiesConfig) request;
        MySQLDB db = _tctx.getDB();
        _aopsbf = db.FindAssert(EN.AOPsBF.getEntityClass(), req.getAOPsBF());

        HashMap<String, String> hmAttr = req.getAttributes();

        ValidateAndConfKey(hmAttr);

        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        return new EventSuccess(req);

    }

    private void ValidateAndConfKey(HashMap<String, String> keytocheck) throws CODEException, Exception, GravityException
    {
        for (String key : keytocheck.keySet())
        {
            AOPsBFProperties.Key aopsbfkey = AOPsBFProperties.Key.valueOf(key);
            switch (aopsbfkey)
            {
                case CIRCLE_AllowedCallbackCauses:
                    setCIRCLE_AllowedCallbackCauses(keytocheck.get(key));
                    break;
                case CIRCLE_MaxScheduled:
                    setCIRCLE_MaxScheduled(keytocheck.get(key));
                    break;
                case CIRCLE_RouteAddress:
                    setCIRCLERoutAddress(keytocheck.get(key));
                    break;
                case CIRCLE_ScheduleOnAfterDialFailed:
                    setCIRCLE_ScheduleOnAfterDialFailed(keytocheck.get(key));
                    break;
                case CIRCLE_RouteAddressType:
                    setCIRCLERoutAddressType(keytocheck.get(key));
                    break;
                case CIRCLE_DialAfter:
                    setCIRCLE_DialAfter(keytocheck.get(key));
                    break;
                case CrossCX_MapTimeout:
                    setCrossCXMapTimeout(keytocheck.get(key));
                    break;
                case CrossCX_MapDIDs:
                    setCrossCXMapDIDs(keytocheck.get(key));
                    break;
                case CrossCX_SecondaryContact_Endpoint:
                    setCrossCXSecondaryContactEndpoint(keytocheck.get(key));
                    break;
                case CrossCX_MapType:
                    setCrossCXMapType(keytocheck.get(key));
                    break;
                case CrossCX_MapPINLen:
                    setCrossCXMapPINLen(keytocheck.get(key));
                    break;
                case CrossCX_Ch_TempalteMsg:
                    setCrossCX_Ch_TemplateMsg(keytocheck.get(key));
                    break;
            }
        }
    }

    private void setCIRCLE_AllowedCallbackCauses(String callbackcause) throws GravityException, Exception, CODEException
    {
        try
        {
            if (callbackcause == null || callbackcause.trim().isEmpty())
            {

                throw new GravityIllegalArgumentException("Invalid callbackcause", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
            }
            String[] arrCl = callbackcause.split(",");
            ArrayList<CallbackCause> alClbkcause = (ArrayList<CallbackCause>) Arrays.asList(arrCl).stream().map(clc -> CallbackCause.valueOf(clc.trim())).collect(Collectors.toList());
            ArrayList<String> al = JSONUtil.FromJSON(alClbkcause.toString(), ArrayList.class);
            JSONArray alcausearray = new JSONArray(al);
            AOPsBFProperties aopbfCallbackCauses = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CIRCLE_AllowedCallbackCauses));
            if (aopbfCallbackCauses == null)
            {
                aopbfCallbackCauses = new AOPsBFProperties();
                aopbfCallbackCauses.setAOPsBF(_aopsbf);
                aopbfCallbackCauses.setConfKey(AOPsBFProperties.Key.CIRCLE_AllowedCallbackCauses.name());
                aopbfCallbackCauses.setConfValue(alcausearray.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopbfCallbackCauses));
            }
            else
            {
                aopbfCallbackCauses.setConfValue(alcausearray.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopbfCallbackCauses));
            }

        }
        catch (IllegalArgumentException e)
        {
            throw new GravityIllegalArgumentException(AOPsBFProperties.Key.CIRCLE_AllowedCallbackCauses.name(), EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }

    }

    private void setCIRCLE_MaxScheduled(String get) throws GravityException, CODEException
    {
        try
        {
            int val = Integer.parseInt(get);
            if (val > 1024)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, AOPsBFProperties.Key.CIRCLE_MaxScheduled.name() + " value Should not be Grater than 1024");
            }
            AOPsBFProperties aopbfmaxscheduled = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CIRCLE_MaxScheduled));
            if (aopbfmaxscheduled == null)
            {
                aopbfmaxscheduled = new AOPsBFProperties();
                aopbfmaxscheduled.setAOPsBF(_aopsbf);
                aopbfmaxscheduled.setConfKey(AOPsBFProperties.Key.CIRCLE_MaxScheduled.name());
                aopbfmaxscheduled.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopbfmaxscheduled));
            }
            else
            {
                aopbfmaxscheduled.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopbfmaxscheduled));
            }
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Integer type", AOPsBFProperties.Key.CIRCLE_MaxScheduled.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setCIRCLERoutAddress(String get) throws GravityException, CODEException
    {

        AOPsBFProperties aopCIRCLERoutAddress = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CIRCLE_RouteAddress));
        if (aopCIRCLERoutAddress == null)
        {
            aopCIRCLERoutAddress = new AOPsBFProperties();
            aopCIRCLERoutAddress.setAOPsBF(_aopsbf);
            aopCIRCLERoutAddress.setConfKey(AOPsBFProperties.Key.CIRCLE_RouteAddress.name());
            aopCIRCLERoutAddress.setConfValue(get);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCIRCLERoutAddress));
        }
        else
        {
            aopCIRCLERoutAddress.setConfValue(get);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCIRCLERoutAddress));
        }

    }

    private void setCIRCLE_ScheduleOnAfterDialFailed(String get) throws GravityException, CODEException
    {
        try
        {
            int val = Integer.parseInt(get);
            if (val<Limits.ContactScheduledOnAfter_MIN || val > Limits.ContactScheduledOnAfter_MAX)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, AOPsBFProperties.Key.CIRCLE_ScheduleOnAfterDialFailed.name() + " value Should not be Grater than 184319");
            }

            AOPsBFProperties aopCIRCLE_ScheduleOnAfterDialFailed = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CIRCLE_ScheduleOnAfterDialFailed));
            if (aopCIRCLE_ScheduleOnAfterDialFailed == null)
            {
                aopCIRCLE_ScheduleOnAfterDialFailed = new AOPsBFProperties();
                aopCIRCLE_ScheduleOnAfterDialFailed.setAOPsBF(_aopsbf);
                aopCIRCLE_ScheduleOnAfterDialFailed.setConfKey(AOPsBFProperties.Key.CIRCLE_ScheduleOnAfterDialFailed.name());
                aopCIRCLE_ScheduleOnAfterDialFailed.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCIRCLE_ScheduleOnAfterDialFailed));
            }
            else
            {
                aopCIRCLE_ScheduleOnAfterDialFailed.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCIRCLE_ScheduleOnAfterDialFailed));
            }
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Integer type", AOPsBFProperties.Key.CIRCLE_ScheduleOnAfterDialFailed.name(), EventFailedCause.ValueOutOfRange);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void setCIRCLERoutAddressType(String get) throws GravityException, CODEException
    {

        AOPsBFProperties aopCIRCLERoutAddressType = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CIRCLE_RouteAddressType));
        if (aopCIRCLERoutAddressType == null)
        {

            aopCIRCLERoutAddressType = new AOPsBFProperties();
            aopCIRCLERoutAddressType.setAOPsBF(_aopsbf);
            aopCIRCLERoutAddressType.setConfKey(AOPsBFProperties.Key.CIRCLE_RouteAddressType.name());
            aopCIRCLERoutAddressType.setConfValue(RequestAOPsBFPropertiesConfigService.CIRCLE_RoutAddressType.valueOf(get).toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCIRCLERoutAddressType));
        }
        else
        {
            aopCIRCLERoutAddressType.setConfValue(RequestAOPsBFPropertiesConfigService.CIRCLE_RoutAddressType.valueOf(get).toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCIRCLERoutAddressType));
        }

    }

    private void setCIRCLE_DialAfter(String get) throws GravityException, CODEException
    {
        try
        {
            AOPsBFProperties aopCIRCLE_DialAfter = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CIRCLE_DialAfter));
            if (aopCIRCLE_DialAfter == null)
            {
                aopCIRCLE_DialAfter = new AOPsBFProperties();
                aopCIRCLE_DialAfter.setAOPsBF(_aopsbf);
                aopCIRCLE_DialAfter.setConfKey(AOPsBFProperties.Key.CIRCLE_DialAfter.name());
                aopCIRCLE_DialAfter.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCIRCLE_DialAfter));
            }
            else
            {
                aopCIRCLE_DialAfter.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCIRCLE_DialAfter));
            }
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Integer type", AOPsBFProperties.Key.CIRCLE_DialAfter.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setCrossCXMapTimeout(String get) throws GravityException, CODEException
    {
        try
        {
            int timeoutmin = Integer.parseInt(get);
            if (timeoutmin < 5)
            {
                throw new GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed.ParamValueOutOfRange, " CrossCX_MapTimeout Key value should be grater than 5");
            }
            AOPsBFProperties aopCrossCXMapTimeout = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapTimeout));
            if (aopCrossCXMapTimeout == null)
            {
                aopCrossCXMapTimeout = new AOPsBFProperties();
                aopCrossCXMapTimeout.setAOPsBF(_aopsbf);
                aopCrossCXMapTimeout.setConfKey(AOPsBFProperties.Key.CrossCX_MapTimeout.name());
                aopCrossCXMapTimeout.setConfValue(Integer.valueOf(get).toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCrossCXMapTimeout));
            }
            else
            {
                aopCrossCXMapTimeout.setConfValue(Integer.valueOf(get).toString());
                UpdateExparyTimeout(Integer.parseInt(get), _aopsbf.getAOPs().getId());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCrossCXMapTimeout));

            }
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Integer type", AOPsBFProperties.Key.CrossCX_MapTimeout.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setCrossCXMapType(String get) throws GravityException, CODEException
    {

        CrossCXMapType.valueOf(get);
        AOPsBFProperties aopCrossCXMapType = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapType));
        if (aopCrossCXMapType == null)
        {
            aopCrossCXMapType = new AOPsBFProperties();
            aopCrossCXMapType.setAOPsBF(_aopsbf);
            aopCrossCXMapType.setConfKey(AOPsBFProperties.Key.CrossCX_MapType.name());
            aopCrossCXMapType.setConfValue(get);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCrossCXMapType));
        }
        else
        {
            aopCrossCXMapType.setConfValue(get);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCrossCXMapType));
        }

    }

    private void setCrossCXMapDIDs(String dids) throws GravityException, CODEException
    {

        if (dids == null || dids.trim().isEmpty())
        {

            throw new GravityIllegalArgumentException("Invalid dids", EventFailedCause.ValueOutOfRange, EvCauseRequestValidationFail.InvalidParamName);
        }
        List<String> aldids = Arrays.asList(dids.split(","));
        JSONArray aldidarray = new JSONArray(aldids);
        AOPsBFProperties aopCrossCX_MapDIDs = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapDIDs));
        if (aopCrossCX_MapDIDs == null)
        {
            aopCrossCX_MapDIDs = new AOPsBFProperties();
            aopCrossCX_MapDIDs.setAOPsBF(_aopsbf);
            aopCrossCX_MapDIDs.setConfKey(AOPsBFProperties.Key.CrossCX_MapDIDs.name());
            aopCrossCX_MapDIDs.setConfValue(aldidarray.toString());
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCrossCX_MapDIDs));
        }
        else
        {
            aopCrossCX_MapDIDs.setConfValue(aldidarray.toString());
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCrossCX_MapDIDs));
        }
    }

    private void setCrossCXMapPINLen(String get) throws GravityException, CODEException
    {

        try
        {
            int pinlength = Integer.parseInt(get);

            if (pinlength < 3 || pinlength > 6)
            {
                throw new GravityIllegalArgumentException("value range must be 3 t0 6", AOPsBFProperties.Key.CrossCX_MapPINLen.name(), EventFailedCause.ValueOutOfRange);
            }
            AOPsBFProperties aopCrossCXMapPINLen = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CrossCX_MapPINLen));
            if (aopCrossCXMapPINLen == null)
            {
                aopCrossCXMapPINLen = new AOPsBFProperties();
                aopCrossCXMapPINLen.setAOPsBF(_aopsbf);
                aopCrossCXMapPINLen.setConfKey(AOPsBFProperties.Key.CrossCX_MapPINLen.name());
                aopCrossCXMapPINLen.setConfValue(get);
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCrossCXMapPINLen));
            }
            else
            {
                aopCrossCXMapPINLen.setConfValue(get);
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCrossCXMapPINLen));
            }
        }
        catch (NumberFormatException nfe)
        {
            throw new GravityIllegalArgumentException("value must be a Integer type", AOPsBFProperties.Key.CrossCX_MapPINLen.name(), EventFailedCause.ValueOutOfRange);
        }
    }

    private void setCrossCXSecondaryContactEndpoint(String get) throws GravityException, CODEException
    {

        AOPsBFProperties aopCrossCXSecondaryContactEndpoint = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CrossCX_SecondaryContact_Endpoint));
        if (aopCrossCXSecondaryContactEndpoint == null)
        {
            aopCrossCXSecondaryContactEndpoint = new AOPsBFProperties();
            aopCrossCXSecondaryContactEndpoint.setAOPsBF(_aopsbf);
            aopCrossCXSecondaryContactEndpoint.setConfKey(AOPsBFProperties.Key.CrossCX_SecondaryContact_Endpoint.name());
            aopCrossCXSecondaryContactEndpoint.setConfValue(get);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCrossCXSecondaryContactEndpoint));
        }
        else
        {
            aopCrossCXSecondaryContactEndpoint.setConfValue(get);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCrossCXSecondaryContactEndpoint));
        }

    }

    private void UpdateExparyTimeout(int minute, long aopid) throws GravityException, CODEException
    {
        ArrayList<CrossCXContactMap> crossconmaps = _tctx.getDB().Select(new CrossCXContactMapQuery().filterByAOPs(aopid));
        for (CrossCXContactMap crossconmap : crossconmaps)
        {
            crossconmap.setExpiryTime(DATEUtil.Add(crossconmap.getCreatedOn(), DATEUtil.Unit.MINUTE, minute));
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), crossconmap));
        }
    }

    private void setCrossCX_Ch_TemplateMsg(String get) throws GravityException, CODEException
    {

        AOPsBFProperties aopCrossCXChTempalteMsg = _tctx.getDB().Find(new AOPsBFPropertiesQuery().filterByAOPsBF(_aopsbf.getId()).filterByConfKey(AOPsBFProperties.Key.CrossCX_Ch_TempalteMsg));
        if (aopCrossCXChTempalteMsg == null)
        {
            aopCrossCXChTempalteMsg = new AOPsBFProperties();
            aopCrossCXChTempalteMsg.setAOPsBF(_aopsbf);
            aopCrossCXChTempalteMsg.setConfKey(AOPsBFProperties.Key.CrossCX_Ch_TempalteMsg.name());
            aopCrossCXChTempalteMsg.setConfValue(get);
            entities.add(new NameValuePair(ENActionList.Action.Insert.name(), aopCrossCXChTempalteMsg));
        }
        else
        {
            aopCrossCXChTempalteMsg.setConfValue(get);
            entities.add(new NameValuePair(ENActionList.Action.Update.name(), aopCrossCXChTempalteMsg));
        }

    }

    enum CIRCLE_RoutAddressType
    {
        Queue, CDN
    }
}
