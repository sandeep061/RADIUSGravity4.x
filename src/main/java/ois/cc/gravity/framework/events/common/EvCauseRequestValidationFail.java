package ois.cc.gravity.framework.events.common;

public enum EvCauseRequestValidationFail
{
    NonOptionalConstraintViolation,

    /**
     * Value of request parameter does not satisfy the regular expression.
     */
    RegularExpressionViolation,
    /**
     * Value received in request is out of the boundary value defined for that attribute.
     */
    DataBoundaryLimitViolation,
    /**
     * Data received in request exceeds the size limit for that attribute.
     */
    DataLengthLimitExceeds,
    /**
     * Value of one parameter must be NULL if another parameter is not NULL (mutual exclusion). <br>
     */
    NullIfNotNullViolation,
    /**
     * Value received in Request parameter is not null and contains an invalid value that is not a member of the set of values expected for the argument.
     */
    ParamValueOutOfRange,

    /**
     * The parameter name received is not valid or not known.
     */
    InvalidParamName,;
}
