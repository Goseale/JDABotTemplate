package DiscordBot.Util;

import net.dv8tion.jda.api.JDA;
import okhttp3.*;

import java.util.Timer;
import java.util.TimerTask;

public class BOT_MONITOR {
    private final OkHttpClient client;

    public BOT_MONITOR(JDA jda) {
        OkHttpClient client = new OkHttpClient();
        this.client = client;
        System.out.println("===[ BOT MONITOR STARTED ]===");

        // RAM MONITOR
        new Thread(() -> {


            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    long heapFreeSize = Runtime.getRuntime().freeMemory();
                    String ramFree = formatSize(heapFreeSize);
                    double free = Double.parseDouble(ramFree.substring(0, ramFree.length() - 3));
                    if (free < 150) {
                        System.out.println("!! MEMORY ISSUE DETECTED !! Ordering Garbage collector...");
                        ramFree = formatSize(heapFreeSize);
                        System.out.println("Ram now: " + ramFree);
                        System.gc();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                        }

                    }


                }
            }, 0, 60 * 1000);


        }).start();
        // --- -------


    }

    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
}