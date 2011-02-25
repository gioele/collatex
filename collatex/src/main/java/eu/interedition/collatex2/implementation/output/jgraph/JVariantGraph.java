package eu.interedition.collatex2.implementation.output.jgraph;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import eu.interedition.collatex2.interfaces.nonpublic.joined_graph.IJVariantGraph;
import eu.interedition.collatex2.interfaces.nonpublic.joined_graph.IJVariantGraphEdge;
import eu.interedition.collatex2.interfaces.nonpublic.joined_graph.IJVariantGraphVertex;

public class JVariantGraph extends DirectedAcyclicGraph<IJVariantGraphVertex, IJVariantGraphEdge> implements IJVariantGraph {
  private static final long serialVersionUID = 1L;
  private IJVariantGraphVertex startVertex;
  private IJVariantGraphVertex endVertex;

  public JVariantGraph() {
    super(IJVariantGraphEdge.class);
    //    startVertex = new JVariantGraphVertex("#");
    //    addVertex(startVertex);
    //    endVertex = new JVariantGraphVertex("#");
    //    addVertex(getEndVertex());
  }

  public static IJVariantGraph create() {
    return new JVariantGraph();
  }

  @Override
  public IJVariantGraphVertex getStartVertex() {
    return startVertex;
  }

  @Override
  public IJVariantGraphVertex getEndVertex() {
    return endVertex;
  }

  @Override
  public void setStartVertex(IJVariantGraphVertex startVertex) {
    if (!containsVertex(startVertex)) {
      addVertex(startVertex);
    }
    this.startVertex = startVertex;
  }

  @Override
  public void setEndVertex(IJVariantGraphVertex endVertex) {
    if (!containsVertex(endVertex)) {
      addVertex(endVertex);
    }
    this.endVertex = endVertex;
  }

}
