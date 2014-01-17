package it.uniroma2.reasoner.utils.Graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;

import javax.swing.JComboBox;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

final class LayoutChooser implements ActionListener
{
    private final JComboBox jcb;
    private final VisualizationViewer<?, ?> vv;
    private Graph<String,String> graph;

    LayoutChooser(JComboBox jcb, VisualizationViewer<?, ?> vv, Graph<String,String> graph)
    {
        super();
        this.jcb = jcb;
        this.vv = vv;
        this.graph = graph;
    }

    @SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent arg0)
    {
        

        Class<?> layoutC = (Class<?>) jcb.getSelectedItem();
        Class<?> lay = layoutC;
        try
        {
            Constructor<?> constructor = lay.getConstructor(new Class[] {Graph.class});
            Object o = constructor.newInstance(graph);
            @SuppressWarnings("rawtypes")
			Layout l = (Layout<?, ?>) o;
            //vv;
            vv.setGraphLayout(l);
            //vv.restart();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	public void setGraph(Graph<String, String> graph) {
		this.graph = graph;
	}

	
    
    
}
