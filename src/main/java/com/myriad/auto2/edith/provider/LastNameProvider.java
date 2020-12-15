package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class LastNameProvider {
    String[] names;
    public LastNameProvider(String content) {
        this.names=content.split(",");
    }

    public String[] getLastNames() {
        return names;
    }
}
