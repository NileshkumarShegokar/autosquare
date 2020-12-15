package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class WordsProvider {
    String[] words;
    public WordsProvider(String content) {
        this.words=content.split(",");
    }

    public String[] getWords() {
        return words;
    }
}
