package com.garrapeta.gameengine.test;

import com.garrapeta.gameengine.Box2DWorld;
import com.garrapeta.gameengine.SyncGameMessage;
import com.garrapeta.gameengine.GameView;
import com.garrapeta.gameengine.GameWorld;
import com.garrapeta.gameengine.actor.Box2DLoopActor;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class WorldSizeTestActivity extends Activity {

    private static final String TAG = GameWorld.TAG_GAME_ENGINE + ".Test";
    private WorldSizeBox2DWorld mWorld;
    private GameView mGameView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (L.sEnabled) Log.i(WorldSizeTestActivity.TAG, "onCreate " + this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.world_size_test);
        mGameView = (GameView) findViewById(R.id.game_surface);
        mWorld = new WorldSizeBox2DWorld(this, mGameView, this);
        mWorld.setDrawDebugInfo(true);
        
        
        {
            Button button = (Button)findViewById(R.id.btn1);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldSize(5, 10, 40);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn2);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldSize(5, 10, 20);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn3);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldSizeAndFitWidth(5, 10);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn4);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldSizeAndFitHeight(5, 10);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn5);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldWidth(5, 40);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn6);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldWidth(5, 20);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn7);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldHeight(10, 40);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn8);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldHeight(10, 20);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn9);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldWidth(5);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn10);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldHeight(10);
                }
            });
        }
        
        {
            Button button = (Button)findViewById(R.id.btn11);
            button.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    mWorld.mViewport.setWorldSizeGivenWorldUnitsPerInchX(1);
                }
            });
        }
    }

    /**
     * World
     */
    class WorldSizeBox2DWorld extends Box2DWorld {

        public WorldSizeBox2DWorld(Activity activity, GameView gameView, Context context) {
            super(gameView, context);
        }

        @Override
        public void onGameViewSizeChanged(int width, int height) {
            if (L.sEnabled) Log.i(WorldSizeTestActivity.TAG, "onGameViewSizeChanged " + this);
            mWorld.start();
        }
 
        @Override
        public void onGameWorldSizeChanged() {
            if (L.sEnabled) Log.i(WorldSizeTestActivity.TAG, "onGameWorldSizeChanged " + this);
            create();
        }

        private void create() {
            post(new SyncGameMessage() {

                @Override
                public void doInGameLoop(GameWorld world) {
                    removeAllActors();


                    RectF vb = mViewport.getWorldBoundaries();

                    float margin = mViewport.pixelsToWorldUnits(1);
                    float left   = vb.left + margin;
                    float bottom = vb.bottom + margin ;
                    float right  = vb.right - margin;
                    float top    = vb.top - margin;

                    // box
                    Box2DLoopActor<WorldSizeBox2DWorld> box = new Box2DLoopActor<WorldSizeBox2DWorld>(mWorld, 
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
                }
            });
        }
    }
}

