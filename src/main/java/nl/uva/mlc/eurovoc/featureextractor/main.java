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
import java.util.Map;
//import nl.uva.mlc.eurovoc.eurovocdoc;

import nl.uva.mlc.settings.Config;
import org.apache.commons.io.FileUtils;
import nl.uva.mlc.learning.Test;
/**
 *
 * @author mosi
 */
public class main {
     public static void FoldCreator(String inDocsFileNameDir, String InputFeatureFile, String outDir)
    {
        try {
            HashMap<String, ArrayList<String>> fileToFold = new HashMap<>();
            for(int i = 0; i < 5; i++)
            {
                BufferedReader bf = new BufferedReader(new FileReader(new File(inDocsFileNameDir + "/fold" + i + "/testDocs.txt")));
                String str;
                while((str = bf.readLine()) != null)
                {
                    if(fileToFold.containsKey(i + "")){
                        ArrayList<String> temp = fileToFold.get(i + "");
                        temp.add(str.split("\\.")[0]);
                        fileToFold.put(i + "", temp);
                    }
                    else{
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(str.split("\\.")[0]);
                        fileToFold.put(i + "", temp);

                    }
                }
            }
            File out = new File(outDir);
            FileUtils.forceMkdir(out);
            
            for(int i = 0; i < 5; i++)
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
		int n = 0; 
                while((s = br.readLine()) != null)
                    {
                        String docId = s.split(" # ")[1].split(" ")[0];
                        if(fileToFold.get(i + "").contains(docId))
                            bw.write(s + "\n");
                        else
                            bwTrain.write(s + "\n");
			if(n % 10000 == 0)
			   System.out.println(i + "  " + n);
			n++;
                    }
                
                bw.close();
                bwTrain.close();
                
            }
        } catch (IOException ex) {
           	System.err.println(ex);
		 //log.error(ex);
        }

    }

    public static void main(String[] args) throws IOException {
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        rfc.main();
        FeaturePropagator fp = new FeaturePropagator();
        fp.main();
        FoldCreator(Config.configFile.getProperty("K_Fold_DOCSNAMES"), 
                "path/to/all/feature/vectors/all_folds.txt", "path/to/K/folds"); 
        PropagationAnalyzer pa = new PropagationAnalyzer();
        pa.main();
        KFCPropagator kfcp = new KFCPropagator();
        kfcp.setKFoldPath("/zfs/ilps-plex1/slurm/datastore/hazarbo1/data/AllRunFolds");
        kfcp.setK(5);
        kfcp.propagator();
 }
    
}
