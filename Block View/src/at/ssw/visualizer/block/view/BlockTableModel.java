package at.ssw.visualizer.block.view;

import at.ssw.visualizer.model.cfg.BasicBlock;
import at.ssw.visualizer.model.cfg.ControlFlowGraph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * TableModel containing the blocks of the currently selected view
 *
 * @author Bernhard Stiftner
 * @author Christian Wimmer
 */
public class BlockTableModel extends AbstractTableModel {
    public static final String[] COLUMN_NAMES = new String[]{"Name", "BCI", "Flags", "Loop Depth", "Loop Index", "Dominator", "Predecessors", "Successors", "XHandlers"};
    public static final int[] COLUMN_WIDTHS = new int[]{60, 60, 60, 80, 80, 60, 120, 120};

    private List<BasicBlock> blocks = Collections.emptyList();

    public void setControlFlowGraph(ControlFlowGraph cfg) {
        if (cfg == null) {
            blocks = Collections.emptyList();
        } else {
            blocks = cfg.getBasicBlocks();
        }
        fireTableDataChanged();
    }

    public int getRowCount() {
        return blocks.size();
    }

    public int getColumnCount() {
        return 9;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    public Object getValueAt(int row, int column) {
        BasicBlock block = blocks.get(row);
        switch (column) {
            case 0:
                return block.getName();
            case 1:
                return "[" + block.getFromBci() + ", " + block.getToBci() + "]";
            case 2:
                return formatFlags(block.getFlags());
            case 3:
                return Integer.toString(block.getLoopDepth());
            case 4:
                return block.getLoopDepth() > 0 ? Integer.toString(block.getLoopIndex()) : "";
            case 5:
                return block.getDominator() != null ? block.getDominator().getName() : "";
            case 6:
                return formatBlocks(block.getPredecessors());
            case 7:
                return formatBlocks(block.getSuccessors());
            case 8:
                return formatBlocks(block.getXhandlers());
            default:
                throw new Error("invalid column");
        }
    }

    private String formatFlags(List<String> flags) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (String flag : flags) {
            sb.append(prefix).append(flag);
            prefix = ", ";
        }
        return sb.toString();
    }

    private String formatBlocks(List<BasicBlock> blocks) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (BasicBlock block : blocks) {
            sb.append(prefix).append(block.getName());
            prefix = ", ";
        }
        return sb.toString();
    }
}
