/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.db.queries;

import code.common.exceptions.CODEException;
import code.db.jpa.JPAQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import code.entities.EntityState;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;
import ois.radius.cc.entities.AEntity_ccad;
import ois.radius.cc.entities.AEntity_cces;

/**
 *
 * @author Deepak
 */
public abstract class EntityQuery
{

    protected EN _en;

    protected StringBuilder _where;
    protected StringBuilder _orderby;

    protected HashMap<String, Object> _params;
    private Integer _limit;
    private Integer _offset;

    public EntityQuery(EN en)
    {
        _en = en;

        _where = new StringBuilder();
        _params = new HashMap<>();
    }

    public EN getEn()
    {
        return _en;
    }

    public Integer getLimit()
    {
        return _limit;
    }

    public void setLimit(Integer limit)
    {
        if (limit == null || limit < 0 || limit > 128)
        {
            this._limit = 128;
        }
        else
        {
            this._limit = limit;
        }
    }

    public Integer getOffset()
    {
        return _offset;
    }

    public void setOffset(Integer offset)
    {
        if (offset == null || offset < 0)
        {
            this._offset = 0;
        }
        else
        {
            this._offset = offset;
        }
    }

    /**
     * Build and return JPAQuery for SELECT.
     *
     * @return
     */
    public JPAQuery toSelect()
    {
        Boolean isAdEn = false;
        Boolean isCcEn = false;
        if (AEntity_ccad.class.isAssignableFrom(_en.getEntityClass()))
        {
            isAdEn = true;
        }

        StringBuilder select = new StringBuilder("Select " + getTableName() + " From " + getTableName() + " " + getTableName());
        if (isAdEn)
        {
            select.append(" Where ").append(getTableName()).append(".Deleted =: isdel");
        }
        if (AEntity_cces.class.isAssignableFrom(_en.getEntityClass()))
        {
            isCcEn = true;
        }
        if (isCcEn)
        {
            select.append(" Where ").append(getTableName()).append(".EntityState =: enstate");
        }
//        String qrystr = select;
        if (_where != null)
        {
            String whrstr = _where.toString();
            if (!whrstr.isEmpty())
            {
                if (isAdEn)
                {
                    select.append(" And ").append(whrstr);
                }
                else if (isCcEn)
                {
                    select.append(" And ").append(whrstr);
                }
                else
                {
                    select.append(" Where ").append(whrstr);
                }

            }
        }
        if (_orderby != null)
        {
            String orderBy = _orderby.toString();
            if (!orderBy.isEmpty())
            {
                select.append(" Order By ").append(orderBy);
            }
        }

        JPAQuery qry = new JPAQuery(select.toString(), _params);
        if (isAdEn)
        {
            qry.setParam("isdel", false);
        }
        if (isCcEn)
        {
            qry.setParam("enstate", EntityState.Active);
        }
        qry.setLimit(_limit);
        qry.setOffset(_offset);

        return qry;

    }

    public void setOrederBy(String attribute, Boolean ordby)
    {
        if (attribute == null || attribute.trim().isEmpty())
        {
            throw new IllegalArgumentException("Attribute name in Orderby can't be NULL.");
        }
        String ordbyCmd = (ordby == null || ordby == true) ? "ASC" : "DESC";
        String ordbyQry = getTableName() + "." + attribute + " " + ordbyCmd;

        AppendOrderby(ordbyQry);
    }

