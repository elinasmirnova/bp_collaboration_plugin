/**
 * author: Marcel Genzmehr
 * 29.11.2011
 */
package org.freeplane.core.ui.components.resizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.LogUtils;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.ui.IMapViewManager;

/**
 *
 */
public class OneTouchCollapseResizer extends JResizer {
	private static final long serialVersionUID = 3836146387249880446L;
	public static final String COLLAPSED = OneTouchCollapseResizer.class.getPackage().getName()+".collapsed";
	private static final String ALREADY_IN_PAINT = OneTouchCollapseResizer.class.getPackage().getName()+".ALREADY_PAINTING";

	protected boolean expanded = true;
	private JPanel hotspot;
	private final int INSET = 2;
	private final Direction direction;
	private Integer resizeComponentIndex;

	private final Set<ComponentCollapseListener> collapseListener = new LinkedHashSet<ComponentCollapseListener>();
	private Dimension lastPreferredSize = null;



	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	/**
	 * @param d
	 */
	public OneTouchCollapseResizer(final Direction d) {
		super(d);
		direction = d;
		this.setDividerSize((int)(UITools.FONT_SCALE_FACTOR * 10 + 0.5));

		MouseListener listener = new MouseListener() {
			private void resetCursor() {
				if(d.equals(Direction.RIGHT)){
					setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				}
				else if(d.equals(Direction.LEFT)){
					setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				}
				else if(d.equals(Direction.UP)){
					setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				}
				else /*Direction.DOWN*/ {
					setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if(e.getComponent() == getHotSpot()) {
					resetCursor();
				}
				if(isExpanded()) {
					resetCursor();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if(e.getComponent() == getHotSpot()) {
					getHotSpot().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				if(!isExpanded() || sliderLock) {
					e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if((e.getComponent() == getHotSpot()) || sliderLock) {

					if (isExpanded()) {
						getHotSpot().setEnabled(true);
						setExpanded(false);
					}
					else {
						setExpanded(true);
					}
				}
				else {
					if (!isExpanded()) {
						setExpanded(true);
					}
				}
			}
		};
		getHotSpot().addMouseListener(listener);
		addMouseListener(listener);

		add(getHotSpot());
	}

	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/

	public boolean isExpanded() {
		return this.expanded;
	}

	public void setDividerSize(int size) {
		final int w;
		final int h;
		if(direction == Direction.RIGHT){
			w = size;
			h = 0;
		}
		else if(direction == Direction.LEFT){
			h = 0;
			w = size;
		}
		else if(direction == Direction.UP){
			h = size;
			w = 0;
		}
		else /*Direction.DOWN*/ {
			h = size;
			w = 0;
		}
		setPreferredSize(new Dimension(w, h));
	}

	public int getDividerSize() {
		if(direction == Direction.RIGHT || direction == Direction.LEFT){
			return getPreferredSize().width;
		}
		else /*Direction.DOWN || Direction.UP*/ {
			return getPreferredSize().height;
		}
	}

	public void setExpanded(boolean expanded) {
		if(this.expanded != expanded) {
			this.expanded = expanded;
			try {
				Component resizedComponent = getResizedComponent();
				if(resizedComponent instanceof JComponent) {
					((JComponent) resizedComponent).putClientProperty(COLLAPSED, (expanded ? null : "true"));
				}
				if(expanded) {
					resizedComponent.setPreferredSize(lastPreferredSize);
				}
				else {
					lastPreferredSize = resizedComponent.isPreferredSizeSet() 
					        && direction.getPreferredSize(resizedComponent) > getDividerSize() ?  resizedComponent.getPreferredSize() : null;
					resizedComponent.setPreferredSize(new Dimension(0,0));
				}
				IMapViewManager mapViewManager = Controller.getCurrentController().getMapViewManager();
				mapViewManager.moveFocusFromDescendantToSelection(resizedComponent);
				resizedComponent.setVisible(expanded);

				fireCollapseStateChanged(resizedComponent, expanded);

				recalibrate();
			}
			catch (Exception e) {
				LogUtils.warn("Exception in org.freeplane.core.ui.components.OneTouchCollapseResizer.setExpanded(enabled): "+e);
			}
		}

	}

	private Component getResizedComponent() {
		final JComponent parent = (JComponent) getParent();
		if(parent != null && resizeComponentIndex == null) {
			resizeComponentIndex = getIndex();
		}
		return parent.getComponent(resizeComponentIndex);
	}

	@Override
    public void paint(Graphics g) {
		if(getClientProperty(ALREADY_IN_PAINT) != null) {
			return;
		}
		putClientProperty(ALREADY_IN_PAINT, "true");
		super.paint(g);
		if((direction == Direction.RIGHT || direction == Direction.LEFT)) {
			int center_y = getHeight()/2;
			int divSize = getDividerSize();
			getHotSpot().setBounds(0, center_y-divSize, divSize, 2 * divSize);
		}
		else {
			int center_x = getWidth()/2;
			int divSize = getDividerSize();
			getHotSpot().setBounds(center_x-divSize, 0, 2 * divSize, divSize);
		}
		Dimension size = getResizedComponent().getPreferredSize();
		if((direction == Direction.RIGHT || direction == Direction.LEFT) && size.width <= getDividerSize()) {
			setExpanded(false);

		}
		else if((direction == Direction.UP || direction == Direction.DOWN) && size.height <= getDividerSize()){
			setExpanded(false);
		}
		else {
			setExpanded(true);
		}
		if(getResizedComponent() instanceof JComponent) {
			((JComponent) getResizedComponent()).putClientProperty(COLLAPSED, (isExpanded() ? null : "true"));
		}
		getHotSpot().paint(g.create(getHotSpot().getLocation().x, getHotSpot().getLocation().y, getHotSpot().getWidth(), getHotSpot().getHeight()));
		putClientProperty(ALREADY_IN_PAINT, null);
	}

	private Component getHotSpot() {
		if(hotspot == null) {
			hotspot = new JPanel() {
				private static final long serialVersionUID = -5321517835206976034L;

				@Override
                public void paint(Graphics g) {
					drawControlArrow(g);
				}

				@Override
                public void updateUI() {
					try {
						super.updateUI();
					}
					catch (Exception e) {
					}
				}
			};
			hotspot.setBackground(Color.BLUE);
		}
		return hotspot;
	}


	private void drawControlArrow(Graphics g) {
		Dimension size = g.getClipBounds().getSize();
		int half_length = (size.height-(INSET*6))/2;
		int center_y = size.height / 2;

		int half_width = (size.width-(INSET*6))/2;
		int center_x = size.width / 2;

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		if(expanded && direction == Direction.RIGHT || ! expanded && direction == Direction.LEFT) {
			arrowRight(g, half_length, center_y);
		}
		else if(expanded && direction == Direction.LEFT || ! expanded && direction == Direction.RIGHT) {
			arrowLeft(g, half_length, center_y);
		}
		else if(expanded && direction == Direction.DOWN || ! expanded && direction == Direction.UP) {
			arrowDown(g, half_width, center_x);
		}
		else if(expanded && direction == Direction.UP || ! expanded && direction == Direction.DOWN) {
			arrowUp(g, half_width, center_x);
		}
	}


	/**
	 * @param g
	 * @param half_length
	 * @param center_y
	 */
	private void arrowLeft(Graphics g, int half_length, int center_y) {
		int[] x = new int[]{INSET, getSize().width - INSET, getSize().width - INSET};
		int[] y = new int[]{center_y, center_y-half_length, center_y + half_length};
		g.setColor(Color.DARK_GRAY);
		g.fillPolygon(x, y, 3);
		g.setColor(Color.DARK_GRAY);
		g.drawLine(INSET, center_y, getSize().width - INSET, center_y - half_length);
		g.setColor(Color.GRAY);
		g.drawLine( getSize().width - INSET, center_y + half_length, INSET, center_y);
		g.setColor(Color.GRAY);
		g.drawLine( getSize().width - INSET, center_y - half_length, getSize().width - INSET, center_y + half_length);
	}

	/**
	 * @param g
	 * @param half_length
	 * @param center_y
	 */
	private void arrowRight(Graphics g, int half_length, int center_y) {
		int[] x = new int[]{INSET, INSET, getSize().width - INSET};
		int[] y = new int[]{center_y+half_length, center_y-half_length, center_y};

		g.setColor( Color.DARK_GRAY);
		g.fillPolygon(x,y,3);
		g.setColor( Color.DARK_GRAY);
		g.drawLine( INSET, center_y + half_length, INSET, center_y - half_length);
		g.setColor(Color.GRAY);
		g.drawLine( INSET, center_y - half_length, getSize().width - INSET, center_y);
		g.setColor( Color.LIGHT_GRAY);
		g.drawLine( getSize().width - INSET, center_y, INSET, center_y + half_length);
	}

	private void arrowUp(Graphics g, int half_length, int center_x) {
		int[] y = new int[]{INSET, getSize().height - INSET, getSize().height - INSET};
		int[] x = new int[]{center_x, center_x-half_length, center_x + half_length};

		g.setColor(Color.DARK_GRAY);
		g.fillPolygon(x, y, 3);

		g.setColor(Color.GRAY);
		g.drawLine(center_x + half_length, getSize().height - INSET, center_x, INSET);
		g.setColor(Color.DARK_GRAY);
		g.drawLine(center_x, INSET, center_x - half_length, getSize().height - INSET);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(center_x - half_length, getSize().height - INSET, center_x + half_length, getSize().height - INSET);

	}

	private void arrowDown(Graphics g, int half_length, int center_x) {
		int[] y = new int[]{INSET, INSET, getSize().height - INSET};
		int[] x = new int[]{center_x+half_length, center_x-half_length, center_x};

		g.setColor( Color.DARK_GRAY);
		g.fillPolygon(x,y,3);

		g.setColor(Color.GRAY);
		g.drawLine( center_x - half_length, INSET, center_x, getSize().height- INSET);
		g.setColor( Color.DARK_GRAY);
		g.drawLine( center_x + half_length, INSET, center_x - half_length, INSET);
		g.setColor( Color.LIGHT_GRAY);
		g.drawLine(center_x,  getSize().height - INSET, center_x + half_length, INSET);
	}

	private int getIndex() {
		final Container parent = getParent();
		for(int i = 0; i < parent.getComponentCount(); i++ ){
			if(OneTouchCollapseResizer.this.equals(parent.getComponent(i))){
				if(direction == Direction.RIGHT){
					return i + 1;
				}
				else if(direction == Direction.LEFT){
					return i - 1;
				}
				else if(direction == Direction.UP){
					return i - 1;
				}
				else if(direction == Direction.DOWN){
					return i + 1;
				}
			}
		}
		return -1;
    }

	public void addCollapseListener(ComponentCollapseListener listener) {
		if(listener == null) return;

		synchronized (collapseListener) {
			collapseListener.add(listener);
		}

	}

	public void removeCollapseListener(ComponentCollapseListener listener) {
		if(listener == null) return;

		synchronized (collapseListener) {
			collapseListener.remove(listener);
		}
	}

	protected void fireCollapseStateChanged(Component resizedComponent, boolean expanded) {
		ResizeEvent event = new ResizeEvent(this, resizedComponent);
		synchronized (this.collapseListener) {
			for(ComponentCollapseListener listener : collapseListener) {
				try {
					if(expanded) {
						listener.componentExpanded(event);
					}
					else {
						listener.componentCollapsed(event);
					}
				}
				catch (Exception e) {
					LogUtils.severe(e);
				}
			}
		}

	}

	public static OneTouchCollapseResizer findResizerFor(Component component) {
				if(component instanceof Container) {
					Component[] children = ((Container) component).getComponents();
					for (Component child : children) {
						if(child instanceof OneTouchCollapseResizer) {
							return (OneTouchCollapseResizer) child;
						}
					}
				}
				if(component == null)
					return null;
				Component parent = component.getParent();
				return findResizerFor(parent);
	}

	public interface ComponentCollapseListener {
		public void componentCollapsed(ResizeEvent event);
		public void componentExpanded(ResizeEvent event);
	}

	public void recalibrate() {
		if(getClientProperty(ALREADY_IN_PAINT) == null) {
			final JComponent parent = (JComponent) getParent();
			if(parent != null) {
				parent.revalidate();
				parent.repaint();
			}
		}
	}
}
