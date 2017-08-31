import java.io.*;
import java.util.*;


// mstw.java
// demonstrates minimum spanning tree with weighted graphs
// to run this program: C>java MSTWApp
////////////////////////////////////////////////////////////////
class Edge
{
    public int start;   // index of a vertex starting edge
    public int end;     // index of a vertex ending edge
    public int weight;  // weight from start to end
    // -------------------------------------------------------------
    public Edge(int sv, int dv, int d)  // constructor
    {
	start = sv;
	end = dv;
	weight = d;
    }
    // -------------------------------------------------------------
}  // end class Edge
////////////////////////////////////////////////////////////////

class HeapPQ // a MINIMUM PQ that uses a Heap
{ // min is at 0 max at size-1
    
    private Edge[] heapArray; //array for Priority Queue using heap based array 
    private int N; // current size
    private int maxSize;
    
    public HeapPQ(int MaxSize)            // constructor
    {
    	this.maxSize = MaxSize;
    	N=0;
    	heapArray = new Edge[maxSize]; //creates new edge array with MaxSize
    }
    
    
    public boolean insertPQ(Edge item){ // insert item in heap (assumed not full) in order (Enqueue) 
    	if(item.weight!=0){ // we use 0 to signify no connection 
    	Edge newE = item;
    	heapArray[N] = newE;
    	trickleUp(N++); //trickle up and increase current size
    	return true;
        }
    	else return false; 
    }
    
   
    public Edge removePQ()//Dequeue for PQ (removes the min)
    {
    	Edge root = heapArray[0]; //stores root
    	heapArray[0] = heapArray[--N];  //decreases current size
    	trickleDown(0); //trickles root down
    	return root;
    }
    
    private void trickleUp(int i) {//used for insert
    	//reach top of heap - done
    	if(i==0) return;
    	//check if parent is smaller
    	int parent = (i-1)/2;
    	if(heapArray[i].weight<heapArray[parent].weight){
    		swap(i,parent); // swap
    		trickleUp(parent);//recursion
    	}
    }
    
    private void swap(int i,int j){//used for both trickleUp and trickleDown
    	Edge temp;
    	temp = heapArray[i]; //store in temp
    	heapArray[i]=heapArray[j];
    	heapArray[j]=temp;
    }

    private void trickleDown(int i) {//used for remove
	    int leftChild = 2*i+1;
	    int rightChild = leftChild+1;
	    //if i is a lead node - done
	    if(leftChild>=N) return;
	    //if i has only a left child
	    if(rightChild>=N){
	    	if(heapArray[i].weight>heapArray[leftChild].weight){
	    		swap(i,leftChild);
	    		trickleDown(leftChild); // recursion
	    	}
	    return;
	    }
	    //if N has two children
	    if(heapArray[i].weight>heapArray[leftChild].weight || heapArray[i].weight>heapArray[rightChild].weight){
	    	//need to continue trickling down
	    	if(heapArray[leftChild].weight<heapArray[rightChild].weight){
	    		//swap with left child
	    		swap(i,leftChild);
	    		trickleDown(leftChild);
	    	}
	    	else{
	    		//swap with right child
	    		swap(i,rightChild);
	    		trickleDown(rightChild); //recursion
	    	}
	    }
    }
    
    // -------------------------------------------------------------
    public int size()              // return number of items
    { return N; }
    // -------------------------------------------------------------
    public boolean isEmpty()      // true if queue is empty
    { return (N==0); }
    // -------------------------------------------------------------
    
    public int find(int findDex){ // find a specific destVal for an edge 
    	for (int j = 0; j<maxSize; j++){
    		if(heapArray[j] == null)
    			break;
    		else if(heapArray[j].end == findDex)
    			return j;}
    	return -1;
    }
    //---------------------------------------------------------------
    public int peekN(int n){return heapArray[n].weight;} //peak item weight at index n
    public void removeN(int n){ //removes element at given index
    	for(int j=n;j<maxSize-1;j++)
    		heapArray[j] = heapArray[j+1];
    	maxSize--;
    }
    
 
}

////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////


class Vertex
{
    public int i,j;       // points coordinate (i,j)
    public boolean isInTree;
    // -------------------------------------------------------------
    public Vertex(int i,int j)   // constructor
    {
	this.i = i;
	this.j=j;
	isInTree = false;
    }
    // -------------------------------------------------------------
    public void display(){System.out.print("("+i+","+j+")");}
}  // end class Vertex


