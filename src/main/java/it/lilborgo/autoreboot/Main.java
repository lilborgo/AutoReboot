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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main class
 * The main plugin handler
 */
public final class Main extends JavaPlugin implements TabCompleter {
    //log to console
    public final Logger logger = new Logger(this.getDescription().getName());

    public Config config;
    public Commands commands;
    public GenericTimer timer;
    public boolean timerChanged;

    @Override
    public void onEnable(){
        this.saveDefaultConfig();

        config = new Config(this);
        commands = new Commands(this);
        config.loadConfig();

        timer = GenericTimer.getTimer(this, config);
        timer.start();

        timerChanged = false;

        logger.log("Started", Logger.LogType.SUCCESS);
    }

    @Override
    public void onDisable(){
        logger.log("Stopped", Logger.LogType.SUCCESS);
    }

    public void reload(){
        String oldType = config.timerType;

        this.reloadConfig();
        config.loadConfig();

        if(config.timerType.equalsIgnoreCase(Config.TimerType.TIMETABLES.toString()) ||
            timerChanged || !(oldType.equalsIgnoreCase(config.timerType) && !config.resetOnReload)){

            timer.stop();
            timer = GenericTimer.getTimer(this, config);
            timer.start();

            timerChanged = false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        commands.execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> argsList = new ArrayList<>();

        if(args.length >= 2){
            if(args[0].equalsIgnoreCase(Commands.ArgType.REBOOT.toString())){
                return Arrays.asList("now", "00:10");
            }
        }

        for(String[] arg: Commands.ARGS)
            if(sender.isOp() || sender.hasPermission("autoreboot."+arg[0]))
                argsList.add(arg[0]);

        return args.length <= 1? argsList : null;
    }
}
