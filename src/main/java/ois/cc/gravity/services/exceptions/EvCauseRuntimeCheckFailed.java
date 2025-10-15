package ois.cc.gravity.services.exceptions;

public enum EvCauseRuntimeCheckFailed
{
    /**
     * Action requested by this user on a Conference is not allowed as the conference is initiated by another User.
     */
    AgentIsNotUnmappedYet,
    ConferenceInitByAnotherUser,
    ContactNotFoundFromALM,
    AOPsNotStopedInRealm,
    ProcessNotFoundInALM,
    ProcessNotMappedWithCampaign,
    CampaignNotFoundInALM,
    IllegalCampaignMedia,
    AOPsIllegalType,
    NoCampaignMediaConfigured,
    ParamValueOutOfRange,
    AOPsAlreadyExistInALM,
    NoProviderimplFound,
    AOPsBFAlreadyEnabled,
    AOPsBFPropertiesNotFound,
    /**
     *
     * The user cannot perform the requested action as 'AllowManualDial' properties is not set for that specific campaign.
     */
    ManualDialNotAllowed,

    /**
     * Action requested by this user in not allowed, as the ContactScheduled entity is not assigned to this user.
     */
    ContactScheduledNotAssigned,
    /**
     * The action is not allowed since there are some pending contact scheduled exist.
     */
    PendingContactScheduledExist,
    /**
     * The action can not be allowed since there is already an active ContactSession for similar request.
     */
    ContactSessionAlreadyExist,
    AOPsNotFoundInUCOS,
    /**
     * Request failed, as the user is not authorized to access the contact session.
     */
    UnAuthorizedAccessToContactSession,
    /**
     * Request failed, as the user is not authorized to access the interaction.
     */
    UnAuthorizedAccessToInteraction,
    UnAuthorizedAccessToContactBook,
    UnAuthorizedAccessToContactBookAddress,
    /**
     * Action is not allowed as some interactions are exists.
     */
    ActiveInteractionsExist,
    /**
     *
     */
    Disposition_ContactSchedule_Assert_Failed,
    /**
     * Agent performing any terminal related activities without register any terminal
     */
    AgentNotRegisteredAnyTerminal,
    /**
     * Default Disposition of Campaign could not Delete.
     */
    Delete_Default_Disposition_NotAllowed,
    /**
     * Agent can't be deleted if he/she have pending scheduled contacts.
     */
    Agent_Delete_NotAllowed_Have_ContactScheduled,
    /**
     * Default Disposition of Campaign entity state should not be edit to Inactive/Deleted.
     */
    EntityState_Edit_Default_Disposition_NotAllowed,
    /**
     * If disposition is not valid as per the request. <br>
     * - for reject contact session category must be null. <br>
     * - for interactions(CM/NC) category should not be null.
     */
    InvalidDispositionCategory,
    /**
     * If Telephone connection is not in Hold.
     */
    TConnectionNotInHold,
    /**
     * If ContactAddress is in DNC.
     */
    AddressIsInDNC,
    /**
     * Agent can't mapped/unmapped with the entities(like Skill,AgentGroup,AdminGroup...etc) as agent have joined the associated campaign.
     */
    AgentHaveJoinedAssociatedCampaign,
    /**
     * Agents are associated with supplied xserver.
     */
    AgentsHaveAssociatedWithXServer,
    /**
     * If a entity is deleting but the referenced entities are still unmapped. <br>
     * Like - Agents and Campaigns from AgentGroup or AdminGroup,Skill is not delete on Queue delete...etc.
     */
    Delete_NotAllowed_MappedEntity_Still_Exist,
    /**
     * If any action performed on a terminal which is currently used by any agent.
     */
    TerminalIsInUse,
    /**
     * Call(CM call) transfer/consult/conference to external number but found its the same Customer who is currently active in call with Agent or any other
     * external party who is already active in current call.
     */
    PartyAlreadyActiveInInteraction,
    /**
     * If no channel or media server not configured to the campaign yet.
     */
    NoMediaConfiguredForCampaign,
    /**
     * User can't delete the default admin.
     */
    Delete_Default_Admin_NotAllowed,
    /**
     * If recording already initiated and server is getting start recording request again.
     */
    RecordingAlreadyInitiated,
    /**
     * If recording is not initiated yet but server is getting request related to recording like stop/play/pause...etc
     */
    RecordingNotInitiatedYet,
    /**
     * When server not able to find the currently active customer's connection.
     */
    OtherActiveConnectionNotFound,
    /**
     * If contact is not loaded in RADClassLoader yet.
     */
    ContactDBNotLoadedYet,
    /**
     * Contact attribute view is disable to agent and the attribute is Not null. In this scenario agent can't able to add a new contact.
     */
    AgentCanNotViewNotNullContactAttribute,
    /**
     * If found any license violation.
     */
    LicenseError,
    /**
     * If agent send terminal unregister request while he is not register any terminal.
     */
    TerminalNotRegisteredYet,
    /**
     * when agent send any request which are not allowed when he/she joined campaign like unregister terminal...etc.
     */
    AgentStillJoinedAOPs,
    /**
     * If a campaign call made with different media server where the specified campaign is mapped. <br>
     * - Let a agent register with multiple channel and joined with multiple campaigns of different media server then may be this scenario come.
     */
    CampaignNotMappedWithMedia,
    IllegalProcessType,
    IllegalCampaignType,
    QueueNotMappedWithSkillYet,
    CTClientPropertiesNotConfigured,
    CTClientDBNotConfigured,
    DBTestConnectionFailed,
    UnAuthorizedRequest,
    UAAPRequestFailed,
    AbandonCallAlreadyCleared,
    UnAuthorizedApplicationAccess,
    /**
     * If the endpoint doesn't belongs to specified ProviderId.
     */
    InvalidEndPointForProvider,
    InteractionAlreadyDisposed,
    InteractionNotFound,
    NoDialerFound,
    MaxAttemptCountReached,
    CampaignStopAlreadyInitiated,
    ActiveXServerExist,
    TenantStopAlreadyInitiated,
    AgentTaskLimitExceed,
    TenantNotFoundFromNucleus,
    TenantNotFoundFromALM,
    TenantNotStartedYet,
    InvalidToken,
    TenantAlreadyStarted,
    ApplicationNotFoundFromNucleus,
    UnAuthenticatedUser,
    UnAuthorizedUser,
    UserNotFoundFromNucleus,
    ActiveUserSessionExist,
    MappedObjectFound,
    EntityNotDeletedYet,
    LimitExceed,
    SurveyLinkExpired,
    OperationNotAllowed,
    EntityNotCreatedYet,
    PrimaryAndSecondaryAddressShouldNotSame,

}
