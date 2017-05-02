package cf.magsoo.magictitles;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicTitles extends JavaPlugin{

    @Override
    public void onEnable() {
        super.onEnable();
        String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        if (v.equals("v1_8_R1")) {
            Titles.oldReflection = true;
        }
        Titles.plugin = this;

        //TODO: Check for plugin updates.
    }
}
