import java.util.StringTokenizer;

class InformedData {

	
 String name;
 static int size = 2*ReversiGameBoard.evalvectorlength+1;   
 static int[] mininum = new int[size];
 static int[] maximum = new int[size];
 int[] arr = new int[size];
 

 static {

	 for(int i=0;i<size-1;i++){
		 mininum[i] = -10;
		 maximum[i] = 10;
	 }
	 mininum[size-1] = 5;
	 maximum[size-1] = 60;
 }
 
 
 // new vector with String
 public InformedData(String str){

	 StringTokenizer st = new StringTokenizer(str);
	 for(int i=0; i<size; i++)
		 arr[i] = this.num(st.nextToken());
	 
	 name=st.nextToken();
 }
 
//String to Int
public static int num(String str) {
	return Integer.parseInt(str.trim());
}
    
}// End of Class
