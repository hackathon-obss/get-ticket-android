package com.met.getticket;

import java.io.Serializable;

/**
 * Created by met on 5/5/2018.
 */

public class User implements Serializable{
    private String id;
    private String eta1;
    private String eta2;
    private String sube;
    private String age;
    private String line;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUid() {
        return id;
    }

    public void setUid(String uid) {
        this.id = uid;
    }

    public String getEta1() {
        return eta1;
    }

    public void setEta1(String eta1) {
        this.eta1 = eta1;
    }

    public String getEta2() {
        return eta2;
    }

    public void setEta2(String eta2) {
        this.eta2 = eta2;
    }

    public String getSube() {
        return sube;
    }

    public void setSube(String sube) {
        this.sube = sube;
    }
}
