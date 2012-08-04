package net.garrapeta.gameengine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Vista sobre la que se renderiza el juego 
 * @author GaRRaPeTa
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
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

	// ----------------------------------------------------------- Métodos propios
	
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
				Log.i(GameWorld.LOG_SRC, "GameView.setSyncDrawing (" + syncDrawing +")");
				setWillNotDraw(syncDrawing);
			}}
		);

	}
	
	// ------------------------------------------------------------ Métodos de View
	
	@Override
	public void onDraw(Canvas canvas) {
		if (world != null) {
			// Esta comprobación de nulidad sirve para evitar NullPointerExceptions
			// en el visualizador de eclipse cuando se abre un Layout con una GameView
			world.drawFrame(canvas);
		}
		
//		synchronized (world) {
//			waitingForDrawingDispatched = false;
//			world.notify();
//		}	
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		world.onTouchEvent(event);
		return true;
	}
	
	// ------------------------------------------ Métodos de SurfaceHolder.Callback
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(GameWorld.LOG_SRC, "GameView.surfaceChanged (" + width + ", " + height +")");
		world.surfaceChanged(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(GameWorld.LOG_SRC, "GameView.surfaceCreated (" + getWidth() + ", " + getHeight() +")");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(GameWorld.LOG_SRC, "GameView.surfaceDestroyed");
	}

}
