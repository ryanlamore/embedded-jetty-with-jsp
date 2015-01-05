package com.salesforce.git.management.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Tests GitHubUserUtil.
 * @author rlamore
 */
public class GitHubUtilFTest {

    @Test
    public void testUserExistsOnGitHubWithValidUser() throws IOException {
        Assert.assertTrue(new GitHubUtil().userExistsOnGitHub("ryanlamore"), "user should exist");
    }

    @Test
    public void testUserExistsOnGitHubWithInvalidUser() throws IOException {
        Assert.assertFalse(new GitHubUtil().userExistsOnGitHub("HopefullyThisUserNeverGetsCreated9803248"), "user should NOT exist");
    }

    @Test
    public void testRepositoryExistsOnGitHubWithValidUser() throws IOException {
        Assert.assertTrue(new GitHubUtil().respositoryExistsOnGitHub("almond"), "user should exist");
    }

    @Test
    public void testRepositoryExistsOnGitHubWithInvalidUser() throws IOException {
        Assert.assertFalse(new GitHubUtil().respositoryExistsOnGitHub("HopefullyThisRepoNeverGetsCreated9803248"), "repo should NOT exist");
    }

}
