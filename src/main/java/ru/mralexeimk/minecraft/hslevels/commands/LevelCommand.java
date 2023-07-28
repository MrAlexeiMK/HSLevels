package ru.mralexeimk.minecraft.hslevels.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mralexeimk.minecraft.hslevels.HSLevels;
import ru.mralexeimk.minecraft.hslevels.builders.MessageConstructor;
import ru.mralexeimk.minecraft.hslevels.configs.Message;
import ru.mralexeimk.minecraft.hslevels.services.LevelService;

import java.util.ArrayList;
import java.util.List;

public class LevelCommand implements CommandExecutor {
    private final LevelService levelService = HSLevels.getInstance().getLevelService();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if(!command.getName().equalsIgnoreCase("level")) return false;
        if(args.length == 0) {
            if(!(sender instanceof Player p)) {
                MessageConstructor.of(Message.ONLY_FROM_PLAYER).send(sender);
                return false;
            }
            if(!sender.hasPermission("hslevels.level")) {
                MessageConstructor.of(Message.NO_PEX).send(sender);
                return false;
            }

            MessageConstructor
                    .of(Message.LEVEL_COMMAND)
                    .replace("%level%", String.valueOf(levelService.getLevel(p)))
                    .replace("%xp_bar%", levelService.getExpBar(p))
                    .replace("%xp%", String.valueOf(levelService.getExp(p)))
                    .replace("%xp_needed%", String.valueOf(levelService.getExpNeeded(p)))
                    .send(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {

            }
            case "set" -> {

            }
            case "addxp" -> {

            }
            case "setxp" -> {

            }
            case "take" -> {

            }
            case "takexp" -> {

            }
            default -> {

            }
        }


        return true;
    }

    private void printHelp(CommandSender sender) {
        if(sender.hasPermission("hslevels.level")) MessageConstructor.of(Message.HELP_LEVEL).send(sender);
        if(sender.hasPermission("hslevels.player")) MessageConstructor.of(Message.HELP_LEVEL_PLAYER).send(sender);
        if(sender.hasPermission("hslevels.add")) MessageConstructor.of(Message.HELP_ADD_LEVEL).send(sender);
        if(sender.hasPermission("hslevels.set")) MessageConstructor.of(Message.HELP_SET_LEVEL).send(sender);
        if(sender.hasPermission("hslevels.addxp")) MessageConstructor.of(Message.HELP_ADD_XP).send(sender);
        if(sender.hasPermission("hslevels.setxp")) MessageConstructor.of(Message.HELP_SET_XP).send(sender);
        if(sender.hasPermission("hslevels.take")) MessageConstructor.of(Message.HELP_TAKE_LEVEL).send(sender);
        if(sender.hasPermission("hslevels.takexp")) MessageConstructor.of(Message.HELP_TAKE_XP).send(sender);
        if(sender.hasPermission("hslevels.reload")) MessageConstructor.of(Message.HELP_RELOAD).send(sender);
    }
}
