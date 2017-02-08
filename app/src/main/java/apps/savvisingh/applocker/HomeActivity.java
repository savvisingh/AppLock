package apps.savvisingh.applocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import apps.savvisingh.applocker.Data.AppInfo;
import apps.savvisingh.applocker.Fragments.AllAppFragment;
import apps.savvisingh.applocker.Fragments.PasswordFragment;
import apps.savvisingh.applocker.Fragments.SettingsFragment;
import apps.savvisingh.applocker.Utils.AppLockConstants;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fragmentManager = getSupportFragmentManager();

        getSupportActionBar().setTitle("All Applications");
        Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getCurrentFragment() instanceof AllAppFragment) {
                super.onBackPressed();
            } else {
                fragmentManager.popBackStack();
                getSupportActionBar().setTitle("All Applications");
                Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
            }
        }
    }

    /**
     * Returns currentfragment
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_apps) {
            getSupportActionBar().setTitle("All Applications");
            Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        } else if (id == R.id.nav_locked_apps) {
            getSupportActionBar().setTitle("Locked Applications");
            Fragment f = AllAppFragment.newInstance(AppLockConstants.LOCKED);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();


        } else if (id == R.id.nav_unlocked_apps) {
            getSupportActionBar().setTitle("Unlocked Applications");
            Fragment f = AllAppFragment.newInstance(AppLockConstants.UNLOCKED);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

        } else if (id == R.id.nav_change_pattern) {
            getSupportActionBar().setTitle("Change Password");
            Fragment f = PasswordFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

        }else if(id == R.id.nav_allow_access){
            getSupportActionBar().setTitle("Allow Access");
            Fragment f = SettingsFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * get the list of all installed applications in the device
     *
     * @return ArrayList of installed applications or null
     */
    public static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> installedApps = new ArrayList();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        if (apps != null && !apps.isEmpty()) {

            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                ApplicationInfo appInfo = null;
                try {
                    if (null != packageManager.getLaunchIntentForPackage(p.packageName)) {
                        // appInfo = packageManager.getApplicationInfo(p.packageName, 0);
                        AppInfo app = new AppInfo();
                        app.setName(p.applicationInfo.loadLabel(packageManager).toString());
                        app.setPackageName(p.packageName);
                        app.setVersionName(p.versionName);
                        app.setVersionCode(p.versionCode);
                        app.setIcon(p.applicationInfo.loadIcon(packageManager));

                        installedApps.add(app);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return installedApps;
        }
        return null;
    }
}
