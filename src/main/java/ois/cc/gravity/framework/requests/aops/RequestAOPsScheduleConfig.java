package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.ca.enums.aops.AOPsType;
import org.vn.radius.cc.platform.requests.Param;

import java.util.Date;

public class RequestAOPsScheduleConfig extends Request {

    private Long AOPs;
    private Boolean IsScheduleEnable;
    private Date StartDate;
    private Date EndDate;
    private AOPsType AOPSType;
    @Param(
            Regex = "(?:[01]\\d|2[0123]):(?:[012345]\\d)",
            Length = 8
    )
    private String StartHour;
    @Param(
            Regex = "(?:[01]\\d|2[0123]):(?:[012345]\\d)",
            Length = 8
    )
    private String EndHour;
    @Param(
            Regex = "^([0-9]+,?)+$",
            Length = 16
    )
    private String Days;
    @Param(
            Regex = "^([0-9]+,?)+$",
            Length = 8
    )
    private String Weeks;
    @Param(
            Regex = "^([0-9]+,?)+$",
            Length = 32
    )
    private String Months;

    public RequestAOPsScheduleConfig(String requestid) {
        super(requestid, GReqType.Config, GReqCode.AOPsScheduleConfig);
    }

    public Long getAOPs() {
        return AOPs;
    }

    public void setAOPs(Long AOPs) {
        this.AOPs = AOPs;
    }

    public Boolean getScheduleEnable() {
        return IsScheduleEnable;
    }

    public void setScheduleEnable(Boolean scheduleEnable) {
        IsScheduleEnable = scheduleEnable;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public String getStartHour() {
        return StartHour;
    }

    public void setStartHour(String startHour) {
        StartHour = startHour;
    }

    public String getEndHour() {
        return EndHour;
    }

    public void setEndHour(String endHour) {
        EndHour = endHour;
    }

    public String getDays() {
        return Days;
    }

    public void setDays(String days) {
        Days = days;
    }

    public String getWeeks() {
        return Weeks;
    }

    public void setWeeks(String weeks) {
        Weeks = weeks;
    }

    public String getMonths() {
        return Months;
    }

    public void setMonths(String months) {
        Months = months;
    }

    public AOPsType getAOPSType() {
        return AOPSType;
    }

    public void setAOPSType(AOPsType AOPSType) {
        this.AOPSType = AOPSType;
    }
}
