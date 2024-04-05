import java.util.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.net.URLConnection;

import sun.net.www.protocol.http.HttpURLConnection;

public class Mybot{
	
	//bot variables
	Vector<String> Foundlinks = new Vector<String>();
	String startlink = null;
	Vector<String> myservers = new Vector<String>();
	Vector<Node> nodelist = new Vector<Node>();
	LinkedList<String> UsAgent = new LinkedList<String>();
	Map<String,List<String>> unallowed = new HashMap<String,List<String>>();
	Vector<String> Mycontent = new Vector<String>();
	
	//constructor
	public Mybot(String start, Vector<String>URLs,Vector<String>Allowed) {
		this.startlink = start;
		this.Foundlinks = URLs;
		this.myservers= Allowed;
	}
	//connect to and parse the robot files for my servers
	public boolean Roboparse(String serv, LinkedList<String> USA, Map<String,List<String>> NAL){
		
			Robotstxt rotxt = new Robotstxt();	
			URL yahoo = null;
			String host = serv;
			BufferedReader read = null;
			try{
				yahoo = new URL("http://" + host + "/robots.txt");
				URLConnection yc;
				yc = yahoo.openConnection();
				yc.setRequestProperty("User-Agent", "CSCI4964-mokszs");
				yc = yahoo.openConnection();
				read = new BufferedReader( 
						new InputStreamReader(
						yc.getInputStream()));
			boolean parsed = rotxt.parse(read, USA, NAL);
				if(parsed){
					return true;
				}
			}catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//System.out.println("MADE IT TO END OF ROBOPARSE");
		return false;
	}
	
	//checks if link from line parse has been found already if it hasnt, adds it and returns false
	//if found returns true, skip onto next link
	private boolean findSet(String alink){
		if(this.Foundlinks.contains(alink)){
			return true;
		}
		else{
			this.Foundlinks.add(alink);
			return false;
		}
	}
	
	//function to check if the link found is relative or not
	//compares the current with the one found, java utils parses and checks if the links 
	//are subsets and returns a new URL
	public String checkrel(String alink, URL current){
		try{
			//mind be current not a string
			URL newurl = new URL(current,alink);
			
			return newurl.toString();
		}catch(MalformedURLException mue){
			System.out.println("malformed url case hit");
			return null;
		}
	}
	//add relative link check to this before the if FLinks.contains
	//add check to robot.txt to see if link is unallowed.
	
	//pattern matching function to find links on a line
	public void hrefchecknstore(String fileline,URL currentcon, Node linknode){
		Pattern p = Pattern.compile("href=\"(.*?)\"");
		Matcher m = p.matcher(fileline);
		
		//After line is matched and link is found, call checkrel to see if it is relative
		//return a string link 
		//take new relative/complete link and match against the HashSet of all found links
		//if not found yet add to Foundlink hash
		//also store link in the nodes list of links
		//BUT DO NOT ADD NODE TO NODE VECTOR
		//do that after all lines are read in the main function
		
		while(m.find()){
			//System.out.println(linknode.thisLinkIs);
			String link = m.group(1);
			link = checkrel(link, currentcon);
			boolean foundyet = findSet(link);
			if(!foundyet){
			
			}
			if(!linknode.thispageLinks.contains(link)){
			linknode.addLink(link);
			}
		}
	}
	//checks if content type is correct 
	private boolean contentcheck(String cont){
		int index =	cont.indexOf(';');
		if(index == -1){
			//not correct content type
			return false;
		}
		else{
			String contype = cont.substring(0, index);
			//write code to store the content type
			//reguardless of what it is
			//then run a serious of ifs to check for correct type
			if(!Mycontent.contains(contype)){
				Mycontent.add(contype);
			}
			if(contype.equals("text/plain")||contype.equals("text/html")){
				//ADD CODE TO UPDATE LIST OF CONTENT TYPE
				return true;
			}
			else{
			return false;
			}
		}	
	}

