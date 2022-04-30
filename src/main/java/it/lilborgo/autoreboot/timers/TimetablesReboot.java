/*
    This file is part of AutoReboot.
    Copyright (C) 2022  lilborgo

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.lilborgo.autoreboot.timers;

import it.lilborgo.autoreboot.Config;
import it.lilborgo.autoreboot.Main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Arrays;
import java.util.List;

/**
 *  TimetablesReboot class
 *  This class is an implementation of GenericTimer.
 *  This type of timer reboot the server only when its a particular timetable.
 */
public class TimetablesReboot extends GenericTimer{
    private long lastUpdate;
    private boolean rebootNextMin;
    private List<String> timetables;
    private long[] seconds;

    public TimetablesReboot(Main plugin, Config config){
        super(plugin, config);

        rebootNextMin = false;
        lastUpdate = 0;
    }

    @Override
    protected void updater(){
        if(!enabled)
            return;

        //check every 1 second
        if(System.currentTimeMillis()/1000 -lastUpdate < 1)
            return;

        lastUpdate = System.currentTimeMillis()/1000;

        //add 1 minute to now time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 1);

        String now = new SimpleDateFormat("HH:mm").format(calendar.getTime());

        //check the now time (plus 1 minutes) is equals to a timetable
        for (String timetable : timetables) {
            //reboot only when the minutes is changed
            if (timetable.equalsIgnoreCase(now)) {
                rebootNextMin = true;
                break;
            } else if (rebootNextMin) {
                rebootNextMin = false;
                reboot();
            }
        }

        updatePlayers(getSecondsNextReboot());
    }

    @Override
    public void start() {
        stop();

        timetables = plugin.config.timetables;

        //get the seconds of the timetables sorted
        seconds  = new long[timetables.size()];

        for(int i = 0; i <seconds.length; i++)
            seconds[i] = convertToSecond(timetables.get(i));

        Arrays.sort(seconds);

        rebootNextMin = false;
        lastUpdate = System.currentTimeMillis()/1000;
        enabled = true;

        startUpdater();
    }

    @Override
    public void stop() {
        //stop the timer
        enabled = false;
        stopUpdater();
    }

    @Override
    protected long getSecondsNextReboot(){
        Date date = new Date();
        final long now = convertToSecond(new SimpleDateFormat("HH:mm").format(date));

        //check the fist higher of the current seconds
        for (long second : seconds) {

            //if its found return the remaining seconds between the hours
            if (now < second)
                return second - now + (60 - Integer.parseInt(new SimpleDateFormat("ss").format(date))) - 60;
        }

        //if now is the latter than every timetable, pick the first tomorrow timetable and calculate the secons
        return (24*60*60)-now+seconds[0]+(60- Integer.parseInt(new SimpleDateFormat("ss").format(date)))-60;
    }
}
