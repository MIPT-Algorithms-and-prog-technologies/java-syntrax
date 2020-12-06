package org.atpfivt.jsyntrax.units.tracks;

import org.atpfivt.jsyntrax.units.Unit;
import org.atpfivt.jsyntrax.visitors.Visitor;

import java.util.ArrayList;

public class Line extends Track {
  public Line(ArrayList<Unit> units) {
    super(units);
  }

  public Line(Unit unit) {
    super(new ArrayList<>());
    units.add(unit);
  }

  public void accept(Visitor visitor) {
    visitor.visitLine(this);
  }
}