	public boolean servercheck(String hoster){
		for(int i = 0; i<this.myservers.size(); i++ ){
			if(hoster.equals(this.myservers.get(i))){
				return true;
			}
		}
		return false;
	}
	
// runs code to check if page is valid type, if there is a redirect, and checks the server
// Adds link to HashMap weather its ok or not, so that i can show all the links i found
	private boolean checkpage(String thelink, Node linkNode){
		URL yahoo = null;
		int code = 0;
		URL responseURL = null;
			try {
				if(thelink.contains("foo")){
					linkNode.updateCode(500);
					int nodeNo = this.nodelist.size()+1;
					linkNode.updateNo(nodeNo);
					this.nodelist.add(linkNode);
					return false;
				}
				if(thelink.contains("mailto")){
					linkNode.updateCode(900);
					int nodeNo = this.nodelist.size()+1;
					linkNode.updateNo(nodeNo);
					this.nodelist.add(linkNode);
					return false;
				}
				yahoo = new URL(thelink);
				URLConnection yc;
				yc = yahoo.openConnection();
				//establishing a http connection to check if there is a redirect
				HttpURLConnection conn  = (HttpURLConnection)yc;
				conn.setFollowRedirects(false);
				conn.setRequestProperty("User-Agent", "CSCI4964-mokszs");
				
				conn.connect();
				code = conn.getResponseCode();

				//need to see if i should relativecheck response url
	
				linkNode.changeMyLink(thelink);
				linkNode.updateCode(code);
				// NEED MORE IFS ONCE ROBOTS Disallows
				
				if(code>=300 && code<400){
					String relink =null;
					relink = conn.getHeaderField("Location");
					linkNode.setRedir(relink);
					this.Foundlinks.add(relink);
					int nodeNo = this.nodelist.size() +1;
					linkNode.updateNo(nodeNo);
					this.nodelist.add(linkNode);
					return false;
				}
				if(code >= 400 && code<500){
					int nodeNo = this.nodelist.size()+1;
					linkNode.updateNo(nodeNo);
					this.nodelist.add(linkNode);
					return false;
				}
				if(code >=500 && code <600){
					int nodeNo = this.nodelist.size()+1;
					linkNode.updateNo(nodeNo);
					this.nodelist.add(linkNode);
					return false;
				}
				//run server match code here
				String host = conn.getURL().getHost();
				boolean serverOK = servercheck(host);
				if(!serverOK){
					linkNode.updateCode(902);
					int nodeNo = this.nodelist.size()+1;
					linkNode.updateNo(nodeNo);
					this.nodelist.add(linkNode);
					return false;
				}
				//first check to see if robots file is found and parsed, then run a loop to compare current 
				//url(thelink) to anything in the Map(csci4964,thelink)&& Map(csci4964,thelink
				
				LinkedList<String> Roboagents = new LinkedList<String>();
				Map<String,List<String>> NoGoHere = new HashMap<String,List<String>>();
				boolean robosparsed = Roboparse(host,Roboagents,NoGoHere);
			//	System.out.println("Made it out of roboparse");
				if(!robosparsed){	//returned no errors so it was parsed	
					List<String>myBotCantGoHere = NoGoHere.get("csci4964");
					List<String>allBotCantGoHere = NoGoHere.get("");
					if(myBotCantGoHere.contains(thelink) || allBotCantGoHere.contains(thelink)){
						linkNode.updateCode(901);
						int nodeNo = this.nodelist.size()+1;
						linkNode.updateNo(nodeNo);
						this.nodelist.add(linkNode);
						return false;						
					}
				}
				//System.out.println("I MADE IT PASSED THE ROBOTS PARSE LIST AND ROBOTS LINK CHECK STATEMENT");
				
				String contenttype = conn.getContentType();				
			//open a connection, check content 
			
				boolean okType = contentcheck(contenttype);
				if(okType){
					// which means now i can read the page for links from getreadpage
					//remember to store links in the node and to continue
					//to store in hashset
					return true;
				
				}
				else{
					//link not right
					linkNode.updateCode(900);
					int nodeNo = this.nodelist.size()+1;
					linkNode.updateNo(nodeNo);
					this.nodelist.add(linkNode);
					return false;
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return false;
	}
	
	//takes in a link, calls all functions to check if a link was already found,
	// if it is in robots ADD ME
	//if it is valid type 
	//if it is on an okay server
	public void getreadpage(String link,Vector<String>listservers){
		Node page = new Node();
		//Check order
		//Connection -redirection; DONE
		//SERVER					DONE
		//ROBOTS					
		//CONTENT					DONE - add content type list update
		//Now scan all pages for links one line at a time, 
		//Hrefncheck will store links in the node 	DONE

		boolean check = checkpage(link, page);
		if(check){
		//all checks complete, connect to page, scan for links
			URL url;
			InputStream is;
			InputStreamReader isr;
			BufferedReader r;
			String str;
			try{
				//readpage line by line
				//System.out.println("Reading URL:" + starter);
				url = new URL(link);
				is = url.openStream();
				isr = new InputStreamReader(is);
				r = new BufferedReader(isr);

				//loop through page line at a time send each line to the hrefparser after its done 
				//add node to node list
				while((str = r.readLine())!= null){
					
						hrefchecknstore(str, url, page);
						//System.out.println(str);
				}
			
			
				//node all done add him to the vector of nodes for node print and arc print later
				int nodeNo = this.nodelist.size()+1;
				page.updateNo(nodeNo);
				this.nodelist.add(page);
						
				
	
				
			}
			catch(MalformedURLException e){
				System.out.println("Must enter a valid URL");
			}catch(IOException ioe){
				System.out.println("Cannot connect");
			}
		
		}
		
	}
}