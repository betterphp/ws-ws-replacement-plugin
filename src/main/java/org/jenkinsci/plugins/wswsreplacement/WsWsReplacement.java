package org.jenkinsci.plugins.wswsreplacement;

import java.io.File;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Node;
import hudson.model.Slave;
import jenkins.model.Jenkins;
import hudson.model.TopLevelItem;
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
            String path = ((Jenkins) node).getRawWorkspaceDir();

            return new FilePath(new File(path));
        } else if (node instanceof Slave) {
            return ((Slave) node).getWorkspaceRoot();
        } else {
            String className = node.getClass().toString();

            throw new IllegalArgumentException("Unhandled node type: ".concat(className));
        }
    }
}
