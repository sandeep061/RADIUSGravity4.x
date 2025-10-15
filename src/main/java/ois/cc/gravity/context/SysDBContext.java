package ois.cc.gravity.context;

import code.common.exceptions.CODEException;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.db.MySQLDBFactory;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.radius.cc.entities.sys.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SysDBContext
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public SysDBContext()
    {

    }

    /**
     * As a practice the steps/logic/codes done here should be completely independent of any other class. <br>
     * Ideally this class must be the first to be executed before doing anything else while starting the server.
     *
     * @throws Exception
     * @throws org.vn.radius.cc.platform.exceptions.RADException
     */
    public MySQLDB TheFirstDBInit() throws Exception, CODEException, GravityException
    {

        /**
         * Get dummy ctclient entity and init db connections. We need dummy here because we can't fetch real ctclient since db is not connected.
         */
        Tenant sysclient = ServerContext.DummySysClient();
        return MySQLDBFactory.CreateGravitySysDB(sysclient);

    }

}
