/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.graphengine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.mlc.eurovoc.featureextractor.FeaturePropagator;
import nl.uva.mlc.settings.Config;

/**
 *
 * @author admin
 */
public class GraphStat {
    
    public static Map<String, Double> propagationGraph = new HashMap<String, Double>();
    public static Map<String, Map<String, Double>> conceptGraph = new HashMap<String, Map<String, Double>>();
    
    public GraphStat(String graphPath){
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(graphPath)));
            String str = "";
            while ((str = br.readLine()) != null) {
                String[] parts = str.split("\t");
                if (conceptGraph.containsKey(parts[0])) {
                    Map<String, Double> temp = conceptGraph.get(parts[0]);
                    temp.put(parts[1], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[0], temp);
                } else {
                    Map<String, Double> temp = new HashMap<>();
                    temp.put(parts[1], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[0], temp);
                }
                if (conceptGraph.containsKey(parts[1])) {
                    Map<String, Double> temp = conceptGraph.get(parts[1]);
                    temp.put(parts[0], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[1], temp);
                } else {
                    Map<String, Double> temp = new HashMap<>();
                    temp.put(parts[0], Double.valueOf(parts[2]));
                    conceptGraph.put(parts[1], temp);
                }

            }

        
        for(Map.Entry<String, Map<String, Double>> ent : conceptGraph.entrySet())
            {
                double sum = 0;
                for(Map.Entry<String, Double> ent2 : ent.getValue().entrySet())
                    sum += ent2.getValue();
                propagationGraph.put(ent.getKey(), sum);
            }
        }catch (FileNotFoundException ex) {} catch (IOException ex) {
            Logger.getLogger(GraphStat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public static void main(String[] args) throws IOException {
        String graphFilePath = Config.configFile.getProperty("CONCEPT_GRAPH_FILE_PATH");
        String outProbMatrix = Config.configFile.getProperty("PROB_GRAPH_FILE_PATH");
        String outProbEdges = Config.configFile.getProperty("PROB_GRAPH_EDGES_FILE_PATH");
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outProbMatrix)));
        BufferedWriter edges = new BufferedWriter(new FileWriter(new File(outProbEdges)));
        GraphStat gs = new GraphStat(graphFilePath);
        for(Map.Entry<String, Map<String, Double>> ent : conceptGraph.entrySet()){
            ArrayList<Double> temp = new ArrayList<>();
            for(Map.Entry<String, Double> ent2 : ent.getValue().entrySet()){
                double prob = ent2.getValue() / propagationGraph.get(ent.getKey());
                temp.add(prob);
                edges.write(ent.getKey() + "\t" + ent2.getKey() + "\t" + prob + "\n");
            }
            
            Collections.sort(temp, Collections.reverseOrder());
            
            String line = ent.getKey() + " ";
            for(Double d : temp)
                line += d + " ";
            bw.write(line + "\n");
        }
        
        bw.close();
        edges.close();;
        
    }
}
