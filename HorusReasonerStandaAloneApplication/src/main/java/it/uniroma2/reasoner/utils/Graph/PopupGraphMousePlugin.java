package it.uniroma2.reasoner.utils.Graph;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
@SuppressWarnings({ "unused" })
public class PopupGraphMousePlugin extends AbstractPopupGraphMousePlugin implements GraphMousePlugin {

	private Graph<String,String> graph;
	private LayoutChooser layoutChooser;

	private JComboBox layoutCombo;
	private JPanel layoutPanel;
	
	public PopupGraphMousePlugin(Graph<String,String> graph, LayoutChooser layoutChooser, JComboBox layoutCombo, JPanel layoutPanel2) {
		this(MouseEvent.BUTTON3_MASK);
		this.graph = graph;
		this.layoutChooser = layoutChooser;
		this.layoutCombo = layoutCombo;
        this.layoutPanel = layoutPanel2;
    }
    public PopupGraphMousePlugin(int modifiers) {
        super(modifiers);
    }
    
    /**
     * If this event is over a Vertex, pop up a menu to
     * allow the user to increase/decrease the voltage
     * attribute of this Vertex
     * @param e
     */
    @SuppressWarnings({ "unchecked", "serial" })
    protected void handlePopup(MouseEvent e) {
        final VisualizationViewer<String,String> vv = 
            (VisualizationViewer<String,String>)e.getSource();
        Point2D p = e.getPoint();//vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
        
        GraphElementAccessor<String,String> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            final String v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            if(v != null) {
                JPopupMenu popup = new JPopupMenu();
                popup.add(new AbstractAction("Show History") {
                    public void actionPerformed(ActionEvent e) {
                    	Layout<String, String> layout = new FRLayout<String, String>(GraphUtils.BreabreadthFirstSearch(graph, v));
                    	layoutChooser.setGraph(GraphUtils.BreabreadthFirstSearch(graph, v));

                        vv.setGraphLayout(layout);
                    }
                });
               
                popup.show(vv, e.getX(), e.getY());
            } else {
                final String edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
                if(edge != null) {
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new AbstractAction(edge.toString()) {
                        public void actionPerformed(ActionEvent e) {
                            System.err.println("got "+edge);
                        }
                    });
                    popup.show(vv, e.getX(), e.getY());
                   
                }
            }
        }
    }

}
