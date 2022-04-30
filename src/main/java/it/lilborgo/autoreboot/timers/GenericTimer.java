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
import it.lilborgo.autoreboot.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

/**
 * GenericTimer class
 * This class is used to create an any type timer (timer or timetable).
 * The method must be redefined in a son class.
 */
public class GenericTimer {
    protected Main plugin;

    //is the timer enabled?
    protected boolean enabled;

    //config file
    protected Config config;

    private long lastMinute;

    //updater task
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            updater();
        }
    };

    private int taskID;

    public GenericTimer(Main plugin, Config config){
        this.plugin = plugin;
        this.config = config;
        enabled = false;
        taskID = 0;
    }

    //start the automatic updater. Call the update method every 10 game ticks
    protected void startUpdater(){
        taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, task, 0, 10);
    }

    protected void stopUpdater(){
        if(Bukkit.getServer().getScheduler().isCurrentlyRunning(taskID))
            Bukkit.getServer().getScheduler().cancelTask(taskID);
    }

    //execute the reboot command
    public static void reboot(){
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, Config.getConfig().restartCommand);
    }

    //update the timer
    protected void updater(){}

    //start the timer
    public void start(){}

    //stop the timer
    public void stop(){}

    //get the remaining time as String
    public String getRemaining(){
        long hours, minutes, seconds, timeLeft;

        if(!enabled)
            return "Timer is off";

        timeLeft = getSecondsNextReboot();

        //convert seconds into hours minutes and seconds
        hours = timeLeft/3600;
        seconds = timeLeft - hours* 3600;
        minutes = seconds/60;
        seconds = seconds-minutes*60;

        return "" + hours + "h " + minutes + "m " + seconds + "s";
    }

    //get the seconds remaining to the next reboot
    protected long getSecondsNextReboot(){
        return 0;
    }

    //return if its running
    public boolean isRunning(){
        return enabled;
    }

    //get the GenericTimer in base of the configuration
    public static GenericTimer getTimer(Main plugin, Config config){
        String timerType = Config.getConfig().timerType;

        if(timerType.equalsIgnoreCase(Config.TimerType.TIMER.toString())){
            return new TimerReboot(plugin, config);
        }
        else if(timerType.equalsIgnoreCase(Config.TimerType.TIMETABLES.toString())){
            return new TimetablesReboot(plugin, config);
        }
        else if(timerType.equalsIgnoreCase(Config.TimerType.OFF.toString())){
            return new GenericTimer(plugin, config);
        }

        plugin.logger.log("Error: '" + timerType + "' is not valid!", Logger.LogType.ERROR);

        return new GenericTimer(plugin, config);
    }

    //convert the hh:mm to seconds
    protected long convertToSecond(String hour){
        long s;
        s = Integer.parseInt(hour.substring(0, hour.indexOf(':')))* 60L;
        s += Integer.parseInt(hour.substring(hour.indexOf(':')+1));

        return s*60;
    }

    //send message to all players of the remaining time before rebooting
    protected void updatePlayers(long remainingPlayers){
        long minutes = (long) Math.ceil((remainingPlayers)/60.0);

        //send minutes update
        if(lastMinute != minutes){
            for(int i = 0; i < config.minutes.length; i++){
                if(config.minutes[i] == minutes){
                    plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format(config.minutesMessage, minutes)));
                    lastMinute = minutes;
                }
            }
        }

        //send second update
        if(remainingPlayers <= config.seconds)
            plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format(config.secondsMessage, remainingPlayers)));
    }
}
