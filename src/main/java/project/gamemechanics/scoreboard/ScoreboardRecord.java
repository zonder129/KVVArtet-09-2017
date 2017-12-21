package project.gamemechanics.scoreboard;

@SuppressWarnings("unused")
public class ScoreboardRecord {
    private Integer userID;
    private String username;
    private Integer gold;
    private Integer frags;

    public Integer getFrags() {
        return frags;
    }

    public ScoreboardRecord() {

    }

    public ScoreboardRecord(Integer userID, Integer gold, Integer frags, String username) {
        this.userID = userID;
        this.gold = gold;
        this.frags = frags;
        this.username = username;
    }

    public void setFrags(Integer frags) {
        this.frags = frags;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
