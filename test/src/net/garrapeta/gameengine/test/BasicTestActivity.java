package net.garrapeta.gameengine.test;

import net.garrapeta.gameengine.GameView;
import net.garrapeta.gameengine.box2d.Box2DWorld;
import net.garrapeta.gameengine.box2d.actor.Box2DCircleActor;
import net.garrapeta.gameengine.box2d.actor.Box2DEdgeActor;
import android.app.Activity;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class BasicTestActivity extends Activity {

    private static final String LOG_SRC = "GameEngineTest";
    private static final int FPS = 60;

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
        mWorld = new BasicTestBox2DWorld(this, mGameView);
        mWorld.setFPS(FPS);
        mWorld.setDrawDebugInfo(true);
    }

    /**
     * Basic Test World
     */
    class BasicTestBox2DWorld extends Box2DWorld {

        public BasicTestBox2DWorld(Activity activity, GameView gameView) {
            super(activity, gameView);
        }

        @Override
        public void processFrame(float gameTimeStep, float physicsTimeStep) {

        }

        @Override
        public synchronized void surfaceChanged(float width, float height) {
            super.surfaceChanged(width, height);
            
            Log.i(BasicTestActivity.LOG_SRC, "surfaceChanged " + this);

            mWorld.create();
            mWorld.startLooping();
            mWorld.play();
        }
        
        

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                PointF worldPos = viewport.screenToWorld(event.getX(), event.getY());
                createCircleActor(worldPos);
            }
        }

        private void create() {

            Log.i(BasicTestActivity.LOG_SRC,"create " + this);
            this.setTimeFactor(1);

            this.viewport.setViewportHeightInWorldUnits(14);
            RectF vb = viewport.getBoundaries();
            this.setGravityY(-9.8f);

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
            float radius = 0.3f;
            Box2DCircleActor actor = new Box2DCircleActor(this, worldPos, radius, true);
            addActor(actor);
        }

    }
}

