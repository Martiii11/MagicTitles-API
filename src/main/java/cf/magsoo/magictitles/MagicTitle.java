package cf.magsoo.magictitles;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static cf.magsoo.magictitles.Titles.*;

public class MagicTitle implements Title {
    private TitleSlot slot = TitleSlot.TITLE_SUBTITLE;
    private String text1;
    private String text2;
    private int fadeIn;
    private int stay;
    private int fadeOut;
    private int timeToComplete;
    private boolean waitForSubtitle = false;

    public MagicTitle(TitleSlot slot, String text, int timeToComplete){
        this(slot, text, 40, timeToComplete);
    }

    public MagicTitle(TitleSlot slot, String text, int stay, int timeToComplete){
        this.slot = slot;
        text1 = text;
        this.stay = stay;
        this.timeToComplete = timeToComplete;
    }

    public MagicTitle(TitleSlot slot, String title, String subtitle, int timeToComplete){
        this(slot, title, subtitle, 20, 60, 20, timeToComplete);
    }

    public MagicTitle(TitleSlot slot, String title, String subtitle, int fadeIn, int stay, int fadeOut, int timeToComplete){
        this(slot, title, subtitle, fadeIn, stay, fadeOut, timeToComplete, false);
    }

    public MagicTitle(TitleSlot slot, String title, String subtitle, int fadeIn, int stay, int fadeOut, int timeToComplete, boolean waitForSubtitle){
        this.slot = slot;
        text1 = title;
        text2 = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.timeToComplete = timeToComplete;
        this.waitForSubtitle = waitForSubtitle;
    }

    @Override
    public void send(Player player) {
        switch (slot) {
            case TITLE_SUBTITLE:
                sendTimesPacket(player, fadeIn, stay + timeToComplete, fadeOut);
                long timePerCharTitle = timeToComplete / ChatColor.stripColor(text1).length();
                if (text1 == null || text1.equals("")) {
                    sendTitlePacket(player, toJSON(""));
                } else {
                    sendTitlePacket(player, toMagicJSON(text1));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            sendTimesPacket(player, 0, stay + timeToComplete, fadeOut);
                            new BukkitRunnable() {
                                int charCountTitle = 0;

                                @Override
                                public void run() {
                                    while (text1.charAt(charCountTitle) == '§') {
                                        charCountTitle += 2;
                                        if (charCountTitle >= text1.length() - 1) {
                                            stay();
                                            return;
                                        }
                                    }
                                    StringBuilder magicTitle = new StringBuilder(text1.substring(0, charCountTitle + 1) + ChatColor.MAGIC);
                                    for (int i = charCountTitle + 1; i < text1.length(); i++) {
                                        if (text1.charAt(i) == '§') {
                                            magicTitle.append(text1.substring(i, i + 2)).append(ChatColor.MAGIC);
                                            i++;
                                        } else {
                                            magicTitle.append(text1.charAt(i));
                                        }
                                    }
                                    sendTitlePacket(player, toJSON(magicTitle.toString()));
                                    charCountTitle++;
                                    if (charCountTitle >= text1.length()) {
                                        stay();
                                    }
                                }

                                void stay() {
                                    sendTimesPacket(player, 0, stay, fadeOut);
                                    sendTitlePacket(player, toJSON(text1));
                                    cancel();
                                }
                            }.runTaskTimer(plugin, 0, timePerCharTitle);
                        }
                    }.runTaskLater(plugin, fadeIn);

                }
                if (text2 != null) {
                    if (!text2.equals("")) {
                        sendSubtitlePacket(player, toMagicJSON(text2));
                        setTitleTasks(player, new BukkitRunnable() {
                            @Override
                            public void run() {
                                sendTimesPacket(player, 0, stay + timeToComplete, fadeOut);
                                long timePerCharSubtitle = timeToComplete / ChatColor.stripColor(text2).length();
                                setTitleTasks(player, new BukkitRunnable() {
                                    int charCountSubtitle = 0;

                                    @Override
                                    public void run() {
                                        while (text2.charAt(charCountSubtitle) == '§') {
                                            charCountSubtitle += 2;
                                            if (charCountSubtitle >= text2.length() - 1) {
                                                stay();
                                                return;
                                            }
                                        }
                                        StringBuilder magicTitle = new StringBuilder(text2.substring(0, charCountSubtitle + 1) + ChatColor.MAGIC);
                                        for (int i = charCountSubtitle + 1; i < text2.length(); i++) {
                                            if (text2.charAt(i) == '§') {
                                                magicTitle.append(text2.substring(i, i + 2)).append(ChatColor.MAGIC);
                                                i++;
                                            } else {
                                                magicTitle.append(text2.charAt(i));
                                            }
                                        }
                                        sendSubtitlePacket(player, toJSON(magicTitle.toString()));
                                        charCountSubtitle++;
                                        if (charCountSubtitle >= text2.length()) {
                                            stay();
                                        }
                                    }

                                    void stay() {
                                        sendTimesPacket(player, 0, stay, fadeOut);
                                        sendSubtitlePacket(player, toJSON(text2));
                                        cancel();
                                    }
                                }.runTaskTimer(plugin, 0, timePerCharSubtitle));
                            }
                        }.runTaskLater(plugin, waitForSubtitle ? (timePerCharTitle * ChatColor.stripColor(text1).length()) + fadeIn : fadeIn));
                    }
                }
                break;
            case ACTIONBAR:
                sendActionbarPacket(player, toMagicJSON(text1));
                long timePerCharHotbar = timeToComplete / ChatColor.stripColor(text1).length();
                setHotbarTasks(player, new BukkitRunnable() {
                    int charCountMessage = 0;

                    @Override
                    public void run() {
                        while (text1.charAt(charCountMessage) == '§') {
                            charCountMessage += 2;
                            if (charCountMessage >= text1.length() - 1) {
                                stay();
                                return;
                            }
                        }
                        StringBuilder magicTitle = new StringBuilder(text1.substring(0, charCountMessage + 1) + ChatColor.MAGIC);
                        for (int i = charCountMessage + 1; i < text1.length(); i++) {
                            if (text1.charAt(i) == '§') {
                                magicTitle.append(text1.substring(i, i + 2)).append(ChatColor.MAGIC);
                                i++;
                            } else {
                                magicTitle.append(text1.charAt(i));
                            }
                        }
                        sendActionbarPacket(player, toJSON(magicTitle.toString()));
                        charCountMessage++;
                        if (charCountMessage >= text1.length()) {
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
                }.runTaskTimer(plugin, 0, timePerCharHotbar));
                break;
        }
    }
}
