package project.websocket.handlers;

import org.springframework.stereotype.Component;
import project.gamemechanics.battlefield.aliveentitiescontainers.CharactersParty;
import project.gamemechanics.world.World;
import project.states.LobbyState;
import project.websocket.messages.LobbyRequestMessage;
import project.websocket.messages.Message;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@Component
public class LobbyRequestHandler extends MessageHandler<LobbyRequestMessage> {

    private final @NotNull World world;
    private final @NotNull LobbyState lobbyState;

    public LobbyRequestHandler(@NotNull World world, @NotNull LobbyState lobbyState) {
        super(LobbyRequestMessage.class);
        this.world = world;
        this.lobbyState = lobbyState;
    }

    @PostConstruct
    private void init() {
        lobbyState.registerHandler(LobbyRequestMessage.class, this);
    }

    @Override
    public Message handle(@NotNull LobbyRequestMessage message, Integer forUser) {
        //TODO some world class method

        return world.getLobby().enqueue(new CharactersParty(world.getPendingLootPool()), message.getGameMode());
    }
}