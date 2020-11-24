package org.atpfivt.jsyntrax.units.nodes;

import com.syntrax.visitors.Visitor;

/**
 * @brief special class for parse None in syntrax language
 */
public class NoneNode extends Node {
    public NoneNode() { super(""); }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
