package project.gamemechanics.battlefield;

import project.gamemechanics.aliveentities.helpers.CashCalculator;
import project.gamemechanics.aliveentities.helpers.ExperienceCalculator;
import project.gamemechanics.aliveentities.npcs.ai.AI;
import project.gamemechanics.battlefield.actionresults.ActionResult;
import project.gamemechanics.battlefield.actionresults.events.EventCategories;
import project.gamemechanics.battlefield.actionresults.events.EventsFactory;
import project.gamemechanics.battlefield.actionresults.events.TurnEvent;
import project.gamemechanics.battlefield.aliveentitiescontainers.SpawnPoint;
import project.gamemechanics.battlefield.aliveentitiescontainers.Squad;
import project.gamemechanics.battlefield.map.BattleMap;
import project.gamemechanics.battlefield.map.helpers.Pathfinder;
import project.gamemechanics.components.properties.PropertyCategories;
import project.gamemechanics.globals.Constants;
import project.gamemechanics.globals.DigitsPairIndices;
import project.gamemechanics.interfaces.Action;
import project.gamemechanics.interfaces.AliveEntity;
import project.gamemechanics.interfaces.Effect;
import project.gamemechanics.interfaces.Updateable;

import javax.validation.constraints.NotNull;
import java.util.*;

public class Battlefield implements Updateable {
    private static final int SQUADS_COUNT = 2;

    private static final int INITIAL_TURN_NUMBER = 1;

    private static final int ACTIONS_PER_TURN = 2;

    public static final int PVE_GAME_MODE = 0;
    public static final int PVP_GAME_MODE = 1;

    @SuppressWarnings("FieldCanBeLocal")
    private final Map<Integer, AI.BehaviorFunction> behaviors;

    private final BattleMap map;
    @SuppressWarnings("FieldCanBeLocal")
    private final Pathfinder pathfinder;

    private final List<Squad> squads = new ArrayList<>(SQUADS_COUNT);

    private final Deque<AliveEntity> battlersQueue = new ArrayDeque<>();

    private final Deque<Action> actionsQueue = new ArrayDeque<>();
    private final List<ActionResult> battleLog = new ArrayList<>();
    private Integer activeBattlerActionsPooled = 0;

    private final List<Effect> appliedEffects = new ArrayList<>();

    private Integer turnCounter = INITIAL_TURN_NUMBER;

    private final Integer mode;

    public static class BattlefieldModel {
        @SuppressWarnings("PublicField")
        public final Map<Integer, AI.BehaviorFunction> behaviors;
        @SuppressWarnings("PublicField")
        public final BattleMap map;
        @SuppressWarnings("PublicField")
        public final List<SpawnPoint> spawnPoints;
        public final Integer mode;

        public BattlefieldModel(@NotNull Map<Integer, AI.BehaviorFunction> behaviors, @NotNull BattleMap map,
                                @NotNull List<SpawnPoint> spawnPoints, @NotNull Integer mode) {
            this.behaviors = behaviors;
            this.map = map;
            this.spawnPoints = spawnPoints;
            this.mode = mode;
        }
    }

    @SuppressWarnings("OverlyComplexMethod")
    public Battlefield(@NotNull BattlefieldModel model) {
        behaviors = model.behaviors;
        map = model.map;
        mode = model.mode;
        pathfinder = new Pathfinder(map);

        emplaceBattlers(model.spawnPoints);

        switch (mode) {
            case PVE_GAME_MODE:
                squads.set(Squad.PLAYERS_SQUAD_ID, model.spawnPoints.get(Squad.PLAYERS_SQUAD_ID).getSquad());
                mergeMonsterSquads(model.spawnPoints);
                break;
            case PVP_GAME_MODE:
                squads.set(Squad.TEAM_ONE_SQUAD_ID, model.spawnPoints.get(Squad.TEAM_ONE_SQUAD_ID).getSquad());
                squads.set(Squad.TEAM_TWO_SQUAD_ID, model.spawnPoints.get(Squad.TEAM_TWO_SQUAD_ID).getSquad());
                break;
            default:
                break;
        }

        final List<AliveEntity> battlersList = new ArrayList<>();
        for (Squad squad : squads) {
            for (Integer i = 0; i < squad.getSquadSize(); ++i) {
                if (squad.getMember(i) != null) {
                    battlersList.add(squad.getMember(i));
                }
            }
        }
        battlersList.sort(Comparator.comparingInt(AliveEntity::getInitiative));
        for (AliveEntity battler : battlersList) {
            battlersQueue.addFirst(battler);
        }

        final Squad monsterSquad = squads.get(Squad.MONSTER_SQUAD_ID);
        for (Integer i = 0; i < monsterSquad.getSquadSize(); ++i) {
            final Map<Integer, AI.BehaviorFunction> monsterBehaviors = new HashMap<>();
            Integer activeBehaviorId = Constants.WRONG_INDEX;
            for (Integer behaviorId : monsterSquad.getMember(i).getCharacterRole().getBehaviorIds()) {
                if (behaviors.containsKey(behaviorId)) {
                    if (activeBehaviorId == Constants.WRONG_INDEX) {
                        activeBehaviorId = behaviorId;
                    }
                    monsterBehaviors.put(behaviorId, behaviors.get(behaviorId));
                }
            }
            monsterSquad.getMember(i).setBehavior(new AI(monsterSquad.getMember(i),
                    monsterSquad, squads.get(Squad.PLAYERS_SQUAD_ID), map, pathfinder,
                    monsterSquad.getMember(i).getCharacterRole().getAllAbilities(),
                    monsterBehaviors, activeBehaviorId));
        }
    }

