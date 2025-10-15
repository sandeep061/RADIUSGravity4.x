/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ois.cc.gravity.framework.events;

import code.ua.events.EventCodeIA;
import ois.radius.cc.entities.tenant.cc.AOPsCDN;
import ois.radius.cc.entities.tenant.cc.AOPsCSATConf;
import ois.radius.cc.entities.tenant.cc.AOPsCallerId;
import ois.radius.cc.entities.tenant.cc.ContactBook;

/**
 * @author Prakasha.prusty 5 Aug, 2024
 */
public enum EventCode implements EventCodeIA
{
    TenantStarted,
    XServerEndpointPropertiesFetched,
    UserMediaFetched,
    AgentMediaMapFetched,
    HealthCheck,
    TenantStoped,
    SurveyInfoFetch,
    ClearTemporaryState,
    VersionInfoFetched,
    UserRegistered,
    UserRegisterFailed,
    Failed,
    ContactBook,
    AOPsCSATConf,
    EntitiesFetched,
    AOPsPropertiesFetched,
    EntityNotFound,
    ObjectIllegalState,
    AttributeInvalid,
    RequestValidationFailed,
    CampaignDeleted,
    EntityExists,
    RESTException,
    SkillQueueUnMapped,
    RuntimeCheckFailed,
    UserPropertiesFetched,
    UserLogout,
    ProcessFailed,
    AOPsCDN,
    AOPsScheduled,
    AOPsCallerId,
    AOPsBFPropertiesFetched,
    CrossCXContactMapFetched,
    SurveyFormFetch,
    SurveyDataFetch,
    SurveyDataInfo,
    OIAlertConfigFetch,
    OISLAMetricsFetch,
    AttributeValidationFailed;
}
