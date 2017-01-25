package com.kmp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Jumbled up words with semi-colon separator, find max overlap in String and reassemble the text
 * Use Knuth Morris Pratt
 * Each line is a jumbled up text line and assemble it back to original
 * https://en.wikipedia.org/wiki/Knuth–Morris–Pratt_algorithm
 * If text has semi-colon will treat it as separator and fail (maybe escape it)
 * @author ade
 **/

public class KMP {
	public static void main(String[] args) throws IOException {     
		URL url = KMP.class.getResource("test.txt");
		try (BufferedReader bufferIn = new BufferedReader(new FileReader(url.getPath()))) {       
			bufferIn.lines().map(KMP::assembleText).forEach(System.out::println);     
		}   
	} 

	/**
	 * Get each line of jumbled up text and output valid line using KMP
	 * @param text
	 * @return
	 */
	public static String assembleText(String text){
		String[] words = text.split(";");
		List<String> wordList = new LinkedList<String>(Arrays.asList(words));
		wordList.remove(0); // remove as we use first word in text buffer and text to compare with
		StringBuffer textBuffer = new StringBuffer(words[0]);
		while(wordList.size()>0){
			// using a TreeMap to get highest value of key (insertion are sorted)
			TreeMap<Integer,String> overLapMap = new TreeMap<Integer,String>();
			Iterator<String> iter = wordList.iterator();
			while (iter.hasNext()){
				String wordToCompare = iter.next();
				Integer overLapInt = overLappedStringLength(textBuffer.toString(), wordToCompare);
				if(overLapInt >0){ // there is a match with another word 
					overLapMap.put(overLapInt, wordToCompare); // put in tree map
				}
			}
			// matches found
			if (!overLapMap.isEmpty() && overLapMap.lastKey() > 0){
				String overLap = overLapMap.get(overLapMap.lastKey()); // get max character match 
				if (overLap.length()>0){ // append it 
					textBuffer.append(overLap.substring(overLapMap.lastKey(), overLap.length()));
					wordList.remove(overLap); // remove other string as not needed
				}
			}else{
				// no matches found - this is important clause - as will infinite loop if not added
				// add textbuffer to list for future searches
				wordList.add(textBuffer.toString()); 
				// and a new word to start comparing with
				textBuffer = new StringBuffer(wordList.get(0));
			}
		}
		// return fully assembled string
		return textBuffer.toString();
	}


	/**
	 * KMP algorithm
	 * @param s1
	 * @param s2
	 * @return 
	 */
	private static Integer overLappedStringLength(String s1, String s2) {
		int s1Length=s1.length();
		int s2Length=s2.length();
		int[] temp = computeBackTrackTable(s2); 
		int m = 0;
		int i = 0;
		while (m + i < s1Length && i < s2Length) {
			char x = s2.toCharArray()[i];
			char y = s1.toCharArray()[m + i];
			if (x == y) {
				i += 1;
			} else {
				m += i - temp[i];
				if (i > 0){
					i = temp[i];
				}
			}
		}
		if (i>0){
			return i;
		}
		return 0;
	}

	/**
	 * KMP algorithm - using back track table
	 * @param str
	 * @return
	 */
	private static int[] computeBackTrackTable(String str) {
		int cnd = 0;
		int pos = 2;
		int strLength = str.length();
		int[] temp = new int[strLength];
		temp[0] = -1;
		temp[1] = 0;

		while (pos < strLength) {
			if (str.toCharArray()[pos - 1] == str.toCharArray()[cnd]) {
				temp[pos] = cnd + 1;
				pos += 1;
				cnd += 1;
			} else if (cnd > 0) {
				cnd = temp[cnd];
			} else {
				temp[pos] = 0;
				pos += 1;
			}
		}
		return temp;
	}
}