/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package ois.cc.gravity.framework.requests;

import code.ua.requests.RequestCode;

/**
 *
 * @author Deepak
 */
public enum GReqCode implements RequestCode
{

    System,
    SurveyDataFetch,
    SUSignin,
    UserSignin,
    Register,
    Login,
    Logout,
    ClearTemporaryState,
    OSLogin,
    AuthCheck,
    //System
    TenantStatFetch,
    LicenseAdd,
    TenantDBAdd,
    TenantAdd,
    TenantEdit,
    TenantFetch,
    TenantDBMap,
    TenantPropertiesConfig,
    TenantStart,
    TenantStop,
    XSPIClientConfig,
    XSPIClientDiscover,
    XSPIConnectConfig,
    XSPIConnectAdd,
    XSPIConnectEdit,
    XSPIClientDelete,
    AOPsCDNAddressEdit,
    AOPsCallerIdAddressEdit,
    AOPsBFPropertiesConfig,
    AOPsBFPropertiesFetch,
    CrossCXContactMapAdd,
    CrossCXContactMapDelete,
    SurveyFormConfig,
    /**
     * break;
     */
    //Config
    EntityAdd,
    EntityEdit,
    EntitiesEdit,
    EntityFetch,
    EntityDelete,
    AOPsPropertiesFetch,
    XPlatformDelete,
    AOPsScheduleConfig,
    /**
     * Config Agent and Admin
     */
    AgentAuthenticate,
    AgentContactScheduledAdd,
    AgentContactScheduledEdit,
    AgentContactScheduledCheck,
    AgentContactScheduledAssigned,
    UserPropertiesConfig,
    DispositionFetch,
    TenantChannelFetch,
    UserPropertiesFetch,
    /**
     * Common for Agent and Admin.
     */
    CampaignFetch,
    AgentStateReasonFetch,
    AgentStateHistoryFetch,
    /**
     * Framework
     */
    VersionInfoFetch,
    UAAPActionListFetch,
    Ping,
    /**
     * Admin.Config.Campaign and Contact
     */
    AOPsPropertiesConfig,
    //    CampaignContactEdit,
    CampaignScheduleConfig,
    ContactAddressAdd,
    ContactScheduledAdd,
    ContactScheduledCancel,
    ContactScheduledReschedule,
    ContactScheduledReassign,
    ContactScheduledFetch,
    ContactListStatFetch,
    PredictiveDialerStatusFetch,
    //common for Admin and Agent.
    AbandonCallFetch,
    FetchDNForContactSchedule,
    //This reqcode is no more used becoz we are restrict separate adding contact attribues. this is merged with ContactDBCraete req.
    //    ContactAttributeAdd,
    ContactAttributeEdit,
    ContactStateRuleApply,
    AgentInfoFetch,
    DNCAddressAdd,
    DNCAddressAddForcefully,
    DNCRequestReview,
    DNCApply,
    CallRecordingStart,
    CallRecordingStop,
    CallRecordingPlay,
    CallRecordingPause,
    CallRecordingStatusFetch,
    DispositionAdd,
    DispositionEdit,
    /**
     * Admin.Config
     */
    AgentSkillDelete,
    ProfileAdd,
    ProfileEdit,
    SkillQueueMap,
    SkillQueueUnMap,
    /**
     * Admin.Config.AgentDisposition
     */
    AgentDispositionFetch,
    /**
     * break;
     */
    //Campaign and Contact
    StartCampaign,
    StopCampaign,
    LoadCampaign,
    UnloadCampaign,
    //    CreateContactDB,
    AgentContactAdd,
    //    ContactEdit,
    //    ContactFetch,
    //    ContactDelete,
    //    ContactMerge,
    //    ContactChurn,
    CallRecordFetch,
    CampaignStatFetch,
    ContactInteractionsFetch,
    //Admin.Control
    BargeInCall,
    CallAbandonCancel,
    /**
     * Agent
     */
    InteractionFetch,
    DNCRequestAdd,
    AgentTaskLimitFetch,
    XSessDispose,
    //Agent.Terminal
    RegisterAgentTerminal,
    UnregisterAgentTerminal,
    AgentTerminalStateFetch,
    AgentSessHistorySummaryFetch,
    AgentSessHistoryFetch,
    FetchTerminalUsages,
    // AgentSessSummaryFetch,
    XSessDisposeClear,
    AgentContactScheduleClear,
    AgentInteractionsFetch,
    // AgentSessSummaryFetch,
    //Agent.State
    AgentReady,
    AgentNotReady,
    AgentWorkReady,
    AgentWorkNotReady,
    ExtendAgentNotReady,
    AgentStateFetch,//--
    //Agent.Campaign
    CampaignAssigned,
    CampaignJoin,
    CampaignLeave,
    AgentTextTemplateFetch,
    //Agent.Contact
    PreviewNextContact,
    ContactSessionReject,
    ContactMarkdone,
    //    AgentContactAdd,
    AgentContactEdit,
    AgentContactFetch,
    ContactSessionFetch,
    ContactSessionInit,
    AgentContactAddressFetch,
    AgentContactAddressDelete,
    AgentContactAddressAdd,
    AgentContactAddressEdit,
    AgentContactAttributeFetch,
    /*Agent.Telephony*/
    DialBack,
    CallAnswer,
    CallHangup,
    CallReject,
    CallHold,
    CallUnhold,
    CallDisconnectConnection,
    FetchCallTransferDNForType,
    FetchCallConsultDNForType,
    FetchCallConfDNForType,
    /*Agent.Telephony.Transfer*/
    CallTransferToAddress,
    CallTransferToAgent,
    CallTransferToCDN,
    CallTransferToQueue,
    /*Agent.Telephony.Consult*/
    CallConsultTransfer,
    CallConsultInitToAddress,
    CallConsultInitToAgent,
    /*Agent.Telephony.Conference*/
    CallConfPartyDialToAddress,
    CallConfPartyDialToAgent,
    CallConfInit,
    CallConfCall,
    CallConfDialToAddress,
    CallConfDialToAgent,
    CallConfAddParty,
    CallConfDeleteParty,
    CallConfMute,
    CallConfUnmute,
    /*Agent.Telephony.Dispose*/
    ScheduleCallBack,
    CallDTMF,
    /**
     * Non campaign call dial request.
     */
    DialDirect,
    /**
     * Campaign call dial request.
     */
    DialManual,
    DialPreview,
    DialContact,
    /*Agent.Email*/
    EmSessOut,
    EmSessAccept,
    EmSessReject,
    EmSessHangup,
    EmSessConsult,
    EmSessTransfer,
    FetchEmSessTransferDNForType,
    /**
     * Chat.
     */
    ChSessAccept,
    ChSessReject,
    ChSessHangup,
    ChSessConfInit,
    ChSessConfPartyAdd,
    ChSessConfPartyRemove,
    ChSessConsult,
    ChSessTransfer,
    FetchChSessConfDNForType,
    FetchChSessTransferDNForType,
    //X-Server
    StartXServer,
    StopXServer,
    FetchActiveXServer,
    FetchXServerUsages,
    XServerStatFetch,
    /**
     * video
     */
    VdSessAccept,
    VdSessConfInit,
    VdSessConfPartyAdd,
    VdSessConfPartyRemove,
    VdSessHangup,
    VdSessTransfer,
    FetchVdSessConfDNForType,
    FetchVdSessTransferDNForType,
    VdSessReject,
    /**
     * SMS
     */
    MSessAccept,
    MSessConfInit,
    MSessConfPartyAdd,
    MSessConfPartyRemove,
    MSessHangup,
    MSessTransfer,
    FetchMSessConfDNForType,
    FetchMSessTransferDNForType,
    MSessReject,
    SurveyDataConfig,
    SurveyInfoFetch,
    //for POM
    XSessEnd,
    AOPsSLAConfig,
    AOPsAIPropertiesAdd,
    AOPsAIPropertiesEdit,
    XAlertDRAdd,
    HealthCheck;

    private GReqCode()
    {

    }
}
