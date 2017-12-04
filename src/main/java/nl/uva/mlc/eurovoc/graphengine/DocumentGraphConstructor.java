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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.EuroVocParser;
import static nl.uva.mlc.settings.Config.configFile;

/**
 *
 * @author admin
 */
public class DocumentGraphConstructor extends EuroVocParser{
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DocumentGraphConstructor.class.getName());
    private Map<String, List<String>> concept2doc = new HashMap<String, List<String>>();
    
    @Override
    public void doSomeAction(EuroVocDoc doc) {
        log.info("Doc " + doc.getId() + " has been processing");
        ArrayList<String> classes = doc.getClasses();
        for(String c1 : classes){
            if(!concept2doc.containsKey(c1))
            {
                List<String> temp = new ArrayList<>();
                concept2doc.put(c1, temp);
            }
            List ls = concept2doc.get(c1);
            ls.add(doc.getId());
            concept2doc.put(c1, ls);
        }
    }
    
    public void writeToFile(Map<String, Double> edgeList) throws FileNotFoundException, IOException
    {
        File file = new File(configFile.getProperty("DOCUMENT_GRAPH_FILE_PATH"));
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        for(Map.Entry<String, Double> ent : edgeList.entrySet())
        {
            String[] s = ent.getKey().split("--");
            bw.write(s[0] + "\t" + s[1] + "\t" + ent.getValue() + "\n");
        }
        
        bw.close();
    }
    
    public void graphMaker() throws IOException{
        fileReader(new File(configFile.getProperty("CORPUS_CON_PATH")));
        Map<String, Double> edgeList = new HashMap<String, Double>();
        for(Entry<String, List<String>> ent : concept2doc.entrySet())
        {
            for(int i = 0; i < ent.getValue().size(); i++)
                for(int j = i + 1; j < ent.getValue().size(); j++)
                {
                    if(edgeList.containsKey(ent.getValue().get(i) + "--" + ent.getValue().get(j)))
                        edgeList.put(ent.getValue().get(i) + "--" + ent.getValue().get(j), edgeList.get(ent.getValue().get(i) + "--" + ent.getValue().get(j)) + 1);
                    else if(edgeList.containsKey(ent.getValue().get(j) + "--" + ent.getValue().get(i)))
                        edgeList.put(ent.getValue().get(j) + "--" + ent.getValue().get(i), edgeList.get(ent.getValue().get(j) + "--" + ent.getValue().get(i)) + 1);
                    else
                        edgeList.put(ent.getValue().get(i) + "--" + ent.getValue().get(j), 1.);
                }
        }
        this.writeToFile(edgeList);
    }
    public static void main(String[] args) throws IOException
    {
        ConceptGraphConstructor gc = new ConceptGraphConstructor();
        gc.graphMaker();
    }

    
}
