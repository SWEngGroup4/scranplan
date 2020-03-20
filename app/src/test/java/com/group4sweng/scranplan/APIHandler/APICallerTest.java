package com.group4sweng.scranplan.APIHandler;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class APICallerTest {

    @Before public void setUp() throws Exception{
        initMocks(this);
    }

    @Test
    public void MakeTestCall() throws Exception{
        // Make a test call and check it returns no exceptions
        int i = new APICaller("https://postman-echo.com/get").prepare().send();

        // Code 200 = success
        assertEquals(200, i);
    }


    @Test
    public void MakeJSONRequest() throws Exception {
        JSONObject object = new APICaller("https://postman-echo.com/get").prepare().sendAndReadJSON();
        assertNotNull(object);
    }


    @Test
    public void CallWithData() throws Exception{
        int i = new APICaller("https://postman-echo.com/get").prepare(APICaller.Method.POST).withData("?foo1=bar1&foo2=bar2").send();
        if(i< 100 || i > 511){
            fail("fail");
        }
    }

    @Test
    public void CallWithHeader() throws Exception{
        int i = new APICaller("http://www.google.com/search?").prepare().withHeaders("q:header").send();
        if(i< 100 || i > 511){
            fail("fail");
        }
    }

    @Test
    public void CallWithHeader() throws Exception{
        int i = new APICaller("http://www.google.com/search?").withHeaders("q:header").send();
        assertNotNull(i);
    }

}