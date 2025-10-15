/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.db;

import CrsCde.CODE.Common.Utils.DATEUtil;
import CrsCde.CODE.Common.Utils.LOGUtil;
import code.entities.AEntity;
import code.entities.AEntity_ad;
import code.entities.AEntity_es;
import code.entities.EntityState;
import ois.radius.cc.entities.AEntity_ccad;
import ois.radius.cc.entities.AEntity_cces;
import ois.radius.cc.entities.tenant.cc.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Deepak
 */
public class EntityProcessor
{

    static final Logger logger = LoggerFactory.getLogger(EntityProcessor.class);

    public static void InsertPreProcess(User user, AEntity entity)
    {
        /**
         * Set common attributes
         */
        Long userid = null;
        if (user == null)
        {
            userid = 1L;
        }
        else
        {
            userid = user.getId();
        }
        if (entity instanceof AEntity_cces aEntity_cces)
        {
            aEntity_cces.setEntityState(EntityState.Active);
        }
        entity.setCreatedBy(userid);
        entity.setCreatedOn(DATEUtil.Now());

    }

    public static void UpdatePreProcess(User user, AEntity entity)
    {
        /**
         * Set common attributes
         */
        entity.setEditedBy(user.getId());
        entity.setEditedOn(DATEUtil.Now());
    }

    static void DeletePreProcess(User user, AEntity entity)
    {
        logger.trace(LOGUtil.ArgString(entity));

        /**
         * Set common attributes
         */
        if (entity instanceof AEntity_ad enad)
        {
            enad.setDeleted(true);
            enad.setDeletedId(entity.getId());
        }
        else if (entity instanceof AEntity_ccad enad)
        {
            enad.setDeleted(true);
            enad.setDeletedId(entity.getId());
        }
        else if (entity instanceof AEntity_es enes)
        {
            enes.setEntityState(EntityState.Deleted);
        }
        else if (entity instanceof AEntity_cces encces)
        {
            encces.setEntityState(EntityState.Deleted);
        }
    }
}
