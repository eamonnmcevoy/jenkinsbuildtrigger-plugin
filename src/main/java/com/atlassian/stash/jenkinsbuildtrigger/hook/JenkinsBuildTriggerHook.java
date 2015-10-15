package com.atlassian.stash.jenkinsbuildtrigger.hook;

import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.setting.*;
import com.atlassian.stash.jenkinsbuildtrigger.hook.URLBuilder;

import java.net.URL;
import java.util.Collection;

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

        URLBuilder urlBuilder = new URLBuilder();
        String url = urlBuilder.buildUrl(context, (RefChange)refChanges.toArray()[0]);
        startBuild(url);
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