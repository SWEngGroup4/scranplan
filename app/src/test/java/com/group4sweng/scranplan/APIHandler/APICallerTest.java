package com.group4sweng.scranplan.APIHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import okhttp3.Response;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class APICallerTest {

    @Before public void setUp() throws Exception{
        initMocks(this);
    }

    @Test
    public void MakeTestCall() throws Exception{
        APICaller mAPIcaller = new APICaller();
        mAPIcaller.setmURL("https://postman-echo.com/get");
        mAPIcaller.setmHeaderKey("User-Agent");
        mAPIcaller.setmHeaderValue("Test");
        Response mResponse = mAPIcaller.sendGet();
        System.out.print(mResponse.toString());
        assertTrue(mResponse.isSuccessful());
    }


    @Test
    public void CallWithData() throws Exception{
        APICaller mAPIcaller = new APICaller();
        mAPIcaller.setmURL("https://postman-echo.com/post");
        mAPIcaller.setmHeaderKey("User-Agent");
        mAPIcaller.setmHeaderValue("Test");
        mAPIcaller.setmQueryKey("foo1");
        mAPIcaller.setmQueryValue("bar1");
        Response mResponse = mAPIcaller.sendPost();
        System.out.print(mResponse.toString());
        assertTrue(mResponse.isSuccessful());
    }

}