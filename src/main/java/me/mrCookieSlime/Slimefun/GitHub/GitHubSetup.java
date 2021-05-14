package me.mrCookieSlime.Slimefun.GitHub;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mrCookieSlime.Slimefun.SlimefunGuide;
import org.jetbrains.annotations.NotNull;

public class GitHubSetup {
    public static void setup() {
        new GitHubConnector() {
            @Override
            public void onSuccess(JsonElement element) {
                SlimefunGuide.contributors.clear();
                JsonArray array = element.getAsJsonArray();

                for (int i = 0; i < array.size(); i++) {
                    JsonObject object = array.get(i).getAsJsonObject();

                    String name = object.get("login").getAsString();
                    String job = "&cAuthor";
                    int commits = object.get("contributions").getAsInt();
                    String profile = object.get("html_url").getAsString();

                    if (!"invalid-email-address".equals(name)) {
                        Contributor contributor = new Contributor(name, job, commits);
                        contributor.setProfile(profile);
                        SlimefunGuide.contributors.add(contributor);
                    }
                }
                SlimefunGuide.contributors.add(new Contributor("AquaLazuryt", "&6Lead Head Artist", 0));
            }

            @Override
            public void onFailure() {
                SlimefunGuide.contributors.clear();
                SlimefunGuide.contributors.add(new Contributor("TheBusyBiscuit", "&cAuthor", 3));
                SlimefunGuide.contributors.add(new Contributor("John000708", "&cAuthor", 2));
                SlimefunGuide.contributors.add(new Contributor("AquaLazuryt", "&6Lead Head Artist", 0));
            }

            @NotNull
            @Override
            public String getRepository() {
                return "Slimefun/Slimefun4";
            }

            @NotNull
            @Override
            public String getFileName() {
                return "contributors";
            }

            @NotNull
            @Override
            public String getURLSuffix() {
                return "/contributors";
            }
        };

        new GitHubConnector() {
            @Override
            public void onSuccess(JsonElement element) {
                JsonObject object = element.getAsJsonObject();
                SlimefunGuide.issues = object.get("open_issues_count").getAsInt();
                SlimefunGuide.forks = object.get("forks").getAsInt();
                SlimefunGuide.stars = object.get("stargazers_count").getAsInt();
                SlimefunGuide.last_update = IntegerFormat.parseGitHubDate(object.get("pushed_at").getAsString());
            }

            @Override
            public void onFailure() {
            }

            @NotNull
            @Override
            public String getRepository() {
                return "Slimefun/Slimefun4";
            }

            @NotNull
            @Override
            public String getFileName() {
                return "repo";
            }

            @NotNull
            @Override
            public String getURLSuffix() {
                return "";
            }
        };

        new GitHubConnector() {
            @Override
            public void onSuccess(JsonElement element) {
                JsonObject object = element.getAsJsonObject();
                SlimefunGuide.code_bytes = object.get("Java").getAsInt();
            }

            @Override
            public void onFailure() {
            }

            @NotNull
            @Override
            public String getRepository() {
                return "Slimefun/Slimefun4";
            }

            @NotNull
            @Override
            public String getFileName() {
                return "languages";
            }

            @NotNull
            @Override
            public String getURLSuffix() {
                return "/languages";
            }
        };
    }
}


