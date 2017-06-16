package cf.magsoo.magictitles;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static cf.magsoo.magictitles.Titles.*;
import static cf.magsoo.magictitles.Titles.sendActionbarPacket;

public class BlinkTitle implements Title {
    private TitleSlot slot = TitleSlot.TITLE_SUBTITLE;
    private String text1;
    private String text2;
    private int fadeIn;
    private int stay;
    private int fadeOut;
    private int blinkInterval;
    private boolean alternate;

    public BlinkTitle(TitleSlot slot, String text, int blinkInterval) {
        this(slot, text, 40, blinkInterval);
    }

    public BlinkTitle(TitleSlot slot, String text, int stay, int blinkInterval) {
        this.slot = slot;
        text1 = text;
        this.stay = stay;
        this.blinkInterval = blinkInterval;
    }

    public BlinkTitle(TitleSlot slot, String title, String subtitle, int blinkInterval, boolean alternate) {
        this(slot, title, subtitle, 20, 60, 20, blinkInterval, alternate);
    }

    public BlinkTitle(TitleSlot slot, String title, String subtitle, int fadeIn, int stay, int fadeOut, int blinkInterval, boolean alternate) {
        this.slot = slot;
        text1 = title;
        text2 = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.blinkInterval = blinkInterval;
        this.alternate = alternate;
    }

    @Override
    public void send(Player player) {
        Titles.plugin.titleDisplayed(3, slot);
        switch (slot) {
            case TITLE_SUBTITLE:
                sendTimesPacket(player, fadeIn, stay, fadeOut);
                sendTitlePacket(player, toJSON(text1));
                sendSubtitlePacket(player, toJSON(text2));
                setTitleTasks(player, new BukkitRunnable() {
                    boolean b = false;
                    int count = 0;

                    @Override
                    public void run() {
                        sendTimesPacket(player, 0, blinkInterval, fadeOut);
                        count = count + blinkInterval;
                        b = !b;
                        if (b) {
                            if (alternate) {
                                sendTitlePacket(player, toJSON(text1));
                                sendSubtitlePacket(player, toJSON(""));
                            } else {
                                sendTitlePacket(player, toJSON(text1));
                                sendSubtitlePacket(player, toJSON(text2));
                            }
                        } else {
                            if (alternate) {
                                sendTitlePacket(player, toJSON(""));
                                sendSubtitlePacket(player, toJSON(text2));
                            } else {
                                sendTitlePacket(player, toJSON(""));
                                sendSubtitlePacket(player, toJSON(""));
                            }
                        }
                        if (count > stay && b) {
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, fadeIn, blinkInterval));
                break;
            case ACTIONBAR:
                sendActionbarPacket(player, toJSON(text1));
                setHotbarTasks(player, new BukkitRunnable() {
                    boolean b = false;
                    int count = 0;

                    @Override
                    public void run() {
                        count = count + blinkInterval;
                        b = !b;
                        if (b) {
                            sendActionbarPacket(player, toJSON(text1));
                        } else {
                            sendActionbarPacket(player, toJSON(""));
                        }
                        if (count > stay && !b) {
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, blinkInterval));
                break;
        }
    }
}
