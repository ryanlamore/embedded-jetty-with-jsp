package com.salesforce.git.management.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by rlamore on 1/1/15.
 */
public class GitHubUtil {
    public static final String GITHUB_DOMAIN = "github.com";
    public static final String FORCE_ORG = "forcedotcom";

    private static final Logger logger = LoggerFactory.getLogger(GitHubUtil.class);

    public boolean userExistsOnGitHub(String username) throws IOException {
        return checkIfUrlValid(String.format("http://%s/%s", GITHUB_DOMAIN, username));
    }

    public boolean respositoryExistsOnGitHub(String repository) throws IOException {
        return checkIfUrlValid(String.format("http://%s/%s/%s", GITHUB_DOMAIN, FORCE_ORG, repository));
    }

    boolean checkIfUrlValid(String url) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(url);

            logger.debug("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<Integer> responseHandler = new ResponseHandler<Integer>() {

                @Override
                public Integer handleResponse(
                        final HttpResponse response) throws IOException {
                    return response.getStatusLine().getStatusCode();
                }

            };
            Integer responseCode  = httpclient.execute(httpget, responseHandler);
            logger.debug("responseCode: {}", responseCode);

            return responseCode == 200;
        }
    }
}
