package org.atpfivt.jsyntrax.groovy_parser

import org.atpfivt.jsyntrax.Configuration
import org.atpfivt.jsyntrax.units.Unit
import org.atpfivt.jsyntrax.units.nodes.Node
import org.atpfivt.jsyntrax.units.nodes.NoneNode
import org.atpfivt.jsyntrax.units.tracks.Choice
import org.atpfivt.jsyntrax.units.tracks.Line
import org.atpfivt.jsyntrax.units.tracks.Track
import org.atpfivt.jsyntrax.units.tracks.loop.Loop
import org.atpfivt.jsyntrax.units.tracks.loop.Toploop
import org.atpfivt.jsyntrax.units.tracks.opt.Opt
import org.atpfivt.jsyntrax.units.tracks.opt.Optx
import org.atpfivt.jsyntrax.units.tracks.stack.Indentstack
import org.atpfivt.jsyntrax.units.tracks.stack.Rightstack
import org.atpfivt.jsyntrax.units.tracks.stack.Stack

class SyntraxScript extends Script {
  Unit node
  String title

  static ArrayList<Unit> unitsToString(Object... units) {
    for (i in 0..<units.length) {
      if (units[i] == null) {
        units[i] = new NoneNode()
      } else if (!(units[i] instanceof Track)) {
        units[i] = new Node(units[i].toString())
      }
    }

    return units as ArrayList<Unit>
  }

  Unit title(String title) {
      this.title = title
      return node
  }

  Line line(Object... units) {
    return node = new Line(unitsToString(units) as ArrayList<Unit>)
  }

  Loop loop(Object... units) {
    return node = new Loop(unitsToString(units) as ArrayList<Unit>)
  }

  Toploop toploop(Object... units) {
    return node = new Toploop(unitsToString(units) as ArrayList<Unit>)
  }

  Choice choice(Object... units) {
    return node = new Choice(unitsToString(units) as ArrayList<Unit>)
  }

  Opt opt(Object... units) {
    return node = new Opt([line(units)] as ArrayList<Unit>)
  }

  Optx optx(Object... units) {
    return node = new Optx([line(units)] as ArrayList<Unit>)
  }

  Stack stack(Object... units) {
    return node = new Stack(unitsToString(units) as ArrayList<Unit>)
  }

  Indentstack indentstack(int indent, Object... units) {
    return node = new Indentstack(indent, unitsToString(units) as ArrayList<Unit>)
  }

  Rightstack rightstack(Object... units) {
    return node = new Rightstack(unitsToString(units) as ArrayList<Unit>)
  }

  Configuration jsyntrax(Track track,
                                Map<String, String> url_map = [:]) {
    return new Configuration(track, url_map)
  }

  @Override
  Object run() {
    return null
  }
}
