package ca.benwu.fingerflinger.presenter;

import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import ca.benwu.fingerflinger.R;
import ca.benwu.fingerflinger.data.GameMode;

/**
 * Created by Ben Wu on 12/28/2015.
 */
public class GameModePresenter extends Presenter {

    protected int IMAGE_WIDTH = 500;
    protected static int IMAGE_HEIGHT = 220;

    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        sDefaultBackgroundColor = parent.getResources().getColor(R.color.colorPrimaryDark);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.colorPrimary);

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);

        return new ViewHolder(cardView);
    }

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int bgColor = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        //int infoColor = selected ? sDefaultBackgroundColor : sSelectedBackgroundColor;

        view.setBackgroundColor(bgColor);
        view.setInfoAreaBackgroundColor(sSelectedBackgroundColor);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        final ImageCardView cardView = (ImageCardView) viewHolder.view;
        GameMode mode = (GameMode) item;

        cardView.setTitleText(mode.getModeName());
        cardView.setMainImageDimensions(IMAGE_WIDTH, IMAGE_HEIGHT);
        cardView.setMainImage(viewHolder.view.getResources()
                .getDrawable(R.drawable.arrow_right_white));
        cardView.setMainImageScaleType(ImageView.ScaleType.FIT_CENTER);

        final Animation anim = mode.getAnimation();
        final Animation secondAnim = mode.getSecondAnimation();

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.getMainImageView().startAnimation(secondAnim);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        secondAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.getMainImageView().startAnimation(anim);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        cardView.getMainImageView().startAnimation(anim);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
