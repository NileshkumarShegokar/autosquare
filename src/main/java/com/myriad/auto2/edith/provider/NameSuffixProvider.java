package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class NameSuffixProvider {
    String[] nameSuffixes;
    public NameSuffixProvider(String content) {
        this.nameSuffixes=content.split(",");
    }

    public String[] getSuffixes() {
        return nameSuffixes;
    }
}