////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////


class Graph
{
    private Vertex vertexList[]; // list of vertices
    private int adjMat[][];      // adjacency matrix
    private int Nvertex;          // Number of vertices/nodes
    private int Nedges;           // Number of vertices/nodes
    private int nTree;			//number of verts in trees
    private HeapPQ arrayHeap;
    private int currentVert;  //current vertex that we are going to work with 
    int weightOne = 0;
  
    
    // -------------------------------------------------------------   
    public Graph(int maxVertex)               // basic constructor
    {	  
	vertexList = new Vertex[maxVertex];
	// adjacency matrix initialization
	adjMat = new int[maxVertex][maxVertex];
	Nvertex = 0;
	for(int j=0; j<maxVertex; j++)      // set adjacency
	    for(int k=0; k<maxVertex; k++)   // matrix to 0 by default
	    	adjMat[j][k] = 0;
	arrayHeap = new HeapPQ(Nedges);
    }  // end constructor



    // -------------------------------------------------------------
    public Graph(String name) throws FileNotFoundException, NoSuchElementException{// constructor- load Graph
    	int count = 0;
    	String v = name + ".v"; //extension for vertex file
    	String e = name + ".e"; //extension for edge file
    	File fileV = new File(v);
    	Scanner scannerV = new Scanner(fileV); //scanner for vertex file
    	File fileE = new File(e);
    	Scanner scannerE = new Scanner(fileE); //scanner for edge file
    	Nvertex = Integer.parseInt(scannerV.nextLine()); //scan in number of vertices
    	vertexList = new Vertex[Nvertex]; //creates array of vertices
    	Nedges = Integer.parseInt(scannerE.nextLine()); //scan in number of edges
    	adjMat = new int[Nvertex][Nvertex]; //creates adjacency matrix
    	arrayHeap = new HeapPQ(Nedges);
    	String ex;
    	String why;
    	for(int vertex = 0; vertex<Nvertex;vertex++){
    		int x = scannerV.nextInt();
    		int y = scannerV.nextInt();
    		Vertex xY = new Vertex(x,y);
    		vertexList[vertex] = xY;
    	}
    	for(int j=0; j<Nvertex; j++){      // set adjacency
    	    for(int k=0; k<Nvertex; k++){   // matrix to 0 by default
    	    	adjMat[j][k] = 0;}}
    	for(int edges = 0; edges<Nedges;edges++){
    		int st = scannerE.nextInt();
    		int end = scannerE.nextInt();
    		int weight = scannerE.nextInt();
    		if(st==end)
				adjMat[st][end] = 0;
			else{
				adjMat[st][end] = weight;
				adjMat[end][st] = weight;}
    	}
    	}
	// end constructor



    // -------------------------------------------------------------
    public Graph(int nx,int ny){               // constructor with random weight grid generator
    	Nvertex = 0;
    	int size = nx*ny;
    	vertexList = new Vertex[size];
    	adjMat = new int[size][size];
    	for(int y = 0;y<ny;y++){
    		for(int x =0;x<nx;x++){
    			addVertex(x,y);}} //add vertices to graph
    	for(int z=0;z<size-nx;z++){
    		int random = (int)((Math.random()*(nx+ny))+1); //generates random from 1 to nx+ny
    		addEdge(z,z+nx,random);} //add edge to graph with random weight
    	for(int q=0;q<size-1;q++){
    		int zero = vertexList[q].j-vertexList[q+1].j;
    		if(zero==0){ //if same j value for each vertex
    			int random = (int)((Math.random()*(nx+ny))+1);//generates random from 1 to nx+ny
    			addEdge(q,q+1,random);}} //add edge to graph with random weight
    }  // end constructor

