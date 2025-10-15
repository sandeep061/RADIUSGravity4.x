package ois.cc.gravity.services.xs;

import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.JSONUtil;
import code.entities.AEntity;
import ois.cc.gravity.db.queries.XServerQuery;
import ois.cc.gravity.services.exceptions.GravityEntityExistsException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.tenant.cc.XServer;
import ois.radius.cc.entities.tenant.cc.XServerEndpointProperties;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.common.RequestEntityEditService;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestXServerEditService extends RequestEntityEditService {
    public RequestXServerEditService(UAClient uac) {
        super(uac);
    }
    @Override
    protected void DoPreProcess(RequestEntityEdit req, AEntity thisentity) throws Throwable
    {
        MySQLDB db = _tctx.getDB();
        XServer xserver = db.FindAssert(EN.XServer.getEntityClass(), req.getEntityId());

          if(req.getAttributes().containsKey("Name"))
        {
            XServerQuery query = new XServerQuery().filterByName(req.getAttributeValueOf(String.class,"Name"));
            XServer xserverdb=_tctx.getDB().Find(query);
            if(xserverdb!=null){
                throw new GravityEntityExistsException(EN.XServer.name(), "Name", OPRelational.Eq, xserverdb.getName());
            }
        }

//        AIXServer aixserver = _tctx.getXServerStore().GetById(xserver.getId());
//        //we are assuming xserver will removed from store.
//        if (aixserver != null)
//        {
//            throw new RADIllegalObjectStateException(EN.XServer.getEntityClass(), xserver.getId(), aixserver.getRtXServer().getProviderState(), ProviderState.Shutdown);
//        }

        /**
         * Note : EndpointProps will process in AttributeCollectionAppend and AttributeCollectionRemove rather Attributes.
         */
        if (req.getAttributes().containsKey("EndPointProps"))
        {
            req.getAttributes().remove("EndPointProps");
        }
    }

    @Override
    protected void appendAttribute(RequestEntityEdit req, AEntity entity) throws NoSuchFieldException, GravityUnhandledException, Exception, GravityIllegalArgumentException
    {
        XServer xs = (XServer) entity;
        HashMap<String, ArrayList<String>> attributeCollectionAppend = req.getAttributeCollectionAppend();
        ArrayList<String> endPntVal = attributeCollectionAppend.get("EndPointTypeProps");
//        EndPointType.ConfigKey endPntKey = null;
//        String confkey = null;
//        for (String val : endPntVal)
//        {
//            JSONObject endpointPropsJson = new JSONObject(val);
//            for (String key : endpointPropsJson.keySet())
//            {
//                try
//                {
                    ArrayList<XServerEndpointProperties> endpointprops = new ArrayList<>();
                    for(String str:endPntVal)
                    {
                        JSONObject obj=new JSONObject(str);
                        XServerEndpointProperties prop = JSONUtil.FromJSON(obj, XServerEndpointProperties.class);
                        endpointprops.add(prop);
//                    EndPointType endpntType = EndPointType.valueOf(key);
//                    Properties prop = new Properties();
//                    if (!endpntType.equals(EndPointType.ExternalPhone))
//                    {
//                        //We don't excepct any value for ExternalPhone endpoint,so other than ExternalPhone we are expecting properties.
//                        prop = JSONUtil.FromJSON(endpointPropsJson.getJSONObject(key), Properties.class);
//                    }
//
//                    for (Object propKey : prop.keySet())
//                    {
//                        confkey = propKey.toString();
//                        endPntKey = EndPointType.ConfigKey.valueOf(propKey.toString());
//                        if (!endPntKey.getEndPoint().equals(endpntType))
//                        {
//                            //TBD:Need to throw a appropeate exceptions.
//                        }
                    }
                    xs.AddEndpointProps(endpointprops);
//                }
//                catch (IllegalArgumentException ex)
//                {
//                    if (ex.getMessage().equals("No enum constant ois.radius.ca.enums.EndPointType" + endPntKey))
//                    {
//                        throw new GravityIllegalArgumentException(key + " is not valid EndPointType", "EndPointProps", EvCauseRequestValidationFail.ParamValueOutOfRange);
//                    }
//                    else
//                    {
//                        throw new GravityIllegalArgumentException(confkey + " is not valid EndPointType.ConfigKey", "EndPointProps", EvCauseRequestValidationFail.ParamValueOutOfRange);
//                    }
//                }

//            }
//        }
    }

    @Override
    protected void removeAttribute(RequestEntityEdit req, AEntity entity) throws NoSuchFieldException, GravityUnhandledException, Exception, GravityIllegalArgumentException
    {
        XServer xs = (XServer) entity;
        HashMap<String, ArrayList<String>> attributeCollectionRemove = req.getAttributeCollectionRemove();
        ArrayList<String> endpints = attributeCollectionRemove.get("EndPointTypeProps");
        for (String val : endpints)
        {
            JSONObject endpointPropsJson = new JSONObject(val);
//            for (String key : endpointPropsJson.keySet())
//            {
            XServerEndpointProperties prop = JSONUtil.FromJSON(endpointPropsJson, XServerEndpointProperties.class);

//            EndPointType endpntType = null;
//                try
//                {
//                    endpntType = EndPointType.valueOf(key);
//                }
//                catch (IllegalArgumentException ex)
//                {
//                    throw new GravityIllegalArgumentException(key + " is not valid EndPointType", "EndPointProps", EvCauseRequestValidationFail.ParamValueOutOfRange);
//                }

            prop.setXServer(null);
//            }
        }
    }
}
