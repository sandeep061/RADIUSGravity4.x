/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.db;

import CrsCde.CODE.Common.Classes.NameValuePair;
import CrsCde.CODE.Common.Enums.OPRelational;
import CrsCde.CODE.Common.Utils.TypeUtil;
import code.common.exceptions.CODEEntityNotFoundException;
import code.common.exceptions.CODEException;
import code.db.jpa.ENActionList;
import code.db.jpa.JPAQuery;
import code.db.jpa.mysql.MySQLem;
import code.entities.*;
import jakarta.persistence.PersistenceException;
import ois.cc.gravity.AppProps;
import ois.cc.gravity.db.queries.EntityQuery;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.radius.cc.entities.AEntity_cc;
import ois.radius.cc.entities.AEntity_ccad;
import ois.radius.cc.entities.AEntity_cces;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.sys.Tenant;
import ois.radius.cc.entities.tenant.cc.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class MySQLDB
{

    private MySQLem _em;
    private Tenant _ctclient;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final SessionFactory _sessionFactory;

    public MySQLDB(Tenant client, ArrayList<Class> enclsnames) throws Exception
    {
        this._ctclient = client;
//        EMFProperties emfprop = new EMFProperties();
//        emfprop.setShow_sql(Boolean.TRUE);
//        emfprop.setUse_ssl(Boolean.FALSE);
//        emfprop.setId_new_generator_mappings(Boolean.TRUE);
//        emfprop.setConnPoolMaxSize(8);

        String schema = ClientDBName(client);
        createDBIfNotExist(schema);

        _sessionFactory = this.createSessionFactory(schema, enclsnames);
        ThreadLocalSessionContext.bind(_sessionFactory.openSession());

        _em = new MySQLem(_sessionFactory);

    }

    private SessionFactory createSessionFactory(String schema, ArrayList<Class> encls)
    {
        Configuration configuration = getHBConfigs(schema, encls);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    private Configuration getHBConfigs(String schema, ArrayList<Class> enclsnames)
    {
        Properties properties = getEMFProps(schema);

        Configuration configuration = new Configuration();
        configuration.setProperties(properties);

        enclsnames.forEach(cls ->
        {
            configuration.addAnnotatedClass(cls);
        });

        return configuration;
    }

    /**
     * Setting all Entity Manger Factory Properties
     *
     * @param schema
     * @return
     */
    private Properties getEMFProps(String schema)
    {
        Properties properties = new Properties();
        // Database connection settings
        properties.setProperty(Environment.JAKARTA_JDBC_DRIVER, "com.mysql.jdbc.Driver");
        properties.setProperty(Environment.JAKARTA_JDBC_URL, "jdbc:mysql://" + AppProps.RAD_DB_IP + ":" + AppProps.RAD_DB_PORT + "/" + schema + "?autoReconnect=true");
        properties.setProperty(Environment.JAKARTA_JDBC_USER, AppProps.RAD_DB_USER);
        properties.setProperty(Environment.JAKARTA_JDBC_PASSWORD, AppProps.RAD_DB_PWD);

        // Hibernate connection settings
        properties.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty(Environment.SHOW_SQL, "true");
        properties.setProperty(Environment.HBM2DDL_AUTO, "update");
        properties.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

        // set connection pool
        properties.setProperty(Environment.CONNECTION_PROVIDER, "org.hibernate.connection.C3P0ConnectionProvider");
        properties.setProperty(Environment.C3P0_MIN_SIZE, "8");
        properties.setProperty(Environment.C3P0_MAX_SIZE, "128");
        properties.setProperty(Environment.C3P0_MAX_STATEMENTS, "16");
        properties.setProperty(Environment.C3P0_IDLE_TEST_PERIOD, "300"); // Tests every 5 minutes
        properties.setProperty(Environment.C3P0_TIMEOUT, "300"); // Seconds a connection can be idle

        properties.setProperty("hibernate.c3p0.acquireRetryAttempts", "3"); // Retry 3 times if connection fails
        properties.setProperty("hibernate.c3p0.acquireRetryDelay", "1000"); // 1 second between retries
        properties.setProperty("hibernate.c3p0.testConnectionOnCheckout", "true");
        properties.setProperty("hibernate.c3p0.testConnectionOnCheckin", "true");
        properties.setProperty("hibernate.c3p0.preferredTestQuery", "SELECT 1");
        properties.setProperty("hibernate.c3p0.validate", "true");

        // DATASource Property Setting
        DataSource dataSource = createDataSource(AppProps.RAD_DB_IP, AppProps.RAD_DB_PORT, schema, AppProps.RAD_DB_USER, AppProps.RAD_DB_PWD);
        properties.put(Environment.JAKARTA_JTA_DATASOURCE, dataSource);
        return properties;
    }

    /**
     * Creating Databse Schema if Not Exist
     *
     * @param schema
     * @throws Exception
     */
    private void createDBIfNotExist(String schema) throws Exception
    {
        logger.trace("Creating  DB Schema :  ", schema);
        String url = "jdbc:mysql://" + AppProps.RAD_DB_IP + ":" + AppProps.RAD_DB_PORT;
        String sql = "CREATE DATABASE IF NOT EXISTS " + schema.toLowerCase();
        try (Connection conn = DriverManager.getConnection(url, AppProps.RAD_DB_USER, AppProps.RAD_DB_PWD); PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.execute();
        }
        catch (Exception e)
        {
            logger.error("Error caught during CreateDB " + e);
            throw e;
        }
    }

    /**
     * Building DataSourse Object
     *
     * @param ip
     * @param port
     * @param schemaname
     * @param username
     * @param password
     * @return
     */
    private DataSource createDataSource(String ip, int port, String schemaname, String username, String password)
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + schemaname + "?autoReconnect=true";
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver"); // Set the driver class name
        return dataSource;
    }

    public Integer SelectCount(EntityQuery enqry) throws CODEException, GravityException
    {
        ArrayList<Long> arr = new ArrayList<>();
        try
        {
            arr = _em.Select(enqry.toCount());
        }
        catch (Exception ex)
        {
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new CODEException(ex);
        }

        if (arr.isEmpty())
        {
            return 0;
        }
        return Integer.valueOf(arr.get(0).toString());
    }

    public <T extends Object> T SelectScalar(JPAQuery query) throws CODEException, GravityException
    {
        ArrayList<T> rs;
        try
        {
            rs = _em.Select(query);

            if (rs.isEmpty() || rs.get(0) == null)
            {
                return null;
            }
            if (rs.size() > 1)
            {
                throw new CODEException("Expecting single value but found a list.");
            }

            Class contentType = rs.get(0).getClass();
            if (!TypeUtil.IsPrimeType(contentType) && !contentType.isEnum())
            {
                throw new IllegalArgumentException("Only primitive and Date type result set expected.");
            }
        }
        catch (Exception ex)
        {
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new CODEException(ex);
        }

        return rs.get(0);
    }

    public AEntity Insert(User user, AEntity entity) throws GravityException, CODEException
    {
        logger.trace("Insert Entity ", entity);
        try
        {
            EntityProcessor.InsertPreProcess(user, entity);
            return _em.Insert(entity);
        }
        catch (PersistenceException ex)
        {
            logger.error("Error caught during Insert " + ex);
            throw DBExceptionHelper.BuildGravityException(ex);
        }
        catch (Exception ex)
        {
            throw new CODEException(ex);
        }
    }

    public void Insert(User user, AEntity... entities) throws GravityException, CODEException
    {
        logger.trace("Insert Entities ", (Object) entities);
        try
        {
            Stream.of(entities).forEach((e) ->
            {
                EntityProcessor.InsertPreProcess(user, e);
            });
            _em.Insert(entities);
        }
        catch (PersistenceException ex)
        {
            logger.error("Error caught during Insert " + ex);

            throw DBExceptionHelper.BuildGravityException(ex);
        }
        catch (Exception ex)
        {
            throw new CODEException(ex);
        }

    }

    public void Insert(User user, ArrayList<? extends AEntity> entities) throws GravityException, CODEException
    {
        logger.trace("Insert Entities ", entities);
        try
        {
            entities.stream().forEach((e) ->
            {
                EntityProcessor.InsertPreProcess(user, (AEntity) e);
            });
            _em.Insert(entities);
        }
        catch (PersistenceException ex)
        {
            logger.error("Error caught during Insert " + ex);
            throw DBExceptionHelper.BuildGravityException(ex);
        }
        catch (Exception ex)
        {
            throw new CODEException(ex);
        }

    }

    public void Update(User user, ArrayList<? extends AEntity> entities) throws GravityException, CODEException
    {
        logger.trace("Update Entities ", entities);
        try
        {
            entities.stream().forEach((e) ->
            {
                EntityProcessor.UpdatePreProcess(user, (AEntity) e);
            });
            _em.Update(entities);
        }
        catch (PersistenceException ex)
        {
            logger.error("Error caught during Update " + ex);
            throw DBExceptionHelper.BuildGravityException(ex);
        }
        catch (Exception ex)
        {
            throw new CODEException(ex);
        }

    }

    public <T extends AEntity> ArrayList<T> Select(Class type, JPAQuery query, int retry) throws GravityException, CODEException
    {
        try
        {
            return _em.Select(type, query);
        }
        catch (Exception ex)
        {
            logger.error("Error caught during Select " + ex);
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new CODEException(ex);
        }
    }

    /**
     * Execute query and return list of objects.
     *
     * @param <T>
     * @param query
     * @return
     * @throws GravityException
     * @throws code.common.exceptions.CODEException
     */
    public <T extends AEntity> ArrayList<T> Select(EntityQuery query) throws GravityException, CODEException
    {
        return Select(query.getEn(), query.toSelect());
    }

    public <T extends AEntity> ArrayList<T> Select(EN en, JPAQuery query) throws GravityException, CODEException
    {
        try
        {
            return _em.Select(en.getClass(), query);
        }
        catch (Exception ex)
        {
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new CODEException(ex);
        }

    }

    public <T extends AEntity> ArrayList<T> Select(Class type, JPAQuery query) throws GravityException, CODEException
    {
        try
        {
            return _em.Select(type, query);
        }
        catch (Exception ex)
        {
            logger.error("Error caught during Select " + ex);
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new GravityException(ex);
        }

    }

    public List Select(JPAQuery query) throws GravityException, CODEException
    {
        try
        {
            return _em.Select(query);
        }
        catch (Exception ex)
        {
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new CODEException(ex);
        }

    }

    /**
     * Execute query and return only one object.
     *
     * @param <T>
     * @param en
     * @param query
     * @return
     * @throws code.common.exceptions.CODEException
     * @throws ois.cc.gravity.services.exceptions.GravityException
     */
    public <T extends AEntity> T Find(EN en, JPAQuery query) throws CODEException, GravityException
    {
        try
        {
            IAEntity ent = _em.Find(en.getEntityClass(), query);

            if (ent != null && ((ent instanceof AEntity_ad entity && !entity.getDeleted()) || (ent instanceof AEntity_es enes && enes.getEntityState().equals(EntityState.Active)) || (ent instanceof AEntity_cc)))

            {
                return (T) ent;
            }

        }
        catch (Exception ex)
        {
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new CODEException(ex);
        }

        return null;
    }

    /**
     * Execute query and return only one object.
     *
     * @param <T>
     * @param encls
     * @param query
     * @return
     * @throws CODEException
     * @throws ois.cc.gravity.services.exceptions.GravityException
     */
    public <T extends AEntity> T Find(Class encls, JPAQuery query) throws CODEException, GravityException
    {
        try
        {
            IAEntity ien = _em.Find(encls, query);
            if (ien != null && ((ien instanceof AEntity_ad entity && !entity.getDeleted()) || (ien instanceof AEntity_es enes && enes.getEntityState().equals(EntityState.Active)) || (ien instanceof AEntity_cc)))
            {
                return (T) ien;
            }

        }
        catch (Exception ex)
        {
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new CODEException(ex);
        }

        return null;
    }

    public <T extends AEntity> T FindAssert(Class encls, Long id) throws CODEException, GravityException
    {
        T t = Find(encls, id);
        if (t == null)
        {
            throw new CODEEntityNotFoundException(encls.getSimpleName(),"Id", OPRelational.Eq,id);
        }

        return t;
    }

    public <T extends AEntity> T FindAssert(EntityQuery enqry) throws GravityException, CODEException
    {
        T t = Find(enqry);
        if (t == null)
        {
            throw new CODEEntityNotFoundException(enqry.getEn().getEntityClass().getSimpleName(),enqry.toSelect().getQryStr());
        }

        return t;
    }

    public <T extends AEntity> T FindAssert(EN en, JPAQuery qry) throws CODEException, GravityException, Exception
    {
        T t = Find(en, qry);
        if (t == null)
        {
            throw new CODEEntityNotFoundException(en.getEntityClass().getSimpleName(),qry.getQryStr());
        }

        return t;
    }

    public <T extends AEntity> T Find(EntityQuery query) throws GravityException, CODEException
    {
        try
        {
            return Find(query.getEn(), query.toSelect());
        }
        catch (PersistenceException | IllegalArgumentException ex)
        {
            throw ex;
        }
    }

    public <T> T Find(Class en, Long id) throws GravityException
    {
        logger.trace("Find Entity ", en, id);
        try
        {
            IAEntity ent = _em.Find(en, id);
            if (ent instanceof AEntity_ad entity && !entity.getDeleted()) {
                return (T) ent;
            }

           else if (ent instanceof AEntity_cces enes && enes.getEntityState().equals(EntityState.Active)) {
                return (T) ent;
            }
            else if (ent instanceof AEntity_cc) {
                return (T) ent;
            }



            return null;

        }
        catch (PersistenceException ex)
        {
            logger.error("Error caught during Find " + ex);
            throw new GravityException(ex);
        }

    }

    public void Update(User user, AEntity entity) throws CODEException, GravityException
    {
        logger.trace("Updating the Entity : ", entity);
        try
        {
            EntityProcessor.UpdatePreProcess(user, entity);
            _em.Update(entity);
        }
        catch (PersistenceException ex)
        {
            logger.error("Error caught during Update " + ex);

            throw DBExceptionHelper.BuildGravityException(ex);
        }
        catch (Exception ex)
        {
            throw new CODEException(ex);
        }

    }

    /**
     * Perform Insert, Update and Delete on set of entities under single transaction.
     *
     * @param user
     * @param entities
     * @throws ois.cc.gravity.services.exceptions.GravityException
     */
    public void Insert_Update_Delete_OneTransact(User user, ArrayList<NameValuePair> entities) throws GravityException, CODEException
    {
        ENActionList enActList = new ENActionList();
        ArrayList<AEntity> aEntities = new ArrayList<>();

        for (NameValuePair nmvl : entities)
        {
            if (nmvl.getValue() != null)
            {
                String name = (String) nmvl.getName();
                switch (name)
                {
                    case "Insert":
                        if (nmvl.getValue().getClass().isArray())
                        {
                            AEntity[] ents = (AEntity[]) nmvl.getValue();
                            for (AEntity e : ents)
                            {
                                EntityProcessor.InsertPreProcess(user, e);
                                enActList.Add(ENActionList.Action.Insert, e);
                            }
                        }
                        else
                        {
                            AEntity e = (AEntity) nmvl.getValue();
                            EntityProcessor.InsertPreProcess(user, e);
                            enActList.Add(ENActionList.Action.Insert, e);
                        }
                        break;
                    case "Update":
                        if (nmvl.getValue().getClass().isArray())
                        {
                            AEntity[] ents = (AEntity[]) nmvl.getValue();
                            for (AEntity e : ents)
                            {
                                EntityProcessor.UpdatePreProcess(user, e);
                                enActList.Add(ENActionList.Action.Update, e);
                            }
                        }
                        else
                        {
                            AEntity e = (AEntity) nmvl.getValue();
                            EntityProcessor.UpdatePreProcess(user, e);
                            enActList.Add(ENActionList.Action.Update, e);
                        }
                        break;
                    case "Delete":
                        if (nmvl.getValue().getClass().isArray())
                        {
                            AEntity[] ents = (AEntity[]) nmvl.getValue();
                            for (AEntity e : ents)
                            {
                                e = Find(e.getClass(), e.getId());
                                if (e instanceof AEntity_ad)
                                {
                                    EntityProcessor.DeletePreProcess(user, e);
                                    enActList.Add(ENActionList.Action.Update, e);
                                }
                                else if (e instanceof AEntity_es)
                                {

//                                    EntityProcessor.DeletePreProcess(user, e);
//                                    enActList.Add(ENActionList.Action.Update, e);
                                    if (!aEntities.contains(e))
                                    {
                                        EntityProcessor.DeletePreProcess(user, e);
                                        enActList.Add(ENActionList.Action.Update, e);
                                    }
                                    else
                                    {
                                        enActList.Add(ENActionList.Action.Delete, e);
                                    }
                                }
                                else if (e instanceof AEntity_cc)
                                {
                                    enActList.Add(ENActionList.Action.Delete, e);
                                }
                            }
                        }
                        else
                        {
                            AEntity ent = (AEntity) nmvl.getValue();
                            ent = Find(nmvl.getValue().getClass(), ent.getId());
                            if (ent instanceof AEntity_ad )
                            {
                                EntityProcessor.DeletePreProcess(user, ent);
                                enActList.Add(ENActionList.Action.Update, ent);
                            }
                            else if (ent instanceof AEntity_es)
                            {
                                if (!aEntities.contains(ent))
                                {
                                    EntityProcessor.DeletePreProcess(user, ent);
                                    enActList.Add(ENActionList.Action.Update, ent);
                                }
                                else
                                {
                                    enActList.Add(ENActionList.Action.Delete, ent);
                                }
//                                EntityProcessor.DeletePreProcess(user, ent);
//                                enActList.Add(ENActionList.Action.Update, ent);
                            }
                            else if (ent instanceof AEntity_cc)
                            {
                                enActList.Add(ENActionList.Action.Delete, ent);
                            }
                        }
                        break;
                }
            }
        }

        try
        {
            _em.Insert_Update_Delete_OneTransact(enActList);
        }
        catch (Exception ex)
        {
            List<AEntity> updatedEnts = new ArrayList<>();
            for (NameValuePair nm : entities)
            {
                if (nm.getName().equals("Update"))
                {
                    Object value = nm.getValue();

                    if (value.getClass().isArray())
                    {
                        AEntity[] arrayObj = (AEntity[]) value;
                        updatedEnts.addAll(Arrays.asList(arrayObj));
                    }
                    else
                    {
                        AEntity en = (AEntity) value;
                        updatedEnts.add(en);
                    }

                }
            }

            if (!updatedEnts.isEmpty())
            {
                throw DBExceptionHelper.Handle_Update_GravityExceptions(this, ex, updatedEnts.toArray(new AEntity[0]));
            }
            throw DBExceptionHelper.BuildGravityException(ex);
        }
    }

//    public void DeleteFromDb(User user, ArrayList<? extends AEntity> entities) throws CODEException, GravityException
//    {
//        try
//        {
//            _em.Delete(entities);
//        }
//        catch (Exception e)
//        {
//            throw DBExceptionHelper.BuildGravityException(e);
//        }
//    }

//    public void DeleteFromDb(User user, AEntity_cces ent) throws CODEException, GravityException
//    {
//
//        try
//        {
//            _em.Delete(ent);
//        }
//
//        catch (Exception ex)
//        {
//            throw DBExceptionHelper.BuildGravityException(ex);
//        }
//    }

//    public void DeleteUpdate(User user, ArrayList<? extends AEntity> entities) throws CODEException, GravityException
//    {
//        ArrayList<AEntity> entList = new ArrayList<>();
//        try
//        {
//            entities.forEach(ent ->
//            {
//                EntityProcessor.DeletePreProcess(user, ent);
//                entList.add(ent);
//            });
//
//            Update(user, entList);
//        }
//        catch (Exception ex)
//        {
//            throw DBExceptionHelper.BuildGravityException(ex);
//        }
//    }
//
//    public void DeleteUpdate(User user, AEntity ent) throws CODEException, GravityException
//    {
//
//        try
//        {
//            EntityProcessor.DeletePreProcess(user, ent);
//            Update(user, ent);
//        }
//        catch (Exception ex)
//        {
//            throw DBExceptionHelper.BuildGravityException(ex);
//        }
//    }

    public <T extends AEntity> T FindFromDB(Class encls,JPAQuery query) throws GravityException, CODEException
    {
        try
        {
            IAEntity ien = _em.Find(encls, query);
            if (ien != null && ((ien instanceof AEntity_ad entity ) || (ien instanceof AEntity_es enes) || (ien instanceof AEntity_cc)))
            {
                return (T) ien;
            }

        }
        catch (Exception ex)
        {
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new CODEException(ex);
        }

        return null;
    }
    public void DeleteEntity(User user, AEntity ent) throws CODEException, GravityException, Exception
    {
        ArrayList<AEntity> aEntities = new ArrayList<>();
        if (ent instanceof AEntity_ad)
        {
            EntityProcessor.DeletePreProcess(user, ent);
            Update(user, ent);
        }
        else if (ent instanceof AEntity_es )
        {
            if (!aEntities.contains(ent))
            {
                EntityProcessor.DeletePreProcess(user, ent);
                Update(user, ent);
            }
            else
            {
                _em.Delete(ent);
            }
        }
        else
        {
            _em.Delete(ent);
        }

    }

    public void DeleteEntities(User user, ArrayList<AEntity> entes) throws CODEException, GravityException, Exception
    {
        ArrayList<NameValuePair> entities =new ArrayList<>();
        entities.add(new NameValuePair("Delete", entes.toArray(new AEntity[0])));
        Insert_Update_Delete_OneTransact( user,  entities);
    }

    public <T extends Object> ArrayList<T> SelectList(JPAQuery query) throws CODEException, GravityException
    {
        ArrayList<T> arr;
        try
        {
            arr = _em.Select(query);

            if (arr.isEmpty() || arr.get(0) == null)
            {
                return null;
            }

            Class contentType = arr.get(0).getClass();
            if (!TypeUtil.IsPrimeType(contentType) && !contentType.isEnum())
            {
                throw new IllegalArgumentException("Only primitive and Date type result set expected.");
            }
        }
        catch (Exception ex)
        {
//            throw DBExceptionHelper.BuildCODEException(ex);
            logger.error("Error caught during Find " + ex);
            if (ex instanceof PersistenceException)
            {
                throw DBExceptionHelper.BuildGravityException(ex);
            }
            throw new GravityException(ex);
        }

        return arr;
    }

    public MySQLem getMsqlem(){
        return _em;
    }

    /**
     * Closes the underlying database connections.
     */
    public void Close()
    {
        _em.Close();
    }

    private String ClientDBName(Tenant client)
    {
        return "ccdb_" + client.getCode().toLowerCase();
    }

}
