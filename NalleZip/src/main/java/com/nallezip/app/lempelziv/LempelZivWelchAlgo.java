/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nallezip.app.lempelziv;

import com.nallezip.app.util.DiyArrayList;
import com.nallezip.app.util.DiyHashMap;
import com.nallezip.app.util.DiyStringBuilder;

/**
 * Luokka toteuttaa Lempel-Ziv-Welch-algoritmin, joka on yksi niistä Lempel
 * Ziv-algoritmeina tunnetuista algoritmeista
 *
 * @author tallbera
 */
public class LempelZivWelchAlgo {

    private DiyArrayList encoded = new DiyArrayList();
    private DiyHashMap<String, Integer> library = new DiyHashMap();
    private DiyHashMap<Integer, String> libraryDecoded = new DiyHashMap();
    private final int SIZE = 512;

    public LempelZivWelchAlgo() {
    }

    /**
     * Metodi enkoodaa annetun string-muotoisen syötteen. Käyttää apumetodeina
     * createLibrary- ja fillLibrary-metodeita.
     *
     * @param string pakattava String
     * @return pakatun viestin
     */
    public DiyArrayList encodeString(String string) {

        createLibraries();
        fillLibrary(string);
        return encoded;
    }

    /**
     * Luodaan sekä enkoodauksessa että dekoodauksessa käytetyt kirjastot.
     *
     *
     */
    public void createLibraries() {
        for (int i = 0; i < SIZE; i++) {

            library.put("" + (char) i, i);
            libraryDecoded.put(i, "" + (char) i);
        }
    }

    /**
     * Enkoodauksessa käytettävän kirjaston täyttäminen annetun syötteen
     * pohjalta
     *
     * @param string viesti
     */
    public void fillLibrary(String string) {
        String a = string.substring(0, 1);
        int size = SIZE;
        for (int i = 1; i < string.length(); i++) {
            String b = string.substring(i, i + 1);
            String ab = a + b;
            if (library.containsKey(ab)) {
                a = ab;
            } else {

                encoded.add(library.get(a));
                library.put(ab, size++);
                a = "" + b;
            }
        }
        encoded.add(library.get(a));

    }

    /**
     * Metodi dekoodaa encodeString-metodin tekemän listan ja palauttaa
     * alkuperäisen Stringin. Käyttää apumetodina decodeLoop-metodia.
     *
     * @return puretun viestin
     */
    public String decodeString() {

        Integer firstOne = encoded.getFirst();
        int first = (int) firstOne;
        String answer = "" + (char) first;
        DiyStringBuilder builder = new DiyStringBuilder(answer);
        decodeLoop(builder, answer);
        return builder.toString();
    }

    /**
     * Metodi toimii apumetodina decodeString-metodille ja nimensä mukaisesti
     * hoitaa enkoodatun listan läpikäynnin.
     *
     * @param builder palautettavan viestin kasaaja
     * @param answer pakkauksen ensimmäinen
     */
    public void decodeLoop(DiyStringBuilder builder, String answer) {
        int size = SIZE;

        for (int i = 1; i < encoded.size(); i++) {
            Integer number = encoded.get(i);
            String s = "";
            if (libraryDecoded.containsKey(number)) {
                s = libraryDecoded.get(number);
            } else if (number == size) {
                s = answer + answer.charAt(0);
            }
            builder.append(s);
            String string = answer + s.charAt(0);
            libraryDecoded.put(size++, string);
            answer = s;
        }
    }

    public DiyHashMap<String, Integer> getLibrary() {
        return library;
    }

    public DiyHashMap<Integer, String> getLibraryDecoded() {
        return libraryDecoded;
    }

    public DiyArrayList getEncoded() {
        return encoded;
    }

}
