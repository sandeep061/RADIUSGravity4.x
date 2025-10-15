package ois.cc.gravity.db;

import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.EnumUtil;
import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.entities.IEntity;
import code.ua.events.EventFailedCause;

import java.util.logging.Level;

import ois.cc.gravity.AppConst;
import ois.cc.gravity.services.exceptions.*;
import ois.radius.cc.entities.EN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DBExceptionHelper //this class must be internal to db pkg.
{

    private static final Logger _logger = LoggerFactory.getLogger(DBExceptionHelper.class);

    public static GravityException Handle_Update_GravityExceptions(MySQLDB db, Throwable ex, AEntity... entities) throws GravityException, CODEException
    {
        for (IEntity e : entities)
        {
            try
            {
//                db.Refresh(e);
            }
            catch (Exception rex)
            {
                throw new GravityException(rex);
            }
        }
        return BuildGravityException(ex);
    }

    public static GravityException BuildGravityException(Throwable ex) throws CODEException
    {
        GravityException rex = null;

        //Exception.getMessage is mandatory.
        if (ex.getMessage() == null)
        {
            return new GravityUnhandledException(ex);
        }

        rex = IsUniqueConstarinatException(ex);
        if (rex != null)
        {
            return rex;
        }
        rex = IsQuerySyntaxException(ex);
        if (rex != null)
        {
            return rex;
        }

        rex = IsUnKnownColoumnException(ex);
        if (rex != null)
        {
            return rex;
        }

        rex = IsNotNullException(ex);
        if (rex != null)
        {
            return rex;
        }
        return rex == null ? new GravityUnhandledException(ex) : rex;

    }

    private static GravityUniqueConstraintViolationException IsUniqueConstarinatException(Throwable ex) throws CODEException
    {

        /**
         * Sample error message in case of unique constraints violation. - <br>
         * 1. could not execute statement [Duplicate entry 'QA_AESPOM3' for key 'xserver.uk_xserver_Code'] [insert into xserver
         * (AuthParams,Channel,Code,CreatedBy,CreatedOn,Deleted,DeletedId,Description,EditedBy,EditedOn,Name,ProviderID) values (?,?,?,?,?,?,?,?,?,?,?,?)]
         *
         * 2. could not execute statement [Duplicate entry '4-SANDEEP5' for key 'disposition.uk_disposition_code_AOPs'] [insert into Disposition<br>
         *
         * 3.could not execute statement [Duplicate entry '1-Call-sandeep' for key 'dncaddress.uk_dncddress_dnclist_channel_address'] [insert into dncaddress
         * (Address,Channel,CreatedBy,CreatedOn,DNCList,EditedBy,EditedOn,EntityState) values (?,?,?,?,?,?,?,?)]
         */
        if (AppConst.DB_PROD.equals(AppConst.DBProd.MySQL))
        {
            String errMsg = ex.getMessage();

            if (errMsg.contains("Duplicate entry"))
            {

                // Extract entity or table name from the key if applicable
                try
                {
                    int start = errMsg.indexOf("Duplicate entry");
                    int end = errMsg.indexOf("]", start);
                    String key = errMsg.substring(errMsg.indexOf("for key '") + 9, errMsg.indexOf("']"));
                    String entity = key.split("\\.")[0];  // Extract entity or table name from the key if applicable
                    EN en = EnumUtil.ValueOf(EN.class, entity);

                    String condition = errMsg.substring(start, end);
                    // Create and return the custom exception
                    return new GravityUniqueConstraintViolationException(ex, en.name(), condition);

                }
                catch (Exception ex1)
                {
                    throw new CODEException(ex1);
                }
            }
        }
        return null;  // Return null if the exception is not a unique constraint violation
    }

    //TBD
    private static GravityException IsQuerySyntaxException(Throwable ex)
    {
        if (AppConst.DB_PROD.equals(AppConst.DBProd.MySQL))
        {
            /**
             * Sample error message in case of syntax exception - <br>
             * Failed to commit transaction: Unique constraint (RetriveObjectDB.TestDB[phone]) failed: Attempt to reuse an existing value ('56564545') (error
             * 613)
             */
            String errMsg = ex.getMessage();
            if (errMsg.contains("Failed to commit transaction: Unique constraint"))
            {
                String column = errMsg.substring(errMsg.indexOf('[') + 1, errMsg.lastIndexOf("]"));
                String value = errMsg.substring(errMsg.indexOf("('") + 2, errMsg.lastIndexOf("')"));
                String condition = column + OPRelational.Eq.Symbol() + value;

                return new GravityException(condition);
            }
        }

        return null;
    }

    /**
     * @param ex
     * @return
     */
    private static GravityNoSuchFieldException IsUnKnownColoumnException(Throwable ex)
    {
        if (AppConst.DB_PROD.equals(AppConst.DBProd.MySQL))
        {
            /**
             * Sample error message in case of UnKnown Column exception - <br>
             * - Field 'code' is not found in type 'RetriveObjectDB.TestDB' (error 761) <br>
             * - Field 'Name' is not found in type 'org.vn.radius.cc.server.entities.client.Agent' <br>
             *
             * Exception in thread "main" org.hibernate.exception.SQLGrammarException: JDBC exception executing SQL [select
             * c1_0.Id,c1_0.AOPSTypes,c1_0.Code,c1_0.CreatedBy,c1_0.CreatedOn,c1_0.Deleted,c1_0.DeletedId,c1_0.Description,c1_0.EditedBy,c1_0.EditedOn,c1_0.Name,p1_0.Id,p1_0.AOPSTypes,p1_0.Code,p1_0.CreatedBy,p1_0.CreatedOn,p1_0.Deleted,p1_0.DeletedId,p1_0.Description,p1_0.EditedBy,p1_0.EditedOn,p1_0.Name,p1_0.ProcessType
             * from aops c1_0 left join (select * from aops t where t.DTYPE='Process') p1_0 on p1_0.Id=c1_0.Process_Id where c1_0.DTYPE='Campaign' and
             * c1_0.Id=?] [Unknown column 'c1_0.Description' in 'field list'] [n/a] at
             * org.hibernate.exception.internal.SQLExceptionTypeDelegate.convert(SQLExceptionTypeDelegate.java:66)
             */

            String errMsg = ex.getMessage();
            if (errMsg.contains("Unknown column"))
            {
                String colName = errMsg.substring(errMsg.indexOf("'") + 1).substring(0, errMsg.indexOf("' in"));

                int fromIndex = errMsg.indexOf("from ");
                int start = fromIndex + 5;
                int end = errMsg.indexOf(" ", start);
                String entityName = errMsg.substring(start, end);

                GravityNoSuchFieldException radex = new GravityNoSuchFieldException(ex, entityName, colName);
                return radex;
            }
        }

        return null;
    }

    private static GravityIllegalArgumentException IsNotNullException(Throwable ex)
    {
        /**
         * Sample error message in case of adding null in NotNull filed of entity. - <br>
         * Failed to commit transaction: Invalid null value in non optional field LoginId
         */

        if (AppConst.DB_PROD.equals(AppConst.DBProd.MySQL))
        {

            String errorMsg = ex.getMessage();
            if (errorMsg.contains("not-null property references a null"))
            {
                String field = errorMsg.substring(errorMsg.indexOf("tenant.cc.") + 10, errorMsg.length());
                GravityIllegalArgumentException e = new GravityIllegalArgumentException(field + " cannot be null", field, EventFailedCause.NonOptionalConstraintViolation);
                return e;
            }
        }

        return null;
    }

}
