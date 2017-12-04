/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.featureextractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author admin
 */
public class FinalFoldCreator {
    
    private int k = 5;
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FinalFoldCreator.class.getName());
    public void setK(int k) {
        this.k = k;
    }
    
    public void FoldCreator(String inDocsFileNameDir, String InputFeatureFile, String outDir)
    {
        try {
            HashMap<String, ArrayList<String>> fileToFold = new HashMap<>();
            for(int i = 0; i < k; i++)
            {
                BufferedReader bf = new BufferedReader(new FileReader(new File(inDocsFileNameDir + "fold" + i + "\testDocs.txt")));
                String str;
                while((str = bf.readLine()) != null)
                {
                    if(fileToFold.containsKey(i + "")){
                        ArrayList<String> temp = fileToFold.get(i + "");
                        temp.add(str.split("\\.")[0].replaceAll("#", "@"));
                        fileToFold.put(i + "", temp);
                    }
                    else{
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(str.split("\\.")[0].replaceAll("#", "@"));
                        fileToFold.put(i + "", temp);

                    }
                }
            }
            File out = new File(outDir);
            FileUtils.forceMkdir(out);
            
            for(int i = 0; i < k; i++)
            {
                File fold = new File(outDir + "/fold" + i);
                FileUtils.forceMkdir(fold);
                File file = new File(outDir + "/fold" + i + "/test.txt");
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                File foldTrain = new File(outDir + "/fold" + i + "/train.txt");
                FileWriter fwTrain = new FileWriter(foldTrain);
                BufferedWriter bwTrain = new BufferedWriter(fwTrain);
                
                BufferedReader br = new BufferedReader(new FileReader(new File(InputFeatureFile)));
                
                String s;
                while((s = br.readLine()) != null)
                    {
                        String docId = s.split(" ")[42];
                        if(fileToFold.get(i + "").contains(docId))
                            bw.write(s + "\n");
                        else
                            bwTrain.write(s + "\n");
                    }
                
                bw.close();
                bwTrain.close();
                
            }
        } catch (IOException ex) {
            log.error(ex);
        }

    }
    
}
