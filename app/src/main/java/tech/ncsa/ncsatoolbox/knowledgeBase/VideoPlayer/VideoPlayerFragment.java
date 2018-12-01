package tech.ncsa.ncsatoolbox.knowledgeBase.VideoPlayer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import tech.ncsa.ncsatoolbox.R;

public class VideoPlayerFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_knowledge_base, container, false); // Might as well reuse it, since it's empty
        return view;
    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String videoURL = (String) getArguments().get("video");

        VideoView videoView = new VideoView(getContext());
        videoView.setVideoURI(Uri.parse(videoURL));
        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);
        videoView.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
        ((LinearLayout) findViewById(R.id.knowledgebaseLayout)).addView(videoView);
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
