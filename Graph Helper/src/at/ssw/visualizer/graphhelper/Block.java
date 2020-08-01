package at.ssw.visualizer.graphhelper;

import java.util.LinkedList;

/**
 * A Block is a connected component. This class is used during the planarity check of a
 * graph.
 *
 * @author Stefan Loidl
 */
public class Block {

    LinkedList<Integer> Latt, Ratt;
    LinkedList<Edge> Lseg, Rseg;

    /** Creates a new instance of Block */
    public Block(Edge e, LinkedList<Integer> A) {
        Latt=new LinkedList<Integer>();
        Ratt=new LinkedList<Integer>();
        Lseg=new LinkedList<Edge>();
        Rseg=new LinkedList<Edge>();

        Lseg.add(e);
        Latt= new LinkedList<Integer>(A);
        A.clear();
    }

    /** Interchanges the two sides of the block*/
    public void flip(){
        LinkedList<Integer> ha;
        LinkedList<Edge> he;

        ha=Ratt; Ratt=Latt; Latt=ha;
        he=Rseg; Rseg=Lseg; Lseg=he;
    }

    public Integer headOfLatt() {
        return Latt.getFirst();
    }

    public boolean emptyLatt(){
        return Latt.isEmpty();
    }

    public Integer headOfRatt() {
        return Ratt.getFirst();
    }

    public boolean emptyRatt(){
        return Ratt.isEmpty();
    }

    /** check for interlacing with the left side of the topmost block of S*/
    public boolean leftInterlace(LinkedList<Block> S){
        assert !Latt.isEmpty();

        if(!S.isEmpty() && !S.getFirst().emptyLatt() && Latt.getLast().intValue() < S.getFirst().headOfLatt().intValue())
            return true;
        else return false;
    }

    /** check for interlacing with the right side of the topmost block of S*/
    public boolean rightInterlace(LinkedList<Block> S){
        assert !Latt.isEmpty();

        if(!S.isEmpty() && !S.getFirst().emptyRatt() && Latt.getLast().intValue() < S.getFirst().headOfRatt().intValue())
            return true;
        else return false;
    }

    /** Add block Bprime to the rear of this block*/
    public void combine(Block Bprime){
        Latt.addAll(Bprime.Latt);
        Ratt.addAll(Bprime.Ratt);
        Lseg.addAll(Bprime.Lseg);
        Rseg.addAll(Bprime.Rseg);
    }

    /** Remove all attachments to w; there may be several */
    public boolean clean(int dfsNumW){
        while(!Latt.isEmpty() && Latt.getFirst().intValue()==dfsNumW) Latt.removeFirst();
        while(!Ratt.isEmpty() && Ratt.getFirst().intValue()==dfsNumW) Ratt.removeFirst();

        if(!Latt.isEmpty() || !Ratt.isEmpty()) return false;

        // If Latt and Ratt are empty we reorder the placement of the subsegments in alpha
        for(Edge e:Lseg) ((DiGraph.PlanarityEdgePayload)e.data).alpha=DiGraph.LEFT;
        for(Edge e:Rseg) ((DiGraph.PlanarityEdgePayload)e.data).alpha=DiGraph.RIGHT;

        return true;
    }

    /** Add a Block to the rear of Att. Flip if necessary*/
    public void addToAtt(LinkedList<Integer> Att, int dfsnumW0){
        if(!Ratt.isEmpty() && headOfRatt().intValue() > dfsnumW0) flip();
        Att.addAll(Latt);
        Latt.clear();
        Att.addAll(Ratt);
        Ratt.clear();

        //Ratt is either empty or {w0}. Also if Ratt is non-empty then all subsequent sets are
        //contained in {w0}. So we indeed compute an ordered set of attachments.
        for(Edge e:Lseg) ((DiGraph.PlanarityEdgePayload)e.data).alpha=DiGraph.LEFT;
        for(Edge e:Rseg) ((DiGraph.PlanarityEdgePayload)e.data).alpha=DiGraph.RIGHT;
    }



}
