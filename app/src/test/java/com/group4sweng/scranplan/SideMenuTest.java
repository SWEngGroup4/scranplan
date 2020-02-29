package com.group4sweng.scranplan;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SideMenuTest {

    FirebaseAuth mockAuth = Mockito.mock(FirebaseAuth.class);
    FirebaseApp mockApp = Mockito.mock(FirebaseApp.class);
    SideMenu mMockMenu;

    @Mock
    private MainActivity mainActivity = new MainActivity();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        SideMenu mockMenu = new SideMenu();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void launchMenuCheckItemsAreDisplayed() throws Exception {
//        int maxLines = mMockMenu.mNavigationView.getItemMaxLines();
//        assertEquals(5, maxLines);
    }

    @Test
    public void onNavigationItemSelected() {
    }
}