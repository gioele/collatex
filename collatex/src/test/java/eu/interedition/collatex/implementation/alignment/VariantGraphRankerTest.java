/**
 * CollateX - a Java library for collating textual sources,
 * for example, to produce an apparatus.
 *
 * Copyright (C) 2010 ESF COST Action "Interedition".
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.interedition.collatex.implementation.alignment;

import com.google.common.collect.Lists;
import eu.interedition.collatex.AbstractTest;
import eu.interedition.collatex.implementation.graph.db.PersistentVariantGraph;
import eu.interedition.collatex.implementation.graph.db.PersistentVariantGraphVertex;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class VariantGraphRankerTest extends AbstractTest {

  @Test
  public void ranking() {
    final PersistentVariantGraph graph = merge("The black cat", "The black and white cat", "The black and green cat").join().rank();
    final List<PersistentVariantGraphVertex> vertices = Lists.newArrayList(graph.traverseVertices(null));

    assertEquals("the", vertices.get(0).getTokens(null).first().getNormalized());
    assertEquals(1, vertices.get(0).getRank());
    assertEquals("black", vertices.get(1).getTokens(null).first().getNormalized());
    assertEquals(2, vertices.get(1).getRank());
    assertEquals("and", vertices.get(2).getTokens(null).first().getNormalized());
    assertEquals(3, vertices.get(2).getRank());
    assertEquals("white", vertices.get(3).getTokens(null).first().getNormalized());
    assertEquals(4, vertices.get(3).getRank());
    assertEquals("green", vertices.get(4).getTokens(null).first().getNormalized());
    assertEquals(4, vertices.get(4).getRank());
    assertEquals("cat", vertices.get(5).getTokens(null).first().getNormalized());
    assertEquals(5, vertices.get(5).getRank());
  }

  @Test
  public void agastTranspositionHandling() {
    final PersistentVariantGraph graph = merge("He was agast, so", "He was agast", "So he was agast").join().rank();
    final List<PersistentVariantGraphVertex> vertices = Lists.newArrayList(graph.traverseVertices(null));

    assertEquals("so", vertices.get(0).getTokens(null).first().getNormalized());
    assertEquals(1, vertices.get(0).getRank());
    assertEquals("he", vertices.get(1).getTokens(null).first().getNormalized());
    assertEquals(2, vertices.get(1).getRank());
    assertEquals("was", vertices.get(2).getTokens(null).first().getNormalized());
    assertEquals(3, vertices.get(2).getRank());
    assertEquals("agast", vertices.get(3).getTokens(null).first().getNormalized());
    assertEquals(4, vertices.get(3).getRank());
    assertEquals("so", vertices.get(4).getTokens(null).first().getNormalized());
    assertEquals(5, vertices.get(4).getRank());
  }
}
