package gamemechanics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gamemechanics.components.affectors.Affector;
import gamemechanics.components.properties.Property;
import gamemechanics.components.properties.PropertyCategories;
import gamemechanics.components.properties.SingleValueProperty;
import gamemechanics.globals.Constants;
import gamemechanics.resources.pcg.items.ItemPart;
import gamemechanics.resources.pcg.items.ItemPartAsset;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class PropertySerializationTest {


    @Test
    public void itemPartSerializationDeserialization() {
        Random random = new Random(System.currentTimeMillis());
        Map<Integer, Affector> testAffectors = new HashMap<>();
        Map<Integer, Property> testProperty = new HashMap<>();
        testProperty.put(PropertyCategories.PC_LEVEL, new SingleValueProperty(Constants.START_LEVEL));
        ItemPart itemPart = new ItemPartAsset(0,"test asset", "test description",
                ItemPart.FIRST_PART_ID, testAffectors, testProperty);
        ObjectMapper mapper = new ObjectMapper();
        String jsonAsset = null;
        try {
            jsonAsset = mapper.writeValueAsString(itemPart);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertNotEquals("data shall be written", jsonAsset, null);
        assertFalse("serialized json shall contain data", jsonAsset.isEmpty());
        ItemPart deserialized = null;
        try {
            deserialized = mapper.readValue(jsonAsset, ItemPartAsset.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String secondJson = null;
        try {
            secondJson = mapper.writeValueAsString(deserialized);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertEquals("serialized data must be equal", jsonAsset, secondJson);

        assertEquals(itemPart.getName(), deserialized.getName());
        assertEquals(itemPart.getDescription(), deserialized.getDescription());
        assertEquals(itemPart.getAllAffectors(), deserialized.getAllAffectors());
        assertEquals(itemPart.getProperty(PropertyCategories.PC_LEVEL),
                deserialized.getProperty(PropertyCategories.PC_LEVEL));
        System.out.println(jsonAsset);
        System.out.println(secondJson);
    }
}