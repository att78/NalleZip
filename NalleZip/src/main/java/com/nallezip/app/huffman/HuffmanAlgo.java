
package com.nallezip.app.huffman;

import com.nallezip.app.util.DiyBitArrayReader;
import com.nallezip.app.util.DiyByteArray;
import com.nallezip.app.util.DiyByteArrayReader;
import com.nallezip.app.util.DiyHashMap;
import com.nallezip.app.util.DiyHeap;
import com.nallezip.app.util.DiySet;
import com.nallezip.app.util.DiyStringBuilder;

/**
 * Huffman algoritmin toteuttava luokka
 *
 * @author tallbera
 */
public class HuffmanAlgo {

    public DiyHashMap<Character, String> huffmanTree = new DiyHashMap();
//käännetty publiciksi suorituskykytestauksen suorittamiseksi. Ei muita syitä
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
    public void setHuffmanTree(HuffmanNode node, DiyStringBuilder builder) {
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
    public void builderAppends(HuffmanNode node, DiyStringBuilder builder) {
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
     * @param string Pakattava string
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
        setHuffmanTree(root, new DiyStringBuilder());
        DiyStringBuilder builder = new DiyStringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);

            builder.append(huffmanTree.get(ch));
        }

        String total = builder.toString();

        byte[] resultBytes = binaryStringToBytes(total);
        resultBytes = addTreeToBytes(resultBytes);

