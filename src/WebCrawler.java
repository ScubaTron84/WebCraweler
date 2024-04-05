import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.Object;

import sun.net.www.protocol.http.HttpURLConnection;
public class WebCrawler {
	public static void main(String[] args) {
		String startwww = null;
		Vector<String> servers = new Vector<String>();
		Vector<String> allfoundlinks = new Vector<String>();
		
		boolean test = handleargs(args);
		if(test){
		startwww = inputparse(servers, args);
		}
				
		//run info for first link last? just put it in the hash for now
		allfoundlinks.add(startwww);
		Mybot Bob = new Mybot(startwww,allfoundlinks,servers);
		int x = Bob.Foundlinks.size();
		System.out.println("START");
		try{
		for(int p = 0; p < x; p++){
			//change link to myhashiter.next().toString();
			Bob.getreadpage(Bob.Foundlinks.get(p), Bob.myservers);
			x = Bob.Foundlinks.size();
		}
        FileOutputStream out; // declare a file output object
        PrintStream p; // declare a print stream object	
		out = new FileOutputStream("mokszs-Output.txt");
        // Connect print stream to the output stream
        p = new PrintStream( out );
        
		p.println("");
		p.println("CONTENT");
		
		for(int y = 0; y < Bob.Mycontent.size(); ++y){
			p.println(Bob.Mycontent.get(y));
		}
		p.println("");
		p.println("NODE");
		for(int j = 0; j < Bob.nodelist.size(); ++j){
			Node node = Bob.nodelist.get(j);
			p.print(node.uniqueNodeNo);
			p.print(',');
			p.print(' ');
			p.print(node.responseCode);
			p.print(',');
			p.print(' ');
			p.print(node.thisLinkIs);
			if(node.responseCode>=200 && node.responseCode < 300){
				p.println(", OK");
			}
			if(node.responseCode >= 300 && node.responseCode < 400){
				p.println(", " + node.redirLink);
			}
			if(node.responseCode >= 400 && node.responseCode < 500){
				p.println(", NG");
			}
			if(node.responseCode >= 900){
				p.println(", DISALLOW");
			}
			if(node.responseCode >=500 && node.responseCode < 600){
				p.println(", UNKNOWN");
			}
		}
		// SUPPOSED TO BE ARC LOOP OPPS 
		/*for(int o = 0; o<Bob.nodelist.size(); ++o){
			System.out.print('[');
			System.out.print(' ');
			for(int z = 0; z < Bob.nodelist.get(x).thispageLinks.size(); ++z){
				for(int w = 0; w < Bob.nodelist.size();++w){
					if(Bob.nodelist.get(w).thisLinkIs.equals(Bob.nodelist.get(x).thispageLinks.get(z))){
						System.out.print(Bob.nodelist.get(w).uniqueNodeNo);
						System.out.print(',');
						System.out.print(' ');
					}
				}
			}
			System.out.println("]");
		}
		*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private static String inputparse(Vector<String> allowed, String[] arg){
		BufferedReader input = null;
		String startli = null;
		try{
			input = new BufferedReader (new FileReader(arg[0]));
			String dump = null;
			
			
			//parseing loop to get all the servers out of the input text, and the starting link
			dump = input.readLine();
			startli = input.readLine();
			
			dump = input.readLine();
			dump = input.readLine();
			String lineallow = null;
			while((lineallow = input.readLine())!=null){
				allowed.add(lineallow);
			}
			
		}catch(FileNotFoundException e){
		 e.printStackTrace();
		}catch(IOException ioe){
			System.out.print("You couldnt open the file");
		}
		return startli;
	}
	
	private static boolean handleargs(String[] args){
		if(args.length < 1){
		return false;
		}
		else{
			//can get ride of out put file in the end
			BufferedReader input = null;

			//PrintWriter outputstream = null;
			try {
				input = new BufferedReader(new FileReader(args[0]));
			//	outputstream = new PrintWriter(new FileWriter("testout.txt"));
				
			/*	String line;
				while((line = input.readLine())!= null){
					outputstream.println(line);
				}
			*/
			}catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}/*catch(IOException ioe){
				System.out.println("Shit");
				System.exit(01234);
			}*/
			finally{
				if(input != null){
					try {
						input.close();
					} catch (IOException ioe2) {
						// TODO Auto-generated catch block
						ioe2.printStackTrace();
					}
				}/*
				if(outputstream !=null){
				  outputstream.close();
				}*/
			}
			
		return true;
		}
	}

	
	
}