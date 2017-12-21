package project.gamemechanics.interfaces;

import project.gamemechanics.scoreboard.ScoreboardRecord;

import java.util.List;

@SuppressWarnings("unused")
public interface Scoreboard {
    ScoreboardRecord getOneRecord(Integer userID);

    List<ScoreboardRecord> getAllRecords();

    void setDefaultRecord(Integer userID);

    void updateRecord(Integer userID, Integer gold, Integer frags);
}
