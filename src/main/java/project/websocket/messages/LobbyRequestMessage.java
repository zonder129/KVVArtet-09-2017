package project.websocket.messages;

@SuppressWarnings("unused")
public class LobbyRequestMessage extends Message {
    private Integer gameMode;

    public Integer getGameMode() {
        return gameMode;
    }

    public void setGameMode(Integer gameMode) {
        this.gameMode = gameMode;
    }
}
