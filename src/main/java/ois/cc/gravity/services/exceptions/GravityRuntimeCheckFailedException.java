package ois.cc.gravity.services.exceptions;

public class GravityRuntimeCheckFailedException extends GravityException
{
    private final EvCauseRuntimeCheckFailed EvCause;

    public GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed EvCause)
    {
        this.EvCause = EvCause;
    }

    public GravityRuntimeCheckFailedException(EvCauseRuntimeCheckFailed EvCause, String Message)
    {
        super(Message);
        this.EvCause = EvCause;
    }

    public EvCauseRuntimeCheckFailed getEvCause()
    {
        return EvCause;
    }
}
