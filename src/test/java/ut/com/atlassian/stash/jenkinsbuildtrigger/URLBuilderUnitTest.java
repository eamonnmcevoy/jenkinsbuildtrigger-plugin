package ut.com.atlassian.stash.jenkinsbuildtrigger;

import com.atlassian.stash.hook.repository.RepositoryHookContext;
import com.atlassian.stash.repository.RefChange;
import com.atlassian.stash.setting.Settings;
import org.junit.Test;
import com.atlassian.stash.jenkinsbuildtrigger.hook.URLBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class URLBuilderUnitTest
{
    String testUrl = "testUrl";
    String testToken = "testToken";
    String testBranchRegex = ".*";

    String testRefId = "testRefId";
    String testFromHash = "testFromHash";
    String testToHash = "testToHash";

    @Test
    public void testUrlIsBuiltCorrectly()
    {
        URLBuilder urlBuilder = new URLBuilder();

        RepositoryHookContext mockContext = getRepositoryHookContext(testUrl,testToken,testBranchRegex);
        RefChange mockRefChange = getRefChange(testRefId,testFromHash,testToHash);

        String expected = testUrl + "/buildWithParameters" +
                          "?token=" + testToken +
                          "&BRANCH_TO_BUILD=" + testRefId +
                          "&PREVIOUS_COMMIT=" + testFromHash +
                          "&NEW_COMMIT=" + testToHash;
        String output = urlBuilder.buildUrl(mockContext, mockRefChange);

        assertEquals(expected,output);
    }

    @Test
    public void testUrlIsNotBuiltIfBranchRegexDoesNotMatchRefId()
    {
        URLBuilder urlBuilder = new URLBuilder();

        RepositoryHookContext mockContext = getRepositoryHookContext(testUrl,testToken,"");
        RefChange mockRefChange = getRefChange(testRefId,testFromHash,testToHash);

        String expected = "";
        String output = urlBuilder.buildUrl(mockContext, mockRefChange);

        assertEquals(expected,output);
    }

    @Test
    public void testUrlIsNotBuiltIfFromHashAndToHashAreTheSame()
    {
        URLBuilder urlBuilder = new URLBuilder();

        RepositoryHookContext mockContext = getRepositoryHookContext(testUrl,testToken,"");
        RefChange mockRefChange = getRefChange(testRefId,"hash","hash");

        String expected = "";
        String output = urlBuilder.buildUrl(mockContext, mockRefChange);

        assertEquals(expected,output);
    }

    private RepositoryHookContext getRepositoryHookContext(String url, String token, String branchRegex)
    {
        Settings mockSettings = mock(Settings.class);
        when(mockSettings.getString("url")).thenReturn(url);
        when(mockSettings.getString("token")).thenReturn(token);
        when(mockSettings.getString("branchRegex")).thenReturn(branchRegex);

        RepositoryHookContext mockContext = mock(RepositoryHookContext.class);
        when(mockContext.getSettings()).thenReturn(mockSettings);

        return mockContext;
    }

    private RefChange getRefChange(String refId, String fromHash, String toHash)
    {
        RefChange mockRefChange = mock(RefChange.class);
        when(mockRefChange.getRefId()).thenReturn(refId);
        when(mockRefChange.getFromHash()).thenReturn(fromHash);
        when(mockRefChange.getToHash()).thenReturn(toHash);

        return mockRefChange;
    }
}