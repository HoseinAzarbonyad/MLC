/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.mlc.eurovoc;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author mosi
 */
public class EuroVocConcept {
    private String id;
    private String title;
    private String text;
    private ArrayList<String> docs;

    public EuroVocConcept(String id, String text, String title, ArrayList<String> docs) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.docs = docs;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDocs(ArrayList<String> docs) {
        this.docs = docs;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
    public ArrayList<String> getDocs() {
        return docs;
    }

  

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EuroVocConcept other = (EuroVocConcept) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
}

