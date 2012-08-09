package net.garrapeta.gameengine;

import net.garrapeta.gameengine.actor.Box2DActor;

public class BodyUserData {
	
	// --------------------------------------------------------- Variables
	
	private Box2DActor actor;
	
	private IBodyDrawer bodyDrawer;
	
	// ------------------------------------------------------ Constructores
	
	public BodyUserData() {
	}

	//------------------------------------------------------------- M�todos

	/**
	 * @return the actor
	 */
	public Box2DActor getActor() {
		return actor;
	}
	
	/**
	 * @param actor the actor to set
	 */
	public void setActor(Box2DActor actor) {
		this.actor = actor;
	}

	/**
	 * @return the bodyDrawer
	 */
	public IBodyDrawer getBodyDrawer() {
		return bodyDrawer;
	}

	/**
	 * @param bodyDrawer the bodyDrawer to set
	 */
	public void setBodyDrawer(IBodyDrawer bodyDrawer) {
		this.bodyDrawer = bodyDrawer;
	}
}
