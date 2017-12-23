package project.gamemechanics.scoreboard;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = ScoreboardController.FRONTED_URL1)
public class ScoreboardController {
    @SuppressWarnings("WeakerAccess")
    static final String FRONTED_URL1 = "https://lands-dangeous.herokuapp.com";
    private final ScoreboardService scoreboardService;

    public ScoreboardController(ScoreboardService scoreboardService) {
        super();
        this.scoreboardService = scoreboardService;
    }

    @GetMapping(value = "restapi//scoreboard", produces = "application/json")
    @ResponseBody
    public List<ScoreboardRecord> getScoreboard() {

        return scoreboardService.getAllRecords();
    }
}