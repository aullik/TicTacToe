package de.htwg.tictactoe.view.gui;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * 
 * @author Youssef Idelhoussain
 *
 */
class Cam extends Group {
	
	
    private Translate t;
    private Translate p;
    private Translate ip;
    private Rotate rx;
    private Rotate ry; 
    private Rotate rz;
    private Scale s;
    public Cam() { 
    	super(); 
    	init();
    	getTransforms().addAll(t, p, rx, rz, ry, s, ip); 
	}
    
    public void init(){
    	t  = new Translate();
        p  = new Translate();
        ip = new Translate();
        rx = new Rotate();
        rx.setAxis(Rotate.X_AXIS);
        ry = new Rotate();
        ry.setAxis(Rotate.Y_AXIS); 
        rz = new Rotate();
        rz.setAxis(Rotate.Z_AXIS);
        s = new Scale();
    }

	public Translate getT() {
		return t;
	}

	public Translate getP() {
		return p;
	}

	public Translate getIp() {
		return ip;
	}

	public Rotate getRx() {
		return rx;
	}

	public Rotate getRy() {
		return ry;
	}

	public Rotate getRz() {
		return rz;
	}

	public Scale getS() {
		return s;
	}
}
