/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author mosi
 */
public class Utility {
        public static String readFileAsString(String filePath) throws IOException{
        return readFileAsString(new File(filePath));
    }
    
    public static String readFileAsString(File file) throws FileNotFoundException, IOException{
        BufferedInputStream f = null;
        String str = "";
            byte[] buffer = new byte[(int) file.length()];
            f = new BufferedInputStream(new FileInputStream(file));
            f.read(buffer);
            str = new String(buffer);
            f.close();
        return str;
    }
    
}
