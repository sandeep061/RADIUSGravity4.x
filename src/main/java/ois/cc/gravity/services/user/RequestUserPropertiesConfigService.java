package ois.cc.gravity.services.user;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Utils.TypeUtil;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.ua.events.*;
import code.ua.requests.Request;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserProperties;
import ois.cc.gravity.Limits;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.db.queries.UserPropertiesQuery;
import ois.cc.gravity.framework.requests.user.RequestUserPropertiesConfig;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import org.vn.radius.cc.platform.exceptions.RADException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static ois.radius.cc.entities.tenant.cc.UserProperties.Keys.XVT_TaskLimit;

public class RequestUserPropertiesConfigService extends ARequestEntityService
{

    public RequestUserPropertiesConfigService(UAClient uac)
    {
        super(uac);
    }

    private final ArrayList<NameValuePair> entities = new ArrayList<>();

    private User _user;

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Throwable
    {

        RequestUserPropertiesConfig req = (RequestUserPropertiesConfig) request;
        MySQLDB db = _tctx.getDB();

        /**
         * - Check user is there or not in Gravity db. -- If not found then fetch form nucleus. --- If not found in nucleus then throw exception.
         */
        _user = _tctx.getNucleusCtx().GetUserById(_tctx.getTenant().getCode(), req.getUserId(), UserRole.Agent.name());

        /**
         * Find the AgentProperties entity for this Agent, if not found create new.
         */
//        UserProperties.getHmProps().clear();
        HashMap<String, String> hmAttr = req.getAttributes();
        UserProperties agProp = db.Find(EN.UserProperties, new UserPropertiesQuery().filterByUser(_user.getId()).toSelect());
        if (agProp == null)
        {
            InitAgentProperties(_user, hmAttr);
        }

        configUserProps(_user, hmAttr);
        _tctx.getDB().Insert_Update_Delete_OneTransact(_uac.getUserSession().getUser(), entities);

        return new EventSuccess(req);
    }

    private void InitAgentProperties(User ag, HashMap<String, String> hmattr)
    {
        List<UserProperties.Keys> keysToCheck = List.of(
                UserProperties.Keys.Global_TaskLimit,
                UserProperties.Keys.Preview_Limit,
                UserProperties.Keys.XC_TaskLimit,
                UserProperties.Keys.XT_TaskLimit,
                UserProperties.Keys.XV_TaskLimit,
                UserProperties.Keys.XM_TaskLimit,
                UserProperties.Keys.XE_TaskLimit,
                UserProperties.Keys.XW_TaskLimit,
                UserProperties.Keys.XS_TaskLimit
        );

        for (UserProperties.Keys key : keysToCheck)
        {
            if (!hmattr.containsKey(key.name()))
            {
                UserProperties agProps = new UserProperties();
                agProps.setUser(ag);
                agProps.setConfKey(key.name());
                agProps.setConfValue("8");
                entities.add(new NameValuePair("Insert", agProps));
            }
        }
    }

    private void configUserProps(User ag, HashMap<String, String> hmattrs) throws GravityException, RADException, CODEException, Exception
    {

        for (String key : hmattrs.keySet())
        {
            UserProperties.Keys upKey = UserProperties.Keys.valueOf(key);
            switch (upKey)
            {
                case Global_TaskLimit:
                    setGlobal_TaskLimit(hmattrs.get(key));
                    break;
                case XT_TaskLimit:
                    setXT_TaskLimit(hmattrs.get(key));
                    break;
                case XC_TaskLimit:
                    setXC_TaskLimit(hmattrs.get(key));
                    break;
                case XE_TaskLimit:
                    setXE_TaskLimit(hmattrs.get(key));
                    break;
                case XM_TaskLimit:
                    setXE_TaskLimit(hmattrs.get(key));
                    break;
                case XS_TaskLimit:
                    setXS_TaskLimit(hmattrs.get(key));
                    break;
                case XV_TaskLimit:
                    setXV_TaskLimit(hmattrs.get(key));
                    break;
                case Preview_Limit:
                    setPreview_Limit(hmattrs.get(key));
                    break;
                case XW_TaskLimit:
                    setXW_TaskLimit(hmattrs.get(key));
                    break;
            }
        }
    }

