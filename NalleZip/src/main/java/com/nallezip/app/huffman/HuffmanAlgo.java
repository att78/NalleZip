/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nallezip.app.huffman;

import com.nallezip.app.util.DiyHashMap;
import com.nallezip.app.util.DiyHeap;
import com.nallezip.app.util.DiySet;

/**
 * Huffman algoritmin toteuttava luokka
 *
 * @author tallbera
 */
public class HuffmanAlgo {

    public DiyHashMap<Character, String> huffmanTree = new DiyHashMap();//käännetty publiciksi suorituskykytestauksen suorittamiseksi. Ei muita syitä
    private HuffmanNode root;

    /**
     * Asettaa luokkamuuttujaan root Huffman-puun juurisolmun.
     *
     * @param position DiyHashMap-olio, jonka avaimet ovat Character ja arvot
     * Integer-tyyppisiä
     * @return Huffman puun juurisolmun
     */
    public HuffmanNode findRootNode(DiyHashMap<Character, Integer> position) {

        DiyHeap nodeQueue = new DiyHeap();

        if (position.size() > 0) {
            nodeQueue = createNodes(position);
        }

        while (nodeQueue.size() > 1) {
            HuffmanNode mom = new HuffmanNode();
            HuffmanNode firstBorn = nodeQueue.poll();
            System.out.println("Eka: " + firstBorn.toString());
            HuffmanNode secondBorn = nodeQueue.poll();
            mom.setLeft(firstBorn);
            mom.setRight(secondBorn);
            mom.setCh('-');
            mom.setPosition(firstBorn.getPosition() + secondBorn.getPosition());
            root = mom;
            nodeQueue.offer(mom);
        }
        return nodeQueue.poll();
    }

    /**
     * Luo prioriteettijonon, jossa on HuffmanNodeja ottaa parametritrina
     * DiyHashMapin.
     *
     * @param position DiyHashMap, jossa on avaimena Charactereja ja arvoina
     * Integerejä.
     * @return DiyHeapin, joka on siis minimikeko.
     */
    public DiyHeap createNodes(DiyHashMap<Character, Integer> position) {
        DiySet keys = position.keySetForCharacters();
        DiyHeap nodeQueue = new DiyHeap(512);

        for (int i = 0; i < keys.length(); i++) {

            if (keys.getTable()[i] != null) {
                Character c = (char) keys.getTable()[i];
                HuffmanNode node = new HuffmanNode();
                node.setLeft(null);
                node.setRight(null);
                node.setCh(c);
                int weight = position.get(c);
                node.setPosition(weight);
                nodeQueue.offer(node);
            }
        }
        return nodeQueue;
    }

    /**
     * metodi asettaa silmuja HuffmanTreehen
     *
     * @param node asetettava HuffmanNode
     * @param builder stringBuilder
     */
    public void setHuffmanTree(HuffmanNode node, StringBuilder builder) {
        //System.out.println("Builder on: "+builder);
        if (node != null) {
            if (node.getLeft() == null && node.getRight() == null) {
                huffmanTree.put(node.getCh(), builder.toString());
            } else {
                builderAppends(node, builder);
            }
        }
    }

    /**
     * metodi on apumetodi setHuffmanTree-metodille. BuilderAppends ja
     * setHuffmanTree toimivat rekursiivisesti, mutta koodin siisteyden takia
     * rekursio on pilkottu kahteen metodiin. Muuten olisi muodostunut yksi
     * jättiläinen.
     *
     * @param node lisättävä HuffmanNode
     * @param builder binääriStringiä keräävä Olio.
     */
    public void builderAppends(HuffmanNode node, StringBuilder builder) {
        builder.append('0');
        setHuffmanTree(node.getLeft(), builder);
        builder.deleteCharAt(builder.length() - 1);

        builder.append('1');
        setHuffmanTree(node.getRight(), builder);
        builder.deleteCharAt(builder.length() - 1);
    }

    /**
     * metodi on apumetodi encodeString-metodille. Luo positioille paikat.
     *
     * @param string
     * @return
     */
    public DiyHashMap<Character, Integer> createPosition(String string) {
        DiyHashMap<Character, Integer> position = new DiyHashMap();
        for (int i = 0; i < string.length(); i++) {

            if (!position.containsKey(string.charAt(i))) {
                position.put(string.charAt(i), 0);
            }
            position.put(string.charAt(i), position.get(string.charAt(i)) + 1);
        }
        return position;
    }

    /**
     * metodi on enkoodausta varten. Metodi käyttää apumetodeita createPosition
     * ja buildTree().
     *
     * @param string pakattava viesti
     * @return byte-taulukko, johon viesti on pakattu.
     */
    public byte[] encodeString(String string) {
        DiyHashMap<Character, Integer> position = createPosition(string);
        root = findRootNode(position);
        setHuffmanTree(root, new StringBuilder());
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);

