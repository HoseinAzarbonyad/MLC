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
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author admin
 */
public class Five_fold_res {
    
    public String inputFiveFoldDir = "";
    public String fileDir = "";
    public String outputFiveFoldDir = "";
    
    public void foldResCreator(String inputDir, String fileDir, String outputDir) throws IOException
    {
        Map<String, Map<String, String>> foldsMap = new HashMap<>();
        for(int i = 1; i <= 5; i++)
        {
            String inputPath = inputDir + "fold" + i + "/" + "res.txt";
            BufferedReader in = new BufferedReader(new FileReader(new File(inputPath)));
            String str;
            while((str = in.readLine()) != null)
            {
                String[] parts = str.split(" ");
                if(foldsMap.containsKey("fold" + i))
                {
                    foldsMap.get("fold" + i).put(parts[0], parts[0]);
                }
                else{
                    Map<String, String> temp = new HashMap<>();
                    temp.put(parts[0], parts[0]);
                    foldsMap.put("fold" + i, temp);
                }
            }
        }
        

        for(int i = 1; i <= 5; i++)
        {
            String outputPath = outputDir + "fold" + i + "/" + "res.txt";
	    FileUtils.forceMkdir(new File(outputDir + "fold" + i + "/"));
            BufferedReader inFile = new BufferedReader(new FileReader(new File(fileDir)));
            File file = new File(outputPath);
            //FileUtils.forceMkdir(file);
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            
            String str;
            while((str = inFile.readLine()) != null)
            {
                String[] parts = str.split(" ");
                if(foldsMap.get("fold" + i).containsKey(parts[0]))
                    bw.write(str + "\n");
            }
            bw.close();
        }
    }
    
}
