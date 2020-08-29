package com.iidp.jgtv;

import com.iidp.jgtv.files.DbfFile;
import com.iidp.jgtv.files.PrjFile;
import com.iidp.jgtv.files.ShpFile;
import com.iidp.jgtv.files.shp.AShape;
import com.iidp.jgtv.others.CLIParser;
import com.iidp.jgtv.others.FilePath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class exportToVTK {
    public static String VERSION = "0.1";

    private static void processFile(String src, String outDir, boolean setAttributes, boolean verbose) throws Exception {
        System.out.println("Processing file: " + src);

        assert FilePath.fileExist(src);
        assert FilePath.checkDirectory(outDir, false);

        var dirname = FilePath.getBasename(src);
        var rootname = FilePath.getFilenameWithoutExtension(src);
        var dbfname = FilePath.join(dirname, rootname + ".dbf");
        var prjname = FilePath.join(dirname, rootname + ".prj");
        var vtkname = FilePath.join(outDir, rootname);
        if (verbose) {
            System.out.println("Shape file: " + src);
            System.out.println("Dbf file: " + dbfname);
            System.out.println("Prj file: " + prjname);
            System.out.println("VTK file: " + vtkname);
        }

        var shp = ShpFile.read(src, verbose);
        var prj = PrjFile.read(prjname, verbose);

        if (setAttributes) {
            var dbf = DbfFile.read(dbfname, verbose);
            var attrs = dbf.getFieldsAsLists();
            shp.setAttrs(attrs);
        }

        shp.addComment(prj.getContent());
        shp.toVTK(vtkname);
    }

    private static void processFiles(List<String> srcFiles, CLIParser cli) throws Exception {
        var verbose = cli.get("verbose").asBoolean();
        var setAttributes = cli.get("attrib").asBoolean();
        var default_z = cli.get("elev").asDouble();
        AShape.setDefaultZValue(default_z);

        var outDir = cli.get("dst").asString();
        FilePath.checkDirectory(outDir, true);

        for(String path: srcFiles) {
            processFile(path, outDir, setAttributes, verbose);
        }
    }

    private static List<String> getFilesToProcess(String path) throws Exception {
        var files = new ArrayList<String>();
        if (FilePath.fileExist(path)) {
            files.add(path);
        } else if (FilePath.checkDirectory(path, false)) {
            var shpFiles = FilePath.getFileList(path, "\\w*\\.shp");
            for (File shp: shpFiles) {
                files.add(shp.toString());
            }
        } else {
            throw new Exception("Source path does not exist: " + path);
        }
        return files;
    }

    private static CLIParser setupCLIParser(String[] args) throws Exception {
        CLIParser cli = new CLIParser("JGTV: Export shape files to VTK");
        //cli.option().shortName("-s").longName("--src").value("examples/ex1_SimpleShapes/multi_points.shp").help("path to shape file or directory");
        cli.option().shortName("-s").longName("--src").value("examples/ex1_SimpleShapes").help("path to shape file or directory");
        cli.option().shortName("-d").longName("--dst").value("tmp/ex1_SimpleShapes").help("to directory where VTK files should be saved");
        cli.option().shortName("-e").longName("--elev").value("0.0").help("default elevation for files that only have (x,y) coordinates");
        cli.option().shortName("-a").longName("--attrib").value("true").help("include attributes in .dbf file in exported VTK file");
        //cli.option().shortName("-g").longName("--gui").value("false").help("run graphical interface").setAsFlag();
        cli.option().shortName("-v").longName("--verbose").value("false").help("Verbose output").setAsFlag();
        cli.option().shortName("-h").longName("--help").value("false").help("Print options").setAsFlag();

        cli.parse(args);

        //cli.printOptions();
        return cli;
    }

    /**
     * Provides an entry to process files.
     * @param args: list of strings with command line options that indicate source files, output directory, etc.
     * @throws Exception
     */
    public static void execute(String[] args) throws Exception {
        var cli = setupCLIParser(args);

        System.out.println("****************************************************************");
        System.out.println("JGTV version: " + VERSION);
        System.out.println("Exporting GIS data to VTK format...");
        System.out.println("Path to shape file(s): " + cli.get("src").asString());
        System.out.println("VTK output file: " + cli.get("dst").asString());
        //System.out.println("Run gui: " + str(args.gui) );
        System.out.println("Verbose: " + cli.get("verbose").asBoolean() );
        System.out.println("Default elevation: " + cli.get("elev").asDouble() );
        System.out.println("****************************************************************");

        var filesSrc = getFilesToProcess(cli.get("src").asString());
        processFiles(filesSrc, cli);

    }

    public static void main(String[] args) throws Exception {
        //CLIParser.listArgs(args);
        var cli = setupCLIParser(args);

        // if args.gui:
        // run_gui(args)

        execute(args);

        System.out.println("*** ALL DONE ***");
    }
}
