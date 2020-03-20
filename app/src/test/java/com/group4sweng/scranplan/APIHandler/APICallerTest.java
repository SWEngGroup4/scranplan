package com.group4sweng.scranplan.APIHandler;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class APICallerTest {

    @Before public void setUp() throws Exception{
        initMocks(this);
    }

    @Test
    public void MakeTestCall() throws Exception{
        // Make a test call and check it returns no exceptions
        int i = new APICaller("http://www.google.com").send();

        // Code 200 = success
        assertEquals(200, i);
    }

    @Test
    public void SendFakeUrl() throws Exception{
        // make a non url request
        int i = new APICaller("notaurl").send();
        assertEquals(-1, i);
    }

    @Test
    public void MakeJSONRequest() throws Exception {
        JSONObject object = new APICaller("http://www.google.com").sendAndReadJSON();
        assertNotNull(object);
    }

    @Test
    public void CallWithData() throws Exception{
        int i = new APICaller("http://www.google.com/search?").withData("q=google").send();
        assertNotNull(i);
    }

    @Test
    public void CallWithHeader() throws Exception{
        int i = new APICaller("http://www.google.com/search?").withHeaders("q:header").send();
        assertNotNull(i);
    }

}