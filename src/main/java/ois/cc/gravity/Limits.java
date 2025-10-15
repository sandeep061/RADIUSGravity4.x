package ois.cc.gravity;

public class Limits
{
    public static Integer ContactScheduledOnAfterNow = 5;
    public static Integer StringValue_Max_Length = 512;
    public static Integer ContactScheduled_AutoExpiry_Hour = 1024;
    public static Integer ContactScheduledOnAfter_MAX = 184319;//128 days in minutes.
    public static Integer ContactScheduledOnAfter_MIN = 5;
    public static Integer Global_RedialExpiryTimeout = 1024;
    public static Long XSPIRequestTimeout_MAX = 120 * 1000L;

    public static Integer DialLimit_MIN = 1;
    public static Integer DialLimit_MAX = 1024;
    public static Integer Default_DialTimeOut = 30;//30 sec.
    public static Integer Global_PreviewTimeout = 0;
    public static Integer AttemptCount_MAX = 1024;
    public static Integer RedialDelay   = 192;
    public static Integer RedialDelay_Min   = 4;
    public static Integer DisposeExtend = 8;
    public static Integer SessionExtend = 8;
    public static Integer AutoAnsDelay  =  1024;
    public static Integer XT_RecordingBeepDuration = 4;
    public static Integer XT_MAXRecordingBeepDuration=128;
     public static Integer Survey_ExpiryTimeOut = 128;

    /**
     * Upto 4 level of sub dispositions.
     */
    public static Integer Disposition_Sub_Level = 4;
    public static Integer Max_NoOf_Disposition = 1024;

    /**
     * Maximum no of concurrent tasks/interactions (irrespective of channel) an agent should handle.
     */
    public static Integer Agent_Max_Task_Limit = 8;

    /**
     * AgentStateChange.
     */
    public static Integer AgentStateChange_Timeout_MAX = 3600;//1 hour.
    public static Integer AgentStateChange_Timeout_MIN = 30;//30 sec
    public static Integer AgentStateChange_ExtendTime_MAX = 4;

    /**
     * Entities DB.
     */
    public static Integer DB_MAX_PoolSize = 256;
    public static Integer DB_MIN_PoolSize = 4;
    public static Integer DB_Query_Max_Limit = 128;
    public static Integer DB_Query_Def_Offset = 0;
}
