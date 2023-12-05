package com.analysis.SalaryStratos.features;
import com.analysis.SalaryStratos.algorithms.EditDistanceAlgo;
import com.analysis.SalaryStratos.dataStructures.trie.TrieDS;
import com.analysis.SalaryStratos.dataStructures.trie.TrieNode;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class SpellChecker {
    // List of invalid words to be excluded from suggestions.
    private final List<String> invalid = Arrays.asList("lol", "abcdefghijklmnopqrstuvwxyz");

    // Initialize the spell checker
    public void initializeSpellChecker(TrieDS trie) {
        Map<String, Integer> dict = trie.getDict();
        try {
            String pathToDictionary = "src/main/resources/dictionaryOfWords.txt";
            FileReader fr = new FileReader(pathToDictionary);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                String word = line.toLowerCase();
                if (!line.contains(" ")) {
                    dict.put(word, dict.getOrDefault(word, 0) +1);
                    trie.insertIntoTrie(word, "fromCorpus");
                } else {
                    String[] strs= line.split("\\s");
                    for(String str: strs) {
                        dict.put(str, dict.getOrDefault(word, 0) +1);
                        trie.insertIntoTrie(str, "fromCorpus");
                    }
                }
            }
            fr.close();
            br.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        System.out.println("Spell Checker Initialized");
    }

    // Generate a map of suggested similar words based on edit distance.
    public TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> suggestSimilarWord(String inputWord, TrieDS trie, int editDistanceLength) {
        Map<String, Integer> dict = trie.getDict();
        if (inputWord.isEmpty() || invalid.contains(inputWord.toLowerCase()))
            return null;
        String s = inputWord.toLowerCase();
        TreeMap<Integer, TreeMap<Integer, TreeSet<String>>> map = new TreeMap<>();
        TrieNode node = trie.searchInTrie(s);
        // If the input word is not present in the trie, find similar words.
        if(node == null) {
            for (String w: dict.keySet()) {
                int dist = EditDistanceAlgo.calculateEditDistance(w, s);
                if(dist <= editDistanceLength) {
                    // Build the map with edit distance, word frequency, and the set of similar words.
                    TreeMap<Integer, TreeSet<String>> similarWords = map.getOrDefault(dist, new TreeMap<>());
                    int wordFrequency = dict.get(w);
                    TreeSet<String> set = similarWords.getOrDefault(wordFrequency, new TreeSet<>());
                    set.add(w);
                    similarWords.put(wordFrequency, set);
                    map.put(dist, similarWords);
                }
            }
            return map;
        } else {
            return null;
        }
    }

}
