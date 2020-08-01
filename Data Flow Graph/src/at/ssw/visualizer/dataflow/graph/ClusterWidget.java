package at.ssw.visualizer.dataflow.graph;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Stefan Loidl
 */
public class ClusterWidget extends Widget{

    private Collection<Widget> widgets;
    private String id;

    private Border VISIBLEBORDER;
    private static Border INVISIBLEBORDER=BorderFactory.createEmptyBorder();

    private static final int DASHSIZE=10;


    private static final int PADDING=10;
    //This value has to be applied to the
    //position because of an error in border framework
    private static final int CORRECT=5;

    private boolean visible=true;

    private LabelWidget label;


    /** Creates a new instance of ClusterWidget */
    public ClusterWidget(String id, Collection<Widget> widgets, Scene scene, Color col) {
        super(scene);
        this.widgets=widgets;
        this.id=id;
        VISIBLEBORDER=BorderFactory.createDashedBorder(col,DASHSIZE,DASHSIZE/2,true);

        label=new LabelWidget(scene,id);
        label.setForeground(col);
        addChild(label);

        //If edit suppored by scene
        if(scene instanceof EditProvider){
            WidgetAction.Chain actions = getActions ();
            actions.addAction(ActionFactory.createEditAction((EditProvider)scene));
        }
    }

    /** Returns the id of the cluster */
    public String getId(){
        return id;
    }

    /** Returns the clustered widgets */
    public Collection<Widget> getWidgets(){
        return widgets;
    }

    /** Sets the border of the widget to modify its visibility*/
    private void setWidgetVisible(boolean v){
        removeChildren();
        if(v) {
            this.setBorder(VISIBLEBORDER);
            addChild(label);
        }
        else this.setBorder(INVISIBLEBORDER);
    }

    /** Defines if border is shown*/
    public void setVisibility(boolean b){
        visible=b;
    }

    /** Refreshes the Compoments- size and position*/
    public void refresh(){
        if(widgets!=null){
            int minX=Integer.MAX_VALUE;
            int minY=Integer.MAX_VALUE;
            int maxX=Integer.MIN_VALUE;
            int maxY=Integer.MIN_VALUE;
            boolean changed=false;

            for(Widget w:widgets){
                //discard invisible widgets
                if(w instanceof InstructionNodeWidget){
                    if(!((InstructionNodeWidget)w).isWidgetVisible()) continue;
                }

                Point p=w.getPreferredLocation();
                Rectangle r=w.getBounds();

                if(w!=null && r!=null){
                    if(minX > p.x) minX=p.x;
                    if(minY > p.y) minY=p.y;
                    if(maxX < p.x+r.width) maxX=p.x+r.width;
                    if(maxY < p.y+r.height) maxY=p.y+r.height;
                    changed=true;
                }
            }

            if(changed && visible){
                setPreferredBounds(new Rectangle(maxX-minX+2*PADDING,maxY-minY+2*PADDING));
                setPreferredLocation(new Point(minX-PADDING-CORRECT,minY-PADDING-CORRECT));
                setWidgetVisible(true);
                revalidate();
            }
            else setWidgetVisible(false);
        }
    }



}
