/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.baseline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.EuroVocParser;
import static nl.uva.mlc.settings.Config.configFile;

/**
 *
 * @author admin
 */
public class dataPreParation extends EuroVocParser{

    public static BufferedWriter bw;
    
    @Override
    public void doSomeAction(EuroVocDoc doc) {
        System.out.println("Doc " + doc.getId() + " has been processing");
        String line = "";
        for(String c : doc.getClasses())
        {
            line += c + " ";
        }
        line += "#" + " " + doc.getId().replaceAll("#", "@");
        try {
            bw.write(line + "\n");
            bw.write(doc.getText() + "\n");
        } catch (IOException ex) {
            Logger.getLogger(dataPreParation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reader()
    {
        fileReader(new File("/Users/admin/Desktop/en-full-eurovoc-1.0/documents-test/"));
    }
    public static void main(String[] args) throws IOException {
        File file = new File("prepared-data.cf");
        FileWriter fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        dataPreParation dp = new dataPreParation();
        dp.reader();
        bw.close();

    }
}
