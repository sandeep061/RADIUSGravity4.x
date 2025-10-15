package ois.cc.gravity.objects;

public class OSurveyInfo extends Object{

    private Boolean IsExpired;

    private Boolean CanView;

    private Boolean CanEdit;

    private Integer MaxAttempt;

    private Integer Attempt;


    public Boolean getIsExpired()
    {
        return IsExpired;
    }

    public void setIsExpired(Boolean IsExpired)
    {
        this.IsExpired = IsExpired;
    }

    public Boolean getCanView()
    {
        return CanView;
    }

    public void setCanView(Boolean canView)
    {
        CanView = canView;
    }

    public Boolean getCanEdit()
    {
        return CanEdit;
    }

    public void setCanEdit(Boolean canEdit)
    {
        CanEdit = canEdit;
    }

    public Integer getMaxAttempt()
    {
        return MaxAttempt;
    }

    public void setMaxAttempt(Integer maxAttempt)
    {
        MaxAttempt = maxAttempt;
    }

    public Integer getAttempt()
    {
        return Attempt;
    }

    public void setAttempt(Integer attempt)
    {
        Attempt = attempt;
    }
}
