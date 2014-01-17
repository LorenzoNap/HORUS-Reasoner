package it.uniroma2.reasoner.utils.Graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.samples.SimpleGraphDraw;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.MultiPickedState;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
// resolve ambiguity



/**
 * A demo applet that shows how to use JGraph to visualize JGraphT graphs.
 *
 * 
 * 
 */
public class GraphView extends JApplet
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static SimpleGraphDraw simpleGraphDraw;
	private Graph<String, String> graph;
	
	@SuppressWarnings({ "rawtypes", "serial" })
	public static void showGraph(final Graph<String, String> graph,String windowTitle){

		//Help string for Help button
		final String help= "<html>"+
		        "<h2>Layout Mode</h2>"+
				"<p>Select layout of graph<p>"+
				"<h2>Mouse Mode</h2>"+
				"<h3>Transforming Mode:</h3>"+
		        "<ul>"+
		        "<li>Mouse1+drag pans the graph"+
		        "<li>Mouse1+Shift+drag rotates the graph"+
		        "<li>Mouse1+CTRL(or Command)+drag shears the graph"+
		        "</ul>"+
				"<h3>Picking Mode:</h3>"+
		        "<ul>"+
		        "<li>Mouse1 on a Vertex selects the vertex"+
		        "<li>Mouse1 elsewhere unselects all Vertices"+
		        "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection"+
		        "<li>Mouse1+drag on a Vertex moves all selected Vertices"+
		        "<li>Mouse1+drag elsewhere selects Vertices in a region"+
		        "<li>Mouse1+Shift+drag adds selection of Vertices in a new region"+
		        "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it"+
		        "</ul>"+
				"<h2>Misc</h2>"+
				"<ul>"+
				"<li>Right click on vertex: show how the vertex has been generated"+
				"<li>Show entire graph button: shows the starting graph"+
				"</ul>"+
		        "</html>";

		//Create graph Draw object
		setSimpleGraphDraw(new SimpleGraphDraw());
		//Create new start Layout
		Layout<String, String> layout = new FRLayout<String, String>(graph);
		// sets the initial size of the space
		layout.setSize(new Dimension(500,500)); 
		
		final VisualizationViewer<String,String> visualizationViewer =  new VisualizationViewer<String,String>(layout);
		visualizationViewer.setPreferredSize(new Dimension(800,600));
		// Show vertex and  labels
		visualizationViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
		//Create support to pick vertex on graph
		ShapePickSupport<String, String> pickSupport = new ShapePickSupport<String, String>(visualizationViewer);
		visualizationViewer.setPickSupport(pickSupport);
		//Multi pick
		PickedState<String> pickedState = new MultiPickedState<String>();
		visualizationViewer.setPickedEdgeState(pickedState);

		//Swing Component
		
		JFrame frame = new JFrame(windowTitle);
		Container content = frame.getContentPane();
		JPanel panel = new JPanel(new GridLayout(1,0));
		panel.add(new GraphZoomScrollPane(visualizationViewer));
		content.add(panel);
		
	 
		
		// Combo Layout Panel 
		final JPanel layoutPanel = new JPanel();
		layoutPanel.setBorder(BorderFactory.createTitledBorder("LayoutMode"));	
		//Get list of layouts
		Class[] combosLayout = getCombos();
		//Create new swing combobox 
		final JComboBox layoutCombo = new JComboBox(combosLayout);
		//Set the name of layout on combobox
		layoutCombo.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				String valueString = value.toString();
				valueString = valueString.substring(valueString.lastIndexOf('.')+1);
				return super.getListCellRendererComponent(list, valueString, index, isSelected,
						cellHasFocus);
			}
		});
		
		//Create new LayoutPanel
		
		//Create new Event Listener for choose layout
		final LayoutChooser chooser = new LayoutChooser(layoutCombo, visualizationViewer, graph);
		//Add listenr to layout combo
		layoutCombo.addActionListener(chooser);
		
		//Create new mouse gesture listner
		DefaultModalGraphMouse graphMouse =  new DefaultModalGraphMouse();
		visualizationViewer.setGraphMouse(graphMouse);
		//Add a new gesture on mouse
		graphMouse.add(new PopupGraphMousePlugin(graph,chooser,layoutCombo,layoutPanel));
		
		//add combobx panel to layoutPanel
		layoutPanel.add(layoutCombo);

		//Zoom Controls
		final ScalingControl scaler = new CrossoverScalingControl(); 
		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(visualizationViewer, 1.1f, visualizationViewer.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(visualizationViewer, 1/1.1f, visualizationViewer.getCenter());
			}
		});
		
		//create Zoom Panel
		JPanel zoomPanel = new JPanel(new GridLayout(1,2));
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		//add button to zoom panel
		zoomPanel.add(plus);
		zoomPanel.add(minus);
		
		//create model Panel
		JPanel modePanel = new JPanel();
		modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		graphMouse.getModeComboBox().addItemListener(graphMouse.getModeListener());
		modePanel.add(graphMouse.getModeComboBox());

		//Information Panel
		JPanel informationPanel = new JPanel();
		informationPanel.setBorder(BorderFactory.createTitledBorder("Information Panel"));

		JLabel label1 = new JLabel();
		label1.setText("New triple discovered: "+((graph.getVertexCount()/3)+1));
		label1.setBounds(0, 0, 200, 50);
		informationPanel.add(label1);
		
		//Add button to show graph
		JButton originalGraph = new JButton("Show entire Graph");
		originalGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Layout<String, String> layout = new FRLayout<String, String>(graph);
				chooser.setGraph(graph);
				visualizationViewer.setGraphLayout(layout);
			}
		});
		//Add help button
		JButton helpButton = new JButton("Help");
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(visualizationViewer, help);
			}});
		//Add help button on information Panel
		informationPanel.add(helpButton);
		//New JPanelControls
		JPanel controls = new JPanel();
		controls.add(zoomPanel);
		controls.add(modePanel);
		controls.add(layoutPanel);
		controls.add(informationPanel);
		controls.add(originalGraph);
		content.add(controls, BorderLayout.SOUTH);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true); 
		
	}

	
	public void  init() 
	{
		final Graph<String, String> stringGraph = new DirectedSparseMultigraph<String, String>();
		stringGraph.addVertex("prova");
		stringGraph.addVertex("pippo");
		stringGraph.addEdge("1","pippo","prova", EdgeType.DIRECTED);
		
		//Help string for Help button
		final String help= "<html>"+
		        "<h2>Layout Mode</h2>"+
				"<p>Select layout of graph<p>"+
				"<h2>Mouse Mode</h2>"+
				"<h3>Transforming Mode:</h3>"+
		        "<ul>"+
		        "<li>Mouse1+drag pans the graph"+
		        "<li>Mouse1+Shift+drag rotates the graph"+
		        "<li>Mouse1+CTRL(or Command)+drag shears the graph"+
		        "</ul>"+
				"<h3>Picking Mode:</h3>"+
		        "<ul>"+
		        "<li>Mouse1 on a Vertex selects the vertex"+
		        "<li>Mouse1 elsewhere unselects all Vertices"+
		        "<li>Mouse1+Shift on a Vertex adds/removes Vertex selection"+
		        "<li>Mouse1+drag on a Vertex moves all selected Vertices"+
		        "<li>Mouse1+drag elsewhere selects Vertices in a region"+
		        "<li>Mouse1+Shift+drag adds selection of Vertices in a new region"+
		        "<li>Mouse1+CTRL on a Vertex selects the vertex and centers the display on it"+
		        "</ul>"+
				"<h2>Misc</h2>"+
				"<ul>"+
				"<li>Right click on vertex: show how the vertex has been generated"+
				"<li>Show entire graph button: shows the starting graph"+
				"</ul>"+
		        "</html>";

		//Create graph Draw object
		setSimpleGraphDraw(new SimpleGraphDraw());
		//Create new start Layout
		Layout<String, String> layout = new FRLayout<String, String>(stringGraph);
		// sets the initial size of the space
		layout.setSize(new Dimension(500,500)); 
		
		final VisualizationViewer<String,String> visualizationViewer =  new VisualizationViewer<String,String>(layout);
		visualizationViewer.setPreferredSize(new Dimension(800,600));
		// Show vertex and  labels
		visualizationViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
		//Create support to pick vertex on graph
		ShapePickSupport<String, String> pickSupport = new ShapePickSupport<String, String>(visualizationViewer);
		visualizationViewer.setPickSupport(pickSupport);
		//Multi pick
		PickedState<String> pickedState = new MultiPickedState<String>();
		visualizationViewer.setPickedEdgeState(pickedState);

		//Swing Component
		
//		JFrame frame = new JFrame("aaaa");
//		Container content = frame.getContentPane();
//		JPanel panel = new JPanel(new GridLayout(1,0));
//		panel.add(new GraphZoomScrollPane(visualizationViewer));
//		content.add(panel);
		
		
//		JRootPane rp = this.getRootPane();
//        rp.putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
//        getContentPane().setLayout(new BorderLayout());
//        getContentPane().setBackground(java.awt.Color.lightGray);
//      
//	
//		
//		JPanel panel = new JPanel(new GridLayout(1,0));
//		panel.add(new GraphZoomScrollPane(visualizationViewer));
		
		
	 
		
		// Combo Layout Panel 
		final JPanel layoutPanel = new JPanel();
		layoutPanel.setBorder(BorderFactory.createTitledBorder("LayoutMode"));	
		//Get list of layouts
		@SuppressWarnings("rawtypes")
		Class[] combosLayout = getCombos();
		//Create new swing combobox 
		final JComboBox layoutCombo = new JComboBox(combosLayout);
		//Set the name of layout on combobox
		layoutCombo.setRenderer(new DefaultListCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8570286539888088487L;

			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				String valueString = value.toString();
				valueString = valueString.substring(valueString.lastIndexOf('.')+1);
				return super.getListCellRendererComponent(list, valueString, index, isSelected,
						cellHasFocus);
			}
		});
		
		//Create new LayoutPanel
		
		//Create new Event Listener for choose layout
		final LayoutChooser chooser = new LayoutChooser(layoutCombo, visualizationViewer, stringGraph);
		//Add listenr to layout combo
		layoutCombo.addActionListener(chooser);
		
		//Create new mouse gesture listner
		@SuppressWarnings("rawtypes")
		DefaultModalGraphMouse graphMouse =  new DefaultModalGraphMouse();
		visualizationViewer.setGraphMouse(graphMouse);
		//Add a new gesture on mouse
		graphMouse.add(new PopupGraphMousePlugin(stringGraph,chooser,layoutCombo,layoutPanel));
		
		//add combobx panel to layoutPanel
		layoutPanel.add(layoutCombo);

		//Zoom Controls
		final ScalingControl scaler = new CrossoverScalingControl(); 
		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(visualizationViewer, 1.1f, visualizationViewer.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(visualizationViewer, 1/1.1f, visualizationViewer.getCenter());
			}
		});
		
		//create Zoom Panel
		JPanel zoomPanel = new JPanel(new GridLayout(1,2));
		zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		//add button to zoom panel
		zoomPanel.add(plus);
		zoomPanel.add(minus);
		
		//create model Panel
		JPanel modePanel = new JPanel();
		modePanel.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		graphMouse.getModeComboBox().addItemListener(graphMouse.getModeListener());
		modePanel.add(graphMouse.getModeComboBox());

		//Information Panel
		JPanel informationPanel = new JPanel();
		informationPanel.setBorder(BorderFactory.createTitledBorder("Information Panel"));

		JLabel label1 = new JLabel();
		label1.setText("New triple discovered: "+((stringGraph.getVertexCount()/3)+1));
		label1.setBounds(0, 0, 200, 50);
		informationPanel.add(label1);
		
		//Add button to show graph
		JButton originalGraph = new JButton("Show entire Graph");
		originalGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Layout<String, String> layout = new FRLayout<String, String>(graph);
				chooser.setGraph(stringGraph);
				visualizationViewer.setGraphLayout(layout);
			}
		});
		//Add help button
		JButton helpButton = new JButton("Help");
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(visualizationViewer, help);
			}});
		//Add help button on information Panel
		informationPanel.add(helpButton);
		//New JPanelControls
		JPanel controls = new JPanel();
		controls.add(zoomPanel);
		controls.add(modePanel);
		controls.add(layoutPanel);
		controls.add(informationPanel);
		controls.add(originalGraph);
		add(controls, BorderLayout.SOUTH);
		getContentPane().add(controls, BorderLayout.SOUTH);
		getContentPane().add(visualizationViewer);
		setVisible(true);

		
		 // FUNZIONANTEEEEEEE
