package ois.cc.gravity.framework.objects;

import jakarta.persistence.*;
import ois.radius.cc.entities.tenant.oi.OIAlertConfig;
import ois.radius.cc.entities.tenant.oi.OIMetrics;

import java.util.ArrayList;
import java.util.List;

public class ORule extends AObject{

    private String Name;

    private ArrayList<OIMetrics> OIMetrics;

    private String RuleCondition;

    private ArrayList<OAlertConfig> OIAlertConfigs;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<OIMetrics> getOIMetrics() {
        return OIMetrics;
    }

    public void setOIMetrics(ArrayList<OIMetrics> OIMetrics) {
        this.OIMetrics = OIMetrics;
    }

    public String getRuleCondition() {
        return RuleCondition;
    }

    public ArrayList<OAlertConfig> getOIAlertConfigs() {
        return OIAlertConfigs;
    }

    public void setOIAlertConfigs(ArrayList<OAlertConfig> OIAlertConfigs) {
        this.OIAlertConfigs = OIAlertConfigs;
    }

    public void setRuleCondition(String ruleCondition) {
        RuleCondition = ruleCondition;
    }



}
