package project.gamemechanics.scoreboard;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("WeakerAccess")
public class ScoreboardMapper implements RowMapper<ScoreboardRecord> {

    @Override
    public ScoreboardRecord mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        return new ScoreboardRecord(rs.getInt("userid"), rs.getInt("gold"),
                rs.getInt("frags"), rs.getString("username"));
    }
}
