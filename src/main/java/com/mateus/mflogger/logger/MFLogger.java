package com.mateus.mflogger.logger;

import com.mateus.mflogger.MFLoggerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MFLogger {

    private final MFLoggerPlugin plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Connection connection;

    public MFLogger(MFLoggerPlugin plugin) {
        this.plugin = plugin;
        try {
            initDB();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(LogType type, Player player, String message, long time) {
        log(type, player,  message, player.getLocation(), time);
    }

    public List<String> getLogsByLocation(Location location, int radius) {
        double x1 = location.getX() + radius;
        double x2 = location.getX() - radius;
        double y1 = location.getY() + radius;
        double y2 = location.getY() - radius;
        double z1 = location.getZ() + radius;
        double z2 = location.getZ() - radius;

        StringBuilder stringBuilder = new StringBuilder();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT type, player, message, world, x, y, z, dateTime FROM logs" +
                    " WHERE" +
                    " (x BETWEEN ? AND ?) AND (y BETWEEN ? AND ?) AND (z BETWEEN ? AND ?) ORDER BY dateTime");
            statement.setDouble(1, x2);
            statement.setDouble(2, x1);
            statement.setDouble(3, y2);
            statement.setDouble(4, y1);
            statement.setDouble(5, z2);
            statement.setDouble(6, z1);

            ResultSet resultSet = statement.executeQuery();
            List<String> logs = new ArrayList<>();
            while (resultSet.next()) {
                String type = resultSet.getString("type");
                String player = resultSet.getString("player");
                String message = resultSet.getString("message");
                String world = resultSet.getString("world");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                String date = resultSet.getString("dateTime");

                stringBuilder.append(ChatColor.RED)
                        .append("[").append(type).append(" ").append(date).append("] ");
                stringBuilder.append(ChatColor.AQUA).append(player);
                stringBuilder.append(": ").append(message);
                stringBuilder.append(ChatColor.DARK_AQUA)
                .append(" (Mundo=").append(world).append(", X=").append(x).append(", Y=").append(y).append(", Z=").append(z).append(")");

                logs.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            }
            resultSet.close();
            return logs;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void log(LogType type, Player player, String message, Location location, long time) {
        assert connection != null;
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO logs( type, player, message, world, x, y, z, dateTime ) " +
                    "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )");
            statement.setString(1, type.toString());
            statement.setString(2, player.getName());
            statement.setString(3, message);
            statement.setString(4, location.getWorld().getName());
            statement.setDouble(5, location.getX());
            statement.setDouble(6, location.getY());
            statement.setDouble(7, location.getZ());
            statement.setString(8, dateFormat.format(time));
            statement.execute();
            statement.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    private void initDB() throws SQLException, IOException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        File dbFile = new File(plugin.getDataFolder(), "logs.db");
        if (!dbFile.exists()) {
            dbFile.createNewFile();
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

        plugin.getLogger().info("Database iniciada.");

        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS logs" +
                "( id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                " type TEXT NOT NULL," +
                " player TEXT NOT NULL," +
                " message TEXT NOT NULL," +
                " world TEXT NOT NULL," +
                " x DOUBLE NOT NULL," +
                " y DOUBLE NOT NULL," +
                " z DOUBLE NOT NULL," +
                " dateTime DATE NOT NULL );");
        statement.execute();
        statement.close();
    }
}
