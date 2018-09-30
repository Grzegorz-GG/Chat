# Chat

Sample chat application written in JavaFX. FXML layout was created in JavaFX Scene Builder.
The project contains:
<br>
<ul>
  <li>Client chat application</li>
  <p>Name of a particular client (client's nickname) is always highlighted on the list with active clients (users that are logged in i.e they established a connection with the server). User after establishing the connection always needs to enter a nickname using "/nick [nickname]". Following information/alerts are received from the server:<br>
    <ul>  
      <li>"/nkok" - information from a server that nick for a given client was accepted</li>
      <li>"/nkrm xxx" - changed/removed a nickname</li>
      <li>"/nonk" - nick was incorrect or not defined at all</li>
      <li>"/nkex" - information that nick already exists</li>
    </ul>
  <p>Each client's nickname is checked whether it is unique in the "realm" of logged users.</p>
  <li>Chat server - multitheaded (each client in a new thread)</li>
</ul>
<p>Further details regarding this application can be found in comments added to majority of source code.</p>
<br>
<br>

![obraz](https://user-images.githubusercontent.com/34214903/45932756-a97c9280-bf81-11e8-8613-ebe7c520c795.png)

    
