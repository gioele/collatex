package com.sd_editions.collatex.Collate;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import com.sd_editions.collatex.Block.BlockStructure;
import com.sd_editions.collatex.Block.BlockStructureCascadeException;
import com.sd_editions.collatex.Block.Word;
import com.sd_editions.collatex.InputPlugin.StringInputPlugin;

public class TextAlignmentVisitorTest extends TestCase {
  public void testAlignment() {
    Table table = wordAlignmentTable("cat", "cat");
    assertEquals("identical: cat", table.get(1, 2).toString());
  }

  public void testAlignmentVariant() {
    Table table = wordAlignmentTable("cat", "mat");
    assertEquals("variant-align: cat / mat", table.get(1, 2).toString());
  }

  public void testNonAlignment() {
    Table table = wordAlignmentTable("cat", "boat");
    assertEquals("non-alignment: cat, boat", table.get(1, 2).toString());
  }

  public void testCapital() {
    Table table = wordAlignmentTable("the", "The");
    assertEquals("identical: the", table.get(1, 2).toString());
  }

  public void testSentence() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("a black cat", "a black cat");
    assertEquals("identical: a", table.get(1, 2).toString());
    assertEquals("identical: black", table.get(1, 4).toString());
    assertEquals("identical: cat", table.get(1, 6).toString());
  }

  public void testPunctuation() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("the black cat", "The, black cat");
    assertEquals("identical: the", table.get(1, 2).toString());
    assertEquals("identical: black", table.get(1, 4).toString());
    assertEquals("identical: cat", table.get(1, 6).toString());
  }

  public void testVariant() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("a white cat", "a black cat");
    assertEquals("identical: a", table.get(1, 2).toString());
    assertEquals("replacement: white / black", table.get(1, 4).toString());
    assertEquals("identical: cat", table.get(1, 6).toString());
  }

  public void testOmission_InTheMiddle() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("a white horse", "a horse");
    assertEquals("identical: a", table.get(1, 2).toString());
    assertEquals("omission: white", table.get(1, 4).toString());
    assertEquals("identical: horse", table.get(1, 6).toString());
  }

  public void testOmission_AtTheStart() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("a certain death", "certain death");
    assertEquals("omission: a", table.get(1, 2).toString());
    assertEquals("identical: certain", table.get(1, 4).toString());
    assertEquals("identical: death", table.get(1, 6).toString());
  }

  public void testOmission_AtTheEnd() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("a calico cat", "a calico");
    assertEquals("identical: a", table.get(1, 2).toString());
    assertEquals("identical: calico", table.get(1, 4).toString());
    assertEquals("omission: cat", table.get(1, 6).toString());
  }

  public void testAddition_InTheMiddle() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("a cat", "a calico cat");
    assertEquals("identical: a", table.get(1, 2).toString());
    assertEquals("addition: calico", table.get(1, 3).toString());
    assertEquals("identical: cat", table.get(1, 4).toString());
  }

  public void testAddition_AtTheEnd() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("to be", "to be lost");
    assertEquals("identical: to", table.get(1, 2).toString());
    assertEquals("identical: be", table.get(1, 4).toString());
    assertEquals("addition: lost", table.get(1, 5).toString());
  }

  public void testAddition_AtTheStart() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("to be", "not to be");
    assertEquals("addition: not", table.get(1, 1).toString());
    assertEquals("identical: to", table.get(1, 2).toString());
    assertEquals("identical: be", table.get(1, 4).toString());
  }

  public void testPhraseAlignment_InTheMiddle() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    Table table = alignmentTable("i saw cloverfield yesterday", "i saw the moon get eclipsed yesterday");
    assertEquals("identical: i", table.get(1, 2).toString());
    assertEquals("identical: saw", table.get(1, 4).toString());
    assertEquals("identical: yesterday", table.get(1, 8).toString());
    assertEquals("replacement: cloverfield / the moon get eclipsed", table.get(1, 6).toString());
  }

  private Table alignmentTable(String baseString, String witnessString) throws FileNotFoundException, IOException, BlockStructureCascadeException {
    BlockStructure base = new StringInputPlugin(baseString).readFile();
    BlockStructure variant = new StringInputPlugin(witnessString).readFile();
    // TextAlignmentVisitor visitor = new TextAlignmentVisitor(variant);
    WordAlignmentVisitor visitor = new WordAlignmentVisitor(variant);
    base.accept(visitor);
    // BlockStructure alignmentInformation = visitor.getResult();
    Table table = new TupleToTable(base, variant, visitor.getResult()).getTable();
    return table;
  }

  private Table wordAlignmentTable(final String string, final String string2) {
    Word base = new Word(string);
    Word variant = new Word(string2);
    TextAlignmentVisitor visitor = new TextAlignmentVisitor(variant);
    visitor.visitWord(base);
    BlockStructure alignmentInformation = visitor.getResult();
    Table table = (Table) alignmentInformation.getRootBlock();
    return table;
  }

  public void testMultipleAdditions() throws FileNotFoundException, IOException, BlockStructureCascadeException {
    // black cat
    // the black and white cat
    Table table = alignmentTable("black cat", "the black and white cat");
    assertEquals("addition: the", table.get(1, 1).toString());
    assertEquals("identical: black", table.get(1, 2).toString());
    assertEquals("identical: cat", table.get(1, 4).toString());
    assertEquals("addition: and white", table.get(1, 3).toString());
  }

}
