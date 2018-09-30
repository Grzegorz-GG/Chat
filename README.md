# Chat

Sample chat application written in JavaFX. FXML layout was created in JavaFX Scene Builder.
The project contains:
<br>
<ul>
  <li style="text-decoration: underline;">Client chat application:</li>
  <p>User have access to following options and information/alerts from a server:<br>
    <ul>  
      <li>"/quit" - enter in a textfield to disconnect client</li>
      <li>"/nick xxx" - type in to choose a client's nickname</li>
      <li>"/nkok" - information from a server that nick for a given client was accepted</li>
      <li>"/nkrm xxx" - change/remove a nickname</li>
      <li>"/nonk" - nick was incorrect or not defined at all</li>
      <li>"/nkex" - information that nick already exists</li>
    </ul>
  <li>Chat server - multitheaded (each client in a new thread)</li>
</ul>
<br>
<br>

![obraz](https://user-images.githubusercontent.com/34214903/45932756-a97c9280-bf81-11e8-8613-ebe7c520c795.png)

    
