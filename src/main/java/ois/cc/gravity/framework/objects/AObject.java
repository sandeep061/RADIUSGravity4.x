/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ois.cc.gravity.framework.objects;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Manoj
 * @since 22 Aug, 2019
 */
public class AObject implements Serializable
{

    protected Long Id;

    //Note : This is commented As we are not using EntityState.
//    /**
//     * v:251219.1 - we remove some attributes from here.
//     */
//    protected EntityState EntityState;
//    public EntityState getEntityState()
//    {
//        return EntityState;
//    }
//
//    public void setEntityState(EntityState EntityState)
//    {
//        this.EntityState = EntityState;
//    }
    public Long getId()
    {
        return Id;
    }

    public void setId(Long Id)
    {
        this.Id = Id;
    }

    /**
     * included when we introdued Lazy entities. v:20062014
     *
     * @param entity
     * @return
     */
    private Class GetImplClass(Object entity)
    {
//        if (entity != null && entity instanceof HibernateProxy)
//        {
//            return (((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation()).getClass();
//        }
        return entity.getClass();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
//
        if (GetImplClass(this) != GetImplClass(obj)) //v:20062014
        {
            return false;
        }
        final AObject other = (AObject) (obj);
        /**
         * v:20062014 using other.getId() as a replacement to other.Id ,beCause lazily initialized objects always return inherited members as null, to get the
         * value ,most significantly we've to use getter and setter.
         */
        return !(!Objects.equals(this.Id, other.getId()) && (this.Id == null || !this.Id.equals(other.getId())));
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 37 * hash + (this.Id != null ? this.Id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + "{" + "Id=" + Id + '}';
    }
}
