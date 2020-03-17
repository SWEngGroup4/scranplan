package com.group4sweng.scranplan.APIHandler;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class APICallerTest {
    @Mock
    Context mContext;

    APICaller mCaller;

    @Before public void setUp() throws Exception{
        mCaller = new APICaller();
        initMocks(this);
    }

    @Test
    public void makeTestCall() throws Exception{
        // Make a test call and check it returns no exceptions
        mCaller.setmUrl("http://www.google.com");
        mCaller.setUpAPICaller(mContext);
    }

    @Test
    public void testSetContext() throws Exception{
        mCaller.setmContext(mContext);
        assertEquals(mContext,mCaller.getmContext());
    }

    @Test
    public void testSetNetwork() throws Exception{
        Network mNetwork = new BasicNetwork(new HurlStack());
        mCaller.setmNetwork(mNetwork);
        assertEquals(mNetwork, mCaller.getmNetwork());
    }

    @Test
    public void testSettingUrl() throws Exception{
        // Set the URL string and test that it returns the correct string
        mCaller.setmUrl("TestString");
        assertEquals("TestString",mCaller.getmUrl());
    }

}