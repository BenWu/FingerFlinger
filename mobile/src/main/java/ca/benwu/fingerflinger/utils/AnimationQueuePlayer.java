package ca.benwu.fingerflinger.utils;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Ben Wu on 12/19/2015.
 */
public class AnimationQueuePlayer {

    private View mView;

    private AnimationQueue mAnimationQueue = new AnimationQueue();

    private Animation mFirstAnim;

    public AnimationQueuePlayer(final View view, Animation... animations) {
        mView = view;

        mFirstAnim = animations[0];

        for(Animation animation : animations) {
            mAnimationQueue.addAnimation(animation);
        }

        Animation current = mFirstAnim;

        while(current != null) {
            final Animation next = mAnimationQueue.dequeue();
            Logutils.v("AnimQueue", "next == null: " + (next == null));
            current.setAnimationListener(new AnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (next != null) {
                        view.startAnimation(next);
                    } else {
                        view.startAnimation(mFirstAnim);
                    }
                }
            });
            current = next;
        }
    }

    public void playQueue() {
        mView.startAnimation(mFirstAnim);
    }

    private class AnimationQueue {
        private AnimationNode mFront;
        private AnimationNode mEnd;
        private int mSize;

        public void addAnimation(Animation animation) {
            AnimationNode node = new AnimationNode(animation);
            if(mFront == null) {
                mFront = node;
                mEnd = mFront;
            } else {
                mEnd = mEnd.setNext(node);
            }
            mSize++;
        }

        public Animation dequeue() {
            if (mFront == null) {
                return null;
            }
            Animation animation = mFront.getAnimation();
            mFront = mFront.mNext;
            mSize--;
            return animation;
        }

        public int size() {
            return mSize;
        }
    }

    private class AnimationNode {
        private Animation mAnimation;
        private AnimationNode mNext;

        public AnimationNode(Animation animation) {
            mAnimation = animation;
            mNext = null;
        }

        public Animation getAnimation() {
            return mAnimation;
        }

        public AnimationNode setNext(Animation next) {
            mNext = new AnimationNode(next);
            return mNext;
        }

        public AnimationNode setNext(AnimationNode next) {
            mNext = next;
            return mNext;
        }
    }

    private abstract class AnimationEndListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

}
