package eu.interedition.collatex2.implementation.vg_analysis;

import eu.interedition.collatex2.interfaces.IPhrase;

public interface IMatch2 {

  String getNormalized();
  
  IPhrase getPhraseA();
  
  IPhrase getPhraseB();

}