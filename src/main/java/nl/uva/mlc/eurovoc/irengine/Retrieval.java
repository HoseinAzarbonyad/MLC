/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.irengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import nl.uva.lucenefacility.IndexInfo;
import nl.uva.lucenefacility.MyAnalyzer;
import nl.uva.mlc.eurovoc.featureextractor.Feature;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author mosi
 */
public class Retrieval {
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Retrieval.class.getName());
    private IndexReader ireader = null;
    private String SimFName= null;
    private Similarity simFunction = null;
    private String field = null;
    private final Boolean stemming = Boolean.valueOf(configFile.getProperty("IF_STEMMING"));
    private final Boolean commonWordsRemoving = Boolean.valueOf(configFile.getProperty("IF_STOPWORD_REMOVING"));
    private MyAnalyzer myAnalyzer = null;
    private ArrayList<String> commonWs = null;
    private TreeMap<Integer, String> indexId_docID_Map = null;
    private List<Float> params = null;

    public void setIreader(IndexReader ireader) {
        this.ireader = ireader;
    }

    public void setIndexId_docID_Map(TreeMap<Integer, String> docsMap) {
        this.indexId_docID_Map = docsMap;
    }
    

    public void setSimFName(String SimFName) {
        this.SimFName = SimFName;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setParams(List<Float> params) {
    // param fo LMDirichlet: mu
    // param fo LMJelinekMercer: lambda
    // param fo LMJelinekMercer: k1,b
        this.params = params;
        this.SIM_FUNCS = new Similarity[]{new LMDirichletSimilarity(params.get(0)),
                                  new LMJelinekMercerSimilarity(params.get(0)),
                                  new BM25Similarity(Float.parseFloat(configFile.getProperty("PARAMETERS_BM25_K1")),params.get(0))
                                  };
    }
    
    
    
    private enum SimilarityFunction {
            LMD, // for LMDirichletSimilarity
            LMJM, //for LMJelinekMercerSimilarity
            BM25 // for Okapi-BM25Similarity            
        }
    
    private Similarity[] SIM_FUNCS;

    public Retrieval(){
            if(commonWordsRemoving){
                myAnalyzer = new MyAnalyzer(stemming, this.getCommonWords());
            } else {
                myAnalyzer = new MyAnalyzer(stemming);
            }
            
            this.SIM_FUNCS = new Similarity[]{new LMDirichletSimilarity(Float.parseFloat(configFile.getProperty("PARAMETERS_LM_DIRICHLET_MU"))),
                                             new LMJelinekMercerSimilarity(Float.parseFloat(configFile.getProperty("PARAMETERS_LM_JM_LAMBDA"))),
                                             new BM25Similarity(Float.parseFloat(configFile.getProperty("PARAMETERS_BM25_K1")), 
                                                         Float.parseFloat(configFile.getProperty("PARAMETERS_BM25_b")))
                                             };
            
    }

    public HashMap<String,Feature> searchAndReturnResults(String queryText, String qId) throws IOException, ParseException {
        Analyzer analyzer = myAnalyzer.getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
        QueryParser qParser = new QueryParser(Version.LUCENE_CURRENT, field ,analyzer);
            BooleanQuery.setMaxClauseCount(queryText.split("\\s+").length);
            Query q = qParser.parse(QueryParser.escape(queryText));
            this.simFunction = SIM_FUNCS[SimilarityFunction.valueOf(SimFName).ordinal()];
            Similarity simFunc = this.simFunction;
            IndexSearcher isearcher = new IndexSearcher(this.ireader);
            isearcher.setSimilarity(simFunc);
            TopFieldCollector tfc = TopFieldCollector.create(Sort.RELEVANCE,ireader.numDocs(), true, true, true, false);
            isearcher.search(q, tfc);
            TopDocs results = tfc.topDocs();
            ScoreDoc[] hits = results.scoreDocs;
            return fillQueryResultList(hits, qId);
    }

    private HashMap<String,Feature> fillQueryResultList(ScoreDoc[] hits, String qID) throws IOException {
		HashMap<String,Feature>  results = new HashMap<String,Feature> ();
		for (int i = 0; i < hits.length; i++) {
			Double Score = (double) hits[i].score;
//			Document hitDoc = ireader.document(hits[i].doc);
//                        Document hitDoc = indexId_docID_Map.get(hits[i].doc); 
//			String docID = hitDoc.get("ID");
                        String docID = this.indexId_docID_Map.get(hits[i].doc);
			Feature f = new Feature(simFunction.toString(),Score, qID, docID, i+1);
                        results.put(docID, f);
		}
		return results;
	}
    
    private ArrayList<String> getCommonWords(){
        if(this.commonWs == null){
            try {
                    IndexReader tmp_ireader = IndexReader.open(new SimpleFSDirectory(
                            new File(configFile.getProperty("DOC_TMP_INDEX_PATH"))));
                    IndexInfo iInfo = new IndexInfo(tmp_ireader);
                                commonWs = iInfo.getTopTerms_TF("TEXT", 50);
                } catch (IOException ex) {
                    log.error(ex);
                } 
        }
        return commonWs;
    }
    
}
