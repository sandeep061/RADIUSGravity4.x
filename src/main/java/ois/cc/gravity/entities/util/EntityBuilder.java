package ois.cc.gravity.entities.util;

import CrsCde.CODE.Common.Utils.DATEUtil;
import CrsCde.CODE.Common.Utils.PWDUtil;
import CrsCde.CODE.Common.Utils.ReflUtils;
import CrsCde.CODE.Common.Utils.TypeUtil;
import code.common.exceptions.CODEException;
import code.entities.AEntity;
import code.entities.annotations.AnAttribute;
import code.ua.events.EventFailedCause;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import ois.cc.gravity.services.exceptions.*;
import ois.radius.cc.entities.EN;
import ois.cc.gravity.db.MySQLDB;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ois.cc.gravity.framework.events.common.EvCauseRequestValidationFail;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class EntityBuilder
{

    private static Logger _logger = LoggerFactory.getLogger(EntityBuilder.class);

    public static <T extends AEntity> T New(EN en) throws GravityInstantiationException
    {
        try
        {
            return (T) en.getEntityClass().getDeclaredConstructor().newInstance();
        }
        catch (Exception iex)
        {
            _logger.error(iex.getMessage(), iex);

            GravityInstantiationException radex = new GravityInstantiationException(iex, en.name());
            throw radex;
        }

    }

    public static void BuildEntity(MySQLDB db, AEntity entity, HashMap<String, Object> hmvals) throws GravityException, Exception
    {
        if (hmvals != null)
        {
            /**
             * Set attribute values for entity as received as input.
             */
            setAttributeValues(db, entity, hmvals);
        }

        /**
         * Set default value to attributes, if configured in annotations.
         */
        setDefaultAttributeValue(entity);

    }

    private static void setAttributeValues(MySQLDB db, AEntity entity, HashMap<String, Object> hmvals) throws GravityException, Exception
    {
        for (String key : hmvals.keySet())
        {
            Field fld;
            try
            {
                fld = ReflUtils.GetField(entity.getClass(), key);
            }
            catch (NoSuchFieldException fex)
            {
                GravityNoSuchFieldException radex = new GravityNoSuchFieldException(fex, entity.getClass().getName(), key);
                throw radex;
            }

            fld.setAccessible(true);

            validateAnAttributeAnnotation(entity, fld, hmvals.get(key));

            /**
             * Validate the @Column attributes. <br>
             * we are doing it separately as ObjectDB implicitly ignore some attributes like length,...etc.
             */
            validateEnColumnAnnotations(entity.getClass().getSimpleName(), fld, hmvals.get(key));

            try
            {
                /**
                 * If an attribute is of AEntity type, then fetch the entity from DB and set.
                 */
                if (AEntity.class.isAssignableFrom(fld.getType()))
                {
                    Long entyId = Long.valueOf(String.valueOf(hmvals.get(key)));
                    AEntity fldEnty = (AEntity) db.FindAssert(fld.getType(), entyId);
                    fld.set(entity, fldEnty);
                }
                else if (fld.getType().equals(Properties.class))
                {
                    /**
                     * If attribute datatype is of Properties, then we expect values must be in json string. Convert json string to properties type.
                     */
                    Properties prop = new Properties();
                    JSONObject jsnObj = new JSONObject(hmvals.get(key).toString());
                    Iterator<String> keyItr = jsnObj.keys();
                    while (keyItr.hasNext())
                    {
                        String jkey = keyItr.next();
                        prop.setProperty(jkey, jsnObj.getString(jkey));
                    }

                    fld.set(entity, prop);
                }
                else
                {
                    /**
                     * Handle primitive type attributes. <br>
                     * We expect here, the valued could be supplied as String, or the actual data type. In case the value is supplied as string, then cast the
                     * value to real type.
                     */
                    Object value = hmvals.get(key);

                    /**
                     * Encrypt password fields. <br>
                     * This must done, only if we get input to update password field value.
                     */
                    AnAttribute annot = fld.getAnnotation(AnAttribute.class);
                    if (annot != null)
                    {
                        if (annot.Password())
                        {
                            value = AppUtil.Encrypt(String.valueOf(value));
                        }
                    }

                    /**
                     * Set final value to field.
                     */
                    if (value instanceof String)
                    {
                        value = TypeUtil.ValueOf(fld.getType(), String.valueOf(value));
                        if (value instanceof String)
                        {
                            String atrVal = value.toString().trim();
//                            if (!atrVal.isEmpty() && atrVal.length() > 512)
//                            {
//                                throw new GravityIllegalArgumentException("Max length is 512 ", fld.getName(), EventFailedCause.DataBoundaryLimitViolation);
//                            }
                        }

                        ReflUtils.InvokeSetter(entity, fld, value);
                    }
                    else
                    {
                        fld.set(entity, value);
                    }
                }
            }
            catch (IllegalAccessException |CODEException ex)
            {
               _logger.error(ex.getMessage(),ex);
               throw new GravityException(ex);
            }

        }
    }

    private static void validateEnColumnAnnotations(String enname, Field fld, Object val) throws GravityAttributeConstraintFailedException
    {
        if (val == null)
        {
            return;
        }
        Annotation[] arrEnAnno = fld.getDeclaredAnnotations();
        for (Annotation anno : arrEnAnno)
        {
            if (anno.annotationType().equals(jakarta.persistence.Column.class))
            {
                Column col = (Column) anno;
                //checking for Column length.
                if (val.toString().length() > col.length())
                {
                    GravityAttributeConstraintFailedException ex = new GravityAttributeConstraintFailedException(enname, fld.getName(), GravityAttributeConstraintFailedException.FailedCause.SizeLimitExceeds);
                    ex.setSizeLimit(col.length());
                    throw ex;
                }
            }
        }
    }

    public static void validateAnAttributeAnnotation(AEntity entity, Field fld, Object value) throws GravityAttributeConstraintFailedException, GravityIllegalArgumentException
    {
        validateAnAttributeIsEditable(entity, fld);
        validateAnAttributeRegx(fld, value);
        validateAnAttributeLength(fld, value);
    }

    private static void validateAnAttributeRegx(Field fld, Object value) throws GravityIllegalArgumentException
    {
        AnAttribute annot = fld.getAnnotation(AnAttribute.class);
        if (annot == null)
        {
            return;
        }
        String regx = annot.ValidRegex();

        if (!(regx == null || regx.isEmpty())
                && !value.toString().matches(annot.ValidRegex()))
        {
            throw new GravityIllegalArgumentException(fld.getName(), EventFailedCause.EventFailedCauseIA, EvCauseRequestValidationFail.RegularExpressionViolation);
        }
    }

    private static void validateAnAttributeIsEditable(AEntity entity, Field fld) throws GravityAttributeConstraintFailedException
    {
        //Check for not editable fields.
        if (entity.getId() != null)
        {
            AnAttribute annot = fld.getAnnotation(AnAttribute.class);
            if (annot != null && !annot.Editable())
            {
                throw new GravityAttributeConstraintFailedException(entity.getClass().getName(), fld.getName(), GravityAttributeConstraintFailedException.FailedCause.IsNotEditable);
            }
        }
    }

    /**
     * This method will validate the Min and Max range of string type attribute value. <br>
     * - If attribute is optional and user is giving value then that must be follow the min and max range. <br>
     *
     * @param fld
     * @param value
     * @throws GravityIllegalArgumentException
     */
    private static void validateAnAttributeLength(Field fld, Object value) throws GravityIllegalArgumentException
    {
        AnAttribute annot = fld.getAnnotation(AnAttribute.class);
        if (annot == null)
        {
            return;
        }

        if (!fld.getType().equals(String.class))
        {
            _logger.trace("Value is not of String type.So ignored");
            return;
        }

        Basic basicAnno = fld.getAnnotation(Basic.class);
        Column colAnno = fld.getAnnotation(Column.class);

        boolean isOptional = (basicAnno == null ? true : basicAnno.optional()) || (colAnno == null ? true : colAnno.nullable());

        String attrVal = value.toString();
        if (attrVal.isEmpty() && isOptional)
        {
            return;
        }
        else
        {
            int min = annot.LengthMin();
            int max = annot.LengthMax();

            if (!(value.toString().length() >= min && value.toString().length() <= max))
            {
                throw new GravityIllegalArgumentException("Value Length Must be In Range :" + "[" + min + "," + max + "]", fld.getName(),EventFailedCause.DataBoundaryLimitViolation);
            }
        }
    }

    public static void setDefaultAttributeValue(AEntity entity) throws Exception
    {

        for (Field fld : ReflUtils.GetDeclaredFields(entity.getClass()))
        {
            fld.setAccessible(true);

            AnAttribute annot = fld.getAnnotation(AnAttribute.class);
            if (annot == null)
            {
                continue;
            }

            /**
             * If current value set to NULL, and default annotation is configured...
             */
            Object currValue = fld.get(entity);
            if (currValue == null && annot.Default() != null)
            {
                Object value = getDefaultAttributeValue(fld, annot.Default());
                fld.set(entity, value);
            }
        }
    }

    /**
     * Append set of object to collection attribute on an entity. <br>
     * If the collection is NULL, then create the collection before appending the attributes. <br>
     *
     * @param db
     * @param entity
     * @param attrfld Attribute's field object.
     * @param values String values in case of entity it will contain Id .
     *
     */
    public static void AppendToAttributeList(MySQLDB db, AEntity entity, Field attrfld, ArrayList<String> values) throws GravityUnhandledException
    {
        try
        {
            Collection col = (Collection) ReflUtils.InvokeGetter(entity, attrfld);

            if (col == null)
            {
                if (List.class
                        .isAssignableFrom(attrfld.getType()))
                {
                    col = new ArrayList();

                }
                else if (Set.class
                        .isAssignableFrom(attrfld.getType()))
                {
                    col = new HashSet();
                }

                ReflUtils.InvokeSetter(entity, attrfld, col);
            }

            col.addAll(GetRealAttributeValues(db, attrfld, values));
        }
        catch (Exception | GravityException ex)
        {
            throw new GravityUnhandledException(ex);
        }
    }

    public static void RemoveFromAttributeList(MySQLDB db, AEntity entity, Field attrfld, ArrayList<String> values) throws GravityUnhandledException
    {
        try
        {
            Collection col = (Collection) ReflUtils.InvokeGetter(entity, attrfld);
            col.removeAll(GetRealAttributeValues(db, attrfld, values));
        }
        catch (Exception | GravityException ex)
        {
            throw new GravityUnhandledException(ex);
        }
    }

    /**
     * Convert to actual datatype from string type. In case of entity fetch the object from db assuming values will contain Id.
     *
     * @param values
     * @return
     */
    private static ArrayList<Object> GetRealAttributeValues(MySQLDB db, Field attrfld, ArrayList<String> values) throws Exception, GravityException
    {
        ArrayList<Object> arrVals = new ArrayList<>();
        Class actualType = ReflUtils.getRealType(attrfld);

        for (String val : values)
        {
            Object attrVal;

            if (AEntity.class.isAssignableFrom(actualType))
            {
                attrVal = db.Find(actualType, Long.valueOf(val));
            }
            else
            {
                attrVal = TypeUtil.ValueOf(actualType, val);
            }
            if (attrVal != null)
            {
                arrVals.add(attrVal);
            }
        }
        return arrVals;
    }

    /**
     * Return the default value for this attribute as set by Attribute Annotation.
     *
     * @param fld
     * @return
     */
    private static Object getDefaultAttributeValue(Field fld, AnAttribute.Default defult)
    {
        Object newVal = null;

        switch (defult)
        {
            case Timestamp:
                newVal = DATEUtil.Now();
                break;
            case Zero:
                newVal = 0;
                break;
            case Integer:
                newVal = fld.getAnnotation(AnAttribute.class).DefaultInteger();
                break;
            case String:
                newVal = fld.getAnnotation(AnAttribute.class).DefaultString();
                break;
            case True:
                newVal = Boolean.TRUE;
                break;
            case False:
                newVal = Boolean.FALSE;
                break;
            case None:
                newVal = null;
        }

        return newVal;
    }

}