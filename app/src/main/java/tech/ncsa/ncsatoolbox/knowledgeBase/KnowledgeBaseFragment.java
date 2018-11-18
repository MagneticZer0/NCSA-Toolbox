package tech.ncsa.ncsatoolbox.knowledgeBase;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.ncsa.ncsatoolbox.R;

public class KnowledgeBaseFragment extends Fragment {

    View view;
    List<List<String>> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_knowledge_base, container, false);

        return view;
    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.knowledge_title));
        String test = "DNS and DHCP Configuration /!\\ https://ncsa.tech/Presentations/DNS%20and%20DHCP%20Configuration.png /!\\ https://ncsa.tech/Presentations/DNS%20and%20DHCP%20Configuration.pptx\n" +
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
        String[] lines = test.split("\n");
        for (String line : lines) {
            items.add(Arrays.asList(line.split(" /!\\\\ ")));
        }
        if (getArguments().get("type").equals("Presentations")) {
            loadPresentations();
        } else if (getArguments().get("type").equals("PDFs")) {
            loadPDFs();
        } else if (getArguments().get("type").equals("Videos")) {
            loadVideos();
        }
    }

    private void loadPresentations() {
        loadGeneric("pptx");
    }

    private void loadPDFs() {
        loadGeneric("pdf");
    }

    private void loadVideos() {
        for (final List<String> item : items) {
            if (!item.get(2).contains("mp4")) {
                continue;
            }

            TextView title = new TextView(getContext());
            title.setText(item.get(0));
            ((LinearLayout) getView().findViewById(R.id.knowledgebaseLayout)).addView(title);

            VideoView videoView = new VideoView(getContext());
            videoView.setVideoURI(Uri.parse(item.get(2)));
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(videoView);
            mediaController.setMediaPlayer(videoView);
            videoView.setMediaController(mediaController);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
            ((LinearLayout) getView().findViewById(R.id.knowledgebaseLayout)).addView(videoView);
        }
    }

    private void loadGeneric(String type) {
        for (final List<String> item : items) {
            ImageView imageview = new ImageView(getContext());

            if (!item.get(2).contains(type)) {
                continue;
            }

            TextView title = new TextView(getContext());
            title.setText(item.get(0));
            ((LinearLayout) getView().findViewById(R.id.knowledgebaseLayout)).addView(title);

            new DownloadImageTask(imageview).execute(item.get(1));
            ((LinearLayout) getView().findViewById(R.id.knowledgebaseLayout)).addView(imageview);

            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    googleDocsTab(item.get(2));
                }
            });
        }
    }

    /**
     * If Google Docs can open the specified file it will
     *
     * @param URL The file to try and open
     */
    private void googleDocsTab(String URL) {
        Uri webpage = Uri.parse("https://docs.google.com/gview?embedded=true&url=" + URL);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.enableUrlBarHiding();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), webpage);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}
