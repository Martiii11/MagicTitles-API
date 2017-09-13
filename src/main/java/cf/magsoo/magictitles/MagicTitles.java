package cf.magsoo.magictitles;

import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MagicTitles extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Titles.oldReflection = v.equals("v1_8_R1");
        Titles.newReflection = v.contains("v1_12");
        Titles.plugin = this;


        new Metrics(this);


        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://magsoo.pe.hu/plugins/MagicTitles/info/version.txt");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Version-Checker");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200){
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String ver = in.readLine();
                        float newVersion = Float.valueOf(ver);
                        float currentVersion = Float.valueOf(getDescription().getVersion());
                        if (newVersion > currentVersion){
                            getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "A new version of MagicTitles API is available.");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
