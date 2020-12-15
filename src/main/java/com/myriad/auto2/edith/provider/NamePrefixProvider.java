package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class NamePrefixProvider {
    String[] namePrefixes;
    public NamePrefixProvider(String content) {
        this.namePrefixes=content.split(",");
    }

    public String[] getPrefixes() {
        return namePrefixes;
    }
}
