package net.blazinblaze.blazinUtils.data;

import org.bukkit.Location;

public class PlayerMemory {
    private int secondsPlayed;
    private String team;
    private Location lastLocationBeforeTP;

    public int getSecondsPlayed() {
        return secondsPlayed;
    }

    public void setSecondsPlayed(int secondsPlayed) {
        this.secondsPlayed = secondsPlayed;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Location getLastLocationBeforeTP() {
        return lastLocationBeforeTP;
    }

    public void setLastLocationBeforeTP(Location lastLocationBeforeTP) {
        this.lastLocationBeforeTP = lastLocationBeforeTP;
    }
}
