package tech.ncsa.ncsatoolbox.knowledgeBase;

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
import java.util.List;

import tech.ncsa.ncsatoolbox.MainActivity;
import tech.ncsa.ncsatoolbox.R;

public class KnowledgeBaseFragment extends Fragment {

    View view;
    List<List<String>> items = MainActivity.getKnowledgebaseItems();

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
            ((LinearLayout) findViewById(R.id.knowledgebaseLayout)).addView(title);

            VideoView videoView = new VideoView(getContext());
            videoView.setVideoURI(Uri.parse(item.get(2)));
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(videoView);
            mediaController.setMediaPlayer(videoView);
            videoView.setMediaController(mediaController);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
            ((LinearLayout) findViewById(R.id.knowledgebaseLayout)).addView(videoView);
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
            ((LinearLayout) findViewById(R.id.knowledgebaseLayout)).addView(title);

            new DownloadImageTask(imageview).execute(item.get(1));
            ((LinearLayout)findViewById(R.id.knowledgebaseLayout)).addView(imageview);

            imageview.setOnClickListener(view -> googleDocsTab(item.get(2)));
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

    /**
     * Used to return a view from th activity
     * @param ID The ID of the view to find
     * @return THe view with that ID
     */
    private View findViewById(int ID) {
        return getView().findViewById(ID);
    }
}
