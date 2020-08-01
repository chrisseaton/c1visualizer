package at.ssw.visualizer.block.view;

import at.ssw.visualizer.core.selection.Selection;
import at.ssw.visualizer.core.selection.SelectionManager;
import at.ssw.visualizer.model.cfg.BasicBlock;
import at.ssw.visualizer.model.cfg.ControlFlowGraph;
import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * TopComponent which displays the BlockView.
 *
 * @author Bernhard Stiftner
 * @author Christian Wimmer
 */
final class BlockViewTopComponent extends TopComponent {
    private ControlFlowGraph curCFG;
    private BasicBlock[] curBlocks;

    private JTable blockTable;
    private BlockTableModel tableModel;
    private boolean selectionUpdating;

    private BlockViewTopComponent() {
        setName("Blocks");
        setToolTipText("List of Blocks");

        tableModel = new BlockTableModel();
        blockTable = new JTable(tableModel);
        blockTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        blockTable.setRowMargin(0);
        blockTable.getColumnModel().setColumnMargin(0);
        blockTable.setShowGrid(false);
        blockTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < BlockTableModel.COLUMN_WIDTHS.length; i++) {
            blockTable.getColumnModel().getColumn(i).setPreferredWidth(BlockTableModel.COLUMN_WIDTHS[i]);
        }
        blockTable.getSelectionModel().addListSelectionListener(listSelectionListener);

        JScrollPane scrollPane = new JScrollPane(blockTable);
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BorderLayout());
        add(scrollPane);
    }

    @Override
    protected void componentShowing() {
        super.componentShowing();
        SelectionManager.getDefault().addChangeListener(selectionChangeListener);
        updateContent();
    }

    @Override
    protected void componentHidden() {
        super.componentHidden();
        SelectionManager.getDefault().removeChangeListener(selectionChangeListener);
        selectionUpdating = true;
        curCFG = null;
        curBlocks = null;
        tableModel.setControlFlowGraph(null);
        selectionUpdating = false;
    }


    private ChangeListener selectionChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent event) {
            updateContent();
        }
    };

    protected void updateContent() {
        if (selectionUpdating) {
            return;
        }
        selectionUpdating = true;
        Selection selection = SelectionManager.getDefault().getCurSelection();
        ControlFlowGraph newCFG = selection.get(ControlFlowGraph.class);
        BasicBlock[] newBlocks = selection.get(BasicBlock[].class);

        if (curCFG != newCFG) {
            // This resets a user-defined sorting.
            blockTable.setAutoCreateRowSorter(true);
            tableModel.setControlFlowGraph(newCFG);
            curBlocks = null;
        }
        
        if (newBlocks != null) { 
            if(newBlocks.length == 0) {
                blockTable.clearSelection();
            } else if (!Arrays.equals(curBlocks, newBlocks)) {
                Map<Object, BasicBlock> blockNames = new HashMap<Object, BasicBlock>();
                for (BasicBlock block : newBlocks) {
                    blockNames.put(block.getName(), block);
                }

                blockTable.clearSelection();
                for (int i = blockTable.getModel().getRowCount() - 1; i >= 0; i--) {
                    BasicBlock block = blockNames.get(blockTable.getValueAt(i, 0));
                    if (block != null) {
                        blockTable.addRowSelectionInterval(i, i);
                        blockTable.scrollRectToVisible(blockTable.getCellRect(i, 0, true));
                    }
                }
            }
        }
        curCFG = newCFG;
        curBlocks = newBlocks;
        selectionUpdating = false;
    }


    private ListSelectionListener listSelectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent event) {
            updateSelection();
        }
    };

    private void updateSelection() {
        if (selectionUpdating) {
            return;
        }
        selectionUpdating = true;

        List<BasicBlock> blocks = new ArrayList<BasicBlock>();
        for (int i = 0; i < blockTable.getModel().getRowCount(); i++) {
            if (blockTable.getSelectionModel().isSelectedIndex(i)) {
                blocks.add(curCFG.getBasicBlockByName((String) blockTable.getValueAt(i, 0)));
            }
        }

        curBlocks = blocks.toArray(new BasicBlock[blocks.size()]);
        Selection selection = SelectionManager.getDefault().getCurSelection();
        selection.put(curBlocks);
        
        selectionUpdating = false;
    }

    // <editor-fold defaultstate="collapsed" desc=" Singleton and Persistence Code ">
    private static final String PREFERRED_ID = "BlockViewTopComponent";
    private static BlockViewTopComponent instance;

    public static synchronized BlockViewTopComponent getDefault() {
        if (instance == null) {
            instance = new BlockViewTopComponent();
        }
        return instance;
    }

    public static synchronized BlockViewTopComponent findInstance() {
        return (BlockViewTopComponent) WindowManager.getDefault().findTopComponent(PREFERRED_ID);
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public Object writeReplace() {
        return new ResolvableHelper();
    }

    static final class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return BlockViewTopComponent.getDefault();
        }
    }
    // </editor-fold>
}
