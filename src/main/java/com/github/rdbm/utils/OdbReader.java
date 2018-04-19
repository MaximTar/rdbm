package com.github.rdbm.utils;

import com.github.rdbm.model.ConnectionManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Modified code written by John Ward.
 * http://digiassn.blogspot.ru/2006/07/java-creating-jdbc-connection-to.html
 */

public class OdbReader {

    private String filePath;
    private String password;
    private String username;


    public OdbReader(String filePath, String username, String password) {
        this.filePath = filePath;
        this.username = username;
        this.password = password;
    }

    public Database getDatabase() throws IOException, SQLException, ClassNotFoundException, PropertyVetoException {
        ZipFile file; //For handling zip files
        ZipEntry ent;
        Enumeration en; //For the entries in the zip file
        BufferedOutputStream out; //For the output from the zip class
        InputStream in; //for reading buffers from the zip file
        File f; //Used to get a temporary file name, not actually used for anything
        int len; //General length counter for loops
        List<String> v = new ArrayList<>(); //Stores list of unzipped file for deletion at end of program

        //Unzip zip file, via info from
        //http://www.devx.com/getHelpOn/10MinuteSolution/20447

        //Open the zip file that holds the OO.Org Base file
        file = new ZipFile(filePath);

        //Create a generic temp file. I only need to get the filename from
        //the tempfile to prefix the extracted files for OO Base
        f = File.createTempFile("ooTempDatabase", "tmp");
        f.deleteOnExit();

        //Get file entries from the zipfile and loop through all of them
        en = file.entries();
        while (en.hasMoreElements()) {
            //Get the current element
            ent = (ZipEntry) en.nextElement();

            //If the file is in the database directory, extract it to our
            //temp folder using the temp filename above as a prefix
            if (ent.getName().startsWith("database/")) {
                byte[] buffer = new byte[1024];

                //Create an input stream file the file entry
                in = file.getInputStream(ent);

                //Create a output stream to write out the entry to, using the
                //temp filename created above
                out = new BufferedOutputStream(new FileOutputStream("/tmp/" + f.getName() + "." + ent.getName().substring(9)));

                //Add the newly created temp file to the tempfile vector for deleting
                //later on
                v.add("/tmp/" + f.getName() + "." + ent.getName().substring(9));

                //Read the input file into the buffer, then write out to
                //the output file
                while ((len = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, len);
                }

                //close both the input stream and the output stream
                out.close();
                in.close();
            }
        }
        //Close the zip file since the temp files have been created
        file.close();

        //Create our JDBC connection based on the temp filename used above
        ConnectionManager connectionManager = new ConnectionManager("org.hsqldb.jdbcDriver",
                "jdbc:hsqldb:file:/tmp/" + f.getName(),
                username, password);

        ComboPooledDataSource cpds;
        cpds = connectionManager.getComboPooledDataSource();

        // todo delete the temporary files, which file names are stored in the v vector
        //  for (len = 0; len <> v.size(); len++)
        // (new File((String)v.get(len))).delete();

        return readDatabase(cpds);
    }

    private Database readDatabase(DataSource dataSource) {
        Platform platform = PlatformFactory.createNewPlatformInstance(dataSource);
        return platform.readModelFromDatabase(null);
    }
}
