# Postman Setup

---
- [Download](https://www.postman.com/downloads/) and install Postman  
- Launch the application and import *Art Stock Exchange* collection  
    - `Import` -> `Upload Files`
    - Find collection on the file system and open it  
- Switch to `Variables` tab to see all variables defined in the collection  
- In order to access all endpoints you need to obtain access token  
    - Open `Art Stock Exchange / oauth2 / Login` request  
    - Switch to `Body` tab and enter admin credentials (`username` and `password`)   
    - Send `http` request by clicking on `Send` button  
    - Upon successful request, access token will be extracted from response body and saved into `access_token` collection variable
      (observe `Tests` tab)
- Once `access_token` is obtained you can access authorized endpoints.
  Open any request that requires authorization and inspect `Authorization` tab.
  Notice `{{access_token}}` notation. `access_token` collection variable is being referenced so we don't have to add access token manually 
  

[<: Application Setup](application_setup.md) | [IntelliJ IDEA Setup :>](intellij_idea_setup.md)