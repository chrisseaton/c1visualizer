package at.ssw.positionmanager;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Thomas Wuerthinger
 */
public class LayoutGraph {


    private Set<? extends Link> links;
    private SortedSet<Vertex> vertices;
    private Hashtable<Vertex, Set<Port>> inputPorts;
    private Hashtable<Vertex, Set<Port>> outputPorts;
    private Hashtable<Port, Set<Link>> portLinks;


    public LayoutGraph(Set<? extends Link> links) {
        this(links, new HashSet<Vertex>());
    }

    /** Creates a new instance of LayoutGraph */
    public LayoutGraph(Set<? extends Link> links, Set<? extends Vertex> additionalVertices) {
        this.links = links;
        assert verify();

        vertices = new TreeSet<Vertex>();
        portLinks = new Hashtable<Port, Set<Link>>();
        inputPorts = new Hashtable<Vertex, Set<Port>>();
        outputPorts = new Hashtable<Vertex, Set<Port>>();

        for(Link l : links) {
            Port p = l.getFrom();
            Port p2 = l.getTo();
            Vertex v1 = p.getVertex();
            Vertex v2 = p2.getVertex();

            if(!vertices.contains(v1)) {

                outputPorts.put(v1, new HashSet<Port>());
                inputPorts.put(v1, new HashSet<Port>());
                vertices.add(v1);
            }

            if(!vertices.contains(v2)) {
                vertices.add(v2);
                outputPorts.put(v2, new HashSet<Port>());
                inputPorts.put(v2, new HashSet<Port>());
            }

            if(!portLinks.containsKey(p)) {
                HashSet<Link> hashSet = new HashSet<Link>();
                portLinks.put(p, hashSet);
            }

            if(!portLinks.containsKey(p2)) {
                portLinks.put(p2, new HashSet<Link>());
            }

            outputPorts.get(v1).add(p);
            inputPorts.get(v2).add(p2);

            portLinks.get(p).add(l);
            portLinks.get(p2).add(l);
        }

        for(Vertex v : additionalVertices) {
            if(!vertices.contains(v)) {
                outputPorts.put(v, new HashSet<Port>());
                inputPorts.put(v, new HashSet<Port>());
                vertices.add(v);
            }
        }
    }

    public Set<Port> getInputPorts(Vertex v) {
        return this.inputPorts.get(v);
    }

    public Set<Port> getOutputPorts(Vertex v) {
        return this.outputPorts.get(v);
    }

    public Set<Link> getPortLinks(Port p) {
        return portLinks.get(p);
    }

    public Set<? extends Link> getLinks() {
        return links;
    }

    public boolean verify() {
        return true;
    }

    public SortedSet<Vertex> getVertices() {
        return vertices;
    }

    private void markNotRoot(Set<Vertex> notRootSet, Set<Vertex> visited, Vertex v, Vertex startingVertex) {

        if(visited.contains(v)) return;
        if(v != startingVertex) {
            notRootSet.add(v);
        }
        visited.add(v);
        Set<Port> outPorts = getOutputPorts(v);
        for(Port p : outPorts) {
            Set<Link> links = getPortLinks(p);
            for(Link l : links) {
                Port other = l.getTo();
                Vertex otherVertex = other.getVertex();
                markNotRoot(notRootSet, visited, otherVertex, startingVertex);
            }
        }
    }

    // Returns a set of vertices with the following properties:
    // - All Vertices in the set startingRoots are elements of the set.
    // - When starting a DFS at every vertex in the set, every vertex of the
    //   whole graph is visited.
    public Set<Vertex> findRootVertices(Set<Vertex> startingRoots) {

        Set<Vertex> notRootSet = new HashSet<Vertex>();
        for(Vertex v : startingRoots) {
            if(!notRootSet.contains(v)) {
                Set<Vertex> visited = new HashSet<Vertex>();
                markNotRoot(notRootSet, visited, v, v);
            }
        }

        Set<Vertex> vertices = getVertices();
        for(Vertex v : vertices) {
            if(!notRootSet.contains(v)) {
                Set<Vertex> visited = new HashSet<Vertex>();
                markNotRoot(notRootSet, visited, v, v);
            }
        }

        Set<Vertex> result = new HashSet<Vertex>();
        for(Vertex v : vertices) {
            if(!notRootSet.contains(v)) {
                result.add(v);
            }
        }

        return result;
    }


    public Set<Vertex> findRootVertices() {
        return findRootVertices(new HashSet<Vertex>());
    }

    public Set<Cluster> getClusters() {

        Set<Cluster> clusters = new HashSet<Cluster>();
        for(Vertex v : getVertices()) {
            if(v.getCluster() != null) {
                clusters.add(v.getCluster());
            }
        }

        return clusters;
    }

}
