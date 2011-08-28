/*
 * #%L
 * Text Repository: Datastore for texts based on Interedition's model.
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
package eu.interedition.text.repository;

import eu.interedition.text.Text;

/**
 * @author <a href="http://gregor.middell.net/" title="Homepage">Gregor Middell</a>
 */
public class Witness {
  private int id;
  private Text source;
  private Text text;
  private Collation collation;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Text getSource() {
    return source;
  }

  public void setSource(Text source) {
    this.source = source;
  }

  public Text getText() {
    return text;
  }

  public void setText(Text text) {
    this.text = text;
  }

  public Collation getCollation() {
    return collation;
  }

  public void setCollation(Collation collation) {
    this.collation = collation;
  }

  @Override
  public int hashCode() {
    return (id == 0 ? super.hashCode() : id);
  }

  @Override
  public boolean equals(Object obj) {
    if (id != 0 && obj != null && obj instanceof Witness) {
      return id == ((Witness) obj).id;
    }
    return super.equals(obj);
  }
}
