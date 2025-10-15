package ois.cc.gravity.ua;

import ois.cc.gravity.framework.requests.GReqCode;
import ois.radius.cc.entities.EN;
import ois.radius.cc.entities.UserRole;
import ois.radius.cc.entities.tenant.cc.User;

public class AuthChecker {

    /**
     * Return TRUE if user is authorized to perform the request.
     *
     * @param user
     * @param reqcode
     * @param en
     * @return
     */
    public static Boolean IsAuthorized(UserRole user, GReqCode reqcode, EN en)
    {
        switch (user)
        {
            case Agent:
                switch (reqcode)
                {
//                    case AgentInfoFetch:
//                    case AgentReady:
//                    case AgentNotReady:
//                    case AgentWorkReady:
//                    case AgentWorkNotReady:
//                    case AgentStateReasonFetch:
//                    case AgentDispositionFetch:
//                    case AgentTaskLimitFetch:
//                    case AgentContactScheduledCheck:
//                    case AgentContactScheduledAssigned:
//                    case AgentContactAttributeFetch:
//                    case AgentContactAdd:
//                    case AgentContactEdit:
//                    case AgentContactFetch:
//                    case ContactSessionFetch:
//                    case AgentTextTemplateFetch:
//                    case CallAnswer:
//                    case CallReject:
//                    case CallHangup:
////                    case CallDispose:
////                    case CallDisposeEdit:
//                    case CallHold:
//                    case CallUnhold:
//                    case CallTransferToAddress:
//                    case CallTransferToAgent:
//                    case CallTransferToCDN:
//                    case CallTransferToQueue:
//                    case CallConfInit:
//                    case CallConfCall:
//                    case CallConfPartyDialToAgent:
//                    case CallConfPartyDialToAddress:
//                    case CallDisconnectConnection:
//                    case FetchCallConsultDNForType:
//                    case FetchCallTransferDNForType:
//                    case CallConsultInitToAddress:
//                    case CallConsultInitToAgent:
//                    case CallConsultTransfer:
//                    case ChSessAccept:
//                    case ChSessReject:
////                    case ChSessDispose:
////                    case ChSessDisposeEdit:
//                    case ChSessTransfer:
//                    case ChSessHangup:
//                    case ChSessConfInit:
//                    case ChSessConfPartyAdd:
//                    case ChSessConfPartyRemove:
////                    case ContactMerge:
//                    case FetchChSessConfDNForType:
//                    case FetchChSessTransferDNForType:
//                    case EmSessAccept:
//                    case EmSessHangup:
//                    case EmSessReject:
//                    case EmSessTransfer:
//                    case EmSessConsult:
//                    case EmSessOut:
////                    case EmSessDispose:
////                    case EmSessDisposeEdit:
//                    case VdSessAccept:
//                    case VdSessReject:
////                    case VdSessDispose:
////                    case VdSessDisposeEdit:
//                    case VdSessTransfer:
//                    case VdSessHangup:
//                    case VdSessConfInit:
//                    case VdSessConfPartyAdd:
//                    case VdSessConfPartyRemove:
//                    case FetchVdSessTransferDNForType:
//                    case FetchEmSessTransferDNForType:
//                    case CampaignProperties:
////                    case ContactAdd:
////                    case ContactAddressAdd:
////                    case ContactAddressDelete:
//                    case ContactInteractionsFetch:
//                    case AgentSessHistoryFetch:
//                    case ContactSessionReject:
//                    case ContactMarkdone:
////                    case ContactFetch:
//                    case AgentContactScheduledAdd:
//                    case AgentContactScheduledEdit:
//                    case FetchDNForContactSchedule:
//                    case ContactScheduledFetch:
//                    case ContactSessionInit:
//                    case DialContact:
//                    case DialDirect:
//                    case DialManual:
//                    case DialPreview:
//                    case ExtendAgentNotReady:
//                    case AgentStateFetch:
//                    case AgentTerminalStateFetch:
//                    case Login:
//                    case Logout:
//                    case PreviewNextContact:
//                    case RegisterAgentTerminal:
//                    case UnregisterAgentTerminal:
//                    case FetchActiveXServer:
//                    case CampaignAssigned:
//                    case CampaignJoin:
//                    case CampaignLeave:
//                    case AgentAuthenticate:
//                    case CallRecordingStart:
//                    case CallRecordingStop:
//                    case CallRecordingStatusFetch:
//                    case VersionInfoFetch:
//                    case AgentContactAddressAdd:
//                    case AgentContactAddressFetch:
//                    case AgentContactAddressEdit:
//                    case AgentContactAddressDelete:
//                    case DNCRequestAdd:
//                    case AgentSessHistorySummaryFetch:
//                    case AbandonCallFetch:
//                    case UAAPActionListFetch:
//                    case FetchTerminalUsages:
//                    case XSessDispose:
//                    case AgentContactScheduleClear:
//                    case XSessDisposeClear:
//                    case CallDTMF:
//                        return true;
//                    default:
//                        return false;
                }
            case Admin:

                switch (reqcode)
                {
                    case EntitiesEdit:
                    case EntityAdd:
                    case EntityDelete:
                    case EntityEdit:
                    case EntityFetch:
                        return isValidENForReqCode(UserRole.Admin, en, reqcode);
                    case Register:
                    case Logout:
                    case AgentStateReasonFetch:
                    case UserPropertiesConfig:
                    case CampaignAssigned:
                    case CampaignFetch:
                    case AOPsPropertiesConfig:
                    case CampaignScheduleConfig:
                    case CampaignStatFetch:
                    case ContactAttributeEdit:
                    case AgentContactFetch:
                    case ContactScheduledAdd:
                    case ContactScheduledCancel:
                    case ContactScheduledReassign:
                    case ContactScheduledReschedule:
                    case ContactScheduledFetch:
                    case FetchDNForContactSchedule:
                    case ContactListStatFetch:
                    case PredictiveDialerStatusFetch:
                    case DNCAddressAdd:
                    case XServerStatFetch:
                    case FetchXServerUsages:
                    case LoadCampaign:
                    case StartCampaign:
                    case StopCampaign:
                    case StartXServer:
                    case StopXServer:
                    case UnloadCampaign:
                    case VersionInfoFetch:
                    case DNCRequestReview:
                    case DNCApply:
                    case DNCAddressAddForcefully:
                    case ContactStateRuleApply:
                    case DispositionAdd:
                    case DispositionEdit:
                    case BargeInCall:
                    case AgentSkillDelete:
                    case CallAbandonCancel:
                    case AbandonCallFetch:
                    case SkillQueueMap:
                    case SkillQueueUnMap:
                    case UAAPActionListFetch:
                        return true;
                    default:
                        return false;
                }
            case System:
                switch (reqcode)
                {
                    case EntityAdd:
                    case EntitiesEdit:
                    case EntityEdit:
                    case EntityDelete:
                    case EntityFetch:
                        return isValidENForReqCode(UserRole.System, en, reqcode);
                    case Login:
                    case Logout:
//                    case CTClientStatFetch:
//                    case CTClientStart:
//                    case CTClientStop:
                    case LicenseAdd:
//                    case CTClientDBAdd:
//                    case CTClientDBMap:
//                    case CTClientDBConnectionCheck:
                    case TenantPropertiesConfig:
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    private static Boolean isValidENForReqCode(UserRole usertype, EN en, GReqCode reqcode)
    {
        switch (usertype)
        {
            case Admin:
                switch (en)
                {
                    case User:
                    case UserGroup:
                    case UserMedia:
//                    case OAgentMediaMap:
//                    case Agent:
//                    case AgentGroup:
                    case AgentStateReason:
//                    case BlockList:
//                    case BlockAddress:
                    case Campaign:
                    case AOPsMedia:
                    case AOPsSchedule:
//                    case ContactList:
//                    case DNCList:
                    case DialIDPlan:
                    case CallerIDPlan:
//                    case Privilege:
                    case Queue:
                    case Skill:
                    case UserProfile:
                    case Terminal:
                    case XServer:
                        switch (reqcode)
                        {
                            case EntityAdd:
                            case EntitiesEdit:
                            case EntityEdit:
                            case EntityFetch:
                            case EntityDelete:
                                return true;
                            default:
                                return false;
                        }
                    case Profile:
                        switch (reqcode)
                        {
                            case EntityFetch:
                            case EntityDelete:
                                return true;
                            default:
                                return false;
                        }
                    case AgentSkill:
                        switch (reqcode)
                        {
                            case EntityAdd:
                            case EntitiesEdit:
                            case EntityEdit:
                            case EntityFetch:
                                return true;
                            default:
                                return false;
                        }
//                    case Application:
                    case AOPsProperties:
//                    case ProcessProperties:
//                    case Tenant:
//                    case DNCRequest:
//                    case UserProperties:
//                        switch (reqcode)
//                        {
//                            case EntityFetch:
//                                return true;
//                            default:
//                                return false;
//                        }
//                    case License:
//                        switch (reqcode)
//                        {
//                            case EntityFetch:
//                                return true;
//                            default:
//                                return false;
//                        }
//                    case ContactAttribute:
//                        switch (reqcode)
//                        {
//                            case EntityFetch:
//                            case EntityDelete:
//                                return true;
//                            default:
//                                return false;
//                        }
                    case Disposition:
                        switch (reqcode)
                        {
                            case EntityFetch:
                            case EntityDelete:
                                return true;
                            default:
                                return false;
                        }
//                    case DNCAddress:
//                    {
//                        switch (reqcode)
//                        {
//                            case EntitiesEdit:
//                            case EntityFetch:
//                            case EntityDelete:
//                                return true;
//                            default:
//                                return false;
//                        }
//                    }
                    default:
                        return false;
                }
            case System:
//                switch (en)
//                {
//                    case Tenant:
//                        switch (reqcode)
//                        {
//                            case EntityAdd:
//                            case EntitiesEdit:
//                            case EntityEdit:
//                            case EntityFetch:
//                                return true;
//                            default:
//                                return false;
//                        }
//                    case CTClientContact:
//                    case Application:
//                    case AppConfig:
//                        switch (reqcode)
//                        {
//                            case EntityAdd:
//                            case EntitiesEdit:
//                            case EntityEdit:
//                            case EntityFetch:
//                            case EntityDelete:
//                                return true;
//                            default:
//                                return false;
//                        }
//                    case License:
//                        switch (reqcode)
//                        {
//                            case EntityFetch:
//                                return true;
//                            default:
//                                return false;
//                        }
//                    case CTClientDB:
//                        switch (reqcode)
//                        {
//                            case EntityFetch:
//                            case EntityDelete:
//                                return true;
//                            default:
//                                return false;
//                        }
//                }

        }

        return false;
    }
}
