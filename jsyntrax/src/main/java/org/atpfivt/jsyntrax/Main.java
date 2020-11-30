package org.atpfivt.jsyntrax;

import org.atpfivt.jsyntrax.generators.SVGCanvas;
import org.atpfivt.jsyntrax.generators.SVGCanvasBuilder;
import org.atpfivt.jsyntrax.groovy_parser.Parser;
import org.atpfivt.jsyntrax.units.Unit;
import org.atpfivt.jsyntrax.units.nodes.Bullet;
import org.atpfivt.jsyntrax.units.tracks.Line;
import org.atpfivt.jsyntrax.units.tracks.Track;

import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String program_path = "syntrax_scripts";
        File file = new File(program_path, "test1.groovy");
        Specification spec = Parser.parse(file);
        Unit root = spec.getRoot();
        System.out.println(root);

        SVGCanvas c = new SVGCanvasBuilder().generateSVG(spec);
        String result = c.generateSVG();

        // write result to file
        try {
            OutputStream os = new FileOutputStream(new File("syntrax_scripts", "output.svg"));
            os.write(result.getBytes());
            os.close();
            System.out.println("Done!");
        }
        catch (IOException e) {
            System.out.println("Problems...");
            System.out.println(e.getMessage());
        }
    }
}
