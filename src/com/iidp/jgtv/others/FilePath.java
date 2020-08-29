/*
 *  Copyright (C) 2020 Paulo A. Herrera <pauloa.herrera@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.iidp.jgtv.others;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * Provides static methods to work with file and directory paths.
 */
public class FilePath {

    public static boolean isFile(String path) {
        var f = new File(path);
        return f.isFile();
    }

    /**
     * Checks if path exists and if it is a file.
     *
     * @param path file path.
     * @return file object pointing to file at path.
     * @throws Exception
     */
    public static boolean fileExist(String path) throws Exception {
        File file = new File(path);
        return fileExist(file);
    }

    /**
     * Checks if path exists and if it is a file that can be read.
     *
     * @param file file path.
     * @return file object pointing to file at path.
     */
    public static boolean fileExist(File file) throws Exception {
        if (file.exists() && file.isFile() && file.canRead()) {
            return true;
        } else if (!file.isFile()) {
            return false; //throw new Exception("Path is not a file: " + file.getAbsolutePath());
        } else if (!file.exists()) {
            return false; //throw new Exception("File does not exist: " + file.getAbsolutePath());
        } else if (!file.canRead()) {
            throw new Exception("Cannot read file: " + file.getAbsolutePath());
        } else {
            throw new Exception("Unknown problem with file: " + file.getAbsolutePath());
        }
    }

    /**
     * Checks if path exists and if it is a directory.
     *
     * @param path directory path.
     * @param createIt if true create directory if it does not exist.
     * @return file object pointint to directory at path.
     */
    public static boolean checkDirectory(String path, boolean createIt) throws Exception {
        File file = new File(path);
        return checkDirectory(file, createIt);
    }

    /**
     * Checks if path exists and if it is a directory.
     *
     * @param file directory path.
     * @param createIt if true create directory if it does not exist.
     * @return file object pointint to directory at path.
     */
    public static boolean checkDirectory(File file, boolean createIt) throws Exception {
        if (file.exists() && file.isDirectory()) {
            return true;
        } else if (!file.exists() && createIt) {
            file.mkdirs();
            return true;
        } else if (file.exists() && !file.isDirectory()) {
            return false; //throw new Exception("Path is not a directory: " + file.getAbsolutePath());
        } else if (!file.exists()) {
            return false; //throw new Exception("Directory does not exist: " + file.getAbsolutePath());
        } else {
            throw new Exception("Unknown problem with directory: " + file.getAbsolutePath());
        }
    }

    /**
     * Return a list of files in directory dir that match a regular
     * expression pattern.
     */
    public static File[] getFileList(String dirName, String pattern) throws Exception {
        File dir = new File(dirName);
        FilePath.checkDirectory(dir, false);

        final Pattern p = Pattern.compile(pattern);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return p.matcher( name ).matches();
            }
        };
        return dir.listFiles( filter );
    }


    /**
     * Joins parts of a file path
     * @param basename directory name
     * @param filename file name
     * @return full path created by joining the parts
     */
    public static String join(String basename, String filename) {
        Path path = Paths.get(basename, filename);
        return path.toAbsolutePath().toString();
    }

    /**
     * Joins parts of a file path
     *
     * @param names list of parts that define the full path
     * @return full path created by joining the parts
     */
    public static String join(String ... names) {
        if (names.length == 1) {
            return Paths.get(names[0]).toString();
        } else {
            var name0 = names[0];
            Path path = null;
            for (int i = 1; i < names.length; i++) {
                path = Paths.get(name0, names[i]);
                name0 = path.toString();
            }
            return path.toAbsolutePath().toString();
        }
    }

    public static String relativePath(String current, String path) throws IOException{
        var currentDir = Paths.get(current).toAbsolutePath();
        var absPath = Paths.get(path).toAbsolutePath();
        if (currentDir.getRoot().equals(absPath.getRoot()) ) {
            return currentDir.relativize(absPath).toString();
        } else {
            return absPath.toString();
        }
    }

    /**
     * Base part of a path. Usually the directory.
     * @param path string that represents a path.
     * @return Base part of the path
     */
    public static String getBasename(String path) {
        Path p = Paths.get(path);
        return p.getParent().toString();
    }

    /**
     * File name included in a path.
     * @param path string that represents a path.
     * @return File name in the path
     */
    public static String getFilename(String path) {
        Path p = Paths.get(path);
        return p.getFileName().toString();
    }

    /**
     * File name included in a path without extension.
     *
     * @param path string that represents a path
     * @return File name in the path without extension
     */
    public static String getFilenameWithoutExtension(String path) {
        var filename = getFilename(path);
        var rootname = filename.split("\\.")[0];
        return rootname;
    }

    /**
     * Extension, e.g. shp, included in a path that should include a file name.
     *
     * @param path string that represents a path
     * @return File name in the path without extension
     */
    public static String getFileExtension(String path) {
        assert path.contains(".");
        assert isFile(path);

        Path p = Paths.get(path);
        var e = p.toAbsolutePath().toString();
        var begin = e.indexOf(".");
        return e.substring(begin + 1);
    }

    // TESTS
    private static void testRelativePath() throws Exception {
        var path = "C:/Users/paulo/IdeaProjects/JGTV";
        var current = "C:/Users/paulo";
        var relPath = FilePath.relativePath(current, path);
        System.out.println("path: " + path);
        System.out.println("current dir: " + current);
        System.out.println("relative path: " + relPath);
    }

    private static void testListDir() throws Exception {
        var path = "examples/ex1_SimpleShapes";
        var files = getFileList( path, "\\w*\\.shp");
        for (File f : files) {
            System.out.println(f.toString());
        }
    }

    private static void testFileExtension() {
        var filename = "examples/ex1_SimpleShapes/multi_points.shp";
        var ext = FilePath.getFileExtension(filename);
        System.out.println("Extension: " + ext);
    }

    private static void testFileNameWithoutExtension() {
        var filename = "examples/ex1_SimpleShapes/multi_points.shp";
        var rootname = getFilenameWithoutExtension(filename);
        System.out.println("Filename: " + filename);
        System.out.println("Rootname: " + rootname);
    }

    private static void testBasename() {
        var filename = "examples/ex1_SimpleShapes/multi_points.shp";
        var dirName = FilePath.getBasename(filename);
        System.out.println("Path: " + filename);
        System.out.println("Directory: " + dirName);
    }

    public static void main(String[] args) throws Exception {
       // testRelativePath();
       // testListDir();
       // testFileExtension();
       // testFileNameWithoutExtension();
        testBasename();
    }
}

