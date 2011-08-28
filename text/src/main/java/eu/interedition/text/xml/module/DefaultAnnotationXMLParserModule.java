/*
 * #%L
 * Text: A text model with range-based markup via standoff annotations.
 * %%
 * Copyright (C) 2010 - 2011 The Interedition Development Group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.interedition.text.xml.module;

import eu.interedition.text.AnnotationRepository;
import eu.interedition.text.QName;
import eu.interedition.text.Range;
import eu.interedition.text.mem.SimpleAnnotation;
import eu.interedition.text.xml.XMLEntity;
import eu.interedition.text.xml.XMLParserState;

import java.util.Map;
import java.util.Stack;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public class DefaultAnnotationXMLParserModule extends AbstractAnnotationXMLParserModule {

  private final ThreadLocal<Stack<Long>> startOffsetStack = new ThreadLocal<Stack<Long>>();
  private final ThreadLocal<Stack<Map<QName, String>>> attributeStack = new ThreadLocal<Stack<Map<QName, String>>>();


  public DefaultAnnotationXMLParserModule(AnnotationRepository annotationRepository, int batchSize) {
    super(annotationRepository, batchSize);
  }

  @Override
  public void start(XMLParserState state) {
    super.start(state);
    startOffsetStack.set(new Stack<Long>());
    attributeStack.set(new Stack<Map<QName, String>>());
  }

  @Override
  public void end(XMLParserState state) {
    attributeStack.remove();
    startOffsetStack.remove();
    super.end(state);
  }

  @Override
  public void start(XMLEntity entity, XMLParserState state) {
    super.start(entity, state);
    startOffsetStack.get().push(state.getTextOffset());
    attributeStack.get().push(entity.getAttributes());
  }

  @Override
  public void end(XMLEntity entity, XMLParserState state) {
    final Range range = new Range(startOffsetStack.get().pop(), state.getTextOffset());
    add(new SimpleAnnotation(state.getTarget(), entity.getName(), range), attributeStack.get().pop());
    super.end(entity, state);
  }
}
