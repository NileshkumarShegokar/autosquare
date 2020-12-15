package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class CityProvider {
    String[] cities;
    public CityProvider(String content) {
            this.cities=content.split(",");
    }

    public String[] getCities() {
        return cities;
    }
}
