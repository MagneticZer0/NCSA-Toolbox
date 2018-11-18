package tech.ncsa.ncsatoolbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import tech.ncsa.ncsatoolbox.knowledgeBase.KnowledgeBaseFragment;
import tech.ncsa.ncsatoolbox.more.about.AboutFragment;
import tech.ncsa.ncsatoolbox.toolbox.ToolboxFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MenuItem mPreviousMenuItem;

    List<List<String>> knowledgebaseItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            // Sets the default item to item 0
            navigationView.getMenu().getItem(0).setChecked(true);
            navigationView.getMenu().getItem(0).setCheckable(true);
            mPreviousMenuItem = navigationView.getMenu().getItem(0);

            FragmentManager fragMan = getSupportFragmentManager();
            fragMan.beginTransaction().replace(R.id.content_frame, new ToolboxFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Updates highlighting
        if (item.getItemId() != R.id.nav_website) {
            item.setCheckable(true);
            item.setChecked(true);
            if (mPreviousMenuItem != null && mPreviousMenuItem != item) {
                mPreviousMenuItem.setChecked(false);
            }
            mPreviousMenuItem = item;
        }

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragMan = getSupportFragmentManager();

        if (id == R.id.Toolbox) {
            fragMan.beginTransaction().replace(R.id.content_frame, new ToolboxFragment()).commit();
        } else if (id == R.id.nav_slideshow) {
            KnowledgeBaseFragment knowledgebase = new KnowledgeBaseFragment();
            Bundle typeArguments = new Bundle();
            typeArguments.putString("type", "Presentations");
            knowledgebase.setArguments(typeArguments);
            fragMan.beginTransaction().replace(R.id.content_frame, knowledgebase).commit();
        } else if (id == R.id.nav_pdf) {
            KnowledgeBaseFragment knowledgebase = new KnowledgeBaseFragment();
            Bundle typeArguments = new Bundle();
            typeArguments.putString("type", "PDFs");
            knowledgebase.setArguments(typeArguments);
            fragMan.beginTransaction().replace(R.id.content_frame, knowledgebase).commit();
        } else if (id == R.id.nav_video) {
            KnowledgeBaseFragment knowledgebase = new KnowledgeBaseFragment();
            Bundle typeArguments = new Bundle();
            typeArguments.putString("type", "Videos");
            knowledgebase.setArguments(typeArguments);
            fragMan.beginTransaction().replace(R.id.content_frame, knowledgebase).commit();
        } else if (id == R.id.nav_website) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            builder.enableUrlBarHiding();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(getString(R.string.ncsa_website)));
//            Uri uri = Uri.parse(getString(R.string.ncsa_website));
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
        } else if (id == R.id.nav_about) {
            fragMan.beginTransaction().replace(R.id.content_frame, new AboutFragment()).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
