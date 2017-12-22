package project.gamemechanics.scoreboard;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import project.gamemechanics.interfaces.Scoreboard;

import java.sql.PreparedStatement;
import java.util.List;

@Service
public class ScoreboardService implements Scoreboard {

    private final JdbcTemplate jdbcTemplate;

    public ScoreboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ScoreboardRecord getOneRecord(Integer userID) {
        final String sql = "SELECT * FROM public.scoreboard WHERE userid = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userID}, (rs, rwNumber) -> {
            final ScoreboardRecord scoreboardRecord = new ScoreboardRecord();
            scoreboardRecord.setUserID(rs.getInt("userid"));
            scoreboardRecord.setGold(rs.getInt("gold"));
            scoreboardRecord.setFrags(rs.getInt("frags"));
            return scoreboardRecord;
        });
    }

    @Override
    public List<ScoreboardRecord> getAllRecords() {
        final String sql = "SELECT SCOR.userID, SCOR.gold, SCOR.frags, US.username "
                + "FROM public.scoreboard AS SCOR JOIN public.user AS US ON (SCOR.userID = US.id) ";
        return jdbcTemplate.query(sql, new ScoreboardMapper());
    }

    @Override
    public void setDefaultRecord(Integer userID) {
        jdbcTemplate.update(con -> {
            final PreparedStatement pst = con.prepareStatement(
                    "INSERT into public.scoreboard(userID, gold, frags)" + " values(?,?,?)");
            pst.setInt(1, userID);
            pst.setInt(2, 0);
            // CHECKSTYLE:OFF
            pst.setInt(3, 0);
            // CHECKSTYLE:ON
            return pst;
        });
    }

    @Override
    public void updateRecord(Integer userID, Integer gold, Integer frags) {
        jdbcTemplate.update(con -> {
            final PreparedStatement pst = con.prepareStatement(
                    "UPDATE public.scoreboard SET gold = ?, frags = ?" + "WHERE userID = ?");
            pst.setInt(1, userID);
            pst.setInt(2, gold);
            // CHECKSTYLE:OFF
            pst.setInt(3, frags);
            // CHECKSTYLE:ON
            return pst;
        });
    }
}
