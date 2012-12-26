package eu.interedition.collatex.needlemanwunsch;

import eu.interedition.collatex.Token;
import eu.interedition.collatex.VariantGraph;

import java.util.Set;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public interface NeedlemanWunschScorer {

  float score(Set<VariantGraph.Vertex> vertices, Token token);

  float gap();
}