package mua;

public class Main {

	public static void main(String[] args) {
		Interpreter interpreter=new Interpreter();
		while(interpreter.init()) {
			try{
				interpreter.parse();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
