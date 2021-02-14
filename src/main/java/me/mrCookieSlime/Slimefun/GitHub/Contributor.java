package me.mrCookieSlime.Slimefun.GitHub;


public class Contributor {
    private final String name;
    private final String job;
    private final int commits;
    private String profile;

    public Contributor(String name, String job, int commits) {
        this.name = name;
        this.job = job;
        this.commits = commits;
    }


    public String getName() {
        return this.name;
    }


    public String getJob() {
        return this.job;
    }


    public String getProfile() {
        return this.profile;
    }

    protected void setProfile(String profile) {
        this.profile = profile;
    }

    public int getCommits() {
        return this.commits;
    }
}


