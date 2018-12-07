package tech.ncsa.ncsatoolbox;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import tech.ncsa.ncsatoolbox.knowledgeBase.KnowledgeBaseFragment;
import tech.ncsa.ncsatoolbox.more.about.AboutFragment;
import tech.ncsa.ncsatoolbox.toolbox.ToolboxFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MenuItem mPreviousMenuItem;

    private static List<List<String>> knowledgebaseItems = new ArrayList<>();

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

        new KnowledgeBaseUpdater().execute();
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

    /**
     * Returns the knowledge base items
     *
     * @return
     */
    public static List<List<String>> getKnowledgebaseItems() {
        return knowledgebaseItems;
    }

    /**
     * Used for updating the Knowledge Base list
     */
    private class KnowledgeBaseUpdater extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            File knowledgeBaseListCache = new File(getCacheDir(), "list.txt");
            if (!knowledgeBaseListCache.exists() || knowledgeBaseListCache.lastModified() + 28800000 < System.currentTimeMillis()) { // If it doesn't exist or is more than an hour old
                // Will download file later
                String testContent = "DNS and DHCP Configuration /!\\ https://ncsa.tech/Presentations/DNS%20and%20DHCP%20Configuration.png /!\\ https://ncsa.tech/Presentations/DNS%20and%20DHCP%20Configuration.pptx\n" +
                        "DNS and DHCP Configuration /!\\ https://ncsa.tech/Presentations/DNS%20and%20DHCP%20Configuration.png /!\\ https://ncsa.tech/Presentations/DNS%20and%20DHCP%20Configuration.mp4\n" +
                        "Windows Active Directory Enviroment /!\\ https://ncsa.tech/Presentations/Active%20Directory.png /!\\ https://ncsa.tech/Presentations/Active%20Directory.pptx\n" +
                        "Windows Active Directory Enviroment /!\\ https://ncsa.tech/Presentations/Active%20Directory.png /!\\ https://ncsa.tech/Presentations/Active%20Directory%20Domain%20Services.mp4\n" +
                        "Visualization /!\\ https://ncsa.tech/Presentations/Virtualization%20-F18.png /!\\ https://ncsa.tech/Presentations/Virtualization%20-F18.pptx\n" +
                        "Basics of Networking /!\\ https://ncsa.tech/Presentations/Basics%20of%20Networking%20-F18.png /!\\ https://ncsa.tech/Presentations/Basics%20of%20Networking%20-F18.pptx\n" +
                        "Introduction /!\\ https://ncsa.tech/Presentations/Intro%20to%20NCSA%20-F18.png /!\\ https://ncsa.tech/Presentations/Intro%20to%20NCSA%20-F18.pptx\n" +
                        "Routing Protocols /!\\ https://ncsa.tech/Presentations/Routing%20Protocols.png /!\\ https://ncsa.tech/Presentations/Routing%20Protocols.pptx\n" +
                        "OSI 7 Layer Model /!\\ https://ncsa.tech/Presentations/The%20OSI%207%20Layer%20Model.png /!\\ https://ncsa.tech/Presentations/The%20OSI%207%20Layer%20Model.pptx\n" +
                        "Virtual Machines /!\\ https://ncsa.tech/Presentations/Creating%20Your%20Own%20Virtual%20Machines.png /!\\ https://ncsa.tech/Presentations/Creating%20Your%20Own%20Virtual%20Machines.pptx\n" +
                        "Introduction to Kali Linux /!\\ https://ncsa.tech/Presentations/Intro%20to%20Kali%20Linux%20_%20Tools.png /!\\ https://ncsa.tech/Presentations/Intro%20to%20Kali%20Linux%20_%20Tools.pptx\n" +
                        "Static/Default Routing /!\\ https://ncsa.tech/Presentations/Static%20and%20Default%20Routing.png /!\\ https://ncsa.tech/Presentations/Static%20and%20Default%20Routing.pdf\n" +
                        "Basics of Networking /!\\ https://ncsa.tech/Presentations/Basics%20of%20Networking.png /!\\ https://ncsa.tech/Presentations/Basics%20of%20Networking.pptx\n" +
                        "Networking Topologies /!\\ https://ncsa.tech/Presentations/Network%20Topologies.png /!\\ https://ncsa.tech/Presentations/Network%20Topologies.pptx\n" +
                        "Cabling /!\\ https://ncsa.tech/Presentations/Cabling.png /!\\ https://ncsa.tech/Presentations/Cabling.pptx";
                try {
                    File listFile = new File(getCacheDir() + "/list.txt");
                    PrintWriter pw = new PrintWriter(listFile);
                    pw.print(testContent);
                    pw.close();
                } catch (IOException e) {
                    Log.e("Error: ", e.getMessage());
                }
            }
            Scanner reader = null;
            try {
                reader = new Scanner(knowledgeBaseListCache);
            } catch (FileNotFoundException e) {
                Log.e("Error: ", e.getMessage());
            }
            while (reader.hasNextLine()) {
                knowledgebaseItems.add(Arrays.asList(reader.nextLine().split(" /!\\\\ ")));
            }
            return null;
        }
    }
}