    // -------------------------------------------------------------
    public Graph mstw(){           // minimum spanning 
    	Graph mst=new Graph(Nvertex); // mst returns as a graph
    	//initialize the nodes (same numbering than graphs)
    	HeapPQ theHeap = new HeapPQ(Nvertex); //initialize the heap with number of vertices
    	Edge theEdge;
    	int N = theHeap.size(); //get size of heap
    	currentVert = 0; //start at zero
    	for(int i=0;i<Nvertex;i++) 
    		mst.addVertex(vertexList[i].i,vertexList[i].j);
    	while(nTree<Nvertex-1){ //while not all verts in tree
    		mst.vertexList[currentVert].isInTree = true; //put currentVert in tree
    		nTree++;
    		 // insert edges adjacent to currentVert into theHeap
    		for(int j = 0;j<Nvertex;j++){ //for each vertex
    			if(j==currentVert) continue; //skip if it's us
    			if(mst.vertexList[j].isInTree) continue; //skip if in tree
    			int weight = adjMat[currentVert][j];
    			if(weight==0) continue; //skip if no edge
    			Edge item = new Edge(currentVert,j,weight);
    			theHeap.insertPQ(item);} //put in theHeap
    		theEdge = theHeap.removePQ();
    		while(mst.vertexList[theEdge.end].isInTree==true) //while the vertex is in the tree
    			theEdge = theHeap.removePQ(); //remove lowest weight edge
    		mst.addEdge(theEdge.start, theEdge.end, theEdge.weight);
    		mst.weightOne = theEdge.weight + mst.weightOne;
    		currentVert = theEdge.end;
    		}
    	for(int j=0; j<Nvertex; j++) // unmark vertices
    		vertexList[j].isInTree = false; // end mstw
    	return mst;}
    // end mstw 

           
    // -------------------------------------------------------------
    public void addVertex(int i, int j)
    {
	vertexList[Nvertex++] = new Vertex(i,j);
    }
    // -------------------------------------------------------------
    public void addEdge(int start, int end, int weight)
    {
	adjMat[start][end] = weight;
	adjMat[end][start] = weight;
	Nedges++;
    }

    public int[][] getAdjMat()
    {
	return(adjMat);
    }

    public Vertex[] getVertexList()
    {
	return(vertexList);
    }

    public int getNvertex() // return number of vertex/nodes
    {
	return(Nvertex);
    }

    public int getNedges() // return number of edges (connected to the number of non-zero elements in matrix)
    {
	return(Nedges);
    }
    
    // -------------------------------------------------------------
    public void plot(int xmin,int xmax,int ymin,int ymax){  //// Plot the Graph using the StdDraw.java library
    	StdDraw.setCanvasSize(600,600); // size canvas	
    	StdDraw.setXscale(xmin-5, xmax+5);    //  x scale
    	StdDraw.setYscale(ymin-5, ymax+5);   //  y scale
    	StdDraw.setPenColor(StdDraw.BLUE);  // change pen color
    	// to complete
    	Vertex[] n = this.vertexList;
		int Move = xmax/ (int)Math.sqrt(Nvertex)-1;
		for (int i=0;i<Nvertex;i++){
			for (int j=0;j<=i;j++) {
				StdDraw.filledCircle(vertexList[i].i, vertexList[i].j,.2); //draw circle at vertex i's x,y
				StdDraw.filledCircle(vertexList[j].i, vertexList[j].j,.2);//draw circle at vertex j's x,y
			}}
		for (int i=0;i<Nvertex;i++){
			for (int j=0;j<=i;j++) {
				if(adjMat[i][j]!=0) //if weight isnt zero
					StdDraw.line(vertexList[i].i,vertexList[i].j,vertexList[j].i,vertexList[j].j); //draw line from vertex i to vertex j
					
				}
		}
    		//make line with vertex at current node, ending point is coordinates for
    		//vertex that edge points to     		
    	
    	
    }
    public void plotMaze(int xmin,int xmax,int ymin,int ymax){  //// Plot the Graph using the StdDraw.java library
    	StdDraw.setCanvasSize(600,600); // size canvas	
    	StdDraw.setXscale(xmin-5, xmax+5);    //  x scale
    	StdDraw.setYscale(ymin-5, ymax+5);   //  y scale
    	StdDraw.setPenColor(StdDraw.WHITE);  // sets circles to white
    	// to complete
    	Vertex[] n = this.vertexList;
		int Move = xmax/ (int)Math.sqrt(Nvertex)-1;
		for (int i=0;i<Nvertex;i++){
			for (int j=0;j<=i;j++) {
				StdDraw.filledCircle(vertexList[i].i, vertexList[i].j,.2); //draw circle at vertex i's x,y
				StdDraw.filledCircle(vertexList[j].i, vertexList[j].j,.2);//draw circle at vertex j's x,y
			}}
		for (int i=0;i<Nvertex;i++){
			for (int j=0;j<=i;j++) {
				StdDraw.setPenColor(StdDraw.BLUE); //sets lines to blue for "maze" effect
				if(adjMat[i][j]!=0) //if weight isnt zero
					StdDraw.line(vertexList[i].i,vertexList[i].j,vertexList[j].i,vertexList[j].j); //draw line from vertex i to vertex j
					
				}
		}
    		//make line with vertex at current node, ending point is coordinates for
    		//vertex that edge points to     		
    	
    	
    }

    
}  // end class Graph





