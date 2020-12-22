package org.atpfivt.jsyntrax.units.tracks.stack;

import org.atpfivt.jsyntrax.units.Unit;
import org.atpfivt.jsyntrax.visitors.Visitor;

import java.util.List;

public class Rightstack extends Stack {
  public Rightstack(List<Unit> units) {
    super(units);
  }

  public void accept(Visitor visitor) {
    visitor.visitRightstack(this);
  }
}
