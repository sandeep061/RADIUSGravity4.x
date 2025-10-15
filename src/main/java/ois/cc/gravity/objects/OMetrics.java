package ois.cc.gravity.objects;

import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.MetricsKey;
import ois.radius.cc.entities.MetricsUnit;

public class OMetrics extends AObject{

    private String Code;

    private String Name;

    private EN Dimension;

    private MetricsKey MetricsKey;

    private MetricsUnit MetricsUnit;

    private Integer Period;

    private Integer Frequency;

    private Boolean IsRetention;

    private String StreamID;


    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public EN getDimension() {
        return Dimension;
    }

    public void setDimension(EN dimension) {
        Dimension = dimension;
    }

    public MetricsKey getMetricsKey() {
        return MetricsKey;
    }

    public void setMetricsKey(MetricsKey metricsKey) {
        MetricsKey = metricsKey;
    }

    public MetricsUnit getMetricsUnit() {
        return MetricsUnit;
    }

    public void setMetricsUnit(MetricsUnit metricsUnit) {
        MetricsUnit = metricsUnit;
    }

    public Integer getPeriod() {
        return Period;
    }

    public void setPeriod(Integer period) {
        Period = period;
    }

    public Integer getFrequency() {
        return Frequency;
    }

    public void setFrequency(Integer frequency) {
        Frequency = frequency;
    }

    public Boolean getRetention() {
        return IsRetention;
    }

    public void setRetention(Boolean retention) {
        IsRetention = retention;
    }

    public String getStreamID() {
        return StreamID;
    }

    public void setStreamID(String streamID) {
        StreamID = streamID;
    }
}
