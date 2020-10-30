package cn.eoe.example.hkmodule.util;

import com.hikvision.netsdk.NET_DVR_TIME;

/**
 * Created by jiang at 2020/10/17
 * 监控回放的，时间段
 */
public class PlayerBackTime {
    NET_DVR_TIME timeStart = new NET_DVR_TIME();
    NET_DVR_TIME timeStop = new NET_DVR_TIME();

    public PlayerBackTime() {
    }
    public void  setTimeStart(int Start_dwYear ,
                              int Start_dwMonth ,
                              int Start_dwDay ,
                              int start_hour
    ){
        timeStart.dwYear=Start_dwYear;
        timeStart.dwMonth=Start_dwMonth;
        timeStart.dwDay=Start_dwDay;
        timeStart.dwHour=start_hour;
    }

    public void  setTimeStart(int Start_dwYear ,
            int Start_dwMonth ,
            int Start_dwDay
       ){
        timeStart.dwYear=Start_dwYear;
        timeStart.dwMonth=Start_dwMonth;
        timeStart.dwDay=Start_dwDay;
    }
    public void  setTimeStart(int Start_dwYear ,
                               int Start_dwMonth ,
                               int Start_dwDay ,
                               int start_hour,int start_dwMinute
    ){
        timeStart.dwYear=Start_dwYear;
        timeStart.dwMonth=Start_dwMonth;
        timeStart.dwDay=Start_dwDay;
        timeStart.dwHour=start_hour;
        timeStart.dwMinute=start_dwMinute;
    }
    public void  setTimeStop(int Stop_dwYear ,
                             int Stop_dwMonth ,
                             int Stop_dwDay

    ){
        timeStop.dwYear=Stop_dwYear;
        timeStop.dwMonth=Stop_dwMonth;
        timeStop.dwDay=Stop_dwDay;
    }
    public void  setTimeStop(int Stop_dwYear ,
                               int Stop_dwMonth ,
                               int Stop_dwDay ,
                               int Stop_hour
    ){
        timeStop.dwYear=Stop_dwYear;
        timeStop.dwMonth=Stop_dwMonth;
        timeStop.dwDay=Stop_dwDay;
        timeStop.dwHour=Stop_hour;
    }
    public void  setTimeStop(int Stop_dwYear ,
                              int Stop_dwMonth ,
                              int Stop_dwDay ,
                              int Stop_hour,int Stop_dwMinute
    ){
        timeStop.dwYear=Stop_dwYear;
        timeStop.dwMonth=Stop_dwMonth;
        timeStop.dwDay=Stop_dwDay;
        timeStop.dwHour=Stop_hour;
        timeStop.dwMinute=Stop_dwMinute;
    }
    public NET_DVR_TIME getTimeStart() {
        return timeStart;
    }

    public NET_DVR_TIME getTimeStop() {
        return timeStop;
    }
}