        return resultBytes;

    }

    /**
     * Yhdistää pakkauksessa käytetyn kirjaston ja pakatun viestin tiedot samaan
     * byte-taulukkoon. Ensin loopataan merkit ja sitten tehdään puun rakenne.
     *
     * @param resultBytes pakattu viesti
     * @return
     */
    private byte[] addTreeToBytes(byte[] resultBytes) {
        DiyByteArray treeBytesArray = new DiyByteArray(huffmanTree.size() * 2);
        DiyByteArray treeBitsArray = new DiyByteArray(huffmanTree.size() * 2);
        writeTree(treeBytesArray, treeBitsArray, root);

        byte[] combined = createCombined(resultBytes, treeBytesArray, treeBitsArray);

        int i = 4;

        byte[] treeBytes = treeBytesArray.getBytes();
        int maxSize = treeBytes.length + 4;

        for (int j = 0; i < maxSize; i++, j++) {
            combined[i] = treeBytes[j];
        }

        byte[] treeBits = treeBitsArray.getBytes();
        maxSize += treeBits.length;

        for (int j = 0; i < maxSize; i++, j++) {
            combined[i] = treeBits[j];
        }

        maxSize += resultBytes.length;

        combined = finalizeCombined(resultBytes, combined, maxSize, i);

        return combined;
    }

    /**
     * apumetodi addTreeToBytes-metodille. Yhdistää kolme byte[]-taulukkoa.
     *
     * @param resultBytes pakattu viesti
     * @param treeBytesArray
     * @param treeBitsArray
     * @return
     */
    private byte[] createCombined(byte[] resultBytes, DiyByteArray treeBytesArray, DiyByteArray treeBitsArray) {
        byte[] combined = new byte[resultBytes.length + treeBytesArray.getSize() + treeBitsArray.getSize() + 4];

        byte[] byteSize = intToByteArray(treeBytesArray.getSize());
        for (int i = 0; i < 4; i++) {
            combined[i] = byteSize[i];
        }

        return combined;
    }

    /**
     * apumetodi addTreeToBytes-metodille. Viimeistelee taulukoiden
     * yhdistämisen.
     *
     * @param resultBytes pakattu viesti
     * @param combined melkein valmis pakkauspuun ja viestien yhdistelmä
     * @param maxSize maksimi
     * @param i indeksiarvo
     * @return
     */
    private byte[] finalizeCombined(byte[] resultBytes, byte[] combined, int maxSize, int i) {
        for (int j = 0; i < maxSize; i++, j++) {
            combined[i] = resultBytes[j];
        }

        return combined;
    }

    /**
     * metodi muuttaa annetun int-tyyppisen luvun byteArrayksi
     *
     * @param number muutettava luku
     * @return
     */
    private byte[] intToByteArray(int number) {
        return new byte[]{
            (byte) ((number >> 24) & 0xff),
            (byte) ((number >> 16) & 0xff),
            (byte) ((number >> 8) & 0xff),
            (byte) (number)};
    }

    /**
     * metodi kääntää annetun byte-taulukon int-tyyppiseksi luvuksi
     *
     * @param packed muutettava bytetaulukko
     * @return
     */
    private int bytesToInt(byte[] packed) {
        return (int) ((0xff & packed[0]) << 24
                | (0xff & packed[1]) << 16
                | (0xff & packed[2]) << 8
                | (0xff & packed[3]));
    }

    /**
     * metodi käy rekursiivisesti läpi puun ja kirjoittaa
     * DiyByteArrayMuotoiseksi puun. Ensin tarkistetaan, onko HuffmanNode lehti
     * vai ei.
     *
     * @param bytes
     * @param treeBits
     * @param node
     */
    private void writeTree(DiyByteArray bytes, DiyByteArray treeBits, HuffmanNode node) {

        if (node.getLeft() == null && node.getRight() == null) {
            bytes.writeByte((byte) node.getCh());
            treeBits.writeBit(true);
        } else {
            treeBits.writeBit(false);
            writeTree(bytes, treeBits, node.getLeft());
            writeTree(bytes, treeBits, node.getRight());
        }
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
        int size = countSize(total);
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

            value = increaseValue(total, value, i);
        }
        if (byteIndex == size) {
            resultBytes[byteIndex] = (byte) value;
        }
        return resultBytes;
    }

    /**
     * metodi on binaryStringToBytes-metodin apumetodi, joka laskee käytettävän
     * size:n parametrina annetun syötteen perusteella.
     *
     * @param total string-muotoinen syöte
     * @return
     */
    private int countSize(String total) {
        int lastByteLength = total.length() % 8;
        int size = (total.length() / 8);
        if (lastByteLength > 0) {
            size++;
        }
        return size;
    }

    /**
     * apumetodi binaryStringToBytes-metodille, kasvattaa value-muuttujan arvoa.
     *
     * @param total
     * @param value
     * @param i
     * @return
     */
    private int increaseValue(String total, int value, int i) {
        value *= 2;
        char ch = total.charAt(i);
        if (ch == '1') {
            value++;
        }

        return value;
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
        DiyStringBuilder builder = new DiyStringBuilder();
        packed = readTree(packed);
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

    /**
     * lukee tiedostoon pakatun huffmanPuun.
     *
     * @param packed pakattu viesti, joka sisältää myös puun.
     * @return
     */
    private byte[] readTree(byte[] packed) {
        int byteSize = bytesToInt(packed);
        byte[] treeBytes = new byte[byteSize];
        int i = 4;
        for (int j = 0; i < byteSize + 4; i++, j++) {
            treeBytes[j] = packed[i];
        }
        DiyByteArrayReader byteReader = new DiyByteArrayReader(treeBytes);
        DiyBitArrayReader bitReader = new DiyBitArrayReader(packed);
        bitReader.setByteIndex(i);
        root = reCreateNextNode(bitReader, byteReader);

        i = bitReader.getByteIndex();
        if (bitReader.partialByte()) {
            i++;
        }

        byte[] valueWithoutTree = valueWithoutTree(packed, i);
        return valueWithoutTree;
    }

    /**
     * apumetodi readTree-metodille. Luo ja palauttaa bytetaulukon.
     *
     * @param packed
     * @param i
     * @return
     */
    public byte[] valueWithoutTree(byte[] packed, int i) {
        byte[] valueWithoutTree = new byte[packed.length - i];
        for (int j = 0; i < packed.length; i++, j++) {
            valueWithoutTree[j] = packed[i];
        }
        return valueWithoutTree;
    }

    /**
     * apumetodi readTree-metodille. Selvittää seuraavan HuffmanNoden.
     *
     * @param bitReader
     * @param byteReader
     * @return Halutun HuffmanNoden
     */
    private HuffmanNode reCreateNextNode(DiyBitArrayReader bitReader, DiyByteArrayReader byteReader) {
        HuffmanNode node = new HuffmanNode();
        boolean leaf = bitReader.readBit();
        if (leaf) {
            char c = (char) byteReader.readByte();

            node.setCh(c);
        } else {

            node.setLeft(reCreateNextNode(bitReader, byteReader));
            node.setRight(reCreateNextNode(bitReader, byteReader));
        }
        return node;
    }

}
