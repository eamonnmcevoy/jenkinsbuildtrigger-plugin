package com.atlassian.stash.jenkinsbuildtrigger.hook;

import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.setting.*;

import java.net.URL;
import java.util.Collection;
import java.util.regex.Pattern;

public class JenkinsBuildTriggerHook implements AsyncPostReceiveRepositoryHook, RepositorySettingsValidator
{
    /**
     * Connects to a configured URL to notify of all changes.
     */
    @Override
    public void postReceive(RepositoryHookContext context, Collection<RefChange> refChanges)
    {
        if(refChanges.isEmpty())
            return;

        startBuild(
                buildUrl(context,
                        (RefChange)refChanges.toArray()[0]));
    }

    @Override
    public void validate(Settings settings, SettingsValidationErrors errors, Repository repository)
    {
        if (settings.getString("url", "").isEmpty())
        {
            errors.addFieldError("url", "Url field is blank, please supply one");
        }
        if (settings.getString("branchRegex", "").isEmpty())
        {
            errors.addFieldError("branchRegex", "Branch regex field is blank, please supply one");
        }
        if (settings.getString("token", "").isEmpty())
        {
            errors.addFieldError("token", "Authentication token field is blank, please supply one");
        }
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

    private String buildUrl(RepositoryHookContext context, RefChange change)
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

        return (isBranchNameValid(context, branch) || isNewCommitHashValid(newCommit,previousCommit)) ?
                 buildUrl :
                 "";
    }

    private void startBuild(String url)
    {
        if (url != null)
        {
            try
            {
                new URL(url).openConnection().connect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}