////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////
class MSTWApp
{
    public static void ToolsMenu() {
	System.out.println("Menu");
	System.out.println("====");
	System.out.println("1- Read Graph from File");
	System.out.println("2- Generate a Graph using a Grid with Random weights");
	System.out.println("3- Compute the Minimum Spanning Tree");
	System.out.println("4- Plot the Maze");
	System.out.println("5- Extra Credit- Plot the Maze with lines");
	System.out.println("0-Exit");
	System.out.println("");
	System.out.print("Command: ");
    }


    public static void main(String[] args) throws FileNotFoundException, NoSuchElementException
    {
    Graph theGraph=null; // original graph
	Graph mst = null;   // MST stored as graph
        EasyIn easy = new EasyIn();
        int N, totalW;
	int mat[][];
	Vertex nodes[];
	
	
	System.out.println("\nWelcome to Maze Generator App");
	System.out.println("===============================\n");

      
	 
	int command = -1;
	while (command != 0) {
	    if (command!=-1) easy.readString(); //just a pause
	    ToolsMenu();         // Print Option Menu
	    command = easy.readInt();
	    switch(command)
		{
		case  1:// Read Graph from File
		    System.out.println("Enter File name: ");
		    theGraph=new Graph(easy.readString());
		    System.out.println("List of edges + weights: ");
		    N=theGraph.getNvertex();

		    nodes=new Vertex[N];
		    nodes=theGraph.getVertexList();

		    // Obtain Matrix
		    mat=new int[N][N];
		    mat=theGraph.getAdjMat();
		   
		    for (int i=0;i<N;i++)
			for (int j=0;j<=i;j++) if(mat[i][j]!=0){
				nodes[i].display();
				System.out.print("<-->");
				nodes[j].display();
				System.out.println("  "+mat[i][j]);
			    }
		  
		    break;

		case  2:    //Generate a Graph using a Grid with Random weights
            System.out.println("Enter Total Grid Size x: ");
		    int nx=easy.readInt();
            System.out.println("Enter Total Grid Size y: ");
		    int ny=easy.readInt(); 
		    theGraph=new Graph(nx,ny);   
		    break;


		
		case  3://   Compute the Minimum Spanning Tree
		    if (theGraph==null){ System.out.println("Graph not defined"); break;}
		    System.out.println("Minimum spanning tree: ");
		    N=theGraph.getNvertex();
		    mst= theGraph.mstw();            // minimum spanning tree
               
		
		    System.out.println("Number of vertices: "+N);
		    System.out.println("Number of edges: "+mst.getNedges());
		    System.out.println("List of edges + weights: ");

		    nodes=new Vertex[N];
		    nodes=mst.getVertexList();

		    // Obtain Matrix
		    mat=new int[N][N];
		    mat=mst.getAdjMat();

		    for (int i=0;i<N;i++)
			for (int j=0;j<=i;j++) 
				if(mat[i][j]!=0){
					nodes[i].display();
					System.out.print("<-->");
					nodes[j].display();
					System.out.println("  "+mat[i][j]);
			    }
		    System.out.println("MST Total weight: " + mst.weightOne);
		    
		    
		    break;
		    
	        case 4: //Plot the maze
	        	if (mst==null){ System.out.println("MST not defined"); break;}

		    // to complete
		    	int random = (int)((Math.sqrt(mst.getNvertex())));
		    	random = random-1;
		    	mst.plot(0,random,0,random);
		                   
		    break;
	        case 5: //Plot the maze
	        	if (mst==null){ System.out.println("MST not defined"); break;}

		    // to complete
		    	int rand = (int)((Math.sqrt(mst.getNvertex())));
		    	rand = rand-1;
		    	mst.plotMaze(0,rand,0,rand);
		                   
		    break;
		case 0: 
		    break;
		default:
		    System.out.println("Selection Invalid!- Try Again");
		}
	}

	System.out.println("Goodbye!");
    }
}








