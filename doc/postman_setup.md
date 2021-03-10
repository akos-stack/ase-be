# Postman Setup

- [Download](https://www.postman.com/downloads/) and install Postman
- `Import` -> `Upload Files` -> Navigate
  to [collection](../src/main/resources/postman/Art%20Stock%20Exchange.postman_collection.json)
- All defined variables
    - `Collections` -> `Art Stock Exchange` -> `Variables`
- Access token is needed in order to access authorized endpoints
    - Open `Art Stock Exchange` -> `oauth2` -> `Login` request
    - Switch to `Body` -> `x-www-form-urlencoded` tab and enter
        - username `ase.admin@mailinator.com`
        - password `Aser_Pr0`
    - Send `http` request by clicking on `Send` button
    - Upon successful request, `access_token` variable will be updated

---

[<: Application Setup](application_setup.md) | [IntelliJ IDEA Setup :>](intellij_idea_setup.md)