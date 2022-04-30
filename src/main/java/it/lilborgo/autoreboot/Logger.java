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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Logger class
 * Logs an event on the console.
 */
public class Logger {
    //plugin prefix
    private final String prefix;

    //types of log
    public enum LogType{
        ERROR(ChatColor.RED),
        INFO(ChatColor.WHITE),
        WARNING(ChatColor.YELLOW),
        SUCCESS(ChatColor.GREEN);

        private final ChatColor color;

        LogType(ChatColor color){
            this.color = color;
        }

        public ChatColor getColor(){
            return color;
        }
    }

    public Logger(String prefix){
        this.prefix = prefix;
    }

    //send message to the console with type
    public void log(String message, LogType type){
        Bukkit.getConsoleSender().sendMessage(type.getColor()+ "["+prefix+"] " + message);
    }

    //send message to the console
    public void log(String message){
        Bukkit.getConsoleSender().sendMessage(LogType.INFO.getColor() + "["+prefix+"] " + message);
    }

    //send a list message
    public void log(String[] messages, LogType type){
        for(String message : messages){
            log(message, type);
        }
    }

    //send to a player
    public static void sendPlayer(String name, String message){
        Player player = Bukkit.getPlayer(name);

        if (player != null) {
            player.sendMessage(message);
        }
    }
}
