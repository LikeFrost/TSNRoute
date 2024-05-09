package Route;

import Route.GraphEntity.*;
import Route.ScheduleMethods.IPL;
import Route.ScheduleMethods.MyTabu;
import Route.ScheduleMethods.TabuSearch;
import Route.Utils.NavigationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleWrapper {
    MyGraph graph;
    List<Flow> flows;
    String algorithm;
    int hyperPeriod;

    public ScheduleWrapper(MyGraph graph, List<Flow> flows, String algorithm, int hyperPeriod) {
        this.graph = graph;
        this.flows = flows;
        this.algorithm = algorithm;
        this.hyperPeriod = hyperPeriod;
    }

    public Map<String, Object> getIPLSchedule() {
        Map<String, Object> scheduleResult = new HashMap<>();
        //新流
        List<Flow> newFlows = new ArrayList<>();
        //流对应的路径
        List<List<Link>> newLinkPathList = new ArrayList<>();
        //流对应的路径数
        List<Integer> flowPathCount = new ArrayList<>();
        for (Flow flow : flows) {
            for (int i = 0; i < flow.redundantPath.get(0).redundantPath.size(); i++) {
                newFlows.add((flow));
                newLinkPathList.add(Link.getLinks(flow.redundantPath.get(0).redundantPath.get(i)));
            }
            flowPathCount.add(flow.redundantPath.get(0).redundantPath.size());
        }

        int[][][][] result = new int[newFlows.size()][graph.point.length][graph.point.length][hyperPeriod];
        IPL ipl = new IPL(graph, newFlows, newLinkPathList);
        long begin = System.currentTimeMillis();
        try {
            result = ipl.schedule();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("IPL算法耗时：" + (end - begin) + "ms");

        //转化为connections
        List<List<List<Connection>>> connections = new ArrayList<>();
        int k = 0;
        int successCount = 0;
        for (int i = 0; i < flows.size(); i++) {
            List<List<Connection>> connection = new ArrayList<>();
            int flag = 1;
            for (int j = 0; j < flowPathCount.get(i); j++) {
                List<Connection> temp = NavigationUtil.result2Connections(result[k++]);
                if (temp.size() == 0) {
                    flag = 0;
                }
                connection.add(temp);
            }
            connections.add(connection);
            successCount += flag;
        }
        scheduleResult.put("successRate", successCount / flows.size());
        scheduleResult.put("connections", connections);
        return scheduleResult;
    }

    public Map<String, Object> getTabuSearchSchedule() {
        List<List<List<LinkUse>>> result = new ArrayList<>();
        TabuSearch tabuSearch = new TabuSearch(graph, this.flows);
        long begin = System.currentTimeMillis();
        try {
            result = tabuSearch.schedule();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("TS算法耗时：" + (end - begin) + "ms");
        List<List<List<Connection>>> connections = new ArrayList<>();

        int successCount = 0;
        for (int i = 0; i < flows.size(); i++) {
            List<List<Connection>> connection = new ArrayList<>();
            int flag = 1;
            if (flows.get(i).selectedPath != null) {
                for (int j = 0; j < flows.get(i).selectedPath.redundantPath.size(); j++) {
                    List<Connection> temp = NavigationUtil.ts2Connections(result.get(i).get(j), this.hyperPeriod / flows.get(i).period);
                    if (temp.size() == 0) {
                        flag = 0;
                    }
                    connection.add(temp);
                }
            }
            connections.add(connection);
            successCount += flag;
        }
        Map<String, Object> scheduleResult = new HashMap<>();
        scheduleResult.put("successRate", successCount / flows.size());
        scheduleResult.put("connections", connections);
        return scheduleResult;
    }

    public Map<String, Object> getMyTabuSearchSchedule() {
        List<List<List<LinkUse>>> result = new ArrayList<>();
        MyTabu tabuSearch = new MyTabu(graph, this.flows);
        long begin = System.currentTimeMillis();
        try {
            result = tabuSearch.schedule();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("MyTS算法耗时：" + (end - begin) + "ms");
        //转化为connections
        List<List<List<Connection>>> connections = new ArrayList<>();

        int successCount = 0;
        for (int i = 0; i < flows.size(); i++) {
            List<List<Connection>> connection = new ArrayList<>();
            int flag = 1;
            if (flows.get(i).selectedPath != null) {
                for (int j = 0; j < flows.get(i).selectedPath.redundantPath.size(); j++) {
                    List<Connection> temp = NavigationUtil.ts2Connections(result.get(i).get(j), this.hyperPeriod / flows.get(i).period);
                    if (temp.size() == 0) {
                        flag = 0;
                    }
                    connection.add(temp);
                }
            }
            connections.add(connection);
            successCount += flag;
        }
        Map<String, Object> scheduleResult = new HashMap<>();
        scheduleResult.put("successRate", successCount / flows.size());
        scheduleResult.put("connections", connections);
        return scheduleResult;
    }

    public Map<String, Object> getSchedule() {
        if (algorithm.equals("IPL")) {
            return getIPLSchedule();
        }

        if (algorithm.equals("TS")) {
            return getTabuSearchSchedule();
        }
        if (algorithm.equals("MyTS")) {
            return getMyTabuSearchSchedule();
        }
        return null;
    }
}
