/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.learning;

import java.io.IOException;
import nl.uva.utilities.Runner;

/**
 *
 * @author mosi
 */
public class Train {
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Train.class.getName());
    
    public void train(){
        
    }
    
    private void runSvmRankLearn(Double cParam, String trainDataPath, String modelPath) {
        try {
            String Command_chmod = "chmod +x svm_rank_learn";
            String Command_svmRank = "svm_rank_learn -c 20.0 train.dat model.dat";
            Runner.runCommand(Command_chmod);
            Runner.runCommand(Command_svmRank);
        } catch (IOException ex) {
            log.error(ex);
        }
    }
    
    private void runSvmRankClassify(String trainDataPath, String modelPath) {
        try {
            String Command_chmod = "chmod +x svm_rank_learn";
            String Command_svmRank = "svm_rank_learn -c 20.0 train.dat model.dat";
            Runner.runCommand(Command_chmod);
            Runner.runCommand(Command_svmRank);
        } catch (IOException ex) {
            log.error(ex);
        }
    }
    
    
    
}
