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

package it.lilborgo.autoreboot;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * Config class
 * Contains all the configurations and reading methods.
 */
public class Config {

    //config

    //message sent when the plugin is reloaded
    public String reloadMessage;

    //timer type (timer, timetable)
    public String timerType;

    //time for the type timer
    public String timerTime;

    //list of timetables to reboot the server
    public List<String> timetables;

    //command sent to the server when rebooting
    public String restartCommand;

    //resent the timer when the plugin is reloaded
    public boolean resetOnReload;

    //minutes before restarting to send a chat warning message
    public int[] minutes;

    //seconds before the warning message
    public int seconds;

    //message sent to player when lasts n minutes
    public String minutesMessage;

    //message sent to player when lasts n seconds
    public String secondsMessage;


    //private

    private static Main plugin;

    private static Config config;

    //types of timer (timetables, timer, off)
    public enum TimerType{
        TIMETABLES("timetables"),
        TIMER("timer"),
        OFF("off");

        private final String type;

        TimerType(String type){
            this.type = type;
        }

        @Override
        public String toString(){
            return type;
        }
    }

    public Config(Main plugin){
        Config.plugin = plugin;

        reloadMessage = "";
        timerType = "";
        timerTime = "";
        timetables = Collections.singletonList("");
        restartCommand = "";
        resetOnReload = false;
        minutes = new int[1];
        seconds = 0;
        minutesMessage = "";
        secondsMessage = "";

        config = this;
    }

    public void loadConfig(){
        FileConfiguration conf = plugin.getConfig();

        reloadMessage = conf.getString("reload-message");
        timerType = plugin.getConfig().getString("timer-type");
        restartCommand = plugin.getConfig().getString("reboot-command");
        resetOnReload = plugin.getConfig().getBoolean("reset-on-reload");
        timerTime = conf.getString("timer-time");
        timetables = conf.getStringList("reboot-timestamp");
        secondsMessage = conf.getString("seconds-message");
        minutesMessage = conf.getString("minutes-message");

        if(restartCommand == null || restartCommand.equalsIgnoreCase(""))
            restartCommand = "restart";

        if(timerTime == null || timerTime.equalsIgnoreCase(""))
            timerType = TimerType.OFF.toString();

        seconds = conf.getInt("seconds");
        List<Integer> minList = conf.getIntegerList("minutes");
        minutes = new int[minList.size()];

        for(int i = 0; i < minList.size(); i++)
            minutes[i] = minList.get(i);
    }

    public static Config getConfig() {
        return config;
    }
}
