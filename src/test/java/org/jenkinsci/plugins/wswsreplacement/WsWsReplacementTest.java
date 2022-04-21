package org.jenkinsci.plugins.wswsreplacement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.slaves.DumbSlave;

import org.junit.Rule;
import org.junit.Test;

import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockFolder;

//
// Whitespace replacement tests
//
public class WsWsReplacementTest
{
    @Rule public JenkinsRule JenkinsRule = new JenkinsRule();

    @Test
    public void freeStyleProjectReplacesSpaces() throws Exception
    {
        // Get a slave to test against
        DumbSlave dumbSlave = JenkinsRule.createOnlineSlave();

        String actualPath = createProjectAndGetPath(dumbSlave, "Project With Spaces");
        String expectedPath = dumbSlave.getRootPath() + "/workspace/Project_With_Spaces";

        // Check the path has been updated correctly
        assertThat( actualPath,
                    equalTo(expectedPath));
    }

    @Test
    public void freeStyleProjectReplacesMultipleSpaces() throws Exception
    {
        // Get a slave to test against
        DumbSlave dumbSlave = JenkinsRule.createOnlineSlave();

        String actualPath = createProjectAndGetPath(dumbSlave, "Project   With   Multiple   Spaces");
        String expectedPath = dumbSlave.getRootPath() + "/workspace/Project_With_Multiple_Spaces";

        // Check the path has been updated correctly
        assertThat( actualPath,
                equalTo(expectedPath));
    }

    @Test
    public void freeStyleProjectReplacesOtherSpecialCharacters() throws Exception
    {
        // Get a slave to test against
        DumbSlave dumbSlave = JenkinsRule.createOnlineSlave();

        String actualPath = createProjectAndGetPath(dumbSlave, "Project-With-A-Stupid-Name (second edition)");
        String expectedPath = dumbSlave.getRootPath() + "/workspace/Project_With_A_Stupid_Name_second_edition_";

        // Check the path has been updated correctly
        assertThat( actualPath,
                equalTo(expectedPath));
    }

    @Test
    public void freeStyleProjectReplacesStartSpaces() throws Exception
    {
        // Get a slave to test against
        DumbSlave dumbSlave = JenkinsRule.createOnlineSlave();

        String actualPath = createProjectAndGetPath(dumbSlave, " Project With Start Spaces");
        String expectedPath = dumbSlave.getRootPath() + "/workspace/_Project_With_Start_Spaces";

        // Check the path has been updated correctly
        assertThat( actualPath,
                equalTo(expectedPath));
    }

    @Test
    public void freeStyleProjectReplacesEndSpaces() throws Exception
    {
        // Get a slave to test against
        DumbSlave dumbSlave = JenkinsRule.createOnlineSlave();

        String actualPath = createProjectAndGetPath(dumbSlave, " Project With End Spaces ");
        String expectedPath = dumbSlave.getRootPath() + "/workspace/_Project_With_End_Spaces_";

        // Check the path has been updated correctly
        assertThat( actualPath,
                equalTo(expectedPath));
    }

    @Test
    public void freeStyleProjectWithNoSpacesStaysTheSame() throws Exception
    {
        // Get a slave to test against
        DumbSlave dumbSlave = JenkinsRule.createOnlineSlave();

        String actualPath = createProjectAndGetPath(dumbSlave, "ProjectNoSpaces");
        String expectedPath = dumbSlave.getRootPath() + "/workspace/ProjectNoSpaces";

        // Check the path has been updated correctly
        assertThat( actualPath,
                equalTo(expectedPath));
    }

    @Test
    public void folderHierarchyReplacesSpaces() throws Exception
    {
        // Get a slave to test against
        DumbSlave dumbSlave = JenkinsRule.createOnlineSlave();

        // Create a project
        MockFolder mockFolder = JenkinsRule.createFolder("Folder With Spaces");
        FreeStyleProject freeStyleProject = mockFolder.createProject(FreeStyleProject.class, "Project With Spaces");

        // Ass the slave to the job
        freeStyleProject.setAssignedNode(dumbSlave);

        // Set up a build which will set up the project
        FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        String actualPath = freeStyleBuild.getWorkspace().getRemote();
        String expectedPath = dumbSlave.getRootPath() + "/workspace/Folder_With_Spaces/Project_With_Spaces";

        // Check the path has been updated correctly
        assertThat( actualPath,
                    equalTo(expectedPath));
    }

    //
    // Returns the path of the creates project
    //
    private String createProjectAndGetPath(DumbSlave dumbSlave, String projectName) throws Exception
    {
        // Create a project and add it to the slave
        FreeStyleProject freeStyleProject = JenkinsRule.createFreeStyleProject(projectName);
        freeStyleProject.setAssignedNode(dumbSlave);

        // Set up a build which will set up the project
        FreeStyleBuild freeStyleBuild = freeStyleProject.scheduleBuild2(0).get();

        // Return the path of the job
        return freeStyleBuild.getWorkspace().getRemote();
    }
}
