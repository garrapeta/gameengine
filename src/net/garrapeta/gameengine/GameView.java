package net.garrapeta.gameengine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Vista sobre la que se renderiza el juego 
 * @author GaRRaPeTa
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
    // ----------------------------------------------------------------- Constants
    
    public static final String LOG_SRC = GameWorld.LOG_SRC_GAME_ENGINE + ".gameView";
    
	// ----------------------------------------------------------------- Variables
	/**
	 *  Holder del la SurfaceView
	 */
	private SurfaceHolder holder;
	
	private GameWorld world;
	
	
//	private boolean waitingForDrawingDispatched = false;
	
	// --------------------------------------------------------------- Constructor
	
	/**
	 * Constructor
	 * @param context
	 * @param attrs 
	 */
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.holder = getHolder();
		this.holder.addCallback(this);
		
		setFocusableInTouchMode(true);
		requestFocus();
	}

	// ----------------------------------------------------------- M�todos propios
	
	/**
	 * @param gameWorld
	 */
	void setGameWorld(GameWorld gameWorld) {
		this.world = gameWorld;
	}
	
	/**
	 * Pinta el mundo sobre la pantalla
	 */
	final void draw() {
		if (willNotDraw()) {
			if (holder != null) {
				Canvas canvas = holder.lockCanvas();
				if (canvas != null) {
					// pintado del frame
					world.drawFrame(canvas);
					holder.unlockCanvasAndPost(canvas);	
				}
			}
		} else {
			postInvalidate();
			
//			synchronized (world) {
//				waitingForDrawingDispatched = true;
//				
//				if (waitingForDrawingDispatched) {
//					try {
//						world.wait();
//					} catch (InterruptedException ie) {
//					}
//				}				
//			}
		}
		
	}
	
	/**
	 * Devuelve el modo de pintado
	 */
	public boolean isSyncDrawing() {
		return willNotDraw();
	}
	
	/**
	 * Establece el modo de pintado
	 * @param syncDrawing
	 */
	public void setSyncDrawing(final boolean syncDrawing) {
		((Activity)getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i(LOG_SRC, "setSyncDrawing (" + syncDrawing +")");
				setWillNotDraw(syncDrawing);
			}}
		);

	}
	
	// ------------------------------------------------------------ M�todos de View
	
	@Override
	public void onDraw(Canvas canvas) {
		if (world != null) {
			// Esta comprobaci�n de nulidad sirve para evitar NullPointerExceptions
			// en el visualizador de eclipse cuando se abre un Layout con una GameView
			world.drawFrame(canvas);
		}
		
//		synchronized (world) {
//			waitingForDrawingDispatched = false;
//			world.notify();
//		}	
	}

	
	// ------------------------------------------ M�todos de SurfaceHolder.Callback
	
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(LOG_SRC, "surfaceCreated (" + getWidth() + ", " + getHeight() +")");
    }

	@Override
	public final void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(LOG_SRC, "surfaceChanged (" + width + ", " + height +")");
		world.gameViewSizeChanged(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(LOG_SRC, "surfaceDestroyed");
	}

}
