import java.util.*;
import java.io.*;
import java.net.*;

public class Node{
	
	//class variables.
	Vector<String> thispageLinks = new Vector<String>();
	int uniqueNodeNo= 0;
	String thisLinkIs=null;
	int responseCode = 000;
	String redirLink = null;
	
	//constructor
	public Node(){
	
	}
	
	//modifiers
	public void addLink(String newLink){
		this.thispageLinks.add(newLink);
	}
	public void updateNo(int number){
		this.uniqueNodeNo = number;
	}
	public void changeMyLink(String myNewLink){
		this.thisLinkIs = myNewLink;
	}
	public void updateCode(int newCode){
		this.responseCode = newCode;
	}
	public void setRedir(String redirected){
		this.redirLink = redirected;
	}

}