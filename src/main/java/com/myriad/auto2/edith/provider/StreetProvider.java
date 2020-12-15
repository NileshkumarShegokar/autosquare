package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class StreetProvider {
    String[] streets;
    public StreetProvider(String content) {
        this.streets=content.split(",");
    }

    public String[] getStreetNames() {
        return streets;
    }
}
