

package nl.uva.mlc.eurovoc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;


/**
 *
 * @author  Mostafa Dehghani
 */

public class EuroVocIndexUtil {
        
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EuroVocDoc.class.getName());
        private HashMap<String, Integer> indexId = null;
        private IndexReader ireader = null;

        public EuroVocIndexUtil() {
        try {
            ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))));
        } catch (IOException ex) {
            log.error(ex);
        }
        }
        
        public int get_indexID(String docName) throws IOException {
        if(this.indexId == null){
            indexId = new HashMap<>();
            for(int id=0;id<ireader.numDocs();id++){
                indexId.put(ireader.document(id).get("DOCID"), id);
            }
        }
        Integer iID = this.indexId.get(docName);
        return iID;
    }  
    
}
