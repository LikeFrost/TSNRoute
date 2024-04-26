package Route.GraphEntity;

public class LinkUse {
    public int srcNode;
    public int dstNode;
    public Timeslot timeslot;

    public LinkUse() {
    }

    public LinkUse(int srcNode, int dstNode, Timeslot timeslot) {
        this.srcNode = srcNode;
        this.dstNode = dstNode;
        this.timeslot = timeslot;
    }
}
