# Chat

Sample chat application written in JavaFX. FXML layout was created in JavaFX Scene Builder.
The project contains:
<br>
<ul>
  <li style="margin-bottom: 5px;">Client chat application</li>
  <p style=" text-align: justify;">Name of a particular client (client's nickname) is always highlighted on the list with active clients (users that are logged in establish a connection with the server). After establishing the connection user needs to enter a nickname using "/nick [nickname]" pattern. Following information/alerts are received in a text form from the server:</p>
    <ul>  
      <li>"/nkok" - information from a server that the nickname for a given client was accepted</li>
      <li>"/nkrm xxx" - changed/removed nickname</li>
      <li>"/nonk" - nick was incorrect or not defined at all</li>
      <li>"/nkex" - information that nickname already exists</li>
    </ul>
  <br>
  <p>Each client's nickname is checked whether it is unique in the "realm" of logged users.</p>
  <li style="margin-bottom: 5px;">Chat server (multithreaded)</li>
  <p>Each client is placed in a separate thread (threadsArray is populated with them).</p>
</ul>
<p>Further details regarding this application can be found in the comments added to the source code.</p>
<br>
<br>

![obraz](https://user-images.githubusercontent.com/34214903/45932756-a97c9280-bf81-11e8-8613-ebe7c520c795.png)

    
