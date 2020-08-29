INTRODUCTION
============

Java Gis-to-Vtk (JGTV) is a small pure Java library to read and export vector GIS
information stored as shape (.shp) and associated (.dbf and .shx) files as VTK binary  
files that can be imported into visualization packages such as Paraview, VisIt 
or Mayavi.     

BACKGROUND
===========

Geographical Information Systems (GIS) store data in different formats. One of 
the most common formats to store vector data is composed of 4 files (plus other 
optional ones):

   1. A shape file (.shp) that stores the geometry of the shapes (points, lines,
   polygons, etc). Each file can only store shapes of the same type, e.g. only 
   points, only lines, etc. Moreover, the initial format only stored plane 
   coordinates (x,y), but later modifications also include the z or elevation 
   coordinate (e.g. PointsZ variant). There is also a M variant that stores a
   value of a variable M for each point, which is also stored in the Z variant.
   
   2. A database file (.dbf) that stores information related to shapes in the 
   .shp file. The information can be of different type: C (text), N or F (numeric),
   D (date), etc. The information for each shape is stored as a record that 
   contains multiple fields of different type, e.g. record = (id: Text, pressure: 
   Numeric, obs_date: Date, etc). QGis exports .dbf files in a DBase III format,
   which is a standard format for small databases, that is not very well documented.
   
   3. A projection file (.prj) that contains information about the geographical projection 
   system used to define the coordinates of the geometric shapes. PyGTV exports this 
   information as comments in the VTK file. It only contains a single line of ASCII text,
    so it is very easy to read.
   
   4. An index file (.shx) that stores information about the location of the data
   in the .dbf file, so it is possible to seek and retrieve information in random
   order without having to parse the file from the beginning, therefore avoiding 
   extra computational cost.

For the purposes of a library such as JGTV only the first three files are important,
since the main objective is exporting the geometry and associated data without 
concern for optimization of the process. Thus, early versions of JGTV only include
pure-Java parsers for the .shp, .dbf and .prj files, which are documented formats.

In addition to the 4 mandatory files, GIS software usually exports other files that
contains additional information, but that are not considered in JGTV.

A main obstacle to export shape files to VTK is that the later does not have an option
for text data associated to grid elements, e.g. cells or lines. Hence, text data
(such as labels, ids, etc) can only be exported as comments that are included in 
the header section (XML) of the binary VTK files.

DOCUMENTATION:
==============

This file together with the included examples in the examples directory in the
source tree provide enough information to start using the package.
 
There are scripts to run the Java application under DOS or Unix-like systems from the
command lines. Prior to use them, it is necessary to change the paths that point to 
the Java directory and the Jar file that contains the main code. See the scripts for 
details (they should be self-explanatory).

Once the scripts are setup, you can add them to the PATH or go to the directory that 
contains them, and type:

`exportToVTK --help`

to obtain a list of required options to process the files that should be exported.

CAVEATS
=======

- The specification for the .shp file includes many different possible types of files depending
  on the type of shapes that is stored. Moreover, the format seems to have evolved, so the structure
  of the file is not easily defined.  
- The format of the .dbf file is not well documented. Moreover, it seems that different 
  software outputs slightly different files, which makes the parsing difficult and brittle.
- Files are stored in binary format with a combination of big and native endianess, which adds some
  complexity and increases the probability of errors while reading the data.
  
  
REQUIREMENTS:
=============

    - JDK-14.

DEVELOPER NOTES:
================

There are several examples included within the package that you can look at 
to understand the details of how to use the package.

I will continue releasing this package as open source, so it is free to be used 
in any kind of project. I will also continue providing support for simple questions 
and making incremental improvements as time allows. 

For further details, please contact me to: paulo.herrera.eirl@gmail.com.

REFERENCES:
===========
 - http://www.independent-software.com/dbase-dbf-dbt-file-format.html
 - http://web.archive.org/web/20150323061445/http://ulisse.elettra.trieste.it/services/doc/dbase/DBFstruct.htm
 - http://web.archive.org/web/20150323061445/http://ulisse.elettra.trieste.it/services/doc/dbase/DBFstruct.htm#C1