    public Integer getTurn() {
        return turnCounter;
    }

    @Override
    public void update() {
        if (actionsQueue.isEmpty()) {
            return;
        }
        ++turnCounter;
        while (!actionsQueue.isEmpty()) {
            battleLog.add(actionsQueue.getFirst().execute());
        }

        /* Note: now only updating an active battler per turn
         * to prevent enormous effects' ticking
         */
        battlersQueue.getFirst().update();

        removeDead();
        removeExpiredEffects();
        processEvents();

        if (!isBattleFinished()) {
            final AliveEntity activeBattler = battlersQueue.getFirst();
            if (activeBattler.isControlledByAI()) {
                while (activeBattlerActionsPooled < ACTIONS_PER_TURN) {
                    pushAction(activeBattler.makeDecision());
                    actionsQueue.pollFirst().execute();
                }
            }

            if (activeBattlerActionsPooled == ACTIONS_PER_TURN) {
                activeBattlerActionsPooled = 0;
                battlersQueue.addLast(battlersQueue.pollFirst());
                // end turn event will be added to the last pooled action
                battleLog.get(battleLog.size() - 1).addEvent(EventsFactory.makeEndTurnEvent());
            }
        } else {
            onBattleEnd();
        }
    }

    public Boolean isBattleFinished() {
        return isVictory() || isDefeat();
    }

    public Boolean isVictory() {
        return squads.get(Squad.TEAM_TWO_SQUAD_ID).areAllDead();
    }

    public Boolean isDefeat() {
        return squads.get(Squad.TEAM_ONE_SQUAD_ID).areAllDead();
    }

    public void pushAction(Action action) {
        if (action.getSender().getInhabitant() == battlersQueue.getFirst()
                && activeBattlerActionsPooled < ACTIONS_PER_TURN) {
            actionsQueue.addLast(action);
            ++activeBattlerActionsPooled;
        }
    }

    @SuppressWarnings("SameReturnValue")
    public Boolean pushAction(/* JSON packet here */) {
        return false;
    }

    public Integer getBattleLogLength() {
        return battleLog.size();
    }

    public List<ActionResult> getBattleLog() {
        return battleLog;
    }

    public ActionResult getBattleLogEntry(Integer entryIndex) {
        if (entryIndex < 0 || entryIndex >= battleLog.size()) {
            return null;
        }
        return battleLog.get(entryIndex);
    }

    private void emplaceBattlers(List<SpawnPoint> spawnPoints) {
        for (SpawnPoint spawnPoint : spawnPoints) {
            if (spawnPoint != null) {
                spawnPoint.emplaceSquad();
            }
        }
    }

    private void mergeMonsterSquads(List<SpawnPoint> spawnPoints) {
        final Squad monsterSquad = new Squad(new ArrayList<>(), Squad.MONSTER_SQUAD_ID);
        for (SpawnPoint spawnPoint : spawnPoints) {
            if (spawnPoint != null) {
                if (spawnPoint.getSquad().getSquadID() == Squad.MONSTER_SQUAD_ID) {
                    for (Integer i = 0; i < spawnPoint.getSquad().getSquadSize(); ++i) {
                        monsterSquad.addMember(spawnPoint.getSquad().getMember(i));
                    }
                }
            }
        }
        squads.set(Squad.MONSTER_SQUAD_ID, monsterSquad);
    }

    private void removeDead() {
        for (AliveEntity battler : battlersQueue) {
            if (!battler.isAlive()) {
                battlersQueue.remove(battler);
            }
        }
    }

    private void removeExpiredEffects() {
        for (Effect effect : appliedEffects) {
            if (effect.isExpired()) {
                appliedEffects.remove(effect);
            }
        }
    }

    private void endBattleCleanup() {
        for (Squad squad : squads) {
            for (Integer i = 0; i < squad.getSquadSize(); ++i) {
                squad.getMember(i).removeProperty(PropertyCategories.PC_COORDINATES);
            }
        }
    }

    private void generateLoot() {
        // collect all loot lists from mobs and generate loot
        for (Integer i = 0; i < squads.get(Squad.MONSTER_SQUAD_ID).getSquadSize(); ++i) {

        }
    }

