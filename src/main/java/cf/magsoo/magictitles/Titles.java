package cf.magsoo.magictitles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class Titles {
	static boolean oldReflection = false;
	static MagicTitles plugin;
    private static HashMap<Player, BukkitTask> titleSubtitleTasks = new HashMap<>();
    private static HashMap<Player, BukkitTask> hotbarTasks = new HashMap<>();
	
	static void sendTimesPacket(Player player, int fadeIn, int stay, int fadeOut) {
		try {
			Constructor<?> constructor;
			Object enumAction;
			if (oldReflection) {
				constructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("EnumTitleAction"),
						getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
				enumAction = getNMSClass("EnumTitleAction").getField("TIMES").get(null);
			} else {
				constructor = getNMSClass("PacketPlayOutTitle").getConstructor(
						getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
						int.class, int.class, int.class);
				enumAction = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
			}

			Object packet = constructor.newInstance(enumAction, null, fadeIn, stay, fadeOut);
			sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void sendTitlePacket(Player player, String JSONTitle) {
		try {

			Constructor<?> constructor;
			Object enumAction;
			Object title;
			if (oldReflection) {
				constructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("EnumTitleAction"),
						getNMSClass("IChatBaseComponent"));

				enumAction = getNMSClass("EnumTitleAction").getField("TITLE").get(null);
				title = getNMSClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, JSONTitle);
			} else {
				constructor = getNMSClass("PacketPlayOutTitle").getConstructor(
						getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));

				enumAction = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
				title = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getDeclaredMethod("a", String.class)
						.invoke(null, JSONTitle);
			}

			Object packet = constructor.newInstance(enumAction, title);
			sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void sendSubtitlePacket(Player player, String JSONSubtitle) {
		try {
			Constructor<?> constructor;
			Object enumAction;
			Object subtitle;
			if (oldReflection) {
				constructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("EnumTitleAction"),
						getNMSClass("IChatBaseComponent"));

				enumAction = getNMSClass("EnumTitleAction").getField("SUBTITLE").get(null);
				subtitle = getNMSClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null,
						JSONSubtitle);
			} else {
				constructor = getNMSClass("PacketPlayOutTitle").getConstructor(
						getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));

				enumAction = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
				subtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
						.getDeclaredMethod("a", String.class).invoke(null, JSONSubtitle);
			}

			Object packet = constructor.newInstance(enumAction, subtitle);
			sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void sendHotbarPacket(Player player, String message) {
		try {
			Constructor<?> constructor = getNMSClass("PacketPlayOutChat")
					.getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
			Object m;
			if (oldReflection) {
				m = getNMSClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, message);
			} else {
				m = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getDeclaredMethod("a", String.class)
						.invoke(null, message);
			}
			Object packet = constructor.newInstance(m, (byte) 2);
			sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void sendHideTitlePacket(Player player) {
		try {
			Constructor<?> constructor;
			Object enumAction;
			if (oldReflection) {
				constructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("EnumTitleAction"),
						getNMSClass("IChatBaseComponent"));

				enumAction = getNMSClass("EnumTitleAction").getField("CLEAR").get(null);
			} else {
				constructor = getNMSClass("PacketPlayOutTitle").getConstructor(
						getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));

				enumAction = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("CLEAR").get(null);
			}
			Object packet = constructor.newInstance(enumAction, null);
			sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerCon = handle.getClass().getField("playerConnection").get(handle);
			playerCon.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerCon, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Class<?> getNMSClass(String name) {
		String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			Class<?> c = Class.forName("net.minecraft.server." + v + "." + name);
			return c;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

    static String toJSON(String text) {
        return "{\"text\": \"" + text + "\"}";
    }

    static String toMagicJSON(String text) {
        StringBuilder magictext = new StringBuilder(ChatColor.MAGIC + "");
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '§') {
                magictext.append(text.substring(i, i + 2)).append(ChatColor.MAGIC);
                i++;
            } else {
                magictext.append(text.charAt(i));
            }
        }
        return "{\"text\": \"" + magictext.toString() + "\"}";
    }

    static void setTitleTasks(Player player, BukkitTask task){
        titleSubtitleTasks.put(player, task);
    }

    static void setHotbarTasks(Player player, BukkitTask task){
        hotbarTasks.put(player, task);
    }

    public static void clearTitle(TitleSlot slot, Player player){
	    switch (slot){
            case TITLE_SUBTITLE:
                sendHideTitlePacket(player);
                if (titleSubtitleTasks.containsKey(player)){
                    titleSubtitleTasks.get(player).cancel();
                    titleSubtitleTasks.remove(player);
                }
                break;
            case ABOVE_HOTBAR:
                sendHotbarPacket(player, toJSON(""));
                if (hotbarTasks.containsKey(player)){
                    hotbarTasks.get(player).cancel();
                    hotbarTasks.remove(player);
                }
                break;
        }
    }
}
