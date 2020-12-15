package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class AddressSuffixProvider {
    String[]addressSuffixes;
    public AddressSuffixProvider(String content) {
            this.addressSuffixes=content.split(",");
    }

    public String[] getAddressSuffixes() {
        return addressSuffixes;
    }
}
