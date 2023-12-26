package GraphEntity;

public class MyGraph {
    public Point[] point;
    public int[] visited;
    public int numPoint;
    public int numEdges;
    public MyGraph() {}
    public MyGraph(int numPoint,int numEdges)
    {
        this.numPoint=numPoint;
        this.numEdges=numEdges;
        point=new Point[numPoint];  //初始化点集数组
        visited=new int[numPoint];
    }
    public void createMyGraph(MyGraph MyGraph,int numPoint,int numEdges,double EdgesPoint[][])  //创建图
    {
        for(int i=0;i<numPoint;i++)
        {
            MyGraph.visited[i]=0;
            MyGraph.point[i]=new Point(i);   //录入顶点的数据域
        }
        for(int i=0;i<numEdges;i++)   //初始化边表,这里使用到了链表中间的头插法
        {
            EdgeNode a=new EdgeNode((int)EdgesPoint[i][1],1/EdgesPoint[i][2]);         //记录出度(使用1/可靠度作为边的权值)
            a.nextEdge=MyGraph.point[(int)EdgesPoint[i][0]].firstArc;  //头插法
            MyGraph.point[(int)EdgesPoint[i][0]].firstArc=a;
        }
    }

    public void DFS(MyGraph MyGraph,int m)
    {
        EdgeNode a = null;                                                 //创建一个边表结点的引用
        MyGraph.visited[m]=1;                                                      //访问该顶点，将该顶点的标志设置未true
        a=MyGraph.point[m].firstArc;
        while(a!=null)
        {
            if(MyGraph.visited[a.adjvex]==0)
                DFS(MyGraph,a.adjvex);
            a=a.nextEdge;                                                    //实际上像是一种遍历链表的行为
        }
    }

}
