package project.websocket.messages;

import project.gamemechanics.interfaces.Ability;
import project.gamemechanics.interfaces.MapNode;

@SuppressWarnings("unused")
public class ActionRequestMessage extends Message {

    private MapNode sender;
    private MapNode target;
    private Ability ability;

    public MapNode getSender() {
        return sender;
    }

    public MapNode getTarget() {
        return target;
    }

    public Ability getAbility() {
        return ability;
    }

    public void setSender(MapNode sender) {
        this.sender = sender;
    }

    public void setTarget(MapNode target) {
        this.target = target;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }
}
