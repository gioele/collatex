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

package eu.interedition.collatex2.legacy.indexing;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import eu.interedition.collatex2.implementation.input.Phrase;
import eu.interedition.collatex2.interfaces.IColumn;
import eu.interedition.collatex2.interfaces.INormalizedToken;
import eu.interedition.collatex2.interfaces.IPhrase;
import eu.interedition.collatex2.interfaces.IWitness;
import eu.interedition.collatex2.interfaces.nonpublic.modifications.IColumns;

public class ColumnPhrase {
  // IColumns columns is a consecutive list of IColumn-s
  // sigla is a list of witness sigla, such that for each IColumn in columns, there is a token in that column for all of the sigli, and all theses tokens match. 
  private IColumns columns;
  private List<IWitness> witnesses;
  String name;

  public ColumnPhrase(final String _name, final IColumns _columns, final List<IWitness> witnesses) {
    this.setColumns(_columns);
    this.witnesses = witnesses;
    this.name = _name;
  }

  public IPhrase getPhrase() {
    List<INormalizedToken> tokens = Lists.newArrayList(); //TODO: do the capacity thing!
    final IWitness witness = witnesses.get(0);
    for (IColumn column : columns.getColumns()) {
      tokens.add(column.getToken(witness));
    }
    return new Phrase(tokens);
  }

  public void addColumnToLeft(final IColumn column) {
    final List<IColumn> columnList = columns.getColumns();
    columnList.add(0, column);
    setColumns(new Columns(columnList));
    if (column instanceof NullColumn) {
      name = new StringBuilder("# ").append(name).toString();
    } else {
      final String normalized = column.getToken(witnesses.get(0)).getNormalized();
      name = new StringBuilder(normalized).append(" ").append(name).toString();
    }
  }

  public void addColumnToRight(final IColumn column) {
    final List<IColumn> columnList = columns.getColumns();
    columnList.add(column);
    setColumns(new Columns(columnList));
    if (column instanceof NullColumn) {
      name = new StringBuilder(name).append(" #").toString();
    } else {
      final String normalized = column.getToken(witnesses.get(0)).getNormalized();
      name = new StringBuilder(name).append(" ").append(normalized).toString();
    }
  }

  public String getNormalized() {
    return name;
  }

  public void setSigla(final Collection<IWitness> witnesses) {
    this.witnesses = Lists.newArrayList(witnesses);
  }

  public List<IWitness> getWitnesses() {
    return witnesses;
  }

  public void setColumns(final IColumns columns1) {
    this.columns = columns1;
  }

  public IColumns getColumns() {
    return columns;
  }
}