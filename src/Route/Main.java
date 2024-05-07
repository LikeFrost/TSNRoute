package Route;

import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    static MyGraph g;
    static List<Flow> flowList = new ArrayList<>();
    static int hyperPeriod = 0;

    public static void init() {
        Gson gson = new Gson();
        try {
            FileReader reader = new FileReader("initialData.json");
            Map<String, Object> data = gson.fromJson(reader, Map.class);
            reader.close();
            g = gson.fromJson(gson.toJson(data.get("graph")), MyGraph.class);
            Type flowListType = new TypeToken<List<Flow>>() {
            }.getType();
            flowList = gson.fromJson(gson.toJson(data.get("flows")), flowListType);
            hyperPeriod = gson.fromJson(gson.toJson(data.get("hyperPeriod")), Integer.class);
        } catch (Exception e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }

    public static void output(Map<String, Object> scheduleResult, String name) {
        try {
            FileWriter writer = new FileWriter(name + ".json");
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            map.put("flows", flowList);
            map.put("connections", scheduleResult);
            writer.write(gson.toJson(map));
            writer.close();
            System.out.println("Result has been written to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("是否重新生成数据？(y/n)");
        String input = scanner.nextLine();
        if (input.equals("y")) {
            GenData.genData();
        }
        init();

        System.out.println("选择算法：0为整数线性规划，1为禁忌搜索，2为自定义禁忌搜索");
        int choice = scanner.nextInt();
        scanner.close();
        if (choice == 0) {
            //整数线性规划
            ScheduleWrapper scheduleIPL = new ScheduleWrapper(g, flowList, "IPL", hyperPeriod);
            Map<String, Object> scheduleResultIPL = scheduleIPL.getIPLSchedule();
            output(scheduleResultIPL, "IPL");
        } else if (choice == 1) {
            //禁忌搜索
            ScheduleWrapper scheduleTS = new ScheduleWrapper(g, flowList, "TS", hyperPeriod);
            Map<String, Object> scheduleResultTS = scheduleTS.getTabuSearchSchedule();
            output(scheduleResultTS, "TS");
        } else if (choice == 2) {
            //自定义禁忌搜索
            ScheduleWrapper scheduleMyTS = new ScheduleWrapper(g, flowList, "MyTS", hyperPeriod);
            Map<String, Object> scheduleResultMyTS = scheduleMyTS.getSchedule();
            output(scheduleResultMyTS, "MyTS");
        }
    }
}