///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package ois.cc.gravity.db;
//
//import CrsCde.CODE.Common.Classes.NameValuePair;
//import code.db.jpa.ENActionList;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
//import org.hibernate.SessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import code.db.jpa.JPAQuery;
//import code.entities.AEntity;
//import code.entities.IAEntity;
//import code.entities.IEntity;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.Query;
//import ois.cc.gravity.entities.util.HibernateUtil;
//import ois.radius.cc.entities.tenant.cc.User;
//
///**
// *
// * @author Manoj-PC
// * @since Sep 21, 2024
// */
//public class MySQLem
//{
//
//    private final Logger logger = LoggerFactory.getLogger(getClass());
//
//    SessionFactory _smf;
//
//    private final EntityManager _emInsert;
//    private final EntityManager _emSelect;
//    private final EntityManager _emUpdate;
//
//    public MySQLem(SessionFactory emf)
//    {
//        this._smf = emf;
//
//        this._emInsert = _smf.createEntityManager();
//        this._emSelect = _smf.createEntityManager();
//        this._emUpdate = _smf.createEntityManager();
//    }
//
//    public synchronized void Close()
//    {
//
//        if (_emInsert.isOpen())
//        {
//            _emInsert.close();
//        }
//        if (_emSelect.isOpen())
//        {
//            _emSelect.close();
//        }
//        if (_smf.isOpen())
//        {
//            _smf.close();
//        }
//    }
//
//    /**
//     * V:010421. <br>
//     * Common method to commit insert entity manager.
//     */
//    private void doCommitInsert()
//    {
//
//        try
//        {
//            if (!_emInsert.getTransaction().isActive())
//            {
//                _emInsert.getTransaction().begin();
//            }
//            _emInsert.getTransaction().commit();
//        }
//        finally
//        {
//            // Detach all merged entities.
//            _emInsert.clear();
//        }
//    }
//
//    /**
//     * Insert an single entity to Database. <br>
//     *
//     * Internally the entity is merged to select entity manager and returned. Caller method must hold the reference to the returned entity in case they want to
//     * do any further action on it.
//     *
//     * @param <T>
//     * @param entity
//     * @return
//     * @throws Exception
//     */
//    protected synchronized <T extends AEntity> T Insert(T entity) throws Exception
//    {
//        logger.trace(entity.toString());
//
//        try
//        {
//            _emInsert.persist(entity);
//
//            doCommitInsert();
//
//            IEntity dummy = _emSelect.merge(entity);
//            return (T) dummy;
//        }
//        catch (Exception e)
//        {
//            if (_emInsert.getTransaction().isActive())
//            {
//                _emInsert.getTransaction().rollback();
//            }
//            logger.error("Error caught during Insert " + e);
//            throw e;
//        }
//    }
//
//    protected synchronized <T extends IEntity> ArrayList<T> Insert(T... entities) throws Exception
//    {
//        return Insert(new ArrayList<>(Arrays.asList(entities)));
//    }
//
//    protected synchronized <T extends IEntity> ArrayList<T> Insert(ArrayList<T> entities) throws Exception
//    {
//        logger.trace(entities.toString());
//
//        ArrayList<T> merged = new ArrayList<>();
//        synchronized (_emInsert)
//        {
//            try
//            {
//                entities.stream().forEach((e) ->
//                {
//                    _emInsert.persist(e);
//                });
//                doCommitInsert();
//
//                // Merge the new entities to select em.
//                entities.stream().forEach(m ->
//                {
//                    T me = _emSelect.merge(m);
//                    merged.add(me);
//                });
//            }
//            catch (Exception e)
//            {
//                if (_emInsert.getTransaction().isActive())
//                {
//                    _emInsert.getTransaction().rollback();
//                }
//                logger.error("Error caught during Insert " + e);
//                throw e;
//            }
//        }
//        return merged;
//    }
//
//    /**
//     * Insert and Update multiple entities in one transaction.
//     *
//     * @param entities
//     * @return
//     * @throws Exception
//     */
//    protected synchronized <T extends IEntity> ArrayList<T> Insert_Update(T... entities) throws Exception
//    {
//
//        logger.trace("No of entities to be insert or update - " + entities.length);
//
//        ArrayList<T> merged = new ArrayList<>();
//        ArrayList<T> arrEns = new ArrayList<>(Arrays.asList(entities));
//
//        synchronized (_emInsert)
//        {
//            try
//            {
//                arrEns.forEach((entity) ->
//                {
//                    if (entity.getId() == null)
//                    {
//                        _emInsert.persist(entity);
//                    }
//                    else
//                    {
//                        _emInsert.merge(entity);
//                    }
//                });
//                doCommitInsert();
//                arrEns.forEach((entity) ->
//                {
//                    T dummy = _emSelect.merge(entity);
//                    merged.add(dummy);
//                });
//            }
//
//            catch (Exception e)
//            {
//                if (_emInsert.getTransaction().isActive())
//                {
//                    _emInsert.getTransaction().rollback();
//                }
//                logger.error("Error caught during Insert_Update " + e);
//                throw e;
//            }
//        }
//
//        return merged;
//    }
//
//    /**
//     * Update a single entity into database.
//     *
//     * @param <T>
//     * @param entity
//     * @return
//     * @throws Exception
//     */
//    protected synchronized <T extends IEntity> T Update(T entity) throws Exception
//    {
//
//        logger.trace(entity.toString());
//
//        synchronized (_emInsert)
//        {
//            try
//            {
//                entity = _emInsert.merge(entity);
//
//                doCommitInsert();
//
//                IEntity dummy = _emSelect.merge(entity);
//                return (T) dummy;
//            }
//            catch (Exception e)
//            {
//                if (_emInsert.getTransaction().isActive())
//                {
//                    _emInsert.getTransaction().rollback();
//                }
//                logger.error("Error caught during Update " + e);
//
//                throw e;
//            }
//        }
//    }
//
//    protected synchronized void Update(ArrayList<? extends IEntity> entities) throws Exception
//    {
//
//        logger.trace(entities.toString());
//
//        ArrayList<IEntity> updated = new ArrayList<>();
//        synchronized (_emInsert)
//        {
//            try
//            {
//                entities.stream().forEach((e) ->
//                {
//                    IEntity e0 = _emInsert.merge(e);
//                    updated.add(e0);
//                });
//                doCommitInsert();
//
//                // Merge the new entities to select em.
//                updated.stream().forEach(m -> _emSelect.merge(m));
//            }
//            catch (Exception e)
//            {
//                if (_emInsert.getTransaction().isActive())
//                {
//                    _emInsert.getTransaction().rollback();
//                }
//                logger.error("Error caught during Update " + e);
//                throw e;
//            }
//        }
//    }
//
//    protected synchronized void Delete(Class type, Long id) throws Exception
//    {
//        logger.trace("Delete Entity", type, id);
//        synchronized (_emSelect)
//        {
//            Object tmpEnty = _emSelect.find(type, id);
//            Delete((IEntity) tmpEnty);
//        }
//    }
//
//    /**
//     * Delete an entity.
//     *
//     * @param entity
//     * @throws Exception
//     */
//    protected synchronized void Delete(IEntity entity) throws Exception
//    {
//
//        try
//        {
//            logger.warn("Deleting entity: " + entity.toString());
//
//            synchronized (_emSelect)
//            {
//                IEntity tmpEnty = _emSelect.find(entity.getClass(), entity.getId());
//                _emSelect.getTransaction().begin();
//                _emSelect.remove(tmpEnty);
//                _emSelect.getTransaction().commit();
//            }
//        }
//        catch (Exception e)
//        {
//            if (_emSelect.getTransaction().isActive())
//            {
//                _emSelect.getTransaction().rollback();
//            }
//            logger.error(e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    /**
//     * Delete the list of entities supplied.
//     *
//     * @param arrentities
//     * @throws Exception
//     */
//    protected synchronized void Delete(ArrayList<? extends IEntity> arrentities) throws Exception
//    {
//        logger.trace("Deleting Entities ", arrentities);
//        try
//        {
//            _emSelect.getTransaction().begin();
//            arrentities.stream().map((entity) -> _emSelect.find(entity.getClass(), entity.getId()))
//                    .forEachOrdered((tmpEnty) ->
//                    {
//                        logger.warn("Deleting entity: " + tmpEnty.toString());
//                        _emSelect.remove(tmpEnty);
//                    });
//            _emSelect.getTransaction().commit();
//        }
//        catch (Exception e)
//        {
//            if (_emSelect.getTransaction().isActive())
//            {
//                _emSelect.getTransaction().rollback();
//            }
//            logger.error(e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    /**
//     * Delete entities as per the query supplied.
//     *
//     * Delete by query uses the same JPQL syntax as normal queries, with one exception: begin your query string with the delete keyword instead of the select
//     * keyword.
//     *
//     * @return no of records deleted.
//     * @throws Exception
//     */
//    protected synchronized Integer Delete(JPAQuery jpsquery) throws Exception
//    {
//
//        try
//        {
//            _emSelect.getTransaction().begin();
//
//            Query query = _emSelect.createQuery(jpsquery.getQryStr());
//            SetWhereParams(query, jpsquery.getParams());
//            LogQuery(query);
//
//            int noRecDel = query.executeUpdate();
//            logger.warn("No of records deleted: " + noRecDel);
//
//            _emSelect.getTransaction().commit();
//
//            return noRecDel;
//        }
//        catch (Exception e)
//        {
//            if (_emSelect.getTransaction().isActive())
//            {
//                _emSelect.getTransaction().rollback();
//            }
//            logger.error(e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    /**
//     *
//     * @param <T>
//     * @param type
//     * @param id - Id must be primary key
//     * @return
//     */
//    protected synchronized <T extends IEntity> T Find(Class<T> type, Long id)
//    {
//        logger.trace("Find Entity ", type, id);
//        IEntity dummy;
//        // Fetch entity using temporary EM.
//        try (EntityManager _emTmp = _smf.createEntityManager())
//        {
//            IEntity tmpEnty = _emTmp.find(type, id);
//            if (tmpEnty == null)
//            {
//                return null;
//            }   // Merge entity to our global _emSelect
//            dummy = _emSelect.merge(tmpEnty);
//            // Close temporary EM
//        }
//
//        return (T) HibernateUtil.unproxy(dummy);
//    }
//
//    protected synchronized <T extends IEntity> T FindByCode(Class<T> type, String code) throws Exception
//    {
//        logger.trace("Find Entity ", type, code);
//
//        JPAQuery query = new JPAQuery("Select p from " + type.getClass().getSimpleName() + " p where p.Code =:code");
//        query.setParam("code", code);
//        return Find(type, query);
//    }
//
//    /**
//     * Execute query and return only one object.
//     *
//     * @param <T>
//     * @param type
//     * @param query
//     * @return
//     * @throws java.lang.Exception
//     */
//    protected synchronized <T extends IEntity> T Find(Class type, JPAQuery query) throws Exception
//    {
//
//        query.setLimit(1);
//        ArrayList<T> rs = Select(type, query);
//        if (rs != null && !rs.isEmpty())
//        {
//            return rs.get(0);
//        }
//
//        return null;
//    }
//
//    protected synchronized <T extends IEntity> ArrayList<T> Select(Class type, JPAQuery jpaqry) throws Exception
//    {
//
//        // Fetch entity using temporary EM.
//        EntityManager _emTmp = _smf.createEntityManager();
//
//        ArrayList listT = doSelect(_emTmp, jpaqry);
//        ArrayList<T> tmpEns = listT;
//
//        // Merge entity to our global _emSelect
//        ArrayList<T> dummies = new ArrayList<>();
//        for (T t : tmpEns)
//        {
//            T dummy = _emSelect.merge(t);
//            dummies.add(HibernateUtil.unproxy(dummy));
//        }
//
//        // Close temporary EM
//        _emTmp.close();
//
//        return dummies;
//    }
//
//    protected synchronized ArrayList Select(JPAQuery jpaqry) throws Exception
//    {
//
//        // Fetch entity using temporary EM.
//        EntityManager _emTmp = _smf.createEntityManager();
//        ArrayList listT = doSelect(_emTmp, jpaqry);
//
//        // No need to merge static data to global _emSelect
//        // Close temporary EM
//        _emTmp.close();
//
//        return listT;
//    }
//
//    protected synchronized int Update(JPAQuery jpaqry) throws Exception
//    {
//
//        int recordsUpdated = doUpdate(jpaqry);
//
//        return recordsUpdated;
//    }
//
//    protected <T extends IEntity> ArrayList<T> SelectUnmanagedFromDB(Class type, JPAQuery jpaq) throws Exception
//    {
//
//        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
//        // Tools | Templates.
//    }
//
//    private ArrayList doSelect(EntityManager _em, JPAQuery jpaqry) throws Exception
//    {
//
//        ArrayList listT;
//        try
//        {
//            Query query = _em.createQuery(jpaqry.getQryStr());
//            SetWhereParams(query, jpaqry.getParams());
//            SetLimitOffset(query, jpaqry);
//            LogQuery(query);
//
//            listT = (ArrayList) query.getResultList();
//
//            return listT;
//        }
////        catch (PersistenceException pex)
////        {
////              //TBD: if any specific exception need to be handled here. ref. ObjDBem.java .
////        }
//        finally
//        {
//
//        }
//    }
//
//    private int doUpdate(JPAQuery jpaqry) throws Exception
//    {
//        try
//        {
//            _emUpdate.getTransaction().begin();
//            Query query = _emUpdate.createQuery(jpaqry.getQryStr());
//            SetWhereParams(query, jpaqry.getParams());
//            LogQuery(query);
//            int noRecUpd = query.executeUpdate();
//            logger.warn("No of records Updated: " + noRecUpd);
//            _emUpdate.getTransaction().commit();
//            return noRecUpd;
//
//        }
//        catch (Exception e)
//        {
//            if (_emUpdate.getTransaction().isActive())
//            {
//                _emUpdate.getTransaction().rollback();
//            }
//            logger.error(e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    /**
//     *
//     * @param query
//     * @param hmparam
//     */
//    protected void SetWhereParams(Query query, HashMap hmparam)
//    {
//
//        // First chech for names parameter set
//        if (hmparam != null)
//        {
//            Iterator<String> itCntQry = hmparam.keySet().iterator();
//            while (itCntQry.hasNext())
//            {
//                String key = itCntQry.next();
//                query.setParameter(key, hmparam.get(key));
//            }
//        }
//    }
//
//    protected void SetLimitOffset(Query query, JPAQuery jpaqry)
//    {
//
//        if (jpaqry.getLimit() != null)
//        {
//            query.setMaxResults(jpaqry.getLimit());
//        }
//        if (jpaqry.getOffset() != null)
//        {
//            query.setFirstResult(jpaqry.getOffset());
//        }
//    }
//
//    /**
//     * Perform Insert, Update and Delete on set of entities under single transaction.
//     *
//     * @param user
//     * @param enactlist
//     */
//    public synchronized void Insert_Update_Delete_OneTransact(User user, ENActionList enactlist)
//    {
//        logger.trace(enactlist.toString());
//        //Step-1. perform actions using insert entity manager, comit the changes to db.
//
//        ArrayList<NameValuePair<ENActionList.Action, IAEntity>> actions = enactlist.getActions();
//        for (NameValuePair<ENActionList.Action, IAEntity> nmvl : actions)
//        {
//            switch (nmvl.getName())
//            {
//                case Insert:
//                {
//                    IEntity e = nmvl.getValue();
//                    EntityProcessor.InsertPreProcess(user, (AEntity) e);
//                    _emInsert.persist(e);
//                }
//                break;
//                case Update:
//                {
//                    IEntity e = nmvl.getValue();
//                    EntityProcessor.UpdatePreProcess(user, (AEntity) e);
//                    _emInsert.merge(e);
//                }
//                break;
//                case Delete:
//                {
//                    IEntity ent = nmvl.getValue();
//                    ent = _emInsert.find(ent.getClass(), ent.getId());
//                    if (!_emInsert.getTransaction().isActive())
//                    {
//                        _emInsert.getTransaction().begin();
//                    }
//                    _emInsert.remove(ent);
//                }
//                break;
//            }
//        }
//
//        doCommitInsert();
//
//        //Step-2. Update the changes to select entity manager as well, to maintain similar state. This step must be done only after transaction is comited, which means changes are actaully accecpted at DB without any error.
//        for (NameValuePair<ENActionList.Action, IAEntity> nmvl : actions)
//        {
//            switch (nmvl.getName())
//            {
//                case Insert:
//                {
//                    IEntity e = nmvl.getValue();
//                    _emSelect.merge(e);
//                }
//                break;
//                case Update:
//                {
//                    IEntity e = nmvl.getValue();
//                    _emSelect.merge(e);
//                }
//                break;
//                case Delete:
//                {
//                    IEntity ent = nmvl.getValue();
//                    ent = _emSelect.find(ent.getClass(), ent.getId());
//                    if (ent != null)
//                    {
//                        _emSelect.remove(ent);
//                    }
//                }
//                break;
//            }
//        }
//        //We need not comit select entitymanager, as the DB updates are already done by insert entitymanager.
//    }
//
//    protected void LogQuery(Query query)
//    {
//
//        if (!logger.isDebugEnabled())
//        {
//            return;
//        }
//        if (query == null)
//        {
//            return;
//        }
//
//        return;
//    }
//}
