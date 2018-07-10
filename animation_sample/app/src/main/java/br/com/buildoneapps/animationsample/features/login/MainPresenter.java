package br.com.buildoneapps.animationsample.features.login;

import java.util.concurrent.TimeUnit;

import br.com.buildoneapps.animationsample.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {
    private final MainContract.View view;
    private int startTimeCount;
    private Long currentTimeCount;
    private boolean isRunning;
    private CompositeDisposable subscriptions;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {
        view.initClock();
        reset();
    }

    private void reset() {
        startTimeCount = 1;
        view.setSecondText(String.valueOf(startTimeCount));
        view.setClockStroke(2);
        view.setClockColor(R.color.colorPrimary);
        subscriptions = new CompositeDisposable();
        subscriptions.add(view.getScrollObservable().
                throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Float>() {
                    @Override
                    public void onNext(Float distanceY) {
                        if (distanceY > 0) {
                            increase();
                        } else {
                            decrease();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        stopCount();
                    }
                }));
    }

    @Override
    public void increase() {
        startTimeCount++;
        view.setSecondText(String.valueOf(startTimeCount));
        view.increaseRotation();
    }

    @Override
    public void decrease() {
        if (startTimeCount - 1 <= 0) {
            stopCount();
            return;
        }
        startTimeCount--;
        view.setSecondText(String.valueOf(startTimeCount));
        view.decreaseRotation();
    }

    @Override
    public void startCount() {
        if (isRunning) {
            stopCount();
            return;
        }
        isRunning = true;

        view.setClockStroke(3);
        view.setClockColor(android.R.color.white);

        view.hideIncreaseAndDecreaseButtons();
        view.setActionButtonPause();
        currentTimeCount = (long) startTimeCount;
        subscriptions.add(Observable.timer(1, TimeUnit.SECONDS)
                .repeat(startTimeCount)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Long>() {
                    @Override
                    public void onNext(Long stepTime) {
                        if (isRunning) {
                            currentTimeCount--;
                            view.setSecondText(String.valueOf(currentTimeCount));

                            int percentage = (int) ((((float) startTimeCount - (float) currentTimeCount) / (float) startTimeCount) * 100);
                            view.rotateClock(percentage);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    @Override
    public void stopCount() {
        isRunning = false;
        view.rotateClock(0);
        view.showIncreaseAndDecreaseButtons();
        view.setActionButtonStart();
        subscriptions.dispose();
        reset();
    }
}
