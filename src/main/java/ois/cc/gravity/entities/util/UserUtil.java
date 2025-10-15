/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.entities.util;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import code.entities.AEntity;
import java.util.ArrayList;
import ois.cc.gravity.context.TenantContext;
import ois.cc.gravity.db.MySQLDB;
import ois.cc.gravity.db.queries.UserProfileQuery;
import ois.cc.gravity.db.queries.UserQuery;
import ois.cc.gravity.services.exceptions.GravityException;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.Profile;
import ois.radius.cc.entities.tenant.cc.User;
import ois.radius.cc.entities.tenant.cc.UserProfile;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Manoj-PC
 * @since Sep 7, 2025
 */
public class UserUtil
{
     static final org.slf4j.Logger _logger = LoggerFactory.getLogger(UserUtil.class);

    public static UserRole GetUserRole(TenantContext tcxt, User user) throws CODEException, GravityException
    {
        MySQLDB db = tcxt.getDB();
        JPAQuery qry = new JPAQuery("Select u.UserRole from UserSession u Order by u.CreatedOn Desc");
		qry.setLimit(1);
        UserRole role = db.SelectScalar(qry);

        return role;
    }
    
    public static void AddProfileForDefaultUser(TenantContext tcxt,User user) throws CODEException, GravityException
    {

        MySQLDB _db = tcxt.getDB();
        UserProfile userProfile = _db.Find(new UserProfileQuery().filterByUser(user.getUserId()));
        if (userProfile != null)
        {
            return;
        }

        ArrayList<AEntity> entites = new ArrayList<>();

        String prf="DefaultProfile_"+user.getUserId();
        Profile profile = new Profile();
        profile.setCode(prf);
        profile.setName(prf);
        profile.setPolicy("[{\"Entity\":\"*\",\"Actions\":\"*\",\"Effect\":\"Allow\",\"Code\":\"ALL_ALLOWED_4571\",\"Conditions\":\"\"}]");

        userProfile = new UserProfile();
        userProfile.setProfile(profile);
        userProfile.setUser(user);

        entites.add(profile);
        entites.add(userProfile);

        _db.Insert(user, entites);
        _logger.info("Default Profile and UserProfile add sucessfully with this tenant " + tcxt.getTenant().getName());
    }

}
