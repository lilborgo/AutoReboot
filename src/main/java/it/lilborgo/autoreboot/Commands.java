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

import it.lilborgo.autoreboot.timers.GenericTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Commands class
 * Contains all the command handler.
 */
public class Commands {
    private final Main plugin;

    //all arguments and explanation
    public static final String[][] ARGS = {
            {"help", "show help list"},
            {"reload", "reload the plugin and restore the config timer"},
            {"reboot", "hh:mm reboot the server in the time"},
            {"tleft", "get remaining time before reboot"},
            {"cancel", "stop the timer"}
    };

    //argument types
    public enum ArgType {
        HELP("help"),
        RELOAD("reload"),
        REBOOT("reboot"),
        TLEFT("tleft"),
        CANCEL("cancel");

        private final String args;

        ArgType(String args){
            this.args = args;
        }

        @Override
        public String toString(){
            return args;
        }
    }

    public Commands(Main plugin){
        this.plugin = plugin;
    }

    //run a command in base its arguments
    public void execute(CommandSender sender, String[] args){
        if(args.length <= 0){
            sendError(sender);
            return;
        }

        if(!(sender.isOp() || sender.hasPermission("autoreboot."+args[0]))){
            sender.sendMessage(ChatColor.RED+"You don't have the permission to run this command!");
            return;
        }

        if(args[0].equalsIgnoreCase(ArgType.RELOAD.toString())){
            plugin.reload();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.config.reloadMessage));
        }
        else if(args[0].equalsIgnoreCase(ArgType.HELP.toString())){
            sendHelp(sender);
        }
        else if(args[0].equalsIgnoreCase(ArgType.REBOOT.toString())){
            setTimerToReboot(sender, args);
        }
        else if(args[0].equalsIgnoreCase(ArgType.TLEFT.toString())){
            sendTimeLeft(sender);
        }
        else if(args[0].equalsIgnoreCase(ArgType.CANCEL.toString())){
            plugin.timer.stop();
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Stopped every timer!");
        }
        else{
            sendError(sender);
        }
    }

    //set a timer for a reboot cancelling the old configuration
    private void setTimerToReboot(CommandSender sender, String[] args){
        if(args.length < 2){
            sendError(sender);
            return;
        }

        if(args[1].equalsIgnoreCase("now")){
            args[1] = "00:00";
        }
        else{
            for(int i = 0, a = 0; i < args[1].length(); i++){
                if(!(args[1].charAt(i) >= '0' && args[1].charAt(i) <= '9')){
                    if(args[1].charAt(i) != ':'){
                        sendError(sender);
                        return;
                    }
                    else{
                        if(a == 0)
                            a++;
                        else{
                            sendError(sender);
                            return;
                        }
                    }
                }
            }
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Warning: this command will stop every other timer.");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Use '" + ChatColor.YELLOW + "/ar reload" + ChatColor.LIGHT_PURPLE + "' to re-enable the config timer.");

        plugin.timerChanged = true;

        plugin.config.timerType = Config.TimerType.TIMER.toString();
        plugin.config.timerTime = args[1];
        plugin.timer.stop();
        plugin.timer = GenericTimer.getTimer(plugin, plugin.config);
        plugin.timer.start();

        sendTimeLeft(sender);
    }

    //send time left before rebooting
    private void sendTimeLeft(CommandSender sender){
        sender.sendMessage(ChatColor.LIGHT_PURPLE+"Time left before reboot: " + ChatColor.YELLOW + plugin.timer.getRemaining());
    }

    //send help page
    private void sendHelp(CommandSender sender){
        sender.sendMessage(ChatColor.LIGHT_PURPLE+"[--AutoReboot commands--]");
        sender.sendMessage(ChatColor.YELLOW + "List of commands:");

        for (String[] arg : ARGS) {
            if(sender.isOp() || sender.hasPermission("autoreboot."+arg[0]))
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/ar " + arg[0] + ": " + arg[1]);
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE+"[----------------------]");
    }

    //send incorrect command usage
    private void sendError(CommandSender sender){
        sender.sendMessage(ChatColor.RED + "Incorrect usage, use '/ar help' for a list of commands");
    }
}
