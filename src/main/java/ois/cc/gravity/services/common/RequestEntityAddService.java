package ois.cc.gravity.services.common;

import code.entities.AEntity;
import code.ua.events.Event;
import code.ua.events.EventEntityAdded;
import code.ua.events.EventEntityExists;
import code.ua.events.EventInvalidEntity;
import code.ua.requests.Request;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.ua.UAClient;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.entities.util.EntityBuilder;
import ois.cc.gravity.framework.requests.common.RequestEntityAdd;
import ois.cc.gravity.services.ARequestEntityService;
import ois.cc.gravity.services.exceptions.GravityInstantiationException;
import ois.cc.gravity.services.exceptions.GravityNoSuchFieldException;
import ois.cc.gravity.services.exceptions.GravityUnhandledException;
import ois.cc.gravity.services.exceptions.GravityUniqueConstraintViolationException;

public class RequestEntityAddService extends ARequestEntityService
{


    public RequestEntityAddService(UAClient uac)
    {
        super(uac);
    }

    //Attributed used in derived classes.
    protected EN _thisen;
    protected MySQLDB _coreDB = _tctx.getDB();

    @Override
    public Event DoProcessEntityRequest(Request request) throws Throwable
    {
        RequestEntityAdd reqAdd=(RequestEntityAdd) request;
        _thisen =  reqAdd.getEntityName();

        if (_thisen == null)
        {
            EventInvalidEntity ev =  new EventInvalidEntity(request, reqAdd.getEntityName().name());
            return ev;
        }

        //pre insert processing by sub request processor. this is not same as pre insert processing done at EntityAdded.
        DoPreProcess(reqAdd);

        AEntity entity = null;
        try
        {
            entity = EntityBuilder.New(_thisen);
            EntityBuilder.BuildEntity(_tctx.getDB(), entity, reqAdd.getAttributes());

            DoPostBuildProcess(reqAdd, entity);

//            _coreDB.Insert(_uac.getUserSession().getUser(), entity);
            _tctx.getDB().Insert(_uac.getUserSession().getUser(),entity);
        }
        catch (GravityInstantiationException | GravityUnhandledException iex)
        {

            throw iex;
        }
        catch (GravityNoSuchFieldException fex)
        {

//            EventAttributeInvalid ev = new EventAttributeInvalid(request,reqAdd.getEntityName().name() , fex.getFlieldName());
            throw fex;
        }
        catch (GravityUniqueConstraintViolationException uex)
        {
            EventEntityExists evEntityExist = new EventEntityExists(request, _thisen.toString());
            evEntityExist.setCondition(uex.getCondition());
            return evEntityExist;
        }

        //will be implemented by derived classes.
        DoPostInsertProcess(reqAdd, entity);

        EventEntityAdded ev = new EventEntityAdded(request, entity);
        return ev;
    }

    /**
     * Facilitate any required pre-processing by specific(derived) entity add processor.
     *
     * @param reqenadd
     * @throws java.lang.Exception
     */
    protected void DoPreProcess(RequestEntityAdd reqenadd) throws Throwable
    {
        //do nothing. to be imple by derived class.
        //not abstract becaue we want this generic EntityAddProcessor class to be non-abstract class.
    }

    /**
     * Entity has been built, but not inserted to DB yet.
     *
     * @param entity
     * @param reqenadd
     * @throws java.lang.Exception
     */
    protected void DoPostBuildProcess(RequestEntityAdd reqenadd, AEntity entity) throws Throwable
    {
        //do nothing. to be imple by derived class.
        //not abstract becaue we want this generic EntityAddProcessor class to be non-abstract class.
    }

    /**
     * Entity inserted to DB ie Id of the entity is generated. <br>
     *
     * This method shall not throw any exception as entity is already inserted by this time.
     *
     * @param reqenadd
     * @param entity
     * @throws java.lang.Throwable
     */
    protected void DoPostInsertProcess(RequestEntityAdd reqenadd, AEntity entity) throws Throwable
    {
        //do nothing. to be imple by derived class.
        //not abstract becaue we want this generic EntityAddProcessor class to be non-abstract class.

    }

//	@Override
//	protected Event DoProcessRequest(Request request) throws Throwable
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}

}
