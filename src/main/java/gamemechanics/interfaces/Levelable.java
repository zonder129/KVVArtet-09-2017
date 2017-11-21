package gamemechanics.interfaces;

/**
 * Interface for entities that have level property.
 * Allows to ask for entity's current level and implements some
 * level-up logic. Extends {@link GameEntity} interface
 */
public interface Levelable extends GameEntity {
    /**
     * get an entity's current level
     *
     * @return entity's current level
     */
    Integer getLevel();

    /**
     * method for level-up logic.
     * can be used both for in-game level-up (as in {@link gamemechanics.aliveentities.UserCharacter} class)
     * or to scale entity's properties on creation (as for {@link gamemechanics.items.IngameItem} class)
     */
    default void levelUp() {
    }
}
