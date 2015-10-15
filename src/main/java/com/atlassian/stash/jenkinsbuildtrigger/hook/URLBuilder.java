package com.atlassian.stash.jenkinsbuildtrigger.hook;

import com.atlassian.stash.hook.repository.RepositoryHookContext;
import com.atlassian.stash.repository.RefChange;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by emcevoy on 15/10/15.
 */
public class URLBuilder {
    public URLBuilder() {}

    public String buildUrl(RepositoryHookContext context, RefChange change)
    {
        String url = context.getSettings().getString("url");
        String token = context.getSettings().getString("token");
        String branch = change.getRefId();
        String previousCommit = change.getFromHash();
        String newCommit = change.getToHash();

        String buildUrl = url + "/buildWithParameters" +
                "?token=" + token +
                "&BRANCH_TO_BUILD=" + branch +
                "&PREVIOUS_COMMIT=" + previousCommit +
                "&NEW_COMMIT=" + newCommit;

        return (isBranchNameValid(context, branch) && isNewCommitHashValid(newCommit,previousCommit)) ?
                buildUrl :
                "";
    }

    private boolean isBranchNameValid(RepositoryHookContext context, String branch)
    {
        String branchRegex = context.getSettings().getString("branchRegex");
        return Pattern.matches(branchRegex, branch);
    }

    private boolean isNewCommitHashValid(String newCommit, String previousCommit)
    {
        return (previousCommit != newCommit);
    }
}
