package me.mrCookieSlime.Slimefun.CSCoreLibSetup;

import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class CSCoreLibLoader {
    Plugin plugin;
    URL url;
    URL download;
    File file;

    public CSCoreLibLoader(Plugin plugin) {
        this.plugin = plugin;
        try {
            this.url = new URL("https://api.curseforge.com/servermods/files?projectIds=88802");
        } catch (MalformedURLException malformedURLException) {
        }
    }


    public boolean load() {
        if (plugin.getServer().getPluginManager().isPluginEnabled("CS-CoreLib")) {
            return true;
        }

        System.err.println(" ");
        System.err.println("#################### - INFO - ####################");
        System.err.println(" ");
        System.err.println(plugin.getName() + " could not be loaded.");
        System.err.println("It appears that you have not installed CS-CoreLib");
        System.err.println("Your Server will now try to download and install");
        System.err.println("CS-CoreLib for you.");
        System.err.println("You will be asked to restart your Server when it's finished.");
        System.err.println("If this somehow fails, please download and install CS-CoreLib manually:");
        System.err.println("https://dev.bukkit.org/projects/cs-corelib");
        System.err.println(" ");
        System.err.println("#################### - INFO - ####################");
        System.err.println(" ");
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> install(), 10L);
        return false;
    }


    private boolean connect() {
        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("User-Agent", "CS-CoreLib Loader (by mrCookieSlime)");
            connection.setDoOutput(true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JSONArray array = (JSONArray) JSONValue.parse(reader.readLine());
            download = traceURL(((String) ((JSONObject) array.get(array.size() - 1)).get("downloadUrl")).replace("https:", "http:"));
            file = new File("plugins/" + ((JSONObject) array.get(array.size() - 1)).get("name") + ".jar");

            return true;
        } catch (IOException e) {
            System.err.println(" ");
            System.err.println("#################### - WARNING - ####################");
            System.err.println(" ");
            System.err.println("Could not connect to BukkitDev.");
            System.err.println("Please download & install CS-CoreLib manually:");
            System.err.println("https://dev.bukkit.org/projects/cs-corelib");
            System.err.println(" ");
            System.err.println("#################### - WARNING - ####################");
            System.err.println(" ");
            return false;
        }
    }

    private URL traceURL(String location) throws IOException {
        HttpURLConnection connection;
        while (true) {
            String loc;
            URL url = new URL(location);
            connection = (HttpURLConnection) url.openConnection();

            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("User-Agent", "Auto Updater (by mrCookieSlime)");

            switch (connection.getResponseCode()) {
                case 301:
                case 302:
                    loc = connection.getHeaderField("Location");
                    location = (new URL(new URL(location), loc)).toExternalForm();
                    continue;
                default:
                    break;
            }

            break;
        }
        return new URL(connection.getURL().toString().replaceAll(" ", "%20"));
    }

    private void install() {
        BufferedInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new BufferedInputStream(this.download.openStream());
            output = new FileOutputStream(this.file);

            byte[] data = new byte[1024];
            int read;
            while ((read = input.read(data, 0, 1024)) != -1) {
                output.write(data, 0, read);
            }
        } catch (Exception ex) {
            System.err.println(" ");
            System.err.println("#################### - WARNING - ####################");
            System.err.println(" ");
            System.err.println("Failed to download CS-CoreLib");
            System.err.println("Please download & install CS-CoreLib manually:");
            System.err.println("https://dev.bukkit.org/projects/cs-corelib");
            System.err.println(" ");
            System.err.println("#################### - WARNING - ####################");
            System.err.println(" ");
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
                System.err.println(" ");
                System.err.println("#################### - INFO - ####################");
                System.err.println(" ");
                System.err.println("Please restart your Server to finish the Installation");
                System.err.println("of " + this.plugin.getName() + " and CS-CoreLib");
                System.err.println(" ");
                System.err.println("#################### - INFO - ####################");
                System.err.println(" ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


