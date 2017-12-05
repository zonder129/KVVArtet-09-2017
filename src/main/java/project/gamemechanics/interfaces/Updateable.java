package project.gamemechanics.interfaces;

/**
 * An interface for entities which have some repeatable routine either
 * for turn-based battle mode or for project.server ticks
 */
public interface Updateable {
    /**
     * routine to repeat on each battle turn
     */
    default void update() {
    } // for battle turns

    /**
     * routine to repeat on each project.server tick (once per given timestep)
     *
     * @param timestep fixed timestep value
     */
    default void update(Integer timestep) {
    } // for any other occasions;
}