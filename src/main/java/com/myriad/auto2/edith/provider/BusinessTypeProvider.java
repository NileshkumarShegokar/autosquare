package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class BusinessTypeProvider {
    String[] businessTypes;
    public BusinessTypeProvider(String content) {
        this.businessTypes=content.split(",");

    }

    public String[] getBusinessTypes() {
        return businessTypes;
    }
}
