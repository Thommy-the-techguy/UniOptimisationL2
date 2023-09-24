package gui;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import util.MyWeightedEdge;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Graph extends JFrame {
    private JGraphXAdapter<String, ?> jgxAdapter;
    private final Map<String, List<String>> vertexMap;
    private final Map<String, List<String>> weightsMap;
    private final DefaultUndirectedWeightedGraph<String, MyWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(MyWeightedEdge.class);

    public Graph(JFrame parentWindow, Map<String, List<String>> vertexMap, Map<String, List<String>> weightsMap) {
        super("Graph");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                parentWindow.setVisible(true);
            }
        });
        setVisible(true);
        this.vertexMap = vertexMap;
        this.weightsMap = weightsMap;
        buildGraph();
    }

    private void buildGraph() {
        addVertexes();
        addEdges();

        jgxAdapter = new JGraphXAdapter<>(graph);
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        mxGraphModel graphModel = (mxGraphModel) graphComponent.getGraph().getModel();
        Collection<Object> cells = graphModel.getCells().values();
        mxUtils.setCellStyles(graphComponent.getGraph().getModel(),
                cells.toArray(), mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        getContentPane().add(graphComponent);

        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
    }

    private void addVertexes() {
//        vertexMap.forEach((key, value) -> graph.addVertex(key));
        weightsMap.forEach((key, value) -> graph.addVertex(key));
    }

    private void addEdges() {
//        vertexMap.forEach((sourceVertex, value) -> value.forEach(targetVertex -> graph.addEdge(sourceVertex, targetVertex)));
        weightsMap.forEach((sourceVertex, value) -> value.forEach((item) -> {
            if (!item.split("-")[0].equals("null")) {
                graph.addEdge(sourceVertex, item.split("-")[0]);
                graph.setEdgeWeight(sourceVertex, item.split("-")[0], Double.parseDouble(item.split("-")[1]));
            }
        }));
    }

    private void setWeights() {

    }
}
