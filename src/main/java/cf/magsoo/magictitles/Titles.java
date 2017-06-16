package cf.magsoo.magictitles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class Titles {
	static boolean oldReflection = false;
	static boolean newReflection = false;
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while sending times packet. Please report this to the dev. Error code: 002");
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while sending title packet. Please report this to the dev. Error code: 003");
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while sending subtitle packet. Please report this to the dev. Error code: 004");
		}
	}

	static void sendActionbarPacket(Player player, String message) {
		try {
		    Object packet;
			if (oldReflection) {
                Constructor<?> constructor = getNMSClass("PacketPlayOutChat")
                        .getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
				Object m = getNMSClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, message);
                packet = constructor.newInstance(m, (byte) 2);
			} else if (newReflection) {
			    Constructor<?> constructor = getNMSClass("PacketPlayOutChat")
                        .getConstructor(getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType"));
                Object enumType = getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
                Object m = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getDeclaredMethod("a", String.class)
                        .invoke(null, message);
                packet = constructor.newInstance(m, enumType);
			} else {
                Constructor<?> constructor = getNMSClass("PacketPlayOutChat")
                        .getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
				Object m = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getDeclaredMethod("a", String.class)
						.invoke(null, message);
                packet = constructor.newInstance(m, (byte) 2);
			}
			sendPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while sending actionbar packet. Please report this to the dev. Error code: 005");
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while sending clear packet. Please report this to the dev. Error code: 006");
		}
	}

	private static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerCon = handle.getClass().getField("playerConnection").get(handle);
			playerCon.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerCon, packet);
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while sending a packet. Please report this to the dev. Error code: 007");
		}
	}

	private static Class<?> getNMSClass(String name) {
		String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			Class<?> c = Class.forName("net.minecraft.server." + v + "." + name);
			return c;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while getting " + name + " class. Please report this to the dev. Error code: 008");
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
            case ACTIONBAR:
                sendActionbarPacket(player, toJSON(""));
                if (hotbarTasks.containsKey(player)){
                    hotbarTasks.get(player).cancel();
                    hotbarTasks.remove(player);
                }
                break;
        }
    }
}
