package geogebra.main;

import java.util.Timer;

import geogebra.kernel.GeoElement;

/**
 * View wrapper that makes sure that only a maximum
 * number of requests are sent to a view in a certain time frame.
 */
public class ThreadedView implements View {

	private static int ADD = 0;
	private static int UPDATE = 1;
	private static int REPAINT = 2;
	
	private View view;
	private Timer timer;
	
	public ThreadedView(View view) {
		this.view = view;
		//timer = new Timer();
	}
	
	public void setDelay(int millis) {
		
	}

	public void add(GeoElement geo) {
		// TODO Auto-generated method stub
		view.add(geo);
	}

	public void clearView() {
		// TODO Auto-generated method stub
		view.clearView();
	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub
		view.remove(geo);
	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub
		view.rename(geo);
	}

	public void repaintView() {
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		view.reset();
	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub
		view.update(geo);
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		view.updateAuxiliaryObject(geo);
	}

}
