/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.graphengine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.EuroVocParser;
import static nl.uva.mlc.settings.Config.configFile;
/**
 *
 * @author Mosi
 */
public class ConceptGraphConstructor extends EuroVocParser{
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ConceptGraphConstructor.class.getName());
    private Map<String, Double> edgeList = new HashMap<String, Double>();
    @Override
    public void doSomeAction(EuroVocDoc doc) {
        conceptDocStat(doc);
    }
    
    public void processDoc(EuroVocDoc doc)
    {
        log.info("Doc " + doc.getId() + " has been processing");
        ArrayList<String> classes = doc.getClasses();
        for(String c1 : classes)
            for(String c2 : classes)
                if(!c1.equals(c2))
                {
                    if(edgeList.containsKey(c1 + "-" + c2))
                    {
                        edgeList.put(c1 + "-" + c2, edgeList.get(c1 + "-" + c2) + 1);
                    }
                    else if(edgeList.containsKey(c2 + "-" + c1))
                    {
                        edgeList.put(c2 + "-" + c1, edgeList.get(c2 + "-" + c1) + 1);
                    }
                    else{
                        edgeList.put(c1 + "-" + c2, 1.);
                    }
                    
                }

    }
    public void conceptDocStat(EuroVocDoc doc)
    {
        log.info("Doc " + doc.getId() + " has been processing");
        ArrayList<String> classes = doc.getClasses();
        for(String c1 : classes){
            if(edgeList.containsKey(c1))
                edgeList.put(c1, edgeList.get(c1) + 1);
            else
                edgeList.put(c1, 1.);
        }
        

    }
    public void writeToFile() throws FileNotFoundException, IOException
    {
        File file = new File(configFile.getProperty("CONCEPT_GRAPH_FILE_PATH"));
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for(Entry<String, Double> ent : edgeList.entrySet())
        {
            String[] s = ent.getKey().split("-");
            bw.write(s[0] + "\t" + s[1] + "\t" + ent.getValue() + "\n");
        }
        
        bw.close();
    }

    public void writeToFileStat() throws FileNotFoundException, IOException
    {
        File file = new File(configFile.getProperty("CONCEPT_GRAPH_STAT_FILE_PATH"));
        FileWriter fw = new FileWriter(file);
        System.out.println("Num of concepts: " + edgeList.size());
        BufferedWriter bw = new BufferedWriter(fw);
        for(Entry<String, Double> ent : edgeList.entrySet())
        {
            bw.write(ent.getKey() + "\t" + ent.getValue() + "\n");
        }
        
        bw.close();
    }

    public void graphMaker() throws IOException{
        fileReader(new File(configFile.getProperty("CORPUS_CON_PATH")));
        this.writeToFile();
        this.writeToFileStat();
    }
    public static void main(String[] args) throws IOException
    {
        ConceptGraphConstructor gc = new ConceptGraphConstructor();
        gc.graphMaker();
    }
    
}
