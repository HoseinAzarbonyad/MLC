/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.learning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 *
 * @author mosi
 */
public class Test {
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Test.class.getName());
    public void trecEvalInputProvider(String testPath, String scorePath, String resultPath, String judgePath){
        ArrayList<resInstance>  res= new ArrayList<>();
        try {
            BufferedReader testBR = new  BufferedReader(new FileReader(new File(testPath)));
            BufferedReader scoreBR = new  BufferedReader(new FileReader(new File(scorePath)));
            BufferedWriter resultBW = new  BufferedWriter(new FileWriter(new File(resultPath)));
            BufferedWriter judgeBW = new  BufferedWriter(new FileWriter(new File(judgePath)));
            String testLine;
            while((testLine=testBR.readLine())!=null){
                String[] lineParts1 = testLine.split(" # ");
                String[] lineParts2 = lineParts1[0].split("\\s+");
                String[] lineParts3 = lineParts1[1].split("\\s+");
                resInstance resI = new resInstance(lineParts3[0],lineParts3[1],Double.parseDouble(scoreBR.readLine()),lineParts2[0]);
                res.add(resI);
//                System.out.println(resI.toString());
            }
            Collections.sort(res, new resInstanceComparator());
            String qidTmp= "null";
            int rank =0;
            for(Integer index=0; index< res.size(); index++){
                if(!res.get(index).qId.equals(qidTmp)){
                    rank = 0;
                    qidTmp = res.get(index).qId;
                }
                resultBW.write(res.get(index).qId + " 0 " + res.get(index).dId + " " + ++rank + " " + res.get(index).score + " :D\n");
                judgeBW.write(res.get(index).qId + " 0 " + res.get(index).dId + " " + res.get(index).label + "\n");
            }
            resultBW.close();
            judgeBW.close();
        } catch (IOException ex) {
            log.error(ex);
        }
    }  
    
    public void res(String inputDir) {
        Test t = new Test();
        for(int i = 0; i < 5; i++)
        {
            String testData = inputDir + "fold" + i + "/" + "test.txt";
            String scoresFile = inputDir + "fold" + i + "/" + "scores.txt";
            String resFile = inputDir + "fold" + i + "/" + "res.txt";
            String judgeFile = inputDir + "fold" + i + "/" + "judge.txt";
            t.trecEvalInputProvider(testData,scoresFile, resFile, judgeFile);
        }
        
    }
    
    
}
class resInstance{
    public String qId;
    public String dId;
    public Double score;
    public String label;

    public resInstance(String qId, String dId, Double score, String label){
        this.qId = qId;
        this.dId = dId;
        this.qId = qId;
        this.score = score;
        this.label = label;
    }
    @Override
        public String toString() {
          return qId +" " + dId;
    }
}
    
 class resInstanceComparator implements Comparator<resInstance> {
    public int compare(resInstance o1, resInstance o2) {
        int value = o1.qId.compareTo(o2.qId);
        if (value == 0) {
                return o2.score.compareTo(o1.score);
        }
        return value;
    }    
}
