package cf.magsoo.magictitles;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static cf.magsoo.magictitles.Titles.*;

public class NormalTitle implements Title {
    private TitleSlot slot = TitleSlot.TITLE_SUBTITLE;
    private String text1;
    private String text2;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public NormalTitle(TitleSlot slot, String text){
        this(slot, text, 40);
    }

    public NormalTitle(TitleSlot slot, String text, int stay){
        this.slot = slot;
        text1 = text;
        this.stay = stay;
    }

    public NormalTitle(TitleSlot slot, String title, String subtitle){
        this(slot, title, subtitle, 20, 60, 20);
    }

    public NormalTitle(TitleSlot slot, String title, String subtitle, int fadeIn, int stay, int fadeOut){
        this.slot = slot;
        text1 = title;
        text2 = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void send(Player player) {
        switch (slot) {
            case TITLE_SUBTITLE:
                sendTimesPacket(player, fadeIn, stay, fadeOut);
                sendTitlePacket(player, toJSON(text1));
                sendSubtitlePacket(player, toJSON(text2));
                break;
            case ABOVE_HOTBAR:
                sendHotbarPacket(player, toJSON(text1));
                setHotbarTasks(player, new BukkitRunnable() {
                    int count = 0;
                    int s = stay - 40;

                    @Override
                    public void run() {
                        sendHotbarPacket(player, toJSON(text1));
                        count++;
                        if (count >= s) {
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 1));
                break;
        }
    }
}
