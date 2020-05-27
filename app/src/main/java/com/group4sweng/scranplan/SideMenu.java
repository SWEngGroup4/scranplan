package com.group4sweng.scranplan;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.group4sweng.scranplan.Administration.SuggestionBox;

public class SideMenu extends Activity implements NavigationView.OnNavigationItemSelectedListener {
    public Toolbar mMenuToolbar;
    public DrawerLayout mMenuDrawer;
    public ActionBarDrawerToggle mDrawerToggle;
    public NavigationView mNavigationView;
    private Activity mActivity;
    private Context mContext;

    private com.group4sweng.scranplan.UserInfo.UserInfoPrivate mUser;

    public void init(Activity activity, Context context){
        this.mActivity = activity;
        this.mContext = context;
        startMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.side_menu, menu);
        return true;
    }

    private void startMenu() {
        mDrawerToggle = new ActionBarDrawerToggle(mActivity, mMenuDrawer, mMenuToolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mMenuDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //TODO implement other pages as they get added to the application
            case R.id.nav_publicProfile:
                if (mContext instanceof Home){
                    ((Home)mContext).onPublicProfileClick();
                }
                break;
            case R.id.nav_editProfile:
                 if (mContext instanceof Home){
                    ((Home)mContext).onProfileEditClick();
                }
                break;
            case R.id.nav_suggestionBox:
                if(mContext instanceof Home) {
                    ((Home)mContext).onSuggestionBoxClick();
                }
                break;
            case R.id.nav_logout:
                if(mContext instanceof Home) {
                    ((Home) mContext).onLogoutMenuClick();
                }
                break;
            case R.id.nav_shoppingList:
                if (mContext instanceof Home){
                    ((Home)mContext).onShoppingListClick();
                }
                break;
        }
     return false;
    }

}