            builder.append(huffmanTree.get(ch));
        }

        String total = builder.toString();

        byte[] resultBytes = binaryStringToBytes(total);

        return resultBytes;

    }

    /**
     * metodi muuttaa enkoodatun ykkösiä ja nollia sisältävän String
     * byte-tauluksi.
     *
     * @param total binaariString- joka pakataan.
     * @return byte-taulukko,jossa viesti on pakattuna
     */
    public byte[] binaryStringToBytes(String total) {

        int lastByteLength = total.length() % 8;

        int size = (total.length() / 8);
        if (lastByteLength > 0) {
            size++;
        }

        byte[] resultBytes = new byte[size + 1];
        int byteIndex = 1;
        int value = 0;
        resultBytes[0] = (byte) lastByteLength;
        for (int i = 0; i < total.length(); i++) {
            if (i % 8 == 0 && i > 0) {
                resultBytes[byteIndex] = (byte) value;
                value = 0;
                byteIndex++;
            }
            value *= 2;
            char ch = total.charAt(i);
            if (ch == '1') {
                value++;
            }
        }
        if (byteIndex == size) {
            System.out.println("Setting last byte to " + value);
            resultBytes[byteIndex] = (byte) value;
        }
        return resultBytes;
    }

    /**
     * Metodin tarkoitus on muuttaa enkoodattu byte-taulukko booleantaulukoksi,
     * jota käytetään dekoodauksessa.
     *
     * @param resultBytes pakattu viesti
     * @return boolean taulukko pakatusta viestistä.
     */
    public Boolean[] byteToBoolean(byte[] resultBytes) {
        Boolean[] zerosAndOnes = new Boolean[resultBytes.length * 8];
        int max = 128;
        int j = 0;
        int lastByteLength = (int) resultBytes[0] & 0xFF;

        for (int i = 1; i < resultBytes.length - 1; i++) {

            byte a = resultBytes[i];
            int number = (int) a & 0xFF;
            j = zerosAndOnesRecursion(zerosAndOnes, number, j, max);
        }

        lastByte(zerosAndOnes, j, max, lastByteLength, resultBytes);

        return zerosAndOnes;
    }

    /**
     * metodi on byteToBoolean-metodin apumetodi, joka käsittelee bytetaulukon
     * viimeisen byten.
     *
     * @param zerosAndOnes Boolean-taulukko, johon purkamistiedot kerätään
     * @param j Boolean taulukon zerosAndOnes indeksi eli kohta jota operoidaan.
     * @param max tavun arvo
     * @param lastByteLength viimeisen tavun pituus
     * @param resultBytes pakattu viesti
     */
    public void lastByte(Boolean[] zerosAndOnes, int j, int max, int lastByteLength, byte[] resultBytes) {
        int rounds = 8 - lastByteLength;

        byte a = resultBytes[resultBytes.length - 1];
        int number = (int) a & 0xFF;

        if (rounds < 8) {
            for (int i = 0; i < rounds; i++) {
                max = max / 2;
            }
        }
        zerosAndOnesRecursion(zerosAndOnes, number, j, max);

    }

    /**
     * Apumetodi bytesToBoolean-metodille, tämä metodi hoitaa rekursio-osan
     * booleantaulukon muodostamisesta.
     *
     * @param zerosAndOnes muodostettava Boolean-taulukko
     * @param number pakkauksessa ollut arvo integer-tyyppisenä numerona
     * @param j boolean-taulukon indeksi
     * @param max tavun maksimi arvo
     * @return
     */
    public int zerosAndOnesRecursion(Boolean[] zerosAndOnes, int number, int j, int max) {

        if (number >= max) {
            zerosAndOnes[j] = true;
            number = number - max;
        } else {
            zerosAndOnes[j] = false;

        }
        j++;
        max = max / 2;
        if (max > 0) {
            j = zerosAndOnesRecursion(zerosAndOnes, number, j, max);
        }
        return j;
    }

    /**
     * metodi palauttaa encodeString-metodilla koodatun Stringin alkuperäiseen
     * muotoon.
     *
     * @param byte[] pakattu viesti
     * @return purettu viesti
     */
    public String decodeString(byte[] packed) {
        StringBuilder builder = new StringBuilder();

        HuffmanNode node = root;

        Boolean[] decompressed = byteToBoolean(packed);

        for (int i = 0; i < decompressed.length; i++) {

            if (decompressed[i] == null) {
                break;
            }

            if (!decompressed[i]) {

                node = node.getLeft();

                if (node.getLeft() == null && node.getRight() == null) {
                    builder.append(node.getCh());
                    node = root;
                }

            } else if (decompressed[i]) {
                node = node.getRight();

                if (node.getLeft() == null && node.getRight() == null) {
                    builder.append(node.getCh());
                    node = root;
                }
            }
        }
        return builder.toString();
    }

}
