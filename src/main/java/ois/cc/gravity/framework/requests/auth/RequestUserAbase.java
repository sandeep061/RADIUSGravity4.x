/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.framework.requests.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import CrsCde.CODE.Common.Utils.TypeUtil;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;


/**
 *
 * @author Deepak
 */
public class RequestUserAbase extends Request
{
    private String TenantCode;
    /**
     * Name of Filter - list of arguments to that filter.
     */
    protected HashMap<String, ArrayList<String>> Filters;
    /**
     * List of attributes based on Order by sequences.<br>
     * List<<HashMap<Attribute-true/false>> <br>
     * true - Ascending, false- Descending.
     */
    protected ArrayList<HashMap<String, Boolean>> OrderBy;
    
    protected Integer Limit;

    protected Integer Offset;

    /**
     * If set to TRUE - then total record count will be included in the event.
     */
    Boolean IncludeCount;

    public RequestUserAbase(String requestid, GReqType type, GReqCode code)
    {
        super(requestid, type, code);
        this.Filters = new HashMap<>();
        this.OrderBy = new ArrayList<>();
        this.IncludeCount = false;
    }

    public String getTenantCode()
    {
        return TenantCode;
    }

    public void setTenantCode(String TenantCode)
    {
        this.TenantCode = TenantCode;
    }

    public final HashMap<String, ArrayList<String>> getFilters()
    {
        return Filters;
    }

    public final void setFilters(HashMap<String, ArrayList<String>> Filters)
    {
        this.Filters = Filters;
    }

    public final <T> ArrayList<T> getFilterList(String name, Class type) throws Exception
    {

        if (this.Filters.get(name) == null)
        {
            return null;
        }

        if (this.Filters.get(name).size() < 1)
        {
            throw new IllegalArgumentException("Excpected type is array, but found primitive.");
        }

        ArrayList<T> list = new ArrayList<>();
        for (String val : this.Filters.get(name))
        {
            list.add(TypeUtil.ValueOf(type, val));
        }

        return list;
    }

    public final <T> T getFilterValue(String name, Class type) throws Exception
    {

        if (this.Filters.get(name) == null)
        {
            return null;
        }

        if (this.Filters.get(name).size() > 1)
        {
            throw new IllegalArgumentException("Excpected type is primitive, but found array.");
        }

        return TypeUtil.ValueOf(type, this.Filters.get(name).stream().findFirst().orElse(null));
    }

    public final void setFilter(String name, String value)
    {
        ArrayList<String> arr = new ArrayList<>();
        arr.add(value);

        this.Filters.put(name, arr);
    }

    public final void setFilter(String name, String... values)
    {
        ArrayList<String> arr = new ArrayList<>();
        arr.addAll(Arrays.asList(values));

        this.Filters.put(name, arr);
    }

    public final void setFilter(String name, ArrayList<String> value)
    {
        this.Filters.put(name, value);
    }

    public ArrayList<HashMap<String, Boolean>> getOrderBy()
    {
        return OrderBy;
    }

    public void setOrderBy(ArrayList<HashMap<String, Boolean>> OrderBy)
    {
        this.OrderBy = OrderBy;
    }

    public Integer getLimit()
    {
        return Limit;
    }

    public void setLimit(Integer Limit)
    {
        this.Limit = Limit;
    }

    public Integer getOffset()
    {
        return Offset;
    }

    public void setOffset(Integer Offset)
    {
        this.Offset = Offset;
    }

    public Boolean getIncludeCount()
    {
        return IncludeCount;
    }

    public void setIncludeCount(Boolean IncludeCount)
    {
        this.IncludeCount = IncludeCount;
    }

}
