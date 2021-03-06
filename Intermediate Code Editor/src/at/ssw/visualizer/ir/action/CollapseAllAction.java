package at.ssw.visualizer.ir.action;

import at.ssw.visualizer.ir.icons.Icons;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.fold.FoldHierarchy;
import org.netbeans.api.editor.fold.FoldUtilities;
import org.netbeans.editor.Utilities;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

public final class CollapseAllAction extends CallableSystemAction {
    public void performAction() {
        JTextComponent comp = Utilities.getFocusedComponent();
        FoldHierarchy hierarchy = FoldHierarchy.get(comp);
        FoldUtilities.collapseAll(hierarchy);
    }

    public String getName() {
        return "Collapse All";
    }

    @Override
    protected String iconResource() {
        return Icons.COLLAPSE_ALL;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

}
