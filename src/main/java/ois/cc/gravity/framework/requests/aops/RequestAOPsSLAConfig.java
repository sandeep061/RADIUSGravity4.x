package ois.cc.gravity.framework.requests.aops;

import code.ua.requests.Param;
import code.ua.requests.Request;
import ois.cc.gravity.framework.requests.GReqCode;
import ois.cc.gravity.framework.requests.GReqType;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.MetricsKey;
import ois.radius.cc.entities.MetricsUnit;

import java.util.ArrayList;

public class RequestAOPsSLAConfig extends Request {

    @Param(Optional = false)
    private EN Entity;
    @Param(Optional = false)
    private String EntityId;


    private OMetrics OIMetrics;


    private ORule OIRule;


    public RequestAOPsSLAConfig(String requestid) {
        super(requestid, GReqType.Config, GReqCode.AOPsSLAConfig);
    }

    public EN getEntity() {
        return Entity;
    }

    public void setEntity(EN entity) {
        Entity = entity;
    }

    public String getEntityId() {
        return EntityId;
    }

    public void setEntityId(String entityId) {
        EntityId = entityId;
    }

    public OMetrics getOIMetrics() {
        return OIMetrics;
    }

    public void setOIMetrics(OMetrics OIMetrics) {
        this.OIMetrics = OIMetrics;
    }

    public ORule getOIRule() {
        return OIRule;
    }

    public void setOIRule(ORule OIRule) {
        this.OIRule = OIRule;
    }

    public class OMetrics {
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

    public class  ORule{
        private String Name;

//        private ArrayList<OMetrics> OMetrics;

        private String RuleCondition;

        private ArrayList<OAlertConfig> OIAlertConfigs;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

//        public List<OISLAMetrics> getOIMetrics() {
//            return OISLAMetrics;
//        }
//
//        public void setOIMetrics(ArrayList<OISLAMetrics> OISLAMetrics) {
//            this.OISLAMetrics = OISLAMetrics;
//        }


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
       public class OAlertConfig{
            private ArrayList<Long> XAlertIDs;

            private ArrayList<String> Users = new ArrayList();

            private Boolean InApp;

            public ArrayList<Long> getXAlertIDs() {
                return XAlertIDs;
            }

            public void setXAlertIDs(ArrayList<Long> XAlertIDs) {
                this.XAlertIDs = XAlertIDs;
            }

            public ArrayList<String> getUsers() {
                return Users;
            }

            public void setUsers(ArrayList<String> users) {
                Users = users;
            }

            public Boolean getInApp() {
                return InApp;
            }

            public void setInApp(Boolean inApp) {
                InApp = inApp;
            }
        }
    }
}
