package br.com.buildoneapps.animationsample.features.login;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.transitionseverywhere.ArcMotion;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Recolor;
import com.transitionseverywhere.Rotate;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import br.com.buildoneapps.animationsample.ClockView;
import br.com.buildoneapps.animationsample.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.ivWhiteBackground)
    ImageView ivWhiteBackground;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_subtract)
    Button btnSubtract;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_second_count)
    TextView tvSecondCount;
    @BindView(R.id.root)
    RelativeLayout root;
    @BindView(R.id.clockView)
    ClockView clockView;
    @BindView(R.id.container_dot)
    LinearLayout containerDot;
    @BindView(R.id.ivDot)
    ImageView ivDot;
    @BindView(R.id.container_actions)
    LinearLayout containerActions;

    private MainContract.Presenter presenter;
    private GestureDetector detector;
    private PublishSubject<Float> scrollObservable = PublishSubject.create();
    private boolean isAnimating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        presenter = new MainPresenter(this);
        presenter.start();
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return detector.onTouchEvent(event);

        }
    };

    @OnClick({R.id.btn_add, R.id.btn_subtract, R.id.btn_action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                presenter.increase();
                break;
            case R.id.btn_subtract:
                presenter.decrease();
                break;
            case R.id.btn_action:
                presenter.startCount();
                break;
        }
    }

    @Override
    public void initClock() {
        detector = new GestureDetector(this, new MyGestureListener());
        clockView.setOnTouchListener(touchListener);
    }

    @Override
    public Observable<Float> getScrollObservable() {
        return scrollObservable;
    }

    @Override
    public void setClockStroke(int stroke) {
        //clockView.setStrokeWidth(stroke);
    }

    @Override
    public void setClockColor(int color) {
        clockView.setClockColor(color);
    }

    @Override
    public void setSecondText(String text) {
        tvSecondCount.setText(text);
    }

    @Override
    public void hideIncreaseAndDecreaseButtons() {

        TransitionSet set = new TransitionSet();
        Fade fade = new Fade(Fade.OUT);
        fade.addTarget(btnAdd);
        set.addTransition(fade);

        Fade fade2 = new Fade(Fade.OUT);
        fade2.setStartDelay(100);
        fade2.addTarget(btnSubtract);

        Slide slide = new Slide(Gravity.BOTTOM);
        slide.addTarget(btnAdd);
        set.addTransition(slide);

        Slide slide2 = new Slide(Gravity.BOTTOM);
        slide2.setStartDelay(100);
        slide2.addTarget(btnSubtract);
        set.addTransition(slide2);

        set.addTransition(new ChangeBounds().setStartDelay(500));
        TransitionManager.beginDelayedTransition(containerActions, set);
        btnAdd.setVisibility(View.GONE);
        btnSubtract.setVisibility(View.GONE);
    }

    @Override
    public void showIncreaseAndDecreaseButtons() {

        TransitionSet set = new TransitionSet();
        Fade fade = new Fade(Fade.IN);
        fade.addTarget(btnAdd);
        fade.addTarget(btnSubtract);
        set.addTransition(new ChangeBounds());
        set.addTransition(fade.setStartDelay(250));

        TransitionManager.beginDelayedTransition(containerActions, set);
        btnAdd.setVisibility(View.VISIBLE);
        btnSubtract.setVisibility(View.VISIBLE);
    }

    @Override
    public void rotateClock(int percentage) {
        Transition rotate = new Rotate().setDuration(800);
        rotate.addTarget(clockView);

        ArcMotion pathMotion = new ArcMotion();
        pathMotion.setMaximumAngle(20);
        Transition transition = new ChangeBounds().setPathMotion(pathMotion)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        transition.addTarget(ivDot);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                TransitionManager.beginDelayedTransition(containerDot,
                        new ChangeBounds().setPathMotion(new ArcMotion())
                                .setDuration(130)
                                .setInterpolator(new AccelerateDecelerateInterpolator()));
                containerDot.setGravity(Gravity.BOTTOM | Gravity.END);
            }

            @Override
            public void onTransitionCancel(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionPause(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionResume(@NonNull Transition transition) {

            }
        });
        TransitionSet set = new TransitionSet();
        set.addTransition(rotate);
        set.addTransition(transition);
        TransitionManager.beginDelayedTransition(root,
                set);
        containerDot.setGravity(Gravity.START | Gravity.TOP);
        clockView.setRotation(clockView.getRotation() - 30);

    }

    @Override
    public void setActionButtonPause() {
        btnAction.setText(R.string.btn_pause);
        TransitionManager.beginDelayedTransition(root, new Recolor());
        btnAction.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_round_corner_dark));
    }

    @Override
    public void setActionButtonStart() {
        btnAction.setText(R.string.btn_start);
        TransitionManager.beginDelayedTransition(root, new Recolor());
        btnAction.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_round_corner_light));
    }

    @Override
    public void increaseRotation() {
        if (!isAnimating) {
            isAnimating = true;
            TransitionManager.beginDelayedTransition(root, new Rotate().addListener(transitionListener));
            clockView.setRotation(clockView.getRotation() + 30);
        }
    }

    @Override
    public void decreaseRotation() {
        if (!isAnimating) {
            isAnimating = true;
            TransitionManager.beginDelayedTransition(root, new Rotate().addListener(transitionListener));
            clockView.setRotation(clockView.getRotation() - 30);
        }
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG", "onDown: ");

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: " + distanceY);
            scrollObservable.onNext(distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d("TAG", "onFling: ");
            return true;
        }
    }


    Transition.TransitionListener transitionListener = new Transition.TransitionListener() {
        @Override
        public void onTransitionStart(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            isAnimating = false;
        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {

        }
    };

}
