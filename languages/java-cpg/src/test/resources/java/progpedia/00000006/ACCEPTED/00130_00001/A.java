import java.util.Scanner;

class Node{

	int val; Node next;
	Node(int v, Node n){
		val=v; next=n;
	}
}

//DeadCodeStart
class List{

	Node first;
	int size;
	List(){ first=null; size=0; }

	void add(int v, int index){

		if(index==0)
		first=new Node(v,first);
		else{
			Node cursor=first;
			for(int i=0;i<index-1;i++)
				cursor=cursor.next;
			cursor.next=new Node(v,cursor.next);
		}
		size++;
	}

	void remove(int index){
		System.out.println(index);
		if(index==0)
			first=first.next;
		else{
			Node cursor=first;
			for(int i=0;i<index-1;i++)
				cursor=cursor.next;
			cursor.next=cursor.next.next;
		}
		size--;
	}
}
//DeadCodeEnd

class Stack{

	Node top;
	int size;

	public Stack(){ top=null; size=0;}
	public int pop(){
		int v=top.val;
		top=top.next;
		size--;
		return v;
	}
	public void push(int v){
		top=new Node(v,top);
		size++;
	}

	boolean exist(int v){

		for(Node cursor=top;cursor!=null;cursor=cursor.next)
			if(cursor.val==v)
				return true;
		return false;
	}

	void print(){

		Stack invert=new Stack();
		while(size>0)
			invert.push(pop());
		for(Node cursor=invert.top;cursor!=null;cursor=cursor.next)
			System.out.println(cursor.val);
	}
}
public class A {

	public static Scanner in=new Scanner(System.in);

	public static void main(String[] args) {

		Stack caminho=new Stack();
		int v=in.nextInt();
		while(v!=0){
			if(!caminho.exist(v))
				caminho.push(v);
			else{
				while(caminho.top.val!=v)
					caminho.pop();
			}
			v=in.nextInt();
		}
		caminho.print();
	}

}
