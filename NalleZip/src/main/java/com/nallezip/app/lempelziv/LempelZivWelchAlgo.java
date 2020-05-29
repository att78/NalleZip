/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nallezip.app.lempelziv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Luokka toteuttaa Lempel-Ziv-Welch-algoritmin, joka on yksi niistä Lempel
 * Ziv-algoritmeina tunnetuista algoritmeista
 *
 * @author tallbera
 */
public class LempelZivWelchAlgo {

    private List<Integer> encoded = new ArrayList();
    private HashMap<String, Integer> library = new HashMap();
    private HashMap<Integer, String> libraryDecoded = new HashMap();

    public LempelZivWelchAlgo() {
    }

    /**
     * Metodi enkoodaa annetun string-muotoisen syötteen. Käyttää apumetodeina
     * createLibrary- ja fillLibrary-metodeita.
     *
     * @param string
     * @return
     */
    public List<Integer> encodeString(String string) {

        createLibraries();
        fillLibrary(string);
        return encoded;
    }

    /**
     * Luodaan sekä enkoodauksessa että dekoodauksessa käytetyt kirjastot.
     *
     * @param string
     */
    public void createLibraries() {
        for (int i = 0; i < 512; i++) {

            library.put("" + (char) i, i);
            libraryDecoded.put(i, "" + (char) i);
        }
        System.out.println(library.size());
    }

    /**
     * Enkoodauksessa käytettävän kirjaston täyttäminen annetun syötteen
     * pohjalta
     *
     * @param string
     */
    public void fillLibrary(String string) {
        String a = string.substring(0, 1);
        int size = 512;
        for (int i = 1; i < string.length(); i++) {
            String b = string.substring(i, i + 1);
            String ab = a + b;
//       for(char ch : string.toCharArray()){
//           String ach = a+ch;
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

    public String decodeString() {

        int first = encoded.remove(0);
        String answer = "" + (char) first;
        StringBuilder builder = new StringBuilder(answer);
        decodeLoop(builder, answer);
        return builder.toString();
    }

    public void decodeLoop(StringBuilder builder, String answer) {
        int size = 512;
        for (int number : encoded) {
            String s = "";
            if (libraryDecoded.containsKey(number)) {
                s = libraryDecoded.get(number);
            } else if (number == size) {
                s = answer + answer.charAt(0);
            }

            builder.append(s);
            libraryDecoded.put(size++, answer + s.charAt(0));
            answer = s;
        }
    }

    public HashMap<String, Integer> getLibrary() {
        return library;
    }

    public HashMap<Integer, String> getLibraryDecoded() {
        return libraryDecoded;
    }

    public List<Integer> getEncoded() {
        return encoded;
    }

}