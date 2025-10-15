package ois.cc.gravity.entities.util;

import ois.radius.cc.entities.tenant.cc.AOPsSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class UtilAOPsSH {
    private static final Logger _logger = LoggerFactory.getLogger(UtilAOPsSH.class);

    public static Date getTodayStartAt(AOPsSchedule campsch)
    {
        // for all started AOPs we need todays startAt. we will not consider wheter start time passed or not.
        Date stTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        try
        {
            startTime.setTime(sdf.parse(campsch.getStartHour()));
            endTime.setTime(sdf.parse(campsch.getEndHour()));
        }
        catch (ParseException pex)
        {
            _logger.error(pex.getMessage());
            return stTime;
        }

        Calendar CurrTime = Calendar.getInstance();
        CurrTime.setMinimalDaysInFirstWeek(1);

        int currWk = CurrTime.get(Calendar.WEEK_OF_MONTH);
        int currD = CurrTime.get(Calendar.DAY_OF_WEEK);
        int currM = CurrTime.get(Calendar.MONTH) + 1;

        //Check wether the current day is part of month,week,day.
        if (IsExist(campsch.getDays(), currD) && IsExist(campsch.getWeeks(), currWk) && IsExist(campsch.getMonths(), currM))
        {
            //Get Start Hour,Minute and End Hour,Minute.
            int startHr = startTime.get(Calendar.HOUR_OF_DAY);
            int startMin = startTime.get(Calendar.MINUTE);

            int endHr = endTime.get(Calendar.HOUR_OF_DAY);
            int endMin = endTime.get(Calendar.MINUTE);

            /**
             * Create todays start time.
             */
            Calendar todayEndTime = Calendar.getInstance();
            todayEndTime.set(Calendar.HOUR_OF_DAY, endHr);
            todayEndTime.set(Calendar.MINUTE, endMin);

            _logger.trace("todayEndTime = " + todayEndTime.getTime() + " " + "CurrTime = " + CurrTime.getTime());
            /**
             * Check the end time also if todays end time already passed then no need to start.
             */
//            if (todayEndTime.after(CurrTime))
            {
                /**
                 * Create the todays start time, based on the end time
                 */
                Calendar todayStartTime = (Calendar) todayEndTime.clone();
                todayStartTime.set(Calendar.SECOND, 0);
                todayStartTime.set(Calendar.MILLISECOND, 0);

                todayStartTime.set(Calendar.MINUTE, startMin);

                //StartHr: 17, End hour: 11
                todayStartTime.set(Calendar.HOUR_OF_DAY, startHr);

                stTime = todayStartTime.getTime();
            }
        }

        return stTime;
    }

    public static Date getNextStartAt(AOPsSchedule campsch)
    {
        Date stTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        try
        {
            startTime.setTime(sdf.parse(campsch.getStartHour()));
            endTime.setTime(sdf.parse(campsch.getEndHour()));
        }
        catch (ParseException pex)
        {
            _logger.error(pex.getMessage());
            return stTime;
        }

        Calendar CurrTime = Calendar.getInstance();
        CurrTime.setMinimalDaysInFirstWeek(1);

        int currWk = CurrTime.get(Calendar.WEEK_OF_MONTH);
        int currD = CurrTime.get(Calendar.DAY_OF_WEEK);
        int currM = CurrTime.get(Calendar.MONTH) + 1;

        //Check wether the current day is part of month,week,day.
        if (IsExist(campsch.getDays(), currD) && IsExist(campsch.getWeeks(), currWk) && IsExist(campsch.getMonths(), currM))
        {
            //Get Start Hour,Minute and End Hour,Minute.
            int startHr = startTime.get(Calendar.HOUR_OF_DAY);
            int startMin = startTime.get(Calendar.MINUTE);

            int endHr = endTime.get(Calendar.HOUR_OF_DAY);
            int endMin = endTime.get(Calendar.MINUTE);

            /**
             * Create todays start time.
             */
            Calendar todayEndTime = Calendar.getInstance();
            todayEndTime.set(Calendar.HOUR_OF_DAY, endHr);
            todayEndTime.set(Calendar.MINUTE, endMin);

            _logger.trace("todayEndTime = " + todayEndTime.getTime() + " " + "CurrTime = " + CurrTime.getTime());
            /**
             * Check the end time also if todays end time already passed then no need to start.
             */
            //  if (todayEndTime.after(CurrTime))
            {
                /**
                 * Create the todays start time, based on the end time
                 */
                Calendar todayStartTime = (Calendar) todayEndTime.clone();
                todayStartTime.set(Calendar.SECOND, 0);
                todayStartTime.set(Calendar.MILLISECOND, 0);

                todayStartTime.set(Calendar.MINUTE, startMin);

//                if (endHr >= startHr)
//                {
                //Ex: StartHr: 05, EndHr:23
                //today start date.
                todayStartTime.add(Calendar.HOUR_OF_DAY, (24 - endHr) + startHr);
//                }
//                else
//                {
//                    //StartHr: 17, End hour: 11
//                    todayStartTime.set(Calendar.HOUR_OF_DAY, startHr);
//                }

                stTime = todayStartTime.getTime();
            }
        }

        return stTime;
    }

    public static Date getNextStopAt(AOPsSchedule campsch)
    {
        Date stopTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        try
        {
            startTime.setTime(sdf.parse(campsch.getStartHour()));
            endTime.setTime(sdf.parse(campsch.getEndHour()));
        }
        catch (ParseException pex)
        {
            _logger.error(pex.getMessage());
            return null;
        }

        Calendar CurrTime = Calendar.getInstance();
        CurrTime.setMinimalDaysInFirstWeek(1);

        int currWk = CurrTime.get(Calendar.WEEK_OF_MONTH);
        int currD = CurrTime.get(Calendar.DAY_OF_WEEK);
        int currM = CurrTime.get(Calendar.MONTH) + 1;

        //Check wether the current day is part of month,week,day.
        if (IsExist(campsch.getDays(), currD) && IsExist(campsch.getWeeks(), currWk) && IsExist(campsch.getMonths(), currM))
        {
            //Get Start Hour,Minute and End Hour,Minute.
            int startHr = startTime.get(Calendar.HOUR_OF_DAY);
            int startMin = startTime.get(Calendar.MINUTE);

            int endHr = endTime.get(Calendar.HOUR_OF_DAY);
            int endMin = endTime.get(Calendar.MINUTE);

            /**
             * Create todays start time.
             */
            Calendar todayStartTime = Calendar.getInstance();
            todayStartTime.set(Calendar.HOUR_OF_DAY, startHr);
            todayStartTime.set(Calendar.MINUTE, startMin);

            _logger.trace("todayStartTime = " + todayStartTime.getTime() + " " + "CurrTime = " + CurrTime.getTime());
//            if (todayStartTime.before(CurrTime))
            {
                /**
                 * Create the todays end time, based on the start time
                 */
                Calendar todayEndTime = (Calendar) todayStartTime.clone();
                todayEndTime.set(Calendar.SECOND, 0);
                todayEndTime.set(Calendar.MILLISECOND, 0);

                todayEndTime.set(Calendar.MINUTE, endMin);

                if (startHr > endHr)
                {
                    //Ex: StartHr: 23, EndHr:05
                    //today end date.
                    todayEndTime.add(Calendar.HOUR_OF_DAY, (24 - startHr) + endHr);
                }
                else
                {
                    //StartHr: 11, End hour: 17
                    todayEndTime.set(Calendar.HOUR_OF_DAY, endHr);
                }

                stopTime = todayEndTime.getTime();
            }
        }

        return stopTime;
    }

    private static Boolean IsExist(String frm, int to)
    {
        return Arrays.asList(frm.split(",")).contains(to + "");
    }

    public static Date getStartAt(AOPsSchedule campsch) throws ParseException
    {

        String timeString = campsch.getStartHour();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Date timePart = timeFormat.parse(timeString);
        // Create a Calendar object and set it to the datePart
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(campsch.getStartDate());
        // Set the hours and minutes from the timePart
        calendar.set(Calendar.HOUR_OF_DAY, timePart.getHours());
        calendar.set(Calendar.MINUTE, timePart.getMinutes());
        // Get the combined date and time
        Date combinedDate = calendar.getTime();

        return combinedDate;

    }

    public static Date getEndAt(AOPsSchedule campsch) throws ParseException
    {

        String Endtime = campsch.getEndHour();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date timePart = timeFormat.parse(Endtime);
        // Create a Calendar object and set it to the datePart
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(campsch.getEndDate());
        // Set the hours and minutes from the timePart
        calendar.set(Calendar.HOUR_OF_DAY, timePart.getHours());
        calendar.set(Calendar.MINUTE, timePart.getMinutes());
        // Get the combined date and time
        Date combinedDate = calendar.getTime();

        return combinedDate;

    }

//    public static void doProcessForSchAlter(TenantContext cctx, AOPs aops) throws CODEException, GravityException {
//
//        AOPsSchedule aopssch = cctx.getDB().Find(new AOPsScheduleQuery().filterByAops(aops.getId()));
//        if (aopssch == null)
//        {
//            _logger.debug("AOPsSchedule not found for AOPs : " + aops);
//            return;
//        }
//        if (!aopssch.getIsScheduleEnable())
//        {
//            _logger.debug("IsSchedule is not enabled for AOPsSchedule : " + aopssch);
//            return;
//        }
//        AIAOPs aiaops = cctx.getAOPsStore().GetById(aops.getId());
//        if (aiaops != null)
//        {
//            aiaops.LoadAOPsSchedule();
//            doProcessForStartAOPss(cctx, aiaops, aopssch);
//        }
//        else
//        {
//            doProcessForStopAOPss(cctx, aopssch);
//        }
//
//    }

//    public static void LoadAllAOPsScheduled(TenantContext cctx) throws CODEException
//    {
//        ArrayList<AOPsSchedule> aopsschs = cctx.getDB().Select(new AOPsScheduleQuery().filterByIsSchEnable(Boolean.TRUE));
//        for (AOPsSchedule aopssch : aopsschs)
//        {
//            doProcessForStopAOPss(cctx, aopssch);
//        }
//    }

//    public static void doProcessForStopAOPss(TenantContext cctx, AOPsSchedule aopssch) throws RADUnhandledException, CODEException
//    {
//
//        AOPsStore aopsstore = cctx.getAOPsStore();
//
//        Date nxtStAt = getNextStartAt(aopssch);
//        if (nxtStAt != null)
//        {
//            aopsstore.putNextStartAt(aopssch.getAOPs(), nxtStAt);
//
//            statEvProc.This().Build_Send_AOPsSchAltEv(cctx, aopssch, nxtStAt, null);
//            _logger.debug(aopssch.getAOPs().toString() + " is scheduled to Start at " + nxtStAt);
//        }
//
//    }
//
//    public static void doProcessForStartAOPss(TenantContext cctx, AIAOPs aiaops, AOPsSchedule aopssch) throws RADUnhandledException, CODEException
//    {
//        AOPsStore aopstore = cctx.getAOPsStore();
//
//        //If AutoStart is enable then only we can start campaign.
//        if (!aiaops.getAOPsProps().getGlobal().getAutoStart())
//        {
//            _logger.trace("AutoStart is not enabled for " + aiaops.toString());
//            return;
//        }
//
//        AOPsSchedule campSch = aiaops.getAOPsSchedule();
//        if (campSch == null)
//        {
//            _logger.trace("AOPsSchedule not found for the " + aiaops.getAOPs().toString());
//            return;
//        }
//
//        Date todayStAt = getTodayStartAt(campSch);
//        Date nextStrtAt = getNextStartAt(campSch);
//        Date nxtStpAt = getNextStopAt(campSch);
//
//        if (todayStAt != null)
//        {
//            aopstore.RemoveNextStartAt(aiaops.getAOPs());
//            aiaops.setAopsStartAt(todayStAt);
//
//            _logger.debug(aiaops.toString() + " is scheduled to Start at " + todayStAt);
//        }
//        if (nextStrtAt != null)
//        {
//            aiaops.setAopNextStartAt(nextStrtAt);
//
//            _logger.debug(aiaops.toString() + " is scheduled to Start at " + todayStAt);
//        }
//
//        if (nxtStpAt != null)
//        {
//            aiaops.setAopsNextStopAt(nxtStpAt);
//
//            _logger.debug(aiaops.toString() + " is scheduled to Stop at " + nxtStpAt);
//        }
//        if (todayStAt != null && nxtStpAt != null)
//        {
//            statEvProc.This().Build_Send_AOPsSchAltEv(cctx, aopssch, todayStAt, nxtStpAt);
//            for (Channel chn : aiaops.getChannels())
//            {
//                XProviderStub xpr = aiaops.getProviders().get(chn);
//                if (xpr != null)
//                {
//                    xpr.AOPsStatus(aiaops.getAOPs().getCode(), todayStAt, nxtStpAt);
//                }
//            }
//        }
//    }

}

//}
