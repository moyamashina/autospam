package kz.autospam;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class SpamState {
    public static String message = "";
    public static double delay = 2.0; // секунды, 0.5 - 10
    public static boolean enabled = false;
    private static long nextTime = 0;

    public static void toggle() {
        enabled = !enabled;
        nextTime = 0;
    }

    public static void tick(MinecraftClient client) {
        if (!enabled) return;
        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        if (client.player == null || handler == null) {
            enabled = false;
            return;
        }
        String m = message.trim();
        if (m.isEmpty()) return;

        long now = System.currentTimeMillis();
        if (now >= nextTime) {
            if (m.startsWith("/")) {
                handler.sendChatCommand(m.substring(1));
            } else {
                handler.sendChatMessage(m);
            }
            nextTime = now + (long) (delay * 1000);
        }
    }
}
