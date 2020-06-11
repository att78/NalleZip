/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nallezip.app.util;

import java.util.Arrays;

/**
 * Tarkoituksena on tehdä luokka, joka pystyy korvaamaan LempelZivwelchAlgossa
 * käytetyt List- ja ArrayList rakenteet. Luokan ei ole edes tarkoitus olla ns.
 * täydellinen javan Array List:in korvike, vaan sellainen, joka piisaa LZW:n
 * tarpeisiin. LZW käyttää listaa,jossa on Integer-tyyppisiä olioita.
 *
 * @author tallbera
 */
public class DiyArrayList {

    /**
     * diyArrayhin tallennettujen määrä. Muuttuva luku
     */
    private int amount = 0;

    /**
     * Arrayhin kerätään Integerejä
     */
    private Integer[] diyArray = new Integer[512];

    private boolean tableIsFull() {
        boolean full = false;
        if (this.diyArray.length == this.amount) {
            full = true;
        }
        return full;
    }

    private void doubleTable(int doubleSize) {

        Integer[] cache = new Integer[doubleSize];
        int cacheNumber = 0;

        for (int i = 0; i < this.diyArray.length; i++, cacheNumber++) {
            Integer number = diyArray[i];
            if (number == null) {
                cacheNumber--;
                continue;
            }
            cache[cacheNumber] = diyArray[i];
        }
        this.diyArray = null;
        this.diyArray = new Integer[cache.length];
        this.diyArray = cache;
    }

    public void add(Integer number) {

        if (tableIsFull()) {
            int doubleSize = size() * 2;
            doubleTable(doubleSize);
        }

        this.diyArray[this.amount] = number;
        this.amount++;

    }

    public int size() {
        return amount;
    }

    // remove-toimii väärin. Onneksi sitä ei tarvitse käyttää.
    public void remove(Integer number) {

        for (int i = 0; i < this.amount; i++) {
            if (number.equals(this.diyArray[i])) {
                this.diyArray[i] = null;
                this.amount--;
                return;
            }
        }
    }

    public Integer getFirst() {
        Integer diy = null;
        diy = this.diyArray[0];
        return diy;
    }

    public int get(int n) {
        Integer diy = null;
        diy = this.diyArray[n];
        int answer;
        if(diy!=null){
            answer = (int) diy;
            return answer;
        }

        return -1;
    }

    // toString lause puuttuu

    @Override
    public String toString() {
        return "DiyArrayList{" + "amount=" + amount + ", diyArray=" + Arrays.toString(diyArray) + '}';
    }
    
}