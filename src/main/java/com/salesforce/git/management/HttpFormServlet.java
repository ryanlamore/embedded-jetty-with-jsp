package com.salesforce.git.management;

import com.salesforce.git.management.util.EmailUtil;
import com.salesforce.git.management.util.GitHubUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class HttpFormServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HttpFormServlet.class);

    public enum Parameter {
        email(true),
        repository_name(true),
        description(true),
        commiters(true),
        users(false),
        ;

        private boolean required;

        Parameter(boolean required) {
            this.required = required;
        }

        public boolean isRequired() {
            return required;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("parameterMap: {}", req.getParameterMap());

        List<String> errors = new LinkedList<>();

        validateRequiredParametersPresent(errors, req);

        GitHubUtil gitUtil = new GitHubUtil();
        validateGitHubUsers(errors, gitUtil, req.getParameter(Parameter.commiters.name()));
        validateGitHubUsers(errors, gitUtil, req.getParameter(Parameter.users.name()));

        validateRepositoryDoesntExist(errors, gitUtil, req.getParameter(Parameter.repository_name.name()));

        StringBuilder responseHtml = new StringBuilder();
        if (errors.isEmpty()) {
            responseHtml.append("<h1>GitHub Repository Request Submitted</h1>");
            StringBuilder emailText = new StringBuilder();
            for (Parameter parameter : Parameter.values()) {
                emailText.append(parameter).append(": ").append(req.getParameter(parameter.name())).append("\n");
            }
            logger.debug("emailText: {}", emailText);

            new EmailUtil("localhost").sendEmail(emailText.toString());
        } else {
            logger.debug("Errors: {}", errors);
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(responseHtml);
    }

    void validateRequiredParametersPresent(List<String> errors, HttpServletRequest req) {
        for (Parameter parameterName : Parameter.values()) {
            if (parameterName.required) {
                String param = req.getParameter(parameterName.name());
                if (param == null || param.isEmpty()) {
                    errors.add("Missing Parameter: " + parameterName);
                }
            }
        }
    }

    void validateRepositoryDoesntExist(List<String> errors, GitHubUtil gitUtil, String repositoryName) throws IOException {
        if (gitUtil.respositoryExistsOnGitHub(repositoryName)) {
            errors.add(String.format("Repository already exists: %s", repositoryName));
        }
    }

    void validateGitHubUsers(List<String> errors, GitHubUtil gitUtil, String usersStr) throws IOException {
        if (usersStr != null && !usersStr.isEmpty()) {
            for (String user : usersStr.split(",")) {
                if (!gitUtil.userExistsOnGitHub(user)) {
                    errors.add(String.format("Invalid User: %s", user));
                }
            }
        }
    }


}
