package com.mateus.mflogger;

import com.mateus.mflogger.command.LogCommand;
import com.mateus.mflogger.listener.MainListener;
import com.mateus.mflogger.logger.MFLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class MFLoggerPlugin extends JavaPlugin {

    private MFLogger logger;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        logger = new MFLogger(this);
        new MainListener(this, logger);
        new LogCommand(this, logger);
    }

    @Override
    public void onDisable() {
        logger.close();
        saveConfig();
    }
}
