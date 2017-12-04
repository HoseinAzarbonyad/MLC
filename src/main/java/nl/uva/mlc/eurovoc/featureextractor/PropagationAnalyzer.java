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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.mlc.eurovoc.EuroVocDoc;
import nl.uva.mlc.eurovoc.EuroVocParser;
import nl.uva.mlc.settings.Config;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mosi
 */
public class PropagationAnalyzer extends EuroVocParser {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PropagationAnalyzer.class.getName());
    private Integer fNum = 22;
    private Integer[] itNums = {};
    private Double[] lambdas = {};

    private String queriesPath = null;
    private IndexReader ireader = null;
    private FeaturesDefinition fd = null;
    private RawFeatureCalculator rfc = null;
    private FeatureNormalizer fn = null;
    private FeaturePropagator fp = null;

    public PropagationAnalyzer() {
        String graphFilePath = Config.configFile.getProperty("CONCEPT_GRAPH_FILE_PATH");
        this.queriesPath = configFile.getProperty("CORPUS_Eval_PATH");
        try {
            this.ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("CONCEPT_INDEX_PATH"))));
        } catch (IOException ex) {
            log.error(ex);
        }
        this.fd = new FeaturesDefinition(ireader);
        this.rfc = new RawFeatureCalculator();
        this.rfc.setFd(this.fd);
        this.fp = new FeaturePropagator(graphFilePath);
        this.fn = new FeatureNormalizer();

        //Cleaning
        File resDir = new File(configFile.getProperty("ANALYSIS_PATH"));
        if (resDir.exists()) {
            try {
                FileUtils.deleteDirectory(resDir);
                log.info("Deletting the existing directory on: " + configFile.getProperty("ANALYSIS_PATH"));
            } catch (IOException ex) {
                log.error(ex);
            }
        }
        resDir.mkdirs();
        File f = new File(Config.configFile.getProperty("ANALYSIS_PATH")
                + "/all_folds_F-" + this.fNum + ".txt");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            log.error(ex);
        }
        for (int itNum : itNums) {
            for (double lambda : lambdas) {
                String fileName = Config.configFile.getProperty("ANALYSIS_PATH")
                        + "/all_folds_F-" + this.fNum + "_Lmbda-" + lambda
                        + "_itNum-" + itNum + ".txt";
                f = new File(fileName);
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    log.error(ex);
                }
            }
        }

    }

    @Override
    public void doSomeAction(EuroVocDoc docAsQuery) {
        addQueryToJudgmentFile(docAsQuery);
        HashMap<String, Feature> oneQ_allD = new HashMap<String, Feature>();
        oneQ_allD = rfc.calculateFeatures(docAsQuery, this.fNum);
        HashMap<String, Feature> oneQ_allD_Normalized = this.fn.normalize(oneQ_allD);
        String raw_fileName = Config.configFile.getProperty("ANALYSIS_PATH")
                + "/all_folds_F-" + this.fNum + ".txt";
        addQueryToResultsFile(docAsQuery, oneQ_allD_Normalized, raw_fileName);

        for (int itNum : itNums) {
            for (double lambda : lambdas) {
                String fileName = Config.configFile.getProperty("ANALYSIS_PATH")
                        + "/all_folds_F-" + this.fNum + "_Lmbda-" + lambda
                        + "_itNum-" + itNum + ".txt";
                fp.setLambda(lambda);
                fp.setNumIteration(itNum);
                HashMap<String, Feature> oneQ_allD_propagated = this.fp.propagator(oneQ_allD_Normalized);
                addQueryToResultsFile(docAsQuery, oneQ_allD_propagated, fileName);
            }
        }
        log.info("All cases has been calculated for document " + docAsQuery.getId());
    }

    private void addQueryToResultsFile(EuroVocDoc docAsQ, HashMap<String, Feature> oneQ_allD, String fileName) {
        ValueComparator bvc = new ValueComparator(oneQ_allD);
        TreeMap<String, Feature> sorted_map = new TreeMap<String, Feature>(bvc);
        sorted_map.putAll(oneQ_allD);
        int rank =1;
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(fileName, true));
            for (Entry<String, Feature> ent : sorted_map.entrySet()) {
                String line = docAsQ.getId() + " 0 " + ent.getKey() + " " + rank++ + " " + ent.getValue().getfValue();
                pw.write(line + " :D\n");
            }
            pw.close();
        } catch (IOException ex) {
            Logger.getLogger(PropagationAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addQueryToJudgmentFile(EuroVocDoc docAsQuery) {
        PrintWriter pw = null;
        try {
            String jugeFileName = Config.configFile.getProperty("ANALYSIS_PATH") + "/judg.txt";
            pw = new PrintWriter(new FileWriter(jugeFileName, true));
            for (String classlbl : docAsQuery.getClasses()) {
                String line = docAsQuery.getId() + " 0 " + classlbl + " 1";
                pw.write(line + "\n");
            }
            pw.close();
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public void main() {
        this.fileReader(new File(queriesPath));
    }

    public static void main(String[] args) {
        PropagationAnalyzer pa = new PropagationAnalyzer();
        pa.main();
    }

}

class ValueComparator implements Comparator<String> {

    Map<String, Feature> base;

    public ValueComparator(Map<String, Feature> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a).getfValue() >= base.get(b).getfValue()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
