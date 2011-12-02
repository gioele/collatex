package eu.interedition.collatex.implementation.graph.db;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import eu.interedition.collatex.implementation.graph.RankedVariantGraphVertex;
import eu.interedition.collatex.implementation.graph.SegmentedVariantGraphVertex;
import eu.interedition.collatex.implementation.output.Apparatus;
import eu.interedition.collatex.interfaces.INormalizedToken;
import eu.interedition.collatex.interfaces.IWitness;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.transform;
import static eu.interedition.collatex.implementation.graph.db.VariantGraphRelationshipType.PATH;
import static java.util.Collections.singleton;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public class PersistentVariantGraph {
  private final GraphDatabaseService db;
  private final PersistentVariantGraphVertex start;
  private final PersistentVariantGraphVertex end;
  private final Resolver<IWitness> witnessResolver;
  private final Resolver<INormalizedToken> tokenResolver;
  private Function<Node, PersistentVariantGraphVertex> vertexWrapper;
  private Function<Relationship, PersistentVariantGraphEdge> edgeWrapper;

  public PersistentVariantGraph(Node start, Node end, Resolver<IWitness> witnessResolver, Resolver<INormalizedToken> tokenResolver) {
    this.db = start.getGraphDatabase();
    this.start = new PersistentVariantGraphVertex(this, start);
    this.end = new PersistentVariantGraphVertex(this, end);
    this.witnessResolver = witnessResolver;
    this.tokenResolver = tokenResolver;
    this.vertexWrapper = PersistentVariantGraphVertex.createWrapper(this);
    this.edgeWrapper = PersistentVariantGraphEdge.createWrapper(this);

  }

  public GraphDatabaseService getDb() {
    return db;
  }

  public PersistentVariantGraphVertex getStart() {
    return start;
  }

  public PersistentVariantGraphVertex getEnd() {
    return end;
  }

  public Resolver<IWitness> getWitnessResolver() {
    return witnessResolver;
  }

  public Resolver<INormalizedToken> getTokenResolver() {
    return tokenResolver;
  }

  public Function<Node, PersistentVariantGraphVertex> getVertexWrapper() {
    return vertexWrapper;
  }

  public Function<Relationship, PersistentVariantGraphEdge> getEdgeWrapper() {
    return edgeWrapper;
  }

  public Map<PersistentVariantGraphVertex, PersistentVariantGraphVertex> getTransposedTokens() {
    throw new UnsupportedOperationException();
  }

  public Iterable<PersistentVariantGraphVertex> traverseVertices(final SortedSet<IWitness> witnesses) {
    return new Iterable<PersistentVariantGraphVertex>() {
      @Override
      public Iterator<PersistentVariantGraphVertex> iterator() {
        return new AbstractIterator<PersistentVariantGraphVertex>() {
          private Map<PersistentVariantGraphVertex, Integer> encountered = Maps.newHashMap();
          private Queue<PersistentVariantGraphVertex> queue = new ArrayDeque<PersistentVariantGraphVertex>(singleton(getStart()));

          @Override
          protected PersistentVariantGraphVertex computeNext() {
            if (queue.isEmpty()) {
              return endOfData();
            }
            final PersistentVariantGraphVertex next = queue.remove();
            for (PersistentVariantGraphEdge edge : next.getOutgoingPaths(witnesses)) {
              final PersistentVariantGraphVertex end = edge.getEnd();
              final int endIncoming = Iterables.size(end.getIncomingPaths(witnesses));
              if (endIncoming == 1) {
                queue.add(end);
              } else if (encountered.containsKey(end)) {
                final int endEncountered = encountered.remove(end);
                if ((endIncoming - endEncountered) == 1) {
                  queue.add(end);
                } else {
                  encountered.put(end, endEncountered + 1);
                }
              } else {
                encountered.put(end, 1);
              }
            }
            return next;
          }
        };
      }
    };
  }

  public Iterable<PersistentVariantGraphEdge> traverseEdges(final SortedSet<IWitness> witnesses) {
    return transform(Traversal.description().relationships(PATH, OUTGOING).uniqueness(Uniqueness.RELATIONSHIP_GLOBAL).breadthFirst().evaluator(new Evaluator() {

      @Override
      public Evaluation evaluate(Path path) {
        if (witnesses != null && !witnesses.isEmpty()) {
          final Relationship lastRel = path.lastRelationship();
          if (lastRel != null) {
            if (!new PersistentVariantGraphEdge(PersistentVariantGraph.this, lastRel).canBeTraversed(witnesses)) {
              return Evaluation.EXCLUDE_AND_PRUNE;
            }
          }
        }

        return Evaluation.INCLUDE_AND_CONTINUE;
      }
    }).traverse(start.getNode()).relationships(), edgeWrapper);
  }

  public PersistentVariantGraphVertex addVertex(INormalizedToken token) {
    return new PersistentVariantGraphVertex(this, Sets.newTreeSet(singleton(token)));
  }

  public PersistentVariantGraphEdge createPath(PersistentVariantGraphVertex from, PersistentVariantGraphVertex to, SortedSet<IWitness> witnesses) {
    Preconditions.checkArgument(!from.equals(to));

    if (from.equals(start)) {
      final PersistentVariantGraphEdge startEndEdge = edgeBetween(start, end);
      if (startEndEdge != null) {
        startEndEdge.delete();
      }
    }

    for (PersistentVariantGraphEdge e : from.getOutgoingPaths(null)) {
      if (to.equals(e.getEnd())) {
        return e.add(witnesses);
      }
    }
    return new PersistentVariantGraphEdge(this, from, to, witnesses);
  }

  public boolean verticesAreAdjacent(PersistentVariantGraphVertex a, PersistentVariantGraphVertex b) {
    return (edgeBetween(a, b) != null);
  }

  public PersistentVariantGraphEdge edgeBetween(PersistentVariantGraphVertex a, PersistentVariantGraphVertex b) {
    final Node aNode = a.getNode();
    final Node bNode = b.getNode();
    for (Relationship r : aNode.getRelationships(PATH)) {
      if (r.getOtherNode(aNode).equals(bNode)) {
        return new PersistentVariantGraphEdge(this, r);
      }
    }
    return null;
  }

  public SortedSet<IWitness> getWitnesses() {
    final SortedSet<IWitness> witnesses = Sets.newTreeSet();
    for (PersistentVariantGraphEdge e : start.getOutgoingPaths(null)) {
      witnesses.addAll(e.getWitnesses());
    }
    return witnesses;
  }

  public PersistentVariantGraph join() {
    final Queue<PersistentVariantGraphVertex> queue = new ArrayDeque<PersistentVariantGraphVertex>();
    for (PersistentVariantGraphEdge e : start.getOutgoingPaths(null)) {
      queue.offer(e.getEnd());
    }

    while (!queue.isEmpty()) {
      final PersistentVariantGraphVertex vertex = queue.poll();
      final List<PersistentVariantGraphEdge> outgoing = Lists.newArrayList(vertex.getOutgoingPaths(null));
      if (outgoing.size() == 1) {
        final PersistentVariantGraphEdge joinCandidateSingleIncoming = outgoing.get(0);
        final PersistentVariantGraphVertex joinCandidate = joinCandidateSingleIncoming.getEnd();
        if (Iterables.size(joinCandidate.getIncomingPaths(null)) == 1) {
          final List<PersistentVariantGraphEdge> joinCandidateOutgoing = Lists.newArrayList(joinCandidate.getOutgoingPaths(null));
          if (joinCandidateOutgoing.size() == 1) {
            final PersistentVariantGraphEdge joinCandidateSingleOutgoing = joinCandidateOutgoing.get(0);
            final SortedSet<IWitness> witnesses = joinCandidateSingleIncoming.getWitnesses();
            if (witnesses.equals(joinCandidateSingleOutgoing.getWitnesses())) {
              vertex.add(joinCandidate.getTokens(null));
              createPath(vertex, joinCandidateSingleOutgoing.getEnd(), witnesses);

              joinCandidateSingleIncoming.delete();
              joinCandidateSingleOutgoing.delete();
              joinCandidate.delete();
              outgoing.remove(joinCandidateSingleIncoming);
            }
          }
        }
      }
      for (PersistentVariantGraphEdge e : outgoing) {
        queue.offer(e.getEnd());
      }
    }

    return this;
  }

  public PersistentVariantGraph rank() {
    for (PersistentVariantGraphVertex v : traverseVertices(null)) {
      int rank = -1;
      for (PersistentVariantGraphEdge e : v.getIncomingPaths(null)) {
        rank = Math.max(rank, e.getStart().getRank());
      }
      v.setRank(rank + 1);
    }

    return this;
  }

  /**
   * Factory method that builds a ParallelSegmentationApparatus from a VariantGraph
   */
  public Apparatus toApparatus() {
    join();
    rank();

    List<Apparatus.Entry> entries = Lists.newArrayList();
    for (PersistentVariantGraphVertex v : traverseVertices(null)) {
      if (v.equals(getStart()) || v.equals(getEnd())) {
        continue;
      }
      Apparatus.Entry entry;
      int rank = v.getRank();
      if (rank > entries.size()) {
        entry = new Apparatus.Entry(getWitnesses());
        entries.add(entry);
      } else {
        entry = entries.get(rank - 1);
      }
      entry.add(v);
    }

    return new Apparatus(getWitnesses(), entries);
  }

  public Iterable<PersistentVariantGraphVertex> findLongestPath() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    return start.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof PersistentVariantGraph) {
      return start.equals(((PersistentVariantGraph) obj).start);
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return Iterables.toString(getWitnesses());
  }
}
