package com.iidp.jgtv.files;

import com.iidp.jgtv.files.dbf.DbfRecord;
import com.iidp.jgtv.files.dbf.FIELD_TYPE;
import com.iidp.jgtv.files.dbf.FieldDescriptor;
import com.iidp.jgtv.files.dbf.FieldList;
import com.iidp.jgtv.others.Echo;
import com.iidp.jgtv.others.LittleEndian;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a simple DBF parser to read records stored as .dbf files.
 *
 * The specifications for this file type are not complete, and it seems
 * that different software create slightly different versions, which makes
 * the parsing difficult and error prompt.
 *
 * Information in the file is stored in a combination of big and native endianess,
 * which also add some extra complexity.
 *
 * REFERENCES:
 *  - http://www.independent-software.com/dbase-dbf-dbt-file-format.html
 *  - http://web.archive.org/web/20150323061445/http://ulisse.elettra.trieste.it/services/doc/dbase/DBFstruct.htm
 *
 * In summary, the file structure includes:
 *  - Header
 *  - List of field descriptors
 *  - List of records
 *
 *  NOTE: - The file created by QGIS seems to follow the dBase III format.
 *        - We assume little endian is the native format for the file.
 */
public class DbfFile {
    /** Path to .dbf file */
    public final String src;
    /** Number of records */
    private int nrecords = -1;
    /** Header size in bytes */
    private int sizeHeader = -1;
    /** Number of fields in each record */
    private int nfields = -1;
    /** Size of section that contains records in bytes */
    private int sizeRecords = -1;
    /** List of descriptor for the fields included in each record */
    List<FieldDescriptor> fields;
    /** List of records in the file */
    List<DbfRecord> records;

    public DbfFile(String _src) {
        src = _src;
        fields = new ArrayList<FieldDescriptor>();
        records = new ArrayList<DbfRecord>();
    }

    /** Writes a summary of the content of this file */
    public void display(PrintStream o) {
        var MAX_NUM_RECORDS = 10;

        o.println("DBF file: " + src);
        o.printf("  - size of header [bytes]: %d\n", sizeHeader);
        o.printf("  - size of records [bytes]: %d\n", sizeRecords);
        o.printf("  - # of fields: %d\n", nfields);
        o.printf("  - # of records: %d\n", nrecords);
        o.println("  + Fields: ");
        var ii = 0;
        for (FieldDescriptor f: fields) {
            o.println("    * " + f);
        }
        o.println("  + Records: "); // sets a max number of records to print
        for (DbfRecord r: records) {
            o.println("    * " + r);
            ii += 1;
            if (ii > MAX_NUM_RECORDS) System.out.println("continue..."); break;
        }
    }

    private static FieldDescriptor readFieldDescriptor(DataInputStream b) throws Exception {
        //System.out.println(" ============== Field descriptor ================= ");
        //0 10	11 bytes	Field name in ASCII (zero-filled)
        var name = LittleEndian.readString(b, 11).strip();
        //System.out.println("Name: " + name);

        //11	1 byte	Field type in ASCII (C, D, F, L, M, or N)
        var ftype = LittleEndian.readString(b, 1);
        assert FIELD_TYPE.isFieldType(ftype.toCharArray()[0]);
        //System.out.println("Field type: " + ftype);

        //12-15	4 bytes	Reserved
        b.read(); b.read(); b.read(); b.read();

        //16	1 byte	Field length in binary[note 1]
        var flength = b.readByte();

        // 17	1 byte	Field decimal count in binary
        var fdecimal = b.readByte();
        //System.out.printf("flength: %d  fdecimal: %d\n", flength, fdecimal);

        // The next 14 bytes contain different type of information that is not important for the
        // purposes of this library and that changes depending on the version of DBASE in use.
        for (int i = 0; i < 14; i++) {
            b.read();
        }
        //System.out.println(" ============== END Field descriptor ================= ");

        return new FieldDescriptor(name, ftype, flength, fdecimal);
    }

