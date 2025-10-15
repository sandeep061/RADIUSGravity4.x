package ois.cc.gravity.objects;

import ois.radius.cc.entities.EN;

public class OISLAMetrics {

    private EN Entity;

    private String EntityId;

    private OMetrics OIMetrics;

    private ORule OIRule;

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
}
