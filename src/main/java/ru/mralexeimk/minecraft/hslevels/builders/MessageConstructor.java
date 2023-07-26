package ru.mralexeimk.minecraft.hslevels.builders;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageConstructor {

    private String message = "";

    private MessageConstructor() {
        this.message = "";
    }

    private MessageConstructor(String message) {
        this.message = message;
    }

    public String getMessage() {
        colors();
        return message;
    }

    public MessageConstructor add(String msg) {
        this.message += msg;
        return this;
    }

    public MessageConstructor replace(String regex, String replacement) {
        this.message = this.message.replaceAll(regex, replacement);
        return this;
    }

    public MessageConstructor replaceAll(Map<String, String> map) {
        map.forEach(this::replace);
        return this;
    }

    private void colors() {
        this.message = translateHexColorCodes(this.message);
        this.message = this.message.replaceAll("&", "§");
    }

    public MessageConstructor replacePlayer(Player p) {
        return replace("%player%", p.getName());
    }

    public MessageConstructor send(Player p) {
        colors();
        p.sendMessage(message);
        return this;
    }

    public MessageConstructor sendAll() {
        colors();
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(message);
        }
        return this;
    }

    public MessageConstructor sendAllAround(Location loc, int radius) {
        colors();
        if(loc.getWorld() == null) sendAll();
        for(Player p : loc.getWorld().getPlayers()) {
            if(p.getLocation().distance(loc) <= radius) p.sendMessage(message);
        }
        return this;
    }

    public MessageConstructor sendAllExcept(Player except) {
        colors();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p != except) p.sendMessage(message);
        }
        return this;
    }

    public MessageConstructor send(CommandSender sender) {
        colors();
        sender.sendMessage(message);
        return this;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public static MessageConstructor build() {
        return new MessageConstructor();
    }

    public static MessageConstructor of(String message) {
        return new MessageConstructor(message);
    }

    public static MessageConstructor of(String... messages) {
        return new MessageConstructor(String.join("\n", messages));
    }

    public static MessageConstructor of(List<String> messages) {
        return new MessageConstructor(String.join("\n", messages));
    }

    public static MessageConstructor of(FileConfiguration config, String path) {
        return new MessageConstructor(config.getString(path));
    }

    public static MessageConstructor ofList(FileConfiguration config, String path) {
        return new MessageConstructor(String.join("\n", config.getStringList(path)));
    }

    private String translateHexColorCodes(String message) {
        final Pattern hexPattern = Pattern.compile("\\{([A-Fa-f0-9]{6})}");
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            char COLOR_CHAR = ChatColor.COLOR_CHAR;
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
}
