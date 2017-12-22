package project.gamemechanics.components.properties;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(SingleValueProperty.class),
        @JsonSubTypes.Type(ListProperty.class),
        @JsonSubTypes.Type(MapProperty.class),
        @JsonSubTypes.Type(SetProperty.class),
})
public interface Property {
    Integer getProperty();

    Integer getProperty(@NotNull Integer propertyIndex);

    void setSingleProperty(@NotNull Integer property);

    void setSingleProperty(@NotNull Integer propertyIndex, @NotNull Integer property);

    List<Integer> getPropertyList();

    void setPropertyList(@NotNull List<Integer> properties);

    Map<Integer, Integer> getPropertyMap();

    Boolean setPropertyMap(@NotNull Map<Integer, Integer> property);

    Set<Integer> getPropertySet();

    Boolean setPropertySet(@NotNull Set<Integer> property);

    Boolean modifyByPercentage(@NotNull Float percentage);

    Boolean modifyByPercentage(@NotNull Integer itemIndex, @NotNull Float percentage);

    void modifyByAddition(@NotNull Integer toAdd);

    Boolean modifyByAddition(@NotNull Integer propertyIndex, @NotNull Integer toAdd);
}

