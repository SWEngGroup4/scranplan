package com.group4sweng.scranplan.APIHandler;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class GrocerySearcherTest {
    @Mock
    GrocerySearcher mGrocerySearcher;

    @Before
    public void setUp() throws Exception{
        initMocks(this);
    }

    @Test
    public void testTescoIngredientSearch() throws IOException, JSONException {
        mGrocerySearcher = new GrocerySearcher(GrocerySearcher.Shop.Tesco);
        int result = mGrocerySearcher.SearchForIngredient("tomato");

    }

}