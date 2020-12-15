package com.myriad.auto2.edith.provider;

/**
 * Created by nileshkumar_shegokar on 6/27/2017.
 */
public class EmailHostProvider {
    String[] emailHosts;
    public EmailHostProvider(String content) {
            this.emailHosts=content.split(",");
    }

    public String[] getEmailHosts() {
        return emailHosts;
    }
}
