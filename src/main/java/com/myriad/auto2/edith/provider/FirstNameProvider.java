package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class FirstNameProvider {
    String[] names;
    public FirstNameProvider(String content) {
        this.names=content.split(",");
    }

    public String[] getFirstNames() {
        return names;
    }
}
