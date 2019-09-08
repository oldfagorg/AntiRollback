package black.nigger.arb;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin implements Listener {

    double size;
    double total;
    double thing;
    String directory;
    String cmd;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        thing = Double.parseDouble(this.getConfig().getString("free-space-before-stop"));
        directory = this.getConfig().getString("directory-to-check");
        cmd = this.getConfig().getString("command-when-space-low");
        System.out.println("        _     _  __                             ");
        System.out.println("       | |   | |/ _|                            ");
        System.out.println("   ___ | | __| | |_ __ _  __ _   ___  _ __ __ _ ");
        System.out.println("  / _ \\| |/ _` |  _/ _` |/ _` | / _ \\| '__/ _` |");
        System.out.println(" | (_) | | (_| | || (_| | (_| || (_) | | | (_| |");
        System.out.println("  \\___/|_|\\__,_|_| \\__,_|\\__, (_)___/|_|  \\__, |");
        System.out.println("                          __/ |            __/ |");
        System.out.println("                         |___/            |___/ ");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                (new Thread(() -> {
                    size = new File(directory).getFreeSpace();
                    total = new File(directory).getTotalSpace();
                    if (convertToGB(size) < thing) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say server is low on free space");
                        File stopFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "STOPSERVER.LOL");
                        if (!stopFile.exists()) {
                            System.out.println(stopFile.getPath() + " created");
                            try {
                                stopFile.createNewFile();
                            } catch (IOException e) {
                                System.out.println("could not create file");
                                e.printStackTrace();
                            }
                        }
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        return;
                    }
                })).start();
            }
        }, 1L, 200L);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("freespace")) {
            sender.sendMessage(ChatColor.AQUA + "-----------------------------------------------------");
            sender.sendMessage(ChatColor.GOLD + "Server has a total of " + convertToGB(total) + "GB");
            sender.sendMessage(ChatColor.GOLD + "Server has used a total of " + (convertToGB(total) - convertToGB(size)) + "GB");
            sender.sendMessage(ChatColor.GOLD + "Server has " + convertToGB(size) + "GB of free space");
            sender.sendMessage(ChatColor.AQUA + "-----------------------------------------------------");
            return true;
        } else if (label.equalsIgnoreCase("freespace-reload")) {
            this.reloadConfig();
            this.saveConfig();
            sender.sendMessage(ChatColor.AQUA + "-----------------------------------------------------");
            sender.sendMessage(ChatColor.GOLD + "reloaded config");
            sender.sendMessage(ChatColor.AQUA + "-----------------------------------------------------");
            return true;
        } else {
            return false;
        }
    }

    public double convertToGB(Double size) {
        return size / (1024.0 * 1024 * 1024) * 1.074;
    }

}