    private void rewardTeam(Integer teamID) {
        final Integer looseTeamID;
        switch (teamID) {
            case Squad.TEAM_ONE_SQUAD_ID:
                looseTeamID = Squad.TEAM_TWO_SQUAD_ID;
                break;
            case Squad.TEAM_TWO_SQUAD_ID:
                looseTeamID = Squad.TEAM_ONE_SQUAD_ID;
                break;
            default:
                return;
        }
        // reward winners with full-scale
        for (Integer i = 0; i < squads.get(teamID).getSquadSize(); ++i) {

        }
        // also reward the team that's lost but with lesser reward
        for (Integer i = 0; i < squads.get(looseTeamID).getSquadSize(); ++i) {

        }
    }

    private void generateReward() {
        switch (mode) {
            case PVE_GAME_MODE:
                if (isVictory()) {
                    generateLoot();
                }
                break;
            case PVP_GAME_MODE:
                if (isVictory()) {
                    rewardTeam(Squad.TEAM_ONE_SQUAD_ID);
                } else {
                    rewardTeam(Squad.TEAM_TWO_SQUAD_ID);
                }
                break;
            default:
                break;
        }
    }

    private void onBattleEnd() {
        endBattleCleanup();
        generateReward();
    }

    @SuppressWarnings("OverlyComplexMethod")
    private void processEvents() {
        for (ActionResult entry : battleLog) {
            if (!entry.getIsProcessed()) {
                for (Integer eventIndex = 0; eventIndex < entry.getEventsCount(); ++eventIndex) {
                    final TurnEvent event = entry.getEvent(eventIndex);
                    if (event.getEventKind() == EventCategories.EC_ROLLBACK) {
                        if (activeBattlerActionsPooled > 0) {
                            --activeBattlerActionsPooled;
                            break;
                        }
                    }

                    if (event.getEventKind() == EventCategories.EC_END_TURN) {
                        break;
                    }

                    if (event.getEventKind() == EventCategories.EC_HITPOINTS_CHANGE) {
                        if (event.getAmount() < 0 && !event.getWhere().getInhabitant().isAlive()) {
                            final Integer squadIdToReward = event.getWhere().getInhabitant()
                                    .getProperty(PropertyCategories.PC_SQUAD_ID) == Squad.TEAM_ONE_SQUAD_ID
                                    ? Squad.TEAM_TWO_SQUAD_ID : Squad.TEAM_ONE_SQUAD_ID;
                            if (squadIdToReward == Squad.PLAYERS_SQUAD_ID || mode == PVP_GAME_MODE) {
                                Integer averagePartyLevel = 0;
                                for (Integer i = 0; i < squads.get(squadIdToReward).getSquadSize(); ++i) {
                                    final AliveEntity member = squads.get(squadIdToReward).getMember(i);
                                    if (member != null) {
                                        if (member.isAlive()) {
                                            averagePartyLevel += member.getLevel();
                                        }
                                    }
                                }
                                final Integer expAmount = ExperienceCalculator.getPartyBiasedXPReward(
                                        ExperienceCalculator.getXPReward(averagePartyLevel
                                                        / squads.get(squadIdToReward).getAliveMembersCount(),
                                                event.getWhere().getInhabitant().getLevel()),
                                        squads.get(squadIdToReward).getAliveMembersCount());
                                final Integer cashAmount = CashCalculator.getPartyBiasedCashReward(
                                        CashCalculator.getCashReward(event.getWhere().getInhabitant().getLevel()),
                                        squads.get(squadIdToReward).getAliveMembersCount());
                                for (Integer i = 0; i < squads.get(squadIdToReward).getSquadSize(); ++i) {
                                    final AliveEntity member = squads.get(squadIdToReward).getMember(i);
                                    if (member != null) {
                                        if (member.isAlive() && member.hasProperty(PropertyCategories.PC_XP_POINTS)
                                                && member.hasProperty(PropertyCategories.PC_CASH_AMOUNT)) {
                                            member.modifyPropertyByAddition(PropertyCategories.PC_XP_POINTS,
                                                    DigitsPairIndices.CURRENT_VALUE_INDEX, expAmount);
                                            member.modifyPropertyByAddition(PropertyCategories.PC_CASH_AMOUNT,
                                                    cashAmount);
                                            entry.addEvent(entry.getEventIndex(event) + 1,
                                                    EventsFactory.makeRewardEvent(map.getTile(
                                                            member.getProperty(PropertyCategories.PC_COORDINATES,
                                                                    DigitsPairIndices.ROW_COORD_INDEX),
                                                            member.getProperty(PropertyCategories.PC_COORDINATES,
                                                                    DigitsPairIndices.COL_COORD_INDEX)),
                                                            expAmount, cashAmount));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                entry.markProcessed();
            }
        }
    }
}