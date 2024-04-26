package Route.GraphEntity;

import java.util.Objects;

public class Timeslot {
    public int startTime;
    public int duration;

    public Timeslot() {
    }
    public Timeslot(int startTime, int duration) {
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeslot that = (Timeslot) o;
        return Objects.equals(startTime, that.startTime) &&
                Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, duration);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Timeslot {\n");
        sb.append("    startTime: ").append(startTime).append("\n");
        sb.append("    duration: ").append(duration).append("\n");
        sb.append("}");
        return sb.toString();
    }
}