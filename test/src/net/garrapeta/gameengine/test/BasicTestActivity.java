package com.garrapeta.gameengine.test;

import com.garrapeta.gameengine.Box2DWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.actor.Box2DCircleActor;
import com.garrapeta.gameengine.actor.Box2DEdgeActor;
import com.garrapeta.gameengine.actor.Box2DPolygonActor;
import com.garrapeta.gameengine.actor.Box2DLoopActor;
import com.garrapeta.gameengine.actor.Box2DOpenChainActor;
import android.app.Activity;
import android.content.Context;
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

    private static final String TAG = GameWorld.TAG_GAME_ENGINE + ".Test";

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
        mWorld = new BasicTestBox2DWorld(this, mGameView, this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            PointF worldPos = mWorld.mViewport.screenToWorld(event.getX(), event.getY());
            mWorld.createCircleActor(worldPos);
        }
        return true;
    }
    
    /**
     * Basic Test World
     */
    class BasicTestBox2DWorld extends Box2DWorld {

        private static final float WORLD_HEIGHT = 14f;

        public BasicTestBox2DWorld(Activity activity, GameView gameView, Context context) {
            super(gameView, context);
            setGravityY(-SensorManager.GRAVITY_EARTH);
            setDrawDebugInfo(true);
            mViewport.setWorldHeight(WORLD_HEIGHT);
        }

        @Override
        public void onGameViewSizeChanged(int width, int height) {
            if (L.sEnabled) Log.i(BasicTestActivity.TAG, "onGameViewSizeChanged " + this);
            mWorld.start();
        }
 
        @Override
        public void onGameWorldSizeChanged() {
            if (L.sEnabled) Log.i(BasicTestActivity.TAG, "onGameWorldSizeChanged " + this);

            
            post(new SyncGameMessage() {

                @Override
                public void doInGameLoop(GameWorld world) {
                    RectF vb = mViewport.getWorldBoundaries();


                    float margin = 0.5f;
                    float left   = vb.left + margin;
                    float bottom = vb.bottom + margin ;
                    float right  = vb.right - margin;
                    float top    = vb.top - margin;

                    // box
                    Box2DLoopActor<BasicTestBox2DWorld> box = new Box2DLoopActor<BasicTestBox2DWorld>(mWorld, 
                            new PointF(0, 0),
                            new PointF[] {
                               new PointF(left,   bottom),
                               new PointF(left,   top),
                               new PointF(right,  top),
                               new PointF(right,  bottom),
                            },
                            false);
                    box.setInitted();
                    addActor(box);

                    // shared vertexes
                    PointF[] vertexes = new PointF[] {
                            new PointF(5, 0),
                            new PointF(3.5f, 3),
                            new PointF(2, 0),
                    };
                    
                    // loop Actor
                    Box2DLoopActor<BasicTestBox2DWorld> loop = new Box2DLoopActor<BasicTestBox2DWorld>(mWorld, new PointF(0, 4), vertexes, false);
                    loop.setInitted();
                    addActor(loop);
                    
                    // open chain actor
                    Box2DOpenChainActor<BasicTestBox2DWorld> openChain = new Box2DOpenChainActor<BasicTestBox2DWorld>(mWorld, new PointF(5, 4), vertexes, false);
                    openChain.setInitted();
                    addActor(openChain);

                    // edge actor
                    Box2DEdgeActor<BasicTestBox2DWorld> edge = new Box2DEdgeActor<BasicTestBox2DWorld>(mWorld, new PointF(10, 4), vertexes[0], vertexes[1], false);
                    edge.setInitted();
                    addActor(edge);

                    // polygon actor
                    Box2DPolygonActor<BasicTestBox2DWorld> polygon = new Box2DPolygonActor<BasicTestBox2DWorld>(mWorld, new PointF(15, 4), vertexes, false);
                    polygon.setInitted();
                    addActor(polygon);
                }});
        }
        

        private void createCircleActor(final PointF worldPos) {
            post(new SyncGameMessage() {
                @Override
                public void doInGameLoop(GameWorld world) {
                    float radius = 0.5f;
                    Box2DCircleActor<BasicTestBox2DWorld> actor = new Box2DCircleActor<BasicTestBox2DWorld>(mWorld, worldPos, radius, true);
                    actor.setInitted();
                    addActor(actor);
                }});

        }

    }


}

