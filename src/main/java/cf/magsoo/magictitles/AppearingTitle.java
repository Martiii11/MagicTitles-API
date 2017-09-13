package cf.magsoo.magictitles;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static cf.magsoo.magictitles.Titles.*;
import static cf.magsoo.magictitles.Titles.plugin;

public class AppearingTitle implements Title {
    private TitleSlot slot = TitleSlot.TITLE_SUBTITLE;
    private String text1;
    private String text2;
    private int stay;
    private int fadeOut;
    private int timeToComplete;
    private boolean waitForSubtitle = false;

    public AppearingTitle(TitleSlot slot, String text, int timeToComplete){
        this(slot, text, 40, timeToComplete);
    }

    public AppearingTitle(TitleSlot slot, String text, int stay, int timeToComplete){
        this.slot = slot;
        text1 = text;
        this.stay = stay;
        this.timeToComplete = timeToComplete;
    }

    public AppearingTitle(TitleSlot slot, String title, String subtitle, int timeToComplete){
        this(slot, title, subtitle, 60, 20, timeToComplete);
    }

    public AppearingTitle(TitleSlot slot, String title, String subtitle, int stay, int fadeOut, int timeToComplete){
        this(slot, title, subtitle, stay, fadeOut, timeToComplete, false);
    }

    public AppearingTitle(TitleSlot slot, String title, String subtitle, int stay, int fadeOut, int timeToComplete, boolean waitForSubtitle){
        this.slot = slot;
        text1 = title;
        text2 = subtitle;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.timeToComplete = timeToComplete;
        this.waitForSubtitle = waitForSubtitle;
    }

    @Override
    public void send(Player player) {
        switch (slot) {
            case TITLE_SUBTITLE:
                sendTimesPacket(player, 0, stay + timeToComplete, fadeOut);
                int timePerCharTitle = timeToComplete / ChatColor.stripColor(text1).length();
                if (text1 == null || text1.equals("")) {
                    sendTitlePacket(player, toJSON(""));
                } else {
                    setTitleTasks(player, new BukkitRunnable() {
                        int charCountTitle = 1;

                        @Override
                        public void run() {
                            while (text1.charAt(charCountTitle - 1) == '§') {
                                charCountTitle += 2;
                            }
                            sendTitlePacket(player, toJSON(text1.substring(0, charCountTitle)));
                            charCountTitle++;
                            if (charCountTitle > text1.length()) {
                                cancel();
                            }
                        }
                    }.runTaskTimer(plugin, 0, timePerCharTitle));
                }
                if (text2 != null) {
                    if (!text2.equals("")) {
                        int timePerCharSubtitle = timeToComplete / ChatColor.stripColor(text2).length();
                        setTitleTasks(player, new BukkitRunnable() {
                            int charCountSubtitle = 1;

                            @Override
                            public void run() {
                                while (text2.charAt(charCountSubtitle - 1) == '§') {
                                    charCountSubtitle += 2;
                                }
                                sendSubtitlePacket(player, toJSON(text2.substring(0, charCountSubtitle)));
                                charCountSubtitle++;
                                if (charCountSubtitle > text2.length()) {
                                    cancel();
                                }
                            }
                        }.runTaskTimer(plugin, waitForSubtitle ? timePerCharTitle * ChatColor.stripColor(text1).length() : 0, timePerCharSubtitle));
                    }
                }
                break;
            case ACTIONBAR:
                int timePerCharMessage = timeToComplete / ChatColor.stripColor(text1).length();
                setHotbarTasks(player, new BukkitRunnable() {
                    int charCountMessage = 1;

                    @Override
                    public void run() {
                        while (text1.charAt(charCountMessage - 1) == '§') {
                            charCountMessage += 2;
                        }
                        sendActionbarPacket(player, toJSON(text1.substring(0, charCountMessage)));
                        charCountMessage++;
                        if (charCountMessage > text1.length()) {
                            stay();
                        }
                    }

                    void stay() {
                        cancel();
                        setHotbarTasks(player, new BukkitRunnable() {
                            int count = 0;
                            int s = stay - 40;

                            @Override
                            public void run() {
                                sendActionbarPacket(player, toJSON(text1));
                                count++;
                                if (count >= s) {
                                    cancel();
                                }
                            }
                        }.runTaskTimer(plugin, 40, 1));
                    }
                }.runTaskTimer(plugin, 0, timePerCharMessage));
                break;
        }
    }
}
