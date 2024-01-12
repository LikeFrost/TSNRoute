package Route.GraphEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Connection {
    public String connectionId;
    public String streamId;
    public String linkId;
    public String srcNodeId;
    public String srcPortId;
    public String dstNodeId;
    public String dstPortId;
    public int trailStep;
    public int hyperPeriod;
    public List<Timeslot> timeslot;

    public List<Timeslot> getTimeslot() {
        Collections.sort(timeslot, new Comparator<Timeslot>() {
            @Override
            public int compare(Timeslot t1, Timeslot t2) {
                return Integer.compare(t1.startTime, t2.startTime);
            }
        });
        return timeslot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Objects.equals(connectionId, that.connectionId) &&
                Objects.equals(streamId, that.streamId) &&
                Objects.equals(linkId, that.linkId) &&
                Objects.equals(srcNodeId, that.srcNodeId) &&
                Objects.equals(srcPortId, that.srcPortId) &&
                Objects.equals(dstNodeId, that.dstNodeId) &&
                Objects.equals(dstPortId, that.dstPortId) &&
                Objects.equals(trailStep, that.trailStep) &&
                Objects.equals(hyperPeriod, that.hyperPeriod) &&
                Objects.equals(timeslot, that.timeslot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionId, streamId, linkId, srcNodeId, srcPortId, dstNodeId, dstPortId, trailStep, hyperPeriod, timeslot);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BasicConnectionDto {\n");
        sb.append("    connectionId: ").append(connectionId).append("\n");
        sb.append("    streamId: ").append(streamId).append("\n");
        sb.append("    linkId: ").append(linkId).append("\n");
        sb.append("    srcNodeId: ").append(srcNodeId).append("\n");
        sb.append("    srcPortId: ").append(srcPortId).append("\n");
        sb.append("    dstNodeId: ").append(dstNodeId).append("\n");
        sb.append("    dstPortId: ").append(dstPortId).append("\n");
        sb.append("    trailStep: ").append(trailStep).append("\n");
        sb.append("    hyperPeriod: ").append(hyperPeriod).append("\n");
        sb.append("    timeslot: ").append(timeslot).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