    private void readHeader(DataInputStream b) throws Exception {
        //0	1 byte	Valid dBASE for DOS file; bits 0-2 indicate version number, bit 3 indicates the presence of a dBASE for DOS memo file, bits 4-6 indicate the presence of a SQL table, bit 7 indicates the presence of any memo file (either dBASE m PLUS or dBASE for DOS)
        var ver = b.read();
        //System.out.printf("Version: %02X\n", ver);
        assert (ver == 03);
        //System.out.printf("Version: %s\n", Integer.toHexString(ver));

        //1-3	3 bytes	Date of last update; formatted as YYMMDD
        var YY = b.read();
        var MM = b.read();
        var DD = b.read();
        //System.out.printf("Date: %d/%d/%d\n", DD, MM, YY);

        // 4-7	32-bit number	Number of records in the database file
        nrecords = LittleEndian.readInt(b);
        //System.out.printf("Number of records: %d \n", nrecords);

        // 8-9	16-bit number	Number of bytes in the header
        var nbh = LittleEndian.readShort(b);
        //System.out.printf("Number of bytes: %d \n", 2 * nbh);
        sizeHeader = 2 * nbh;

        // # field descriptors.
        nfields = (int)( (nbh - 33) / 32); // Each field descriptor has 32 bytes and 32 bytes header + 1 byte end of header
        //System.out.printf("Number of field descriptors: %d \n", nf);

        //10-11	16-bit number	Number of bytes in the record section
        var nbr = LittleEndian.readShort(b);
        //System.out.printf("Number of bytes in records: %d \n", 2*nbr);
        sizeRecords = 2 * nbr;

        //12-13	2 bytes	Reserved; fill with 0
        b.read(); b.read();

        //14	1 byte	Flag indicating incomplete transaction[note 1]
        b.read();

        //15	1 byte	Encryption flag[note 2]
        b.read();

        //16-27	12 bytes	Reserved for dBASE for DOS in a multi-user environment
        b.read(new byte[12]);

        // 28	1 byte	Production .mdx file flag; 0x01 if there is a production .mdx file, 0x00 if not
        b.read();

        //29	1 byte	Language driver ID
        b.read();

        //30-31	2 bytes	Reserved; fill with 0
        b.read(); b.read();

        //32-n[note 3][note 4]	32 bytes each
        // Field descriptor array (the structure of this array is shown in Table Database field descriptor bytes)
        //fields = []
        for (int i = 0; i < nfields; i++) {
            var _fd = readFieldDescriptor(b);
            //System.out.println(_fd);
            if (_fd.flength <= 0) {
                System.out.println("WARNING: Negative field length");
            }
            this.fields.add(_fd);
        }

        //n +1	1 byte	0x0D as the field descriptor array terminator
        var end = b.read();
        var send = String.format("%X", end);
        assert "D".equals(send);
        //System.out.printf("End: %X\n", end);
        //System.out.println("Done reading header");
    }

    private void readRecords(DataInputStream b) throws Exception {
        for (int i = 0; i < nrecords; i++) {
            var r = DbfRecord.readRecord(b, fields);
            records.add(r);
            //System.out.println(r);
        }
    }

    public static DbfFile read(String path) throws Exception {
        return read(path, false);
    }

    public static DbfFile read(String path, boolean verbose) throws Exception {
        var src = new File(path);
        Echo.msg("Reading .dbf from: " + src.getAbsolutePath(), 0);

        var dbf = new DbfFile(src.getAbsolutePath());
        var fi = new FileInputStream(src);
        var bi = new BufferedInputStream(fi);
        var b = new DataInputStream(bi);

        dbf.readHeader(b);
        dbf.readRecords(b);
        b.close();

        if (verbose) {
            System.out.println(dbf);
        }
        Echo.msg("   Done reading .dbf file.", 0);
        return dbf;
    }

    public List<FieldList> getFieldsAsLists() {
        var lists = new ArrayList<FieldList>();
        for (int i = 0; i < nfields; i++) {
            var fd = fields.get(i);
            var lr = DbfRecord.getListOfField(records, i);
            var fl = new FieldList(fd, lr);
            lists.add(fl);
        }
        return lists;
    }

    public static void main(String[] args) throws Exception {
        var src = "examples/ex1_SimpleShapes/points.dbf";

        var dbf = read(src);
        dbf.display(System.out);

        var lists = dbf.getFieldsAsLists();

        System.out.println("*** ALL DONE ***");
    }
}
