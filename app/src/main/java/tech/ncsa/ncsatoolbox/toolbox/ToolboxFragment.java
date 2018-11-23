package tech.ncsa.ncsatoolbox.toolbox;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import tech.ncsa.ncsatoolbox.R;
import tech.ncsa.ncsatoolbox.toolbox.subnetter.SubnetterFragment;

public class ToolboxFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_toolbox, container, false);
        return view;
    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        configureButtons(findViewById(R.id.subnetter), findViewById(R.id.subnetArea), findViewById(R.id.enterSubnetter), "subnetter");
        getActivity().setTitle(getString(R.string.app_name));
    }

    /**
     * Configures buttons by adding necessary action listeners and animations/arrows.
     *
     * @param openButton     The button used for opening a menu
     * @param infoArea       The ConstrainedLayout used to store data like descriptions/images
     * @param enterButton    The button used to enter the new activity
     * @param activitySwitch The activity switch method to use
     */
    private void configureButtons(View openButton, final View infoArea, View enterButton, String activitySwitch) {
        final Button opener = (Button) openButton;
        opener.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_button_up_arrow, 0);
        opener.setOnClickListener(view -> {
            if (infoArea.getVisibility() == View.GONE) {
                opener.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_button_down_arrow, 0);
                openAnimation(infoArea);
            } else {
                opener.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_button_up_arrow, 0);
                closeAnimation(infoArea);
            }
        });
        Button enterer = (Button) enterButton;
        assignEntererMethod(enterer, activitySwitch);
    }

    /**
     * Sets the method to run when a button is pressed
     * @param button The button
     * @param activitySwitch The method to run
     */
    private void assignEntererMethod(final Button button, String activitySwitch) {
        if (activitySwitch.equals("subnetter")) {
            button.setOnClickListener(this::switchToSubnetter);
        }
    }

    /**
     * The opening animation for a ConstrainedLayout
     *
     * @param ID The View to add the animation to
     */
    private void openAnimation(View ID) {
        ID.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        scaleAnimation.setDuration(150);
        ID.startAnimation(scaleAnimation);
    }

    /**
     * The closing animation for a ConstrainedLayout
     *
     * @param ID The View to add the animation to
     */
    private void closeAnimation(final View ID) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        scaleAnimation.setDuration(150);
        ID.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ID.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * Used to switch to the activities
     */
    public void switchToSubnetter(View view) {
        String viewName = getResources().getResourceName(view.getId()).split("/")[1];
        if (viewName.equals("enterSubnetter")) {
            FragmentManager fragMan = getFragmentManager();
            fragMan.beginTransaction().replace(R.id.content_frame, new SubnetterFragment()).commit();
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
