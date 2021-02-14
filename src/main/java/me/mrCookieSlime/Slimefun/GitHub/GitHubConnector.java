package me.mrCookieSlime.Slimefun.GitHub;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.mrCookieSlime.Slimefun.SlimefunStartup;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashSet;
import java.util.Set;


public abstract class GitHubConnector {
    public static Set<GitHubConnector> connectors = new HashSet<>();

    private final File file;

    public GitHubConnector() {
        this.file = new File("plugins/Slimefun/cache/github/" + getFileName() + ".json");
        connectors.add(this);
    }


    public abstract String getFileName();


    public abstract String getRepository();

    public void pullFile() {
        if (SlimefunStartup.getCfg().getBoolean("options.print-out-github-data-retrieving"))
            System.out.println("[Slimefun - GitHub] Retrieving '" + getFileName() + ".json' from GitHub...");

        try {
            URL website = new URL("https://api.github.com/repos/" + getRepository() + getURLSuffix());

            URLConnection connection = website.openConnection();
            connection.setConnectTimeout(3000);
            connection.addRequestProperty("User-Agent", "Slimefun 4 GitHub Agent (by TheBusyBiscuit)");
            connection.setDoOutput(true);

            ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(this.file);
            fos.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
            fos.close();
            parseData();
        } catch (IOException e) {
            if (SlimefunStartup.getCfg().getBoolean("options.print-out-github-data-retrieving"))
                System.err.println("[Slimefun - GitHub] ERROR - Could not connect to GitHub in time.");

            if (hasData()) {
                parseData();
            } else {

                onFailure();
            }
        }
    }

    public abstract String getURLSuffix();

    public abstract void onSuccess(JsonElement paramJsonElement);

    public abstract void onFailure();

    public boolean hasData() {
        return getFile().exists();
    }

    public File getFile() {
        return this.file;
    }

    public void parseData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFile()));

            String full = "";

            String line;
            while ((line = reader.readLine()) != null) {
                full = full + line;
            }

            reader.close();

            JsonElement element = (new JsonParser()).parse(full);

            onSuccess(element);
        } catch (IOException e) {
            e.printStackTrace();
            onFailure();
        }
    }
}


