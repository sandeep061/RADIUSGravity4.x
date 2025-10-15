package ois.cc.gravity.objects;



import java.util.ArrayList;

public class ORule extends AObject{

    private String Name;

    private ArrayList<ois.cc.gravity.objects.OMetrics> OIMetrics;

    private String RuleCondition;

    private ArrayList<OAlertConfig> OIAlertConfigs;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<OMetrics> getOIMetrics() {
        return OIMetrics;
    }

    public void setOIMetrics(ArrayList<OMetrics> OIMetrics) {
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
