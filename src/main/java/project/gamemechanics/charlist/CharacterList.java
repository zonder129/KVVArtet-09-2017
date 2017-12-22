package project.gamemechanics.charlist;

import project.gamemechanics.aliveentities.AbstractAliveEntity.UserCharacterModel;
import project.gamemechanics.aliveentities.UserCharacter;
import project.gamemechanics.interfaces.Charlist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class CharacterList implements Charlist {

    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger(0);
    private final Integer charlistID = INSTANCE_COUNTER.getAndIncrement();
    private List<UserCharacter> characterList = new ArrayList<>();
    private final Integer ownerID;

    @SuppressWarnings({"InstanceMethodNamingConvention", "unused", "RedundantSuppression"})
    public static class CharacterListModel {
        //noinspection VisibilityModifier
        // CHECKSTYLE:OFF
        @SuppressWarnings("RedundantSuppression")
        List<UserCharacter> characterList = new ArrayList<>();
        final Integer ownerID;
        // CHECKSTYLE:ON

        public CharacterListModel(Integer ownerID, List<UserCharacter> characterList) {
            this.ownerID = ownerID;
            this.characterList = characterList;
        }

        public CharacterListModel(Integer ownerID) {
            this.ownerID = ownerID;
            this.setDefaultCharacters();
        }

        @SuppressWarnings("EmptyMethod")
        void setDefaultCharacters() {
            //TODO MAKE IT WORK
        }
    }

    public CharacterList(CharacterListModel characterListModel) {
        this.characterList = characterListModel.characterList;
        this.ownerID = characterListModel.ownerID;
    }


    @Override
    public Integer getID() {
        return charlistID;
    }

    @Override
    public Integer getInstancesCount() {
        return INSTANCE_COUNTER.get();
    }

    @Override
    public UserCharacter createChar(UserCharacterModel userModel) {
        final UserCharacter newUserCharacter = new UserCharacter(userModel);
        this.characterList.add(newUserCharacter);
        //insert in database
        return newUserCharacter;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void deleteChar(Integer index) {
        final Integer charID = this.characterList.get(index).getID();
        this.characterList.remove(index);
        //delete in database with charID
    }

    @Override
    public Integer getOwnerId() {
        return ownerID;
    }

    public List<UserCharacter> getCharacterList() {
        return characterList;
    }
}
