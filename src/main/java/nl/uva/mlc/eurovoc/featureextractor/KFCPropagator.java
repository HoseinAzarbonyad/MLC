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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.mlc.settings.Config;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author admin
 */
public class KFCPropagator {
    
    private String KFoldPath = "";
    private int k;
    public FeaturePropagator fp = null;
    private Integer[] itNums = {1, 2, 3, 5};
    private Double[] lambdas = {0.0,0.6,0.7,0.8,0.9,0.92,0.95,0.97};
    public void setKFoldPath(String KFoldPath) {
        this.KFoldPath = KFoldPath;
    }

    public void setK(int k) {
        this.k = k;
    }
    
    public void propagator()
    {
        String graphFilePath = Config.configFile.getProperty("CONCEPT_GRAPH_FILE_PATH");
        this.fp = new FeaturePropagator(graphFilePath);
        Map<String, HashMap<String, Double>> scores = new HashMap<>();
        for(int i = 1; i < k+1; i++)
        {
            try {
                    File file2 = new File(KFoldPath + "/fold" + i + "/judg.txt");
                    if(file2.exists())
                    	FileUtils.forceDelete(file2);
                    file2.createNewFile();
	            FileWriter fw2;
                    fw2 = new FileWriter(file2);
                    BufferedWriter judg = new BufferedWriter(fw2);

                    BufferedReader testFile = new BufferedReader(new FileReader(new File(KFoldPath + "/fold" + i + "/test.txt")));
                    BufferedReader scoresFile = new BufferedReader(new FileReader(new File(KFoldPath + "/fold" + i + "/scores.txt")));

                    String str, str2;
                    while((str = testFile.readLine()) != null)
                    {
                        String[] parts = str.split(" # ")[1].split(" ");
                        if(str.split(" ")[0].equals("1"))
                            judg.write(parts[0] + " 0 " + parts[1] + " 1" + "\n");
                        str2 = scoresFile.readLine().split("	")[2];
                        //str2 = scoresFile.readLine();
                        if(scores.containsKey(parts[0]))
                            scores.get(parts[0]).put(parts[1], Double.valueOf(str2));
                        else
                        {
                            HashMap<String, Double> temp = new HashMap<>();
                            temp.put(parts[1], Double.valueOf(str2));
                            scores.put(parts[0], temp);
                        }
                    }
      	            judg.close();
           for(int itr : itNums)
              for(double lambda : lambdas)
                 {
                    this.fp.lambda = lambda;
                    this.fp.numIteration = itr;
                    File f = new File(KFoldPath + "/fold" + i + "/anal-modified");
                    if(f.exists())
                       f.delete();
                    else
                       FileUtils.forceMkdir(f);
                    File file = new File(KFoldPath + "/fold" + i + "/anal-modified/propagatedRes_" + "lambda" + lambda + "_itrNum" + itr + ".txt");
                    if(file.exists())
                       FileUtils.forceDelete(file);
                    file.createNewFile();

                    FileWriter fw;
                    fw = new FileWriter(file);
                    BufferedWriter propRes = new BufferedWriter(fw);              
                    for(Entry<String, HashMap<String, Double>> ent : scores.entrySet())
                    {
                        /*for(int itr : itNums)
                            for(double lambda : lambdas)
                            {
                                this.fp.lambda = lambda;
                                this.fp.numIteration = itr;
                                File f = new File(KFoldPath + "/fold" + i + "/anal");
                                if(f.exists())
                                    f.delete();
                                else
                                */    FileUtils.forceMkdir(f);
                                
                                HashMap<String, Double> propScores = fp.finalScorePropagator(ent.getValue());
                                MyValueComparator bvc = new MyValueComparator(propScores);
                                TreeMap<String, Double> sorted_map = new TreeMap<>(bvc);
                                sorted_map.putAll(propScores);
                                int rank = 1;
                                for(Entry<String, Double> ent2 : propScores.entrySet())
                                {
                                    String line = ent.getKey() + " 0 " + ent2.getKey() + " " + rank++ + " " + ent2.getValue();
                                    propRes.write(line + " RUN" + "\n");
                                }
				//System.out.println("Fold" + i + " lambda:" + lambda + "iteration:" + itr + " is finished");
                     }
		     System.out.println("Fold" + i + " lambda:" + lambda + " iteration:" + itr + " is finished");
		     propRes.close();
                 }
                
            } catch (IOException ex) {
                Logger.getLogger(KFCPropagator.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}

class MyValueComparator implements Comparator<String> {

    Map<String, Double> base;

    public MyValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
