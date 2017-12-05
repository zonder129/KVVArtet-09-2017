package project.gamemechanics.flyweights;

import project.gamemechanics.interfaces.Ability;
import project.gamemechanics.interfaces.CharacterRole;
import project.gamemechanics.interfaces.Perk;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class CharacterClass implements CharacterRole {
    private final Integer characterClassID;

    private final String name;
    private final String description;

    private final Map<Integer, Ability> abilities;
    private final Map<Integer, PerkBranch> branches;

    public static class CharacterClassModel {
        public final Integer id;
        public final String name;
        public final String description;
        @SuppressWarnings("PublicField")
        public final Map<Integer, Ability> abilities;
        @SuppressWarnings("PublicField")
        public final Map<Integer, PerkBranch> branches;

        public CharacterClassModel(@NotNull Integer id, @NotNull String name,
                                   @NotNull String description, @NotNull Map<Integer, Ability> abilities,
                                   @NotNull Map<Integer, PerkBranch> branches) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.abilities = abilities;
            this.branches = branches;
        }
    }

    public CharacterClass(@NotNull CharacterClassModel model) {
        characterClassID = model.id;
        name = model.name;
        description = model.description;
        abilities = model.abilities;
        branches = model.branches;
    }

    @Override
    public Integer getInstancesCount() {
        return 0;
    }

    @Override
    public Integer getID() {
        return characterClassID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Ability getAbility(Integer abilityID) {
        return abilities.getOrDefault(abilityID, null);
    }

    @Override
    public Map<Integer, Ability> getAllAbilities() {
        return abilities;
    }

    @Override
    public PerkBranch getBranch(Integer branchID) {
        return branches.get(branchID);
    }

    @Override
    public Perk getPerk(Integer branchID, Integer perkID) {
        return branches.get(branchID).getPerk(perkID);
    }
}