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

/**
 * TimerReboot class
 * Reboot the server every n seconds.
 */
public class TimerReboot extends GenericTimer{
    private long beginTimer;
    private long timeToWait;
    private long lastUpdate;

    public TimerReboot(Main plugin, Config config) {
        super(plugin, config);

        beginTimer = 0;
        timeToWait = 0;
        lastUpdate = 0;
    }

    @Override
    protected void updater(){
        final long now, timePassed;

        if(!enabled)
            return;

        //check every 1 second
        if(System.currentTimeMillis()/1000 -lastUpdate < 1)
            return;

        now = System.currentTimeMillis()/1000;
        timePassed = now-beginTimer;

        lastUpdate = now;

        //reboot
        if(timePassed > timeToWait) {
            beginTimer = System.currentTimeMillis()/1000;
            reboot();
        }

        updatePlayers(timeToWait-timePassed);
    }

    @Override
    public void start() {
        stop();

        timeToWait = convertToSecond(config.timerTime);

        beginTimer = System.currentTimeMillis()/1000;
        enabled = true;

        lastUpdate = beginTimer;

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
        return timeToWait-(System.currentTimeMillis()/1000-beginTimer);
    }
}