    /**
     * Build and return JPAQuery to get COUNT. <br>
     * No need to inlcude OrderBy for count.
     *
     * @return
     */
    public JPAQuery toCount()
    {
        //V:121021 - COUNT(getTableName()) replaced with COUNT(getTableName().Id), as we have experinece huge delay with earlier.
        Boolean isAdEn = false;
        Boolean isCcEn = false;
        if (AEntity_ccad.class.isAssignableFrom(_en.getEntityClass()))
        {
            isAdEn = true;
        }

        StringBuilder select = new StringBuilder("Select COUNT(" + getTableName() + ".Id" + ") From " + getTableName() + " " + getTableName());

        if (isAdEn)
        {
            select.append(" Where ").append(getTableName()).append(".Deleted =: isdel");
        }
        if (AEntity_cces.class.isAssignableFrom(_en.getEntityClass()))
        {
            isCcEn = true;
        }
        if (isCcEn)
        {
            select.append(" Where ").append(getTableName()).append(".EntityState =: enstate");
        }

        StringBuilder qrystr = select;
        if (_where != null)
        {
            String whrstr = _where.toString();
            if (!whrstr.isEmpty())
            {
                if (isAdEn)
                {
                    select.append(" And ").append(whrstr);
                }
                else if (isCcEn)
                {
                    select.append(" And ").append(whrstr);
                }
                else
                {
                    select.append(" Where ").append(whrstr);
                }

            }
        }

        JPAQuery qry = new JPAQuery(qrystr.toString(), _params);
//        qry.setParam("isdel", false);
        if (isAdEn)
        {
            qry.setParam("isdel", false);
        }
        if (isCcEn)
        {
            qry.setParam("enstate", EntityState.Active);
        }
        return qry;
    }

    public void AppendWhere(String whrstr)
    {
        if (_where == null||_where.isEmpty())
        {
            _where = new StringBuilder();
        }
        if (_where.length() == 0)
        {
            if (whrstr.startsWith("And"))
            {
                whrstr = whrstr.replaceFirst("And", "");
            }
        }

        _where.append(" ").append(whrstr);

    }

    public void AppendOrderby(String orderby)
    {
        if (_orderby == null)
        {
            _orderby = new StringBuilder();
        }
        if (_orderby.length() > 0)
        {
            _orderby.append(" , ");
        }
        _orderby.append(orderby);
    }

    public final void ApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable
    {
        //Do basic validation check.
        if (filters == null || filters.isEmpty())
        {
            return;
        }

        doApplyFilters(filters);
    }

    public abstract void doApplyFilters(HashMap<String, ArrayList<String>> filters) throws Throwable;

    public final void ApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException
    {
        //Do basic validation check.
        if (orderby == null || orderby.isEmpty())
        {
            return;
        }

        doApplyOrderBy(orderby);
    }

    protected abstract void doApplyOrderBy(ArrayList<HashMap<String, Boolean>> orderby) throws CODEException, GravityIllegalArgumentException;

    public <T extends EntityQuery> T filterById(Long id)
    {
        AppendWhere("And " + getTableName() + ".Id=:id");
        _params.put("id", id);

        return (T) this;
    }

    public <T extends EntityQuery> T filterByIds(List<Long> ids)
    {
        AppendWhere("And " + getTableName() + ".Id IN :ids");
        _params.put("ids", ids);

        return (T) this;
    }

    protected Boolean _isfilterByEntityState = false;

    public <T extends EntityQuery> T orderById(Boolean ordrby)
    {
        setOrederBy("Id", ordrby);
        return (T) this;
    }

    public <T extends EntityQuery> T orderByEntityState(Boolean ordrby)
    {
        setOrederBy("EntityState", ordrby);
        return (T) this;
    }

    public <T extends EntityQuery> T orderByCreatedOn(Boolean isasc)
    {
        setOrederBy("CreatedOn", isasc);
        return (T) this;
    }

    /**
     * This method will return the table name of entity to build the query. <br>
     * - This is required query classes whose table name is not same as EN.name (like contact). <br>
     * - Instead of override toSelect() and toCount() we implement this method.
     *
     * @return
     */
    protected String getTableName()
    {
        return _en.name();
    }

    public JPAQuery toCountAll()
    {
        String qrystr;

        qrystr = "Select " + "COUNT(" + getTableName() + ")" + " From " + getTableName() + " " + getTableName() + " " + " Where " + getTableName() + ".Deleted =: isdel ";

//        String qrystr = select;
        JPAQuery qry = new JPAQuery(qrystr);
        qry.setParam("isdel", false);
        return qry;
    }

}
