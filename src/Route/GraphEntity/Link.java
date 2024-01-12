package Route.GraphEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Link {
    public int srcNode;
    public int dstNode;
    public int hops;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(srcNode, link.srcNode) && Objects.equals(dstNode, link.dstNode) && Objects.equals(hops, link.hops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcNode, dstNode, hops);
    }

    public static List<Link> getLinks(MyPath path) {
        List<Link> links = new ArrayList<>();
        for (int i = 1; i < path.path.size(); i++) {
            Link link = new Link();
            link.srcNode = path.path.get(i-1);
            link.dstNode = path.path.get(i);
            link.hops = i;
            links.add(link);
        }
        return links;
    }
}