    private void setGlobal_TaskLimit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userGTasklimit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.Global_TaskLimit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userGTasklimit == null)
            {
                userGTasklimit = new UserProperties();
                userGTasklimit.setUser(_user);
                userGTasklimit.setConfKey(UserProperties.Keys.Global_TaskLimit.name());
                userGTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userGTasklimit));
            }
            else
            {
                userGTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userGTasklimit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.Global_TaskLimit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }

    private void setXT_TaskLimit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userXTTasklimit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.XT_TaskLimit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userXTTasklimit == null)
            {
                userXTTasklimit = new UserProperties();
                userXTTasklimit.setUser(_user);
                userXTTasklimit.setConfKey(UserProperties.Keys.XT_TaskLimit.name());
                userXTTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userXTTasklimit));
            }
            else
            {
                userXTTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userXTTasklimit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.XT_TaskLimit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }

    private void setXC_TaskLimit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userXCTasklimit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.XC_TaskLimit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userXCTasklimit == null)
            {
                userXCTasklimit = new UserProperties();
                userXCTasklimit.setUser(_user);
                userXCTasklimit.setConfKey(UserProperties.Keys.XC_TaskLimit.name());
                userXCTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userXCTasklimit));
            }
            else
            {
                userXCTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userXCTasklimit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.XC_TaskLimit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }

    private void setXE_TaskLimit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userXETasklimit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.XE_TaskLimit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userXETasklimit == null)
            {
                userXETasklimit = new UserProperties();
                userXETasklimit.setUser(_user);
                userXETasklimit.setConfKey(UserProperties.Keys.XE_TaskLimit.name());
                userXETasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userXETasklimit));
            }
            else
            {
                userXETasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userXETasklimit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.XE_TaskLimit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }

    private void setXM_TaskLimit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userXTTasklimit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.XM_TaskLimit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userXTTasklimit == null)
            {
                userXTTasklimit = new UserProperties();
                userXTTasklimit.setUser(_user);
                userXTTasklimit.setConfKey(UserProperties.Keys.XM_TaskLimit.name());
                userXTTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userXTTasklimit));
            }
            else
            {
                userXTTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userXTTasklimit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.XM_TaskLimit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }

    private void setXV_TaskLimit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userXVTasklimit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.XV_TaskLimit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userXVTasklimit == null)
            {
                userXVTasklimit = new UserProperties();
                userXVTasklimit.setUser(_user);
                userXVTasklimit.setConfKey(UserProperties.Keys.XV_TaskLimit.name());
                userXVTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userXVTasklimit));
            }
            else
            {
                userXVTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userXVTasklimit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.XV_TaskLimit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }

    private void setXS_TaskLimit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userXSTasklimit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.XS_TaskLimit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userXSTasklimit == null)
            {
                userXSTasklimit = new UserProperties();
                userXSTasklimit.setUser(_user);
                userXSTasklimit.setConfKey(UserProperties.Keys.XS_TaskLimit.name());
                userXSTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userXSTasklimit));
            }
            else
            {
                userXSTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userXSTasklimit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.XS_TaskLimit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }

    private void setPreview_Limit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userPreview_Limit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.Preview_Limit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userPreview_Limit == null)
            {
                userPreview_Limit = new UserProperties();
                userPreview_Limit.setUser(_user);
                userPreview_Limit.setConfKey(UserProperties.Keys.Preview_Limit.name());
                userPreview_Limit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userPreview_Limit));
            }
            else
            {
                userPreview_Limit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userPreview_Limit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.Preview_Limit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }

    private void setXW_TaskLimit(String value) throws GravityException, CODEException, Exception
    {

        UserProperties userXWTasklimit = _tctx.getDB().Find(new UserPropertiesQuery().filterByUserId(_user.getUserId()).filterByConfKey(UserProperties.Keys.XW_TaskLimit));

        Integer input = null;
        try
        {
            input = TypeUtil.ValueOf(Integer.class, value);
            if (input == null || input <= 0 || input > Limits.Agent_Max_Task_Limit)
            {
                throw new Exception();
            }
            if (userXWTasklimit == null)
            {
                userXWTasklimit = new UserProperties();
                userXWTasklimit.setUser(_user);
                userXWTasklimit.setConfKey(UserProperties.Keys.XW_TaskLimit.name());
                userXWTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Insert.name(), userXWTasklimit));
            }
            else
            {
                userXWTasklimit.setConfValue(input.toString());
                entities.add(new NameValuePair(ENActionList.Action.Update.name(), userXWTasklimit));
            }
        }
        catch (Exception ex)
        {
            String msg = "TaskLimit value must be between 1 and " + Limits.Agent_Max_Task_Limit;
            GravityIllegalArgumentException rex = new GravityIllegalArgumentException(msg, UserProperties.Keys.XW_TaskLimit.name(), EventFailedCause.ValueOutOfRange);
            throw rex;
        }
    }


}
