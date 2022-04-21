package org.jenkinsci.plugins.wswsreplacement;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Node;
import hudson.model.Slave;
import hudson.model.TopLevelItem;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jenkins.model.Jenkins;
import jenkins.slaves.WorkspaceLocator;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Extension
@Restricted(NoExternalUse.class)
public class WsWsReplacement extends WorkspaceLocator
{
    @Override
    public FilePath locate(TopLevelItem item, Node node)
    {
        FilePath workspaceRoot = this.getWorkspaceRootForNode(node);
        String workspaceFolder = item
            .getFullName()
            .replaceAll("[^a-zA-Z0-9/]+", "_");

        return workspaceRoot.child(workspaceFolder);
    }

    private FilePath getWorkspaceRootForNode(Node node)
    {
        if (node instanceof Jenkins) {
            String rawWorkspacePath = ((Jenkins) node).getRawWorkspaceDir();
            String rootPath = ((Jenkins) node).getRootDir().getAbsolutePath();

            Pattern pattern = Pattern.compile("(.+)[/\\\\][$][{]ITEM_FULL_?NAME[}](.*)");
            Matcher matcher = pattern.matcher(rawWorkspacePath);

            if (!matcher.matches()) {
                throw new IllegalStateException("Could not find match in ".concat(rawWorkspacePath));
            }

            String resolvedPath = matcher.group(1).replace("${JENKINS_HOME}", rootPath);

            return new FilePath(new File(resolvedPath));
        } else if (node instanceof Slave) {
            return ((Slave) node).getWorkspaceRoot();
        } else {
            String className = node.getClass().toString();

            throw new IllegalArgumentException("Unhandled node type: ".concat(className));
        }
    }
}
