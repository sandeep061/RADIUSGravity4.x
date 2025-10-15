package ois.cc.gravity.services.common;

import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.ReflUtils;
import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityEdited;
import code.ua.events.EventEntityNotFound;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.services.exceptions.GravityUniqueConstraintViolationException;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.requests.common.RequestEntityEdit;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import org.vn.radius.cc.platform.exceptions.RADException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class RequestEntityEditService extends ARequestEntityService
{

    public RequestEntityEditService(UAClient uac)
    {
        super(uac);
    }

    //    protected CoreDB _coreDB = _cctx.getCoreDB();
    protected AEntity _thisEntity = null;

    @Override
    protected Event DoProcessEntityRequest(Request request) throws Exception, GravityUnhandledException, Throwable
    {
        RequestEntityEdit reqEdit = (RequestEntityEdit) request;

        EN en = reqEdit.getEntityName();
        if (en == null)
        {
            EventInvalidEntity ev = new EventInvalidEntity(request, reqEdit.getEntityName().name());
            return ev;
        }

        //Find _thisEntity by Id supplied.
        this._thisEntity = _tctx.getDB().Find(en.getEntityClass(), reqEdit.getEntityId());
        if (_thisEntity == null)
        {
            EventEntityNotFound ev = new EventEntityNotFound(request, reqEdit.getEntityName().name());
            ev.setCondition("Id", OPRelational.Eq, String.valueOf(reqEdit.getEntityId()));
            return ev;
        }

        DoPreProcess(reqEdit, _thisEntity);
        //Set updated attribute values.
        try
        {
            if (reqEdit.getAttributes() != null && !reqEdit.getAttributes().isEmpty())
            {
                EntityBuilder.BuildEntity(_tctx.getDB(), _thisEntity, reqEdit.getAttributes());
            }
        }
        catch (GravityNoSuchFieldException radex)
        {
            throw new GravityNoSuchFieldException(radex, _thisEntity.getEN().name(), radex.getFlieldName());
        }

        /**
         * @since v:13092019.<br>
         * Mapping of collection attributes to entity will be done in RequestEntityEdit. No need to maintain separate request class.<br>
         * Previously we are maintaining a separate class(RequestEntityAttributeListEdit) but for same functionality we need not maintain two request.
         */
        if (reqEdit.getAttributeCollectionRemove() != null && !reqEdit.getAttributeCollectionRemove().isEmpty())
        {
            removeAttribute(reqEdit, _thisEntity);
        }

        if (reqEdit.getAttributeCollectionAppend() != null && !reqEdit.getAttributeCollectionAppend().isEmpty())
        {
            appendAttribute(reqEdit, _thisEntity);
        }

        DoPostBuildProcess(reqEdit, _thisEntity);

        try
        {
            _tctx.getDB().Update(_uac.getUserSession().getUser(), _thisEntity);
        }
        catch (Throwable e)
        {

            if (e instanceof GravityUniqueConstraintViolationException ex)
            {
                _tctx.getDB().Find(en.getEntityClass(), reqEdit.getEntityId());
                throw new GravityUniqueConstraintViolationException(e.getCause(), ex.getEntityName(), ex.getCondition());
            }
            throw e;

        }

        DoPostUpdateProcess(reqEdit, _thisEntity);

        EventEntityEdited ev = new EventEntityEdited(request, _thisEntity);
        return ev;
    }

    protected void removeAttribute(RequestEntityEdit req, AEntity entity) throws NoSuchFieldException, GravityException, Exception, RADException, CODEException
    {
        String key = Arrays.asList(req.getAttributeCollectionRemove().keySet().toArray()).get(0).toString();

        Field fld = ReflUtils.GetField(entity.getClass(), key);
        ArrayList<String> alValues = req.getAttributeCollectionRemove().get(fld.getName());
        EntityBuilder.RemoveFromAttributeList(_tctx.getDB(), entity, fld, alValues);
    }

    protected void appendAttribute(RequestEntityEdit req, AEntity entity) throws NoSuchFieldException, GravityUnhandledException, Exception, RADException, CODEException, GravityException
    {
        String key = Arrays.asList(req.getAttributeCollectionAppend().keySet().toArray()).get(0).toString();

        Field fld = ReflUtils.GetField(entity.getClass(), key);
        ArrayList<String> alValues = req.getAttributeCollectionAppend().get(fld.getName());
        EntityBuilder.AppendToAttributeList(_tctx.getDB(), entity, fld, alValues);
    }

    /**
     * Facilitate any required pre-processing by specific(derived) _thisEntity add processor.
     *
     * @param reqenedit
     * @param thisentity
     * @throws java.lang.Exception
     */
    protected void DoPreProcess(RequestEntityEdit reqenedit, AEntity thisentity) throws Throwable
    {
        //do nothing. to be imple by derived class.
        //not abstract becaue we want this generic EntityAddProcessor class to be non-abstract class.
    }

    /**
     * Entity has been built, but not inserted to DB yet.
     *
     * @param entity
     * @param reqenedit
     * @throws java.lang.Exception
     */
    protected void DoPostBuildProcess(RequestEntityEdit reqenedit, AEntity entity) throws Throwable
    {
        //do nothing. to be imple by derived class.
        //not abstract becaue we want this generic EntityAddProcessor class to be non-abstract class.
    }

    /**
     * Entity inserted to DB ie Id of the _thisEntity is generated. <br>
     * This method shall not throw any exception as _thisEntity is already inserted by this time.
     *
     * @param reqenedit
     * @param entity
     * @throws java.lang.Throwable
     */
    protected void DoPostUpdateProcess(RequestEntityEdit reqenedit, AEntity entity) throws Throwable
    {
        //do nothing. to be imple by derived class.
        //not abstract becaue we want this generic EntityAddProcessor class to be non-abstract class.

    }

}
