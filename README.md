# is-apim-selfsignup-handler
This guide explains how to do user self registration in WSO2 API Manager 3.2.0 with WSO2 Identity Server 5.10.0.

### Build & Deploy
- To build the project, execute the following command from the root directory of the project.

```
mvn clean install
```

- After building the project, copy the built JAR artifact from <is-apim-selfsignup-handler>/target directory and place it inside the <IS_HOME>/repository/components/dropins directory.
- Open the **deployment.toml** file inside the <IS_HOME>/repository/conf directory.
- Add the below configuration segment to the file to engage the custom self registration handler.
```
[[event_handler]]
name="customUserPostSelfRegistration"
subscriptions=["POST_ADD_USER"]
```
  
- Start the Identity Server after above changes.
  
For the rest of configurations, please refer to the medium article[1].

[1] https://sumudusahanweerasuriya.medium.com/wso2-api-manager-3-2-0-with-wso2-identity-server-5-10-0-sso-self-registration-ec14bdf2fe97
