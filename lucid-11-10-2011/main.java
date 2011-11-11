import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class main {

	public static void main(String[] args) throws FileNotFoundException {

		ScriptEngineManager sm = new ScriptEngineManager();		
		FileReader file = new FileReader("test.js");		
		ScriptEngine jsEngine = sm.getEngineByName("jav8");
		
		int iter = Integer.parseInt(args[0]);
		
		try {
			long acum = 0;
			for(int i=0; i<iter; i++){
				long start = System.currentTimeMillis();
				Object ob = jsEngine.eval(file);
				long end = System.currentTimeMillis();
				
				acum += end - start;
			}
			System.out.println(acum);
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}
	}
}