//		 Graph<Integer, String> g = new SparseMultigraph<Integer, String>();
//		 g.addVertex((Integer)1);
//		 g.addVertex((Integer)2);
//		 g.addVertex((Integer)3); 
//		 g.addVertex((Integer)4); 
//		 g.addEdge("Edge-A", 1,3);
//		 g.addEdge("Edge-B", 2,3, EdgeType.DIRECTED);
//		 g.addEdge("Edge-C", 3, 2, EdgeType.DIRECTED);
//		 g.addEdge("Edge-P", 2,3); // A parallel edge
//		 g.addEdge("Edge-D", 2,4);
//		 g.addEdge("Edge-ggg", 1,4);
//		
//	 //We create our graph in here
//		 // The Layout<V, E> is parameterized by the vertex and edge types
//		 Layout<Integer, String> layout = new CircleLayout(g);
//		 layout.setSize(new Dimension(300,300)); // sets the initial size of the space
//		 // The BasicVisualizationServer<V,E> is parameterized by the edge types
//		 BasicVisualizationServer<Integer,String> vv = 
//		 new BasicVisualizationServer<Integer,String>(layout);
//		 vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
//		 
//	
//
//
//		getContentPane().add(vv);
//		setVisible(true);
		
	}
	
//	public static void main(String[] args) {
//	        // create a frome to hold the graph
////		       final JFrame frame = new JFrame();
////		       Container content = frame.getContentPane();
////		       content.add(new GraphView());
////	       frame.pack();
////		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////		       frame.setVisible(true);
//		GraphView g = new GraphView();
//		    }

	@SuppressWarnings("rawtypes")
	private static Class[] getCombos()
	{
		List<Class> layouts = new ArrayList<Class>();
		layouts.add(KKLayout.class);
		layouts.add(FRLayout.class);
		layouts.add(CircleLayout.class);
		layouts.add(SpringLayout.class);
		layouts.add(ISOMLayout.class);
		return layouts.toArray(new Class[0]);
	}



	public static SimpleGraphDraw getSimpleGraphDraw() {
		return simpleGraphDraw;
	}


	public static void setSimpleGraphDraw(SimpleGraphDraw simpleGraphDraw) {
		GraphView.simpleGraphDraw = simpleGraphDraw;
	}


	public Graph<String, String> getGraph() {
		return graph;
	}


	public void setGraph(Graph<String, String> graph) {
		this.graph = graph;
	}

	
}
