import java.io.*;
import java.net.*;
public class WebServer {
	public static void main(String args[]){
	 int i = 1,PORT = 8000; 
	 ServerSocket server = null;
	 Socket client = null;
	 
	 try{
		 server = new ServerSocket(PORT);
		 System.out.println("WebServer is listening on port"+server.getLocalPort());
		 for(;;){
			 client = server.accept();
			 new ConnectionThread(client,i).start();
			 i++;
	     }
	 } catch(Exception e){System.out.println(e);}
	 }
	}

  class ConnectionThread extends Thread{
	    	 Socket client;
	    	 int counter;
	     
	public ConnectionThread(Socket c1,int c){
		client = c1;
		counter = c;
	}
	public void run() {
		try {
			String destIP = client.getInetAddress().toString();
			int destPort = client.getPort();
			System.out.println("Connection"+" :connect to"+destIP+" on port "+destPort );
			
			PrintStream outstream = new PrintStream(client.getOutputStream());
			DataInputStream instream = new DataInputStream(client.getInputStream());
			
			String inline = instream.readLine();
			
			System.out.println("Received"+inline);
			if (getrequest(inline)){
				String filename = getfilename(inline);
				System.out.println(filename);
				File file = new File(filename);
				
				if (file.exists()){
					System.out.println(filename+" requested.");
					outstream.println("HTTP/1.1 200 OK");
					outstream.println("MIME_version:1.1");
					outstream.println("Content_Tpe:text/html");
					int len = (int)file.length();
					outstream.println("Content_Length:"+len);
					outstream.println("");
					sendfile(outstream,file);
					outstream.flush();				
				}
				else{
					String notfound="<html><head><title>Not Found</title></head><body> <h1>Error 404-file not found</h1></body></html>";
              		outstream.println("HTTP/1.0 404 no found");
              		outstream.println("Content_Length:text/html"+notfound.length()+2);
              		outstream.println("");
              		outstream.println(notfound);
              		outstream.flush();	
				}	
			}
			long m1=1;
			while(m1<11100000){m1++;}
			client.close();	 
	 }catch(Exception e){
		 System.out.println("Exceptione:"+e);
	 }
	}

	 boolean  getrequest(String s){
		if (s.length()>0){
			if (s.substring(0,3).equalsIgnoreCase("GET")){
				return true;
			}
		}return false;
	}

	String getfilename(String s) {
		String f = s.substring(s.indexOf(' ')+1);
		f = f.substring(0, f.indexOf(' '));
		try{
			if(f.charAt(0)=='/')
				f=f.substring(1);
		}catch(StringIndexOutOfBoundsException e){
			System.out.println("Exceptione:"+e);
		}
		if (f.equals(""))f="index.html";
		return f;
	}

	 void sendfile(PrintStream outs,File file){
		try{
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int len = (int) file.length();
			byte buf[] = new byte[len];
			
			in.readFully(buf);
			outs.write(buf,0,len);
			outs.flush();
			in.close();	
		}catch(Exception e){
			System.out.println("Error retrieving file");
			System.exit(1);
			
		}
	}
}
