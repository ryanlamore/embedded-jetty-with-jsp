package com.salesforce.git.management;

import com.salesforce.git.management.util.GitHubUtil;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests the HttpFormServlet.
 * @author rlamore
 */
public class HttpFormServletTest {
    HttpFormServlet servlet;

    @BeforeMethod
    public void beforeMethod() {
        servlet = new HttpFormServlet();
    }

    @DataProvider
    Object[][] testValidParametersDP() {
        return new Object[][] {
                { "some@email.com", "repo", "desc", "commiter1", "user1", Collections.<String>emptyList()},
                // null fields:
                { null, "repo", "desc", "commiter1", null, Collections.singletonList("Missing Parameter: email")},
                { "some@email.com", null, "desc", "commiter1", "user1", Collections.singletonList("Missing Parameter: repository_name")},
                { "some@email.com", "repo", null, "commiter1", "user1", Collections.singletonList("Missing Parameter: description")},
                { "some@email.com", "repo", "desc", null, "user1", Collections.singletonList("Missing Parameter: commiters")},
                { "some@email.com", "repo", "desc", "commiter1", null, Collections.<String>emptyList()},
                // empty fields:
                { "", "repo", "desc", "commiter1", null, Collections.singletonList("Missing Parameter: email")},
                { "some@email.com", "", "desc", "commiter1", "user1", Collections.singletonList("Missing Parameter: repository_name")},
                { "some@email.com", "repo", "", "commiter1", "user1", Collections.singletonList("Missing Parameter: description")},
                { "some@email.com", "repo", "desc", "", "user1", Collections.singletonList("Missing Parameter: commiters")},
                { "some@email.com", "repo", "desc", "commiter1", "", Collections.<String>emptyList()},

        };
    }

    @Test(dataProvider = "testValidParametersDP")
    public void testValidateRequiredParametersPresent(String email, String repo, String description, String commiters, String users, List<String> expectedErrors) {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getParameter("email")).thenReturn(email);
        Mockito.when(req.getParameter("repository_name")).thenReturn(repo);
        Mockito.when(req.getParameter("description")).thenReturn(description);
        Mockito.when(req.getParameter("commiters")).thenReturn(commiters);
        Mockito.when(req.getParameter("users")).thenReturn(users);

        List<String> actualErrors = new ArrayList<>();
        servlet.validateRequiredParametersPresent(actualErrors, req);

        Assert.assertEquals(actualErrors, expectedErrors,
                String.format("Actual errors (%s) didn't match expected (%s)", actualErrors, expectedErrors));
    }

    @Test
    public void testValidateGitHubUsersSuccess() throws IOException {
        List<String> actualErrors = new ArrayList<>();
        GitHubUtil gitHubUtil = Mockito.mock(GitHubUtil.class);
        Mockito.when(gitHubUtil.userExistsOnGitHub(Mockito.anyString())).thenReturn(true);
        final String users = "user1,user2";

        servlet.validateGitHubUsers(actualErrors, gitHubUtil, users);

        Mockito.verify(gitHubUtil).userExistsOnGitHub("user1");
        Mockito.verify(gitHubUtil).userExistsOnGitHub("user2");
        Assert.assertTrue(actualErrors.isEmpty());
    }

    @DataProvider
    Object[][] emptyDP() {
        return new Object[][] {
                {null},
                {""},
        };
    }
    @Test(dataProvider = "emptyDP")
    public void testValidateGitHubUsersWithNoUser(String users) throws IOException {
        List<String> actualErrors = new ArrayList<>();
        GitHubUtil gitHubUtil = Mockito.mock(GitHubUtil.class);

        servlet.validateGitHubUsers(actualErrors, gitHubUtil, users);

        Assert.assertTrue(actualErrors.isEmpty());
        Mockito.verify(gitHubUtil, Mockito.never()).userExistsOnGitHub(Mockito.anyString());
    }

    @Test
    public void testValidateGitHubUsersWithInvalidUser() throws IOException {
        String users = "user1,user2";
        List<String> actualErrors = new ArrayList<>();
        GitHubUtil gitHubUtil = Mockito.mock(GitHubUtil.class);

        servlet.validateGitHubUsers(actualErrors, gitHubUtil, users);

        Assert.assertEquals(actualErrors, Arrays.asList("Invalid User: user1", "Invalid User: user2"));
        Mockito.verify(gitHubUtil).userExistsOnGitHub("user1");
        Mockito.verify(gitHubUtil).userExistsOnGitHub("user2");
    }

    @Test
    public void testValidateRepositoryDoesntExistSuccess() throws IOException {
        GitHubUtil gitHubUtil = Mockito.mock(GitHubUtil.class);
        Mockito.when(gitHubUtil.respositoryExistsOnGitHub(Mockito.anyString())).thenReturn(false);
        List<String> actualErrors = new ArrayList<>();

        servlet.validateRepositoryDoesntExist(actualErrors, gitHubUtil, "repoName");

        Mockito.verify(gitHubUtil).respositoryExistsOnGitHub("repoName");
        Assert.assertTrue(actualErrors.isEmpty());
    }

    @Test
    public void testValidateRepositoryDoesntExistFailure() throws IOException {
        GitHubUtil gitHubUtil = Mockito.mock(GitHubUtil.class);
        Mockito.when(gitHubUtil.respositoryExistsOnGitHub(Mockito.anyString())).thenReturn(true);
        List<String> actualErrors = new ArrayList<>();

        servlet.validateRepositoryDoesntExist(actualErrors, gitHubUtil, "repoName");

        Mockito.verify(gitHubUtil).respositoryExistsOnGitHub("repoName");
        Assert.assertEquals(actualErrors, Collections.singletonList("Repository already exists: repoName"));
    }

}
