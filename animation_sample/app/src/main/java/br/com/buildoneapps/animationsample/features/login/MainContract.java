package br.com.buildoneapps.animationsample.features.login;

import android.support.annotation.ColorRes;

import io.reactivex.Observable;

public interface MainContract {
    interface View {
        void setSecondText(String text);

        void hideIncreaseAndDecreaseButtons();

        void showIncreaseAndDecreaseButtons();

        void rotateClock(int percentage);

        void setActionButtonPause();

        void setActionButtonStart();

        void increaseRotation();

        void decreaseRotation();

        void initClock();

        Observable<Float> getScrollObservable();

        void setClockStroke(int stroke);

        void setClockColor(@ColorRes int color);
    }

    interface Presenter {
        void start();

        void increase();

        void decrease();

        void startCount();

        void stopCount();

    }
}
