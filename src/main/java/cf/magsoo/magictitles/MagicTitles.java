package cf.magsoo.magictitles;

import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MagicTitles extends JavaPlugin {
    private int[] titleSubtitleCount = new int[4];
    private int[] actionbarCount = new int[4];
    private YamlConfiguration data;
    private File dataFile;

    @Override
    public void onEnable() {
        super.onEnable();
        String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Titles.oldReflection = v.equals("v1_8_R1");
        Titles.newReflection = v.contains("v1_12");
        Titles.plugin = this;

        File bStatsFolder = new File(getDataFolder().getParentFile(), "bStats");
        dataFile = new File(bStatsFolder, "MT-data.yml");
        data = YamlConfiguration.loadConfiguration(dataFile);
        if (data.contains("TS")){
            titleSubtitleCount[0] = data.getInt("TS.0");
            titleSubtitleCount[1] = data.getInt("TS.1");
            titleSubtitleCount[2] = data.getInt("TS.2");
            titleSubtitleCount[3] = data.getInt("TS.3");
            actionbarCount[0] = data.getInt("A.0");
            actionbarCount[1] = data.getInt("A.1");
            actionbarCount[2] = data.getInt("A.2");
            actionbarCount[3] = data.getInt("A.3");
        }

        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.AdvancedBarChart("titles_displayed") {
            @Override
            public HashMap<String, int[]> getValues(HashMap<String, int[]> hashMap) {
                hashMap.put("Title & Subtitle", titleSubtitleCount);
                hashMap.put("ActionBar", actionbarCount);
                return hashMap;
            }
        });

        //TODO: Check for plugin updates.
    }

    @Override
    public void onDisable() {
        super.onDisable();
        data.set("TS.0", titleSubtitleCount[0]);
        data.set("TS.1", titleSubtitleCount[1]);
        data.set("TS.2", titleSubtitleCount[2]);
        data.set("TS.3", titleSubtitleCount[3]);
        data.set("A.0", actionbarCount[0]);
        data.set("A.1", actionbarCount[1]);
        data.set("A.2", actionbarCount[2]);
        data.set("A.3", actionbarCount[3]);
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while saving metrics data. Please report this to the dev. Error code: 001");
        }
    }

    void titleDisplayed(int type, TitleSlot slot) {
        switch (slot) {
            case TITLE_SUBTITLE:
                titleSubtitleCount[type]++;
                break;
            case ACTIONBAR:
                actionbarCount[type]++;
                break;
        }
    }
}
