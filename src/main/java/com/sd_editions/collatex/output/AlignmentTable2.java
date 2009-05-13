package com.sd_editions.collatex.output;

import java.util.List;

import com.google.common.collect.Lists;
import com.sd_editions.collatex.permutations.Witness;
import com.sd_editions.collatex.permutations.WitnessBuilder;
import com.sd_editions.collatex.permutations.Word;

// Note: for the TEI xml output it is easier to
// have a Column be a list<phrase>
// However, for building the alignment table it
// is easier to have a Column be a list<word>
public class AlignmentTable2 {
  private final List<Column> columns;
  private final List<Witness> witnesses;

  public AlignmentTable2() {
    this.columns = Lists.newArrayList();
    this.witnesses = Lists.newArrayList();
  }

  public void add(DefaultColumn defaultColumn) {
    columns.add(defaultColumn);
  }

  public Witness createSuperBase() {
    WitnessBuilder builder = new WitnessBuilder();
    String collectedStrings = "";
    String delim = "";
    for (Column column : columns) {
      collectedStrings += delim + column.toString();
      delim = " ";
    }
    Witness superWitness = builder.build("superbase", collectedStrings);
    return superWitness;
  }

  void addFirstWitness(Witness w1) {
    for (Word word : w1.getWords()) {
      add(new DefaultColumn(word));
    }
    witnesses.add(w1);
  }

  public String toXML() {
    return "<xml></xml>";
  }

  @Override
  public String toString() {
    // TODO: make this work for multiple witnesses!
    Witness witness = witnesses.get(0);
    String collectedStrings = witness.id + ": ";
    String delim = "";
    for (Column column : columns) {
      collectedStrings += delim + column.getWord(witness).toString();
      delim = "|";
    }
    return collectedStrings;
  }
}
