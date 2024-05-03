/* MiniWebserver.java

For CSC435, when you copy this to MiniWebserver.java: Add the appropriate class header HERE.

Copyright (C) 2020 with all rights reserved. Clark Elliott

1.1




Point your browser to:

http://localhost:2540/  or...
http://localhost:2540/WebAdd.fake-cgi  or...
http://localhost:2540/any/string/you/want.abc

...to get a response back. Keep in mind there may be FavIco requests.

Use the WebAdd.html form to submit a query string to MiniWebserver, based on
the input to the form. You can probably "click on" the file in your
directory. Locally it will have a URL of something like:

file:///C:/Users/Elliott/435/java/MiniWebserver/WebAdd.html

You should see:

Hello Browser World N
...along with some request information.

See WebAdd.html source HTML below.

To complete the MiniWebserver.java assignment: (a) Modify this file (or
start your own from scratch) so that your MiniWebserver returns (from the
WebAdd.html request) HTML containing the person's name and the sum of the
two numbers. (b) Continue modifications so that the return screen is valid
HTML that has the user's name entered by default (but modifiable), the two
numbers entered by default (but modifiable), and a working Submit button. In
other words, the user can keep pressing submit to get a response an infinite
number of times.

You can use the Firefox console (control-shift E / Network / Inspector) to
see the Internet traffic. (Note: drag the top line up to give a bigger console
window.)

You can use Wireshark to view the traffic as well.

HTML Reference site:
https://www.w3schools.com/

You may find that including the following in your HTML header helps with
facivon problems (Thanks Thomas K.!):

<head> <link rel="icon" href="data:,"> </head>

https://stackoverflow.com/questions/1321878/how-to-prevent-favicon-ico-requests

For the MiniWebserver assignment answer these questions briefly in YOUR OWN
WORDS here in your comments:

1. How MIME-types are used to tell the browser what data is coming.

2. How you would return the contents of requested files of type HTML
(text/html)

3. How you would return the contents of requested files of type TEXT
(text/plain)



*/

import java.io.*;  // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.util.regex.*;//Import regular expression to parse the HTML request headers


class ListenWorker extends Thread {    // Class definition
  Socket sock;                   // Class member, socket, local to ListnWorker.
  ListenWorker (Socket s) {sock = s;} // Constructor, assign arg s to local sock
  public void run(){
    PrintStream out = null;   // Input from the socket
    BufferedReader in = null; // Output to the socket
    try {
      out = new PrintStream(sock.getOutputStream());
      in = new BufferedReader
        (new InputStreamReader(sock.getInputStream()));

      // System.out.println("Sending the HTML Reponse now: " +
			
      //  Integer.toString(MiniWebserver.i) + "\n" );


      //The below statement is used to get the first request header.
      String requestedPath = in.readLine();

      System.out.println(requestedPath);

      //The regular expression is used to get the requesting resource/path from the client
      Pattern getResourcePattern = Pattern.compile("(?<=GET ).*?(?=\\?)");
      Matcher getResourcematcher = getResourcePattern.matcher(requestedPath);
      boolean requestedResource = getResourcematcher.find();
      String requestedResourceStr = getResourcematcher.group();


      //If the requested resource is the one mentioned in the html form i.e /WebAdd.fake-cgi
      if(requestedResourceStr.equals("/WebAdd.fake-cgi")){

          String htmlFilePath = "./WebAdd.html";
          //We are using StringBuilder instead of string to create A mutable version of string, as we perform multiple replace functions. 
          //StringBuilder increases efficiency.
          StringBuilder htmlContent = new StringBuilder();
          //Reading The Content From the HTML file, and appending it to the htmlContent variable.
          try (BufferedReader reader = new BufferedReader(new FileReader(htmlFilePath))) {
              String line;
              while ((line = reader.readLine()) != null) {
                  htmlContent.append(line).append("\n");
              }
          } catch (IOException e) {
              e.printStackTrace();
              return;
          }


          //The below RegularExpression is used to extract the parameter Key->Value pairs.
          Pattern getURLParametersPattern = Pattern.compile("((?<=\\?).*(?=\\sHTTP\\/1.1))");
          Matcher getURLParametersMatcher = getURLParametersPattern.matcher(requestedPath);
          boolean URLParameters = getURLParametersMatcher.find();
          String URLParametersStr = getURLParametersMatcher.group();
          //URL parameters are seperated by '&'. so we are seperating each key-value pair by using split function
          String[] valuesStr = URLParametersStr.split("&");
          String[] valueArr = new String[3];

          for(int i =0;i<valuesStr.length;i++){
            String[] tempKeyValue = valuesStr[i].split("=");//The key-value pairs are seperated by '='.
            valueArr[i] = tempKeyValue[1];//In 1 We have value and in 0 we have key
          }//for end

          int sum = Integer.parseInt(valueArr[1])+Integer.parseInt(valueArr[2]);

        // Replace the value attributes
        String replacedContent = htmlContent.toString()
                .replaceAll("(?<=id='clientRequest'>).*?(?=<)","Client Requests: "+Integer.toString(MiniWebserver.i++))
                .replaceAll("(?<=id='nameOfThePerson' value\\="+'"'+").*?(?="+'"'+")",valueArr[0])   // Replace first value attribute
                .replaceAll("(?<=id='firstNumber' value\\="+'"'+").*?(?="+'"'+")",valueArr[1])  // Replace second value attribute
                .replaceAll("(?<=id='secondNumber' value\\="+'"'+").*?(?="+'"'+")",valueArr[2]) // Replace third value attribute
                .replaceAll("(?<=id='nameAndSum'>).*?(?=<)","Name: "+valueArr[0]+" , Sum: "+String.valueOf(sum)); //Replace The P Attribute     

        System.out.println(valueArr[0]);

        System.out.println(valueArr[1]);

        System.out.println(valueArr[2]);

        // System.out.println(replacedContent);


      out.println("HTTP/1.1 200 OK");
      out.println("Connection: close"); // Can fool with this.
      out.println("Content-Length: " + Integer.toString(replacedContent.length()));
      out.println("Content-Type: text/html \r\n\r\n");
      out.println(replacedContent);

      }//if end

      else if("/viewFiles"){
        System.out.println("YET TO IMPLEMENT!");
        // //Variable to store html content
        // String viewFilesHTMLContent;
        // String Links;
        // // Create a File object representing the directory
        // File directory = new File("./");
        // File[] files = directory.listFiles();
        // for (File file:files) {
        //   Links += "<a href='file:///"+file.getName()+"'>"+file.getName()+"</a>";
        // }//for-end

        // out.println("HTTP/1.1 200 OK");
        // out.println("Connection: close"); // Can fool with this.
        // out.println("Content-Length: " + Integer.toString(viewPageContent.length()));
        // out.println("Content-Type: text/html \r\n\r\n");

        // out.println(viewPageContent);
      }//else-if end
	
      sock.close(); // close this connection, but not the server;
    } catch (IOException x) {
      System.out.println("Error: Connetion reset. Listening again...");
    }
  }
}

public class MiniWebserver {

  static int i = 1;

  public static void main(String a[]) throws IOException {
    int q_len = 6; /* Number of requests for OpSys to queue */
    int port = 2540;
    Socket sock;

    ServerSocket servsock = new ServerSocket(port, q_len);

    System.out.println("Clark Elliott's MiniWebserver running at 2540.");
    System.out.println("Point Firefox browser to http://localhost:2540/abc.\n");
    while (true) {
      // wait for the next client connection:
      sock = servsock.accept();
      new ListenWorker (sock).start();
    }
  }
}

