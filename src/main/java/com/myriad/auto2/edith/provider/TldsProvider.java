package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class TldsProvider {
    String[] tlds;
    public TldsProvider(String content) {
        this.tlds=content.split(",");
    }

    public String[] getTlds() {
        return tlds;
    }
}
