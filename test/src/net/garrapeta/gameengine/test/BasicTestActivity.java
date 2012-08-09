package net.garrapeta.gameengine.test;

import net.garrapeta.gameengine.Box2DWorld;
import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.GameWorld;
import net.garrapeta.gameengine.actor.Box2DCircleActor;
import net.garrapeta.gameengine.actor.Box2DEdgeActor;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

public class BasicTestActivity extends Activity implements OnTouchListener {

    private static final String LOG_SRC = GameWorld.LOG_SRC_GAME_ENGINE + ".Test";

    private BasicTestBox2DWorld mWorld;
    private GameView mGameView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.basic_test);
        mGameView = (GameView) findViewById(R.id.game_surface);
        mGameView.setOnTouchListener(this);
        mWorld = new BasicTestBox2DWorld(this, mGameView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            PointF worldPos = mWorld.viewport.screenToWorld(event.getX(), event.getY());
            mWorld.createCircleActor(worldPos);
        }
        return true;
    }
    
    /**
     * Basic Test World
     */
    class BasicTestBox2DWorld extends Box2DWorld {

        private static final float WORLD_HEIGHT = 14f;

        public BasicTestBox2DWorld(Activity activity, GameView gameView) {
            super(activity, gameView);
            setGravityY(-SensorManager.GRAVITY_EARTH);
            setDrawDebugInfo(true);
            viewport.setWorldHeight(WORLD_HEIGHT);
        }

        @Override
        public void onGameViewSizeChanged(int width, int height) {
            Log.i(BasicTestActivity.LOG_SRC, "onGameViewSizeChanged " + this);
            mWorld.startLooping();
            mWorld.play();
        }
 
        @Override
        public void onGameWorldSizeChanged() {
            Log.i(BasicTestActivity.LOG_SRC, "onGameWorldSizeChanged " + this);

            RectF vb = viewport.getWorldBoundaries();


            float margin = 0.5f;
            float left   = vb.left + margin;
            float bottom = vb.bottom + margin ;
            float right  = vb.right - margin;
            float top    = vb.top - margin;

            // upper edge
            addActor(new Box2DEdgeActor(this, 
                                         new PointF(0, 0),
                                         new PointF(left,   top),  
                                         new PointF(right,  top),
                                         false));
            
            // bottom edge
            addActor(new Box2DEdgeActor(this,
                                         new PointF(0, 0),  
                                         new PointF(left,   bottom),  
                                         new PointF(right,  bottom),
                                         false));
            
            // left edge
            addActor(new Box2DEdgeActor(this,
                                         new PointF(0,0 ),
                                         new PointF(left,   bottom),  
                                         new PointF(left,   top),
                                         false));            

            // right edge
            addActor(new Box2DEdgeActor(this,
                                        new PointF(0, 0),
                                        new PointF(right,   bottom),  
                                        new PointF(right,   top ),
                                        false));

        }
        

        private void createCircleActor(PointF worldPos) {
            float radius = 0.5f;
            Box2DCircleActor actor = new Box2DCircleActor(this, worldPos, radius, true);
            addActor(actor);
        }

    }


}

