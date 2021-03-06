package com.group4sweng.scranplan.MealPlanner;

import com.group4sweng.scranplan.Exceptions.PortionConvertException;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Portions JUnit tests.
 * Author: JButler
 * (c) CoDev 2020
 *
 * -- USER STORY TESTS LINKED WITH ---
 *  C34 , A14
 */
public class PortionsTest {
    private HashMap<String, String> ingredients = new HashMap<>();
    private HashMap<String, String> scaledIngredients = new HashMap<>();

    //  Test we can retrieve a numerical value from a proportion. Checks integer values and decimal values.
    @Test
    public void testQuantityCanBeRetrievedFromPortion(){
        float correctQuantityAndString = Portions.retrieveQuantity("Test String 100");

        assertEquals(100.0f, correctQuantityAndString, 0.0f);

        float invalidQuantityDecimal = Portions.retrieveQuantity("Test String 1.3452");
        assertEquals(1.3452f, invalidQuantityDecimal, 0.0f);
    }

    //  Test Portion Quantity isn't retrieved if none is present.
    @Test
    public void testQuantityNotRetrieved() {
        float noQuantityString = Portions.retrieveQuantity("Test String");

        assertEquals(-1.0f, noQuantityString, 0.0f);
    }

    //  Test invalid Portion quantity inputs.
    //  Negative values will be handled but inputs with more than one '.' character will and should fail.
    @Test
    public void testInvalidQuantityInputs() {
        float invalidQuantityNegative = Portions.retrieveQuantity("Test String -100");
        assertEquals(100f, invalidQuantityNegative, 0.0f);

        float invalidQuantityFullstops = Portions.retrieveQuantity("Test String 1.3452...");
        assertEquals(-1.0f, invalidQuantityFullstops, 0.0f);
    }

    //  Test the corresponding measurement for an ingredient can be retrieved.
    @Test
    public void testMeasurementRetrieved() {
        String validMeasurement = Portions.retrieveMeasurement("100mg");
        assertEquals("mg", validMeasurement);

        String complexValidMeasurement = Portions.retrieveMeasurement("2 Peeled and Sliced");
        assertEquals(" Peeled and Sliced", complexValidMeasurement);
    }

    @Test
    public void testBlankMeasurementInput() {
        String blankMeasurement = Portions.retrieveMeasurement("");
        assertNull(blankMeasurement);
    }

    //  Test our ingredients multiplier that adjusts scaling of 'alcohol & spices' works.
    @Test
    public void testIngredientMultiplier() {
        float alcoholMultiplier = Portions.checkMultiplier("2 liters of Expensive Pinot Noir");
        assertEquals(0.5f, alcoholMultiplier, 0.0f);

        float spiceMultiplier = Portions.checkMultiplier("2g of Cumin");
        assertEquals(0.5f, spiceMultiplier, 0.0f);

        float spiceInvalidInput = Portions.checkMultiplier("ginger beer");
        assertEquals(1.0f, spiceInvalidInput, 0.0f);

        float spiceExactMultiplier = Portions.checkMultiplier("Ginger");
        assertEquals(0.5f, spiceExactMultiplier, 0.0f);
    }

    private void setupIngredientList() {
        ingredients = new HashMap<>();

        //  A variety of different inputs and quantities. Some integers, some decimals, some with no quantity.
        ingredients.put("Chopped Tomatoes", "400g can");
        ingredients.put("cod fillets", "4.0");
        ingredients.put("thyme", "few sprigs");
        ingredients.put("pale ale", "2.5 liters");
    }

    //  Test a full ingredients HashMap can be converted.
    //  Tests if portions can be increased/decreased.
    @Test
    public void testPortionConversion() throws PortionConvertException {
        setupIngredientList();

        scaledIngredients = Portions.convertPortions(ingredients, 4, 6);

        assertEquals(600.0f, Portions.retrieveQuantity(scaledIngredients.get("Chopped Tomatoes")), 0.0f);
        assertEquals(6.0f, Portions.retrieveQuantity(scaledIngredients.get("cod fillets")), 0.0f);
        assertEquals(-1.0f, Portions.retrieveQuantity(scaledIngredients.get("thyme")), 0.0f);
        assertEquals(3.12f, Portions.retrieveQuantity(scaledIngredients.get("pale ale")), 0.0f);

        scaledIngredients = Portions.convertPortions(ingredients, 6, 4);
        assertEquals(267.0f, Portions.retrieveQuantity(scaledIngredients.get("Chopped Tomatoes")), 1.0f);
        assertEquals(2.7f, Portions.retrieveQuantity(scaledIngredients.get("cod fillets")), 1.0f);
        assertEquals(-1.0f, Portions.retrieveQuantity(scaledIngredients.get("thyme")), 0.0f);
        assertEquals(2.1f, Portions.retrieveQuantity(scaledIngredients.get("pale ale")), 0.1f);
    }
}
