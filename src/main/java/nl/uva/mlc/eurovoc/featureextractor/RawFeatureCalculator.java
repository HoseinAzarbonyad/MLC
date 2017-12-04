/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.mlc.eurovoc.featureextractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.EuroVocParser;
import nl.uva.mlc.settings.Config;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author mosi
 */
public class RawFeatureCalculator extends EuroVocParser {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RawFeatureCalculator.class.getName());
    private Integer[] featureNumbers = null;
    private FeaturesDefinition fd = null;
    private FeatureNormalizer fn = new FeatureNormalizer();
    private Integer qId=1;

    public void setFd(FeaturesDefinition fd) {
        this.fd = fd;
    }
    
    
    /**
     * <code>featureNumbers</code>: Features numbers are defined as follow: 1-
     * Retrieval based on Language Model using Dirichlet smoothing 2- Retrieval
     * based on Language Model using Jelinek Mercer smoothing 3- Retrieval based
     * on Okapi BM25
     *
     *
     *
     */

    
    
    @Override
    public void doSomeAction(EuroVocDoc docAsQuery) {
        TreeMap<Integer, HashMap<String, Feature>> allFeature_oneQ_allD = new TreeMap<Integer, HashMap<String, Feature>>();
        for (int fnum : featureNumbers) {
            allFeature_oneQ_allD.put(fnum,this.calculateFeatures(docAsQuery,fnum));
        }
        normalizeAndWriteToFile(docAsQuery, allFeature_oneQ_allD);
        log.info("All features has been calculated for document " + docAsQuery.getId());
        this.qId++;

    }

    public HashMap<String, Feature> calculateFeatures(EuroVocDoc docAsQuery, Integer fnum) {
       HashMap<String, Feature> allFeature_oneQ_allD = new HashMap<String, Feature>();
        HashMap<String, Feature> f = null;
        List<Float> params = null;
        switch (fnum) {
            case 1:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery,"TEXT", "TEXT", "LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 2:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery,"TEXT", "TEXT", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 3:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery,"TEXT", "TEXT", "BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 4:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "TITLE", "LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 5:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "TITLE", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 6:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery,"TEXT", "TITLE","BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 7:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "DESC","LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 8:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "DESC", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 9:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "DESC","BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 10:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "UNDESC", "LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 11:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "UNDESC", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 12:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "UNDESC", "BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 13:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "CUMDESC","LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 14:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "CUMDESC", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 15:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "CUMDESC", "BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 16:
                params= Arrays.asList(2000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "CUMUNDESC", "LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 17:
                params= Arrays.asList(0.6F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "CUMUNDESC", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 18:
                params= Arrays.asList(0.75F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TEXT", "CUMUNDESC", "BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 19:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery,"TITLE", "TEXT", "LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 20:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery,"TITLE", "TEXT", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 21:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery,"TITLE", "TEXT", "BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 22:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "TITLE", "LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 23:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "TITLE", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 24:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery,"TITLE", "TITLE","BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 25:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "DESC","LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 26:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "DESC", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 27:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "DESC","BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 28:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "UNDESC", "LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 29:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "UNDESC", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 30:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "UNDESC", "BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 31:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "CUMDESC","LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 32:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "CUMDESC", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 33:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "CUMDESC", "BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 34:
                params= Arrays.asList(1000F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "CUMUNDESC", "LMD", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 35:
                params= Arrays.asList(0.2F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "CUMUNDESC", "LMJM", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 36:
                params= Arrays.asList(0.65F);
                f = fd.F_1_2_3_retrievalBased(docAsQuery, "TITLE", "CUMUNDESC", "BM25", params);
                allFeature_oneQ_allD.putAll(f);
                break;
            case 37:
                f = fd.F_4_degreeInHierarchy(docAsQuery,"p");
                allFeature_oneQ_allD.putAll(f);
                break;
            case 38:
                f = fd.F_4_degreeInHierarchy(docAsQuery,"c");
                allFeature_oneQ_allD.putAll(f);
                break;
            case 39:
                f = fd.F_5_docNum(docAsQuery);
                allFeature_oneQ_allD.putAll(f);
                break;

            default:
                log.info("Not valid feature number: " + fnum);
        }
        return allFeature_oneQ_allD;
        
    }

    public void conceptBaseFeatureCalc() {
        
       try {
            File resDir = new File(configFile.getProperty("FEATURE_K-FOLD_PATH"));
            if (resDir.exists()) {
                    FileUtils.deleteDirectory(resDir);
                    log.info("Deletting the existing directory on: " + configFile.getProperty("FEATURE_K-FOLD_PATH"));
            }
            resDir.mkdirs();
            File file = new File(Config.configFile.getProperty("FEATURE_K-FOLD_PATH")+"/all_folds.txt");
            file.createNewFile();
            this.featureNumbers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
                                                22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39};
            String queriesPath = configFile.getProperty("CORPUS_Eval_PATH");
            IndexReader ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("CONCEPT_INDEX_PATH"))));
            this.fd = new FeaturesDefinition(ireader);
            fileReader(new File(queriesPath));
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public void docBaseFeatureCalc() {
        try {
            featureNumbers = new Integer[]{1, 2, 3};
            String queriesPath = configFile.getProperty("CORPUS_Eval_PATH");
            IndexReader ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))));
            this.fd = new FeaturesDefinition(ireader);
            fileReader(new File(queriesPath));
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    private void normalizeAndWriteToFile(EuroVocDoc docAsQuery, TreeMap<Integer, HashMap<String, Feature>> allFeature_oneQ_allD) {
        HashMap<String, TreeMap<Integer,Feature>> docs = new HashMap<>();
        TreeMap<Integer, HashMap<String, Feature>> allFeature_oneQ_allD_Normalized = this.fn.oneQueryNormalizer(allFeature_oneQ_allD);
        for(Map.Entry<Integer, HashMap<String, Feature>> ent: allFeature_oneQ_allD_Normalized.entrySet()){
            Integer fnum = ent.getKey();
            for(Map.Entry<String, Feature> ent2: ent.getValue().entrySet()){
                TreeMap<Integer,Feature> fs = docs.get(ent2.getKey());
                if(fs==null)
                    fs = new TreeMap<Integer,Feature>();
                fs.put(fnum, ent2.getValue());
                docs.put(ent2.getKey(), fs);
            }
        }
        try{
        PrintWriter pw = new PrintWriter(new FileWriter(Config.configFile.getProperty("FEATURE_K-FOLD_PATH")+"/all_folds.txt",true));
        for(Map.Entry<String, TreeMap<Integer,Feature>> ent: docs.entrySet()){

                String lbl = (docAsQuery.getClasses().contains(ent.getKey()))? "1" : "0";
                String docId = ent.getKey();
                String qName = docAsQuery.getId();
                String tmpLine = "";
                for(int fnum:this.featureNumbers){
                    if(!ent.getValue().containsKey(fnum)){
                        ent.getValue().put(fnum, new Feature(qName, 0D, qName, docId, lbl));
                    }
                }
                for(Map.Entry<Integer,Feature> ent2: ent.getValue().entrySet()){
                                tmpLine += ent2.getKey() + ":" + ent2.getValue().getfValue().toString() + " ";
                }
                String line =  lbl+ " "
                            + "qid:" + qId + " "
                            + tmpLine 
                            + "# "
                            + qName + " "
                            + docId
                            +"\n";
                pw.write(line);
            }
        pw.close();
        }catch(IOException ex){
            log.error(ex);
        }    }
     public void main(){
        RawFeatureCalculator rfc = new RawFeatureCalculator();
        rfc.conceptBaseFeatureCalc();
    }
}
