package gamemechanics.components.mappers;

import gamemechanics.components.affectors.AffectorCategories;
import gamemechanics.components.properties.PropertyCategories;
import gamemechanics.globals.Constants;

import java.util.HashMap;
import java.util.Map;

public final class PropertyToAffectorMapper {
    private  PropertyToAffectorMapper() {}

    private static final Map<Integer, Integer> propertyAffectorMapping = initMap();

    public static final Integer getAffectorKind(Integer propertyKind) {
        if (!propertyAffectorMapping.containsKey(propertyKind)) {
            return Constants.WRONG_INDEX;
        }
        return propertyAffectorMapping.get(propertyKind);
    }

    private static Map<Integer, Integer> initMap() {
        Map<Integer, Integer> mapping = new HashMap<>();
        mapping.put(PropertyCategories.PC_STATS, AffectorCategories.AC_STATS_AFFECTOR);
        mapping.put(PropertyCategories.PC_RATINGS, AffectorCategories.AC_RATINGS_AFFECTOR);
        mapping.put(PropertyCategories.PC_HITPOINTS, AffectorCategories.AC_HEALTH_AFFECTOR);
        mapping.put(PropertyCategories.PC_BASE_DAMAGE, AffectorCategories.AC_DAMAGE_AFFECTOR);
        mapping.put(PropertyCategories.PC_BASE_DEFENSE, AffectorCategories.AC_DEFENSE_AFFECTOR);
        return mapping;
    }
}
