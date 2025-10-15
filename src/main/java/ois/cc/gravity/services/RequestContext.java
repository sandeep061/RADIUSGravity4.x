package ois.cc.gravity.services;

import CrsCde.CODE.Common.Utils.ReflUtils;
import code.ua.events.EventFailedCause;
import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import ois.cc.gravity.services.exceptions.GravityIllegalArgumentException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class RequestContext
{
    private static final HashMap<Class, ArrayList<Field>> _hmAllFlds = new HashMap<>();

    /**
     * @{code Request class -> ( Field Name -> Field) }
     */
    private static final HashMap<Class, HashMap<String, Field>> _hmFldNames = new HashMap<>();

    private static final HashMap<Class, ArrayList<Field>> _hmParamFlds = new HashMap<>();

    static ArrayList<Field> AllFields(Class reqcls)
    {
        if (!_hmAllFlds.containsKey(reqcls))
        {

            ArrayList<Field> allFlds = ReflUtils.GetDeclaredFields(reqcls);
            //set field accessible to true.
            allFlds.forEach(f -> f.setAccessible(true));

            _hmAllFlds.put(reqcls, allFlds);

            //init Name-Field mapping.
            _hmFldNames.put(reqcls, new HashMap<>());
            allFlds.forEach(f -> _hmFldNames.get(reqcls).put(f.getName(), f));
        }

        return _hmAllFlds.get(reqcls);
    }

    /**
     * Return Field object of the request class by Name.
     *
     * @param reqcls
     * @param name
     * @return
     */
    static Field Field(Class reqcls, String name)
    {
        if (!_hmAllFlds.containsKey(reqcls))
        {
            AllFields(reqcls);
        }

        return _hmFldNames.get(reqcls).get(name);
    }

    /**
     * Return list of Fields which has Param annotation defined.
     *
     * @param reqcls
     * @return
     */
    static ArrayList<Field> FieldsToValidate(Class reqcls)
    {
        if (!_hmParamFlds.containsKey(reqcls))
        {
            ArrayList<Field> allFlds = ReflUtils.GetDeclaredFields(reqcls);

            ArrayList<Field> fldsAnno = (ArrayList<Field>) allFlds.stream().filter(f -> f.getAnnotation(Param.class) != null)
                    .collect(Collectors.toList());

            //set field accessible to true.
            fldsAnno.forEach(f -> f.setAccessible(true));

            _hmParamFlds.put(reqcls, fldsAnno);
        }

        return _hmParamFlds.get(reqcls);
    }

    public static EN GetENFromRequest(Request req) throws GravityIllegalArgumentException
    {
        Field fldEntity = null;
        EN en = null;
        try
        {
            fldEntity = ReflUtils.GetField(req.getClass(), "EntityName");
        }
        catch (NoSuchFieldException nex)
        {

        }
        if (fldEntity != null)
        {
            //Request have attribute EntityName but user didn't send value for it.
            try
            {
                Object enName = ReflUtils.InvokeGetter(req, fldEntity);
                if (enName == null)
                {
                    throw new GravityIllegalArgumentException("EntityName", EventFailedCause.NonOptionalConstraintViolation, EvCauseRequestValidationFail.InvalidParamName);
                }
                en = EN.valueOf(enName.toString());
            }
            catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException  iex)
            {

            }
            
        }
        return en;
    }

}
