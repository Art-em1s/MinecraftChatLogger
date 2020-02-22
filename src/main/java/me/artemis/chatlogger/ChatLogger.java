package me.artemis.chatlogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatLogger extends JavaPlugin implements Listener, CommandExecutor {
    private static PluginDescriptionFile pdfFile = null;

    @Override
    public void onEnable() {
        pdfFile = getDescription();
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("ChatLogger has been enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("ChatLogger has been disabled.");
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        logMessage(event.getPlayer(), event.getMessage());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        logMessage(event.getPlayer(), event.getMessage());
    }

    private void logMessage(Player player, String message) {
        long currentUnixTime = 0;
        SimpleDateFormat mySdfDate = null;
        SimpleDateFormat mySdfTime = null;
        Date messageDateTime = null;
        String messagePrettyDate = null;
        String messagePrettyTime = null;
        boolean logExists = false;
        Location whereAt = null;
        String locString = null;
        currentUnixTime = System.currentTimeMillis();
        mySdfDate = new SimpleDateFormat("dd-MM-yyyy");
        mySdfTime = new SimpleDateFormat("HH:mm:ss");
        messageDateTime = new java.sql.Date(currentUnixTime);
        messagePrettyDate = mySdfDate.format(messageDateTime);
        messagePrettyTime = mySdfTime.format(messageDateTime);
        String logFilename = null;
        File logFile = null;
        logFilename = "ChatLog.csv";
        FileOutputStream logStream = null;
        logFile = new File(logFilename);
        try {
            logExists = logFile.exists();
            logStream = new FileOutputStream(logFile, true);
            if (!logExists) {
                writeDataToLog(logStream, "Timestamp, Date, Time, Player Name, Player IP, Game World Location, Chat Message, ; ");
            }
            whereAt = player.getLocation();
            locString = whereAt.getWorld().getName() + " (" + Math.round(whereAt.getBlockX()) + " / " + Math.round(whereAt.getBlockY()) + " / " + Math.round(whereAt.getBlockZ()) + ")";
            writeDataToLog(logStream, currentUnixTime + ", " + messagePrettyDate + ", " + messagePrettyTime + ", " + player.getName() + ", " + player.getAddress() + ", " + locString + ",\"" + message + "\",; ");
            logStream.close();
        } catch (IOException oops) {
            System.out.println("[" + pdfFile.getName() + "] Error writing Chat Log file '" + logFilename + "'. ");
            oops.printStackTrace(System.err);
        }
    }

    private void writeDataToLog(FileOutputStream fileStream, String output) throws IOException {
        try {
            String newline = System.getProperty("line.separator");
            output = output + newline;
            byte[] data = output.getBytes();
            fileStream.write(data, 0, data.length);
        } catch (IOException oops) {
            System.out.println("[" + pdfFile.getName() + "] Error writing file. ");
            oops.printStackTrace(System.err);
        }
    }
}
