/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc.featureextractor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import nl.uva.mlc.eurovoc.EuroVocDoc;

/**
 *
 * @author admin
 */
public class FeatureNormalizer {
    
    public HashMap<String, Feature> normalize(Map<String, Feature> features)
    {
        HashMap<String, Feature> normalizeFeatures = new HashMap<>();
        double sum = 0;
        for(Entry<String, Feature> ent : features.entrySet())
            sum += ent.getValue().getfValue();
        for(Entry<String, Feature> ent : features.entrySet())
        {
            Feature f = ent.getValue();
            if(sum > 0)
                f.setfValue(ent.getValue().getfValue() / sum);
            else
                f.setfValue(0D);
            normalizeFeatures.put(ent.getKey(), f);
        }
        return normalizeFeatures;
    }
    
    public HashMap<String, Double> finalScoreNormalize(Map<String, Double> scores)
    {
        HashMap<String, Double> normalizeFeatures = new HashMap<>();
        double sum = 0;
        for(Entry<String, Double> ent : scores.entrySet())
            sum += ent.getValue();
        for(Entry<String, Double> ent : scores.entrySet())
        {
            double normVal = 0;
            if(sum > 0)
                normVal = ent.getValue() / sum;
            normalizeFeatures.put(ent.getKey(), normVal);
        }
        return normalizeFeatures;
    }

    
    public TreeMap<Integer, HashMap<String, Feature>> oneQueryNormalizer(TreeMap<Integer, HashMap<String, Feature>> features){
        TreeMap<Integer,HashMap<String,Feature>> NormalizedFeatures = new TreeMap<>();
        for(Entry<Integer,HashMap<String,Feature>> ent : features.entrySet())
        {
            NormalizedFeatures.put(ent.getKey(), this.normalize(ent.getValue()));
        }
        return NormalizedFeatures;
    }
    
    public TreeMap<Integer,HashMap<String,HashMap<String,Feature>>> allQueryNormalizer(TreeMap<Integer,HashMap<String,HashMap<String,Feature>>> features){
        TreeMap<Integer,HashMap<String,HashMap<String,Feature>>> RawFeatures = new TreeMap<>();
        FeatureNormalizer fn = new FeatureNormalizer();
        for(Entry<Integer,HashMap<String,HashMap<String,Feature>>> ent : features.entrySet())
        {
            HashMap<String,HashMap<String,Feature>> temp = new HashMap<>();
            for(Entry<String,HashMap<String,Feature>> ent2 : ent.getValue().entrySet())
            {
                temp.put(ent2.getKey(), fn.normalize(ent2.getValue()));
            }
            RawFeatures.put(ent.getKey(), temp);
        }
        return RawFeatures;
    }
   
}
