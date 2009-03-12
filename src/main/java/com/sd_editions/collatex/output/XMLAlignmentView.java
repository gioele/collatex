package com.sd_editions.collatex.output;

import java.util.List;

import com.sd_editions.collatex.permutations.LevenshteinMatch;
import com.sd_editions.collatex.permutations.Modification;
import com.sd_editions.collatex.permutations.Modifications;
import com.sd_editions.collatex.permutations.collate.Addition;
import com.sd_editions.collatex.permutations.collate.Removal;
import com.sd_editions.collatex.permutations.collate.Replacement;
import com.sd_editions.collatex.permutations.collate.Transposition;

public class XMLAlignmentView {

  private final Modifications modifications;

  public XMLAlignmentView(Modifications modifications) {
    super();
    this.modifications = modifications;
  }

  String modificationsView(int base) {
    StringBuffer xml = new StringBuffer("<modifications>");
    List<Modification> modificationsL = modifications.getModifications();
    if (!modificationsL.isEmpty()) {
      for (Modification modification : modificationsL) {
        if (modification instanceof LevenshteinMatch) {
          xml.append("<li>" + levenshteinMatch((LevenshteinMatch) modification) + "</li>");
        } else if (modification instanceof Addition) {
          xml.append(additionView((Addition) modification, base));
        } else if (modification instanceof Removal) {
          xml.append(removalView((Removal) modification));
        } else if (modification instanceof Transposition) {
          xml.append("<li>" + transpositionView((Transposition) modification) + "</li>");
        } else if (modification instanceof Replacement) {
          xml.append("<li>" + replacementView((Replacement) modification) + "</li>");
        }
      }
    } else {}
    xml.append("</modifications>");
    return xml.toString();
  }

  private String replacementView(Replacement modification) {
    // TODO Auto-generated method stub
    return null;
  }

  private String transpositionView(Transposition modification) {
    // TODO Auto-generated method stub
    return null;
  }

  private String removalView(Removal modification) {
    return "<omission position=\"" + modification.getPosition() + "\">" + modification.getRemovedWords() + "</omission>";
  }

  private String additionView(Addition modification, int base) {
    return "<addition position=\"" + modification.getPosition() + "\">" + modification.getAddedWords() + "</addition>";
  }

  private String levenshteinMatch(LevenshteinMatch modification) {
    // TODO Auto-generated method stub
    return null;
  }

  public Modifications getModifications() {
    return modifications;
  }

}
