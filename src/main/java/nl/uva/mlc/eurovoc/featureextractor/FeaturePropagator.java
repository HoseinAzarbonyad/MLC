/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.mlc.eurovoc.featureextractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;
import nl.uva.mlc.settings.Config;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Mostafa Dehgani
 */
public class FeaturePropagator {
    
    HashSet<Integer> propagationBlackList = null;
    Integer numIteration = Integer.parseInt(Config.configFile.getProperty("ITERATION_NUM"));
    Double lambda = Double.parseDouble(Config.configFile.getProperty("LAMBDA"));
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FeaturePropagator.class.getName());
    public Map<String, Map<String, Double>> conceptGraph = new HashMap<String, Map<String, Double>>();
    public Map<String, Double> propagationGraph = new HashMap<String, Double>();
    private TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> features;

    public void setNumIteration(Integer numIteration) {
        this.numIteration = numIteration;
    }

    public void setLambda(Double lambda) {
        this.lambda = lambda;
    }

    
    public FeaturePropagator(){
        features = new TreeMap<Integer, HashMap<String, HashMap<String, Feature>>>();   
     }
    
    public FeaturePropagator(String graphPath) {
        features = new TreeMap<Integer, HashMap<String, HashMap<String, Feature>>>();
        try {
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
            
            for(Entry<String, Map<String, Double>> ent : conceptGraph.entrySet())
            {
                double sum = 0;
                for(Entry<String, Double> ent2 : ent.getValue().entrySet())
                    sum += ent2.getValue();
                propagationGraph.put(ent.getKey(), sum);
            }
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }
        
        //load blacklist
        this.propagationBlackList = new HashSet<Integer>();
        for(String s: Config.configFile.getProperty("PROPAGATION_BLACKLIST").split(",")){
            this.propagationBlackList.add(Integer.parseInt(s));
        }
    }

     public HashMap<String, Feature> propagator(HashMap<String, Feature> features) {
        int itr = 0;
        FeatureNormalizer fn = new FeatureNormalizer();
        HashMap<String, Feature> oldValues = features;
        //HashMap<String, Feature> newValues = new HashMap<>();
        while (itr < numIteration) {
            HashMap<String, Feature> newValues = new HashMap<>();
            for (Entry<String, Feature> ent : oldValues.entrySet()) {
                double newValue = lambda * ent.getValue().getfValue();
                for (Entry<String, Double> ent2 : conceptGraph.get(ent.getKey()).entrySet()) {
                    if (oldValues.containsKey(ent2.getKey())) {
                        newValue += (1 - lambda) * oldValues.get(ent2.getKey()).getfValue() * (ent2.getValue() / propagationGraph.get(ent2.getKey()));
                    }
                }
                Feature f = new Feature(ent.getValue().getfName(), newValue, ent.getValue().getqId(), ent.getValue().getdId(), ent.getValue().getLabel());
                f.setfValue(newValue);
                newValues.put(ent.getKey(), f);
            }
            oldValues = fn.normalize(newValues);
            itr++;
        }
        return oldValues;
    }

     
    public HashMap<String, Double> finalScorePropagator(HashMap<String, Double> scores) {
        int itr = 0;
        FeatureNormalizer fn = new FeatureNormalizer();
        HashMap<String, Double> oldValues = scores;
	//HashMap<String, Double> normOldValues = fn.finalScoreNormalize(oldValues);
        //HashMap<String, Feature> newValues = new HashMap<>();
        while (itr < numIteration) {
            HashMap<String, Double> newValues = new HashMap<>();
            for (Entry<String, Double> ent : oldValues.entrySet()) {
		//////////
                double newValue = lambda * ent.getValue();
                //double newValue = lambda * normOldValues.get(ent.getKey());
		//////////
                for (Entry<String, Double> ent2 : conceptGraph.get(ent.getKey()).entrySet()) {
                    if (oldValues.containsKey(ent2.getKey())) {
                        newValue += (1 - lambda) * oldValues.get(ent2.getKey()) * (ent2.getValue() / propagationGraph.get(ent2.getKey()));
                    }
                }
                //Feature f = new Feature(ent.getValue().getfName(), newValue, ent.getValue().getqId(), ent.getValue().getdId(), ent.getValue().getLabel());
                //f.setfValue(newValue);
                newValues.put(ent.getKey(), newValue);
            }
            oldValues = fn.finalScoreNormalize(newValues);
            itr++;
        }
        return oldValues;
    }
 
    public TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> propagateAndConcatFeatures(TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> RawFeatures) {
        FeatureNormalizer fn = new FeatureNormalizer();
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> tempRawFeatures = new TreeMap<>();
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> FinaltempRawFeatures = new TreeMap<>();
        int fnumber = RawFeatures.size();
        for (Entry<Integer, HashMap<String, HashMap<String, Feature>>> ent : RawFeatures.entrySet()) {
            if(this.propagationBlackList.contains(ent.getKey()))
                continue;
            HashMap<String, HashMap<String, Feature>> propagatedFeature = new HashMap<>();
            for (Entry<String, HashMap<String, Feature>> ent2 : ent.getValue().entrySet()) {
                HashMap<String, Feature> proFeatures = this.propagator(ent2.getValue());
                propagatedFeature.put(ent2.getKey(), proFeatures);
            }
            tempRawFeatures.put(++fnumber, propagatedFeature);
        }

        FinaltempRawFeatures = fn.allQueryNormalizer(tempRawFeatures);
        TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> FinalFeatures = new TreeMap<>();
        for (Entry<Integer, HashMap<String, HashMap<String, Feature>>> ent : RawFeatures.entrySet()) {
            FinalFeatures.put(ent.getKey(), ent.getValue());
        }
        for (Entry<Integer, HashMap<String, HashMap<String, Feature>>> ent : FinaltempRawFeatures.entrySet()) {
            FinalFeatures.put(ent.getKey(), ent.getValue());
        }

        return FinalFeatures;
    }

    public void readRawFeaturesAndPropagation() {
        BufferedReader br = null;
        //
        try {
            File resDir = new File(configFile.getProperty("FEATURE_PROPAGATED_K-FOLD_PATH"));
            if (resDir.exists()) {
                    FileUtils.deleteDirectory(resDir);
                    log.info("Deletting the existing directory on: " + configFile.getProperty("FEATURE_PROPAGATED_K-FOLD_PATH"));
            }
            resDir.mkdirs();
            File file = new File(Config.configFile.getProperty("FEATURE_PROPAGATED_K-FOLD_PATH")+"/all_folds.txt");
            file.createNewFile();
            //
            TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> feature = new TreeMap<>();
            String Kfold_path = Config.configFile.getProperty("FEATURE_K-FOLD_PATH") + "/all_folds.txt";
            br = new BufferedReader(new FileReader(new File(Kfold_path)));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineParts1 = line.split(" # ");
                String[] lineParts2 = lineParts1[0].split("\\s+");
                String[] lineParts3 = lineParts1[1].split("\\s+");
                for (int i = 2; i < lineParts2.length ; i++) {
                    Integer fNum = Integer.parseInt(lineParts2[i].split(":")[0]);
                    Double fVal = Double.parseDouble(lineParts2[i].split(":")[1]);
                    String qId = lineParts2[1].split(":")[1];
                    String qName = lineParts3[0];
                    String dId = lineParts3[1];
                    Feature f = new Feature(fNum.toString(), fVal, qName, dId, lineParts2[0]);
                    HashMap<String, HashMap<String, Feature>> allq_AllD_oneF = feature.get(fNum);
                    HashMap<String, Feature> oneQ_allD = null;
                    if (allq_AllD_oneF == null) {
                        allq_AllD_oneF = new HashMap<String, HashMap<String, Feature>>();
                        oneQ_allD = new HashMap<String, Feature>();
                    } else {
                        oneQ_allD = allq_AllD_oneF.get(qId);
                        if (oneQ_allD == null){
                            oneQ_allD = new HashMap<String, Feature>();
                            //
//                            this.addFeatures(this.propagateAndConcatFeatures(feature));
                            this.addFeature2File(this.propagateAndConcatFeatures(feature));
                            log.info("All features of query " + qId + " are propagated...");
                            feature = new TreeMap<>();
                            allq_AllD_oneF = new HashMap<>();
                            //
                        }
                    }
                    oneQ_allD.put(dId, f);
                    allq_AllD_oneF.put(qId, oneQ_allD);
                    feature.put(fNum, allq_AllD_oneF);
                }
            }
            this.addFeature2File(this.propagateAndConcatFeatures(feature));
            log.info("Feature propagation is completed successfully for all queries");
            feature = new TreeMap<>();
            //
            br.close();
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }        
    }

    private void addFeature2File(TreeMap<Integer, HashMap<String, HashMap<String, Feature>>> f){
        TreeMap<String,HashMap<String, TreeMap<Integer,Feature>>> lines = new TreeMap<>();
        Set<String> qIds =  f.firstEntry().getValue().keySet();
        
        for(String q: qIds){
            HashMap<String, TreeMap<Integer,Feature>> docs = new HashMap<>();
            for(Map.Entry<Integer, HashMap<String, HashMap<String, Feature>>> ent: f.entrySet()){
                Integer fnum = ent.getKey();
                for(Entry<String, Feature> ent2: ent.getValue().get(q).entrySet()){
                    TreeMap<Integer,Feature> fs = docs.get(ent2.getKey());
                    if(fs==null)
                        fs = new TreeMap<>();
                    fs.put(fnum, ent2.getValue());
                    docs.put(ent2.getKey(), fs);
                }
            }
            lines.put(q, docs);
        }
        try{
        PrintWriter pw = new PrintWriter(new FileWriter(Config.configFile.getProperty("FEATURE_PROPAGATED_K-FOLD_PATH")+"/all_folds.txt",true));
        for(Entry<String,HashMap<String, TreeMap<Integer,Feature>>> ent: lines.entrySet()){
            for(Entry<String, TreeMap<Integer,Feature>> ent2: ent.getValue().entrySet()){
                String lbl = "";
                String docId = "";
                String qName = "";
                String tmpLine = "";
                for(Entry<Integer,Feature> ent3: ent2.getValue().entrySet()){
                                lbl = ent3.getValue().getLabel();
                                docId = ent3.getValue().getdId();
                                qName = ent3.getValue().getqId();
                                tmpLine += ent3.getKey() + ":" + ent3.getValue().getfValue().toString() + " ";
                }
                String line =  lbl+ " "
                            + "qid:" + ent.getKey() + " "
                            + tmpLine 
                            + "# "
                            + qName + " "
                            + docId
                            +"\n";
                pw.write(line);
            }
        }
        pw.close();
        }catch(IOException ex){
            log.error(ex);
        }
    }
    public void main() {
        String graphFilePath = Config.configFile.getProperty("CONCEPT_GRAPH_FILE_PATH");
        FeaturePropagator fp = new FeaturePropagator(graphFilePath);
        fp.readRawFeaturesAndPropagation();
        
    }
}
