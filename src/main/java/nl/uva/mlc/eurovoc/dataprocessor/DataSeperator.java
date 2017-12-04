/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.dataprocessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static nl.uva.mlc.settings.Config.configFile;
import org.apache.commons.io.FileUtils;


/**
 *
 * @author admin
 */
public class DataSeperator {
    
        int conceptPerc = (int)(16730 * 0.7);
        public static int fileCount = 0;
        public void fileReader(File mainFile, String ConceptDir, String TrainDir) throws IOException {
        File[] files = mainFile.listFiles();
        for(File folder:files){
            if(folder.isDirectory())
            {
                for(File file : folder.listFiles())
                    if(file.getName().startsWith("jrc")&& file.getName().endsWith(".xml"))
                    {

                        if(fileCount <= conceptPerc)
                        {
                            File f = new File(ConceptDir + "/" + file.getName());
                            Files.copy(file.toPath(), f.toPath(), REPLACE_EXISTING);
                        }
                        else
                        {
                            File f = new File(TrainDir + "/" + file.getName());
                            Files.copy(file.toPath(), f.toPath(), REPLACE_EXISTING);
                        }
                        fileCount ++;
                    }
            }
            else
                {
                    if(folder.getName().startsWith("jrc")&& folder.getName().endsWith(".xml"))
                    {

                        if(fileCount <= conceptPerc)
                        {
                            File f = new File(ConceptDir + "/" + folder.getName());
                            Files.copy(folder.toPath(), f.toPath(), REPLACE_EXISTING);
                        }
                        else
                        {
                            File f = new File(TrainDir + "/" + folder.getName());
                            Files.copy(folder.toPath(), f.toPath(), REPLACE_EXISTING);
                        }
                        fileCount ++;
                    }

                }
        }
        
        
    }
        
    public void foldCreator(int k, String inDir, String outDir) throws IOException
    {
        File out = new File(outDir);
//        out.createNewFile();
        FileUtils.forceMkdir(out);
        File in = new File(inDir);
        File[] inFiles = in.listFiles();
        int inNumFiles = inFiles.length;
        int numFilesPerFold = inNumFiles / k;
        for(int i = 0; i < k; i++)
        {
            File fold = new File(outDir + "/fold" + i + "/test");
//            fold.mkdir();
            FileUtils.forceMkdir(fold);
            for(int j = i * numFilesPerFold; j < (i + 1) * numFilesPerFold; j++)
            {
                if(inFiles[j].getName().startsWith("jrc")&& inFiles[j].getName().endsWith(".xml")){
                    File f = new File(fold.getAbsoluteFile() + "/" + inFiles[j].getName());
                    Files.copy(inFiles[j].toPath(), f.toPath(), REPLACE_EXISTING);
                }
            }
            File foldTrain = new File(outDir + "/fold" + i + "/train");
            FileUtils.forceMkdir(foldTrain);
            for(int j = 0; j < inFiles.length; j++)
            {
                if(j < i * numFilesPerFold || j >= (i + 1) * numFilesPerFold)
                {
                    if(inFiles[j].getName().startsWith("jrc")&& inFiles[j].getName().endsWith(".xml")){
                        File f = new File(foldTrain.getAbsoluteFile() + "/" + inFiles[j].getName());
                        Files.copy(inFiles[j].toPath(), f.toPath(), REPLACE_EXISTING);
                    }
                }
            }
            
        }
    }
    public static void main(String[] args) throws IOException {
        String ConceptDir = configFile.getProperty("CORPUS_Con_PATH");
        String TrainDir = configFile.getProperty("CORPUS_Eval_PATH");
//        String FoldsDir = "/Users/admin/Desktop/five_year/data/folds";
        DataSeperator ds = new DataSeperator();
        ds.fileReader(new File(configFile.getProperty("MAIN_DOC_PATH")), ConceptDir, TrainDir);
//        ds.foldCreator(5, TrainDir, FoldsDir);
    }
    
}
