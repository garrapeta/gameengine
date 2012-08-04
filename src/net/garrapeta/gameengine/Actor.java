package net.garrapeta.gameengine;


import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Actor del juego
 * @author GaRRaPeTa
 */
public abstract class Actor {
	
	// --------------------------------------------------- Variables
	
	/** Mundo del actor */
	public GameWorld gameWorld;
	
	/**
	 *  Si el actor está presionado por el usuario
	 */
	private boolean pressed;
	
	/**
	 * Z-index del actor. Cuanto mayor, más en primer plano.
	 */
	private int zIndex = 0;
	
	// ----------------------------------------------- Constructores
	
	/**
	 * Constructor protegido
	 */
	protected Actor(GameWorld gameWorld) {
		this(gameWorld, 0);
	}
	
	/**
	 * Constructor protegido
	 */
	protected Actor(GameWorld gameWorld, int zIndex) {
		this.gameWorld = gameWorld;
		this.zIndex = zIndex;
	}
	
	
	// ------------------------------------------- Getters y Setters
	
	
	
	// ----------------------------------------------------- Métodos
	/**
	 * Pinta el actor
	 * @param canvas
	 */
	public abstract void draw(Canvas canvas);
	
	/**
	 * Realiza la lógica del frame
	 * @param gameTimeStep tiempo del frame anterior, en ms
	 */
	public abstract void doLogic(float gameTimeStep);
	
	/**
	 * Comprueba si el actor está bajo el dedo del usuario en pantalla
	 * @param event
	 * @return
	 */
	protected boolean checkPressed(MotionEvent event) {
		
		int actionMasked = event.getActionMasked();
		int pointerIndex = ((event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
		
		for (int i = 0; i < event.getPointerCount(); i++) {
			if (i == pointerIndex && (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_POINTER_UP)) {
				continue;
			}
			if (isPointInActor(gameWorld.viewport.pixelsToWorldUnits(event.getX(i)), 
							   gameWorld.viewport.pixelsToWorldUnits(event.getY(i)))) {
				
				return true;
			}
		}
		
		return false;
	}
	
	public abstract boolean isPointInActor(float worldX, float worldY);

	/**
	 * @return the pressed
	 */
	public final boolean isPressed() {
		return pressed;
	}

	/**
	 * @param pressed the pressed to set
	 */
	final void setPressed(boolean pressed) {
		boolean wasPressed = this.pressed;
		this.pressed = pressed;
		if (!wasPressed) {
			if (pressed) {
				onPressed();
			}
		} else {
			if (!pressed) {
				onReleased();
			}	
		}
	}
	
	/**
	 *  Callback invocado cuando el actor es presionado
	 */
	public void onPressed() {
	}
	
	/**
	 *  Callback invocado cuando el actor deja de ser presionado
	 */
	public void onReleased() {
	}
	
	/** Método ejecutado cuando el actor entra en el mundo */
	public void onAddedToWorld() {
	}
	
	/**
	 *  Método dispose, ejecutado cuando el actor se quita del mundo
	 */
	public void onRemovedFromWorld() {
	}

	/**
	 *  Devuelve zIndex
	 */
	public final int getZIndex() {
		return zIndex;
	}

	/**
	 *  Devuelve zIndex
	 */
	public final void setZIndex(int zIndex) {
		this.zIndex = zIndex;
		gameWorld.onZindexChanged(this);
	}




}
