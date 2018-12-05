# Sample Backend for Java

This repository contains a sample backend code that demonstrates how to generate a Virgil JWT using the [Java/Android SDK](https://github.com/VirgilSecurity/virgil-sdk-java-android)

## Prerequisites

* Java Development Kit (JDK) 8+
* Maven 3+

## Clone

Clone the repository from GitHub.

```
$ git clone https://github.com/VirgilSecurity/e3kit-kotlin.git
```

## Get Virgil Credentials

If you don't have an account yet, [sign up for one](https://dashboard.virgilsecurity.com/signup) using your e-mail.

To generate a JWT the following values are required:

| Variable Name                     | Description                    |
|-----------------------------------|--------------------------------|
| virgil.app.id                     | ID of your Virgil Application. |
| virgil.api.private_key            | Private key of your API key that is used to sign the JWTs. |
| virgil.api.key_id                 | ID of your API key. A unique string value that identifies your account in the Virgil Cloud. |

## Build sources

```
$ mvn clean package -DskipTests
```

JAR file will be build in `target` directory.

## Add Virgil Credentials to `application.properties`

- open `target` directory at the project folder
- create a `application.properties` file
- fill it with your account credentials (take a look at the `application.properties.example` file to find out how to setup your own `application.properties` file)
- save the `application.properties` file

## Run the Server

```
$ java -jar server.jar
```

Now, use your client code to make a request to get a JWT from the sample backend that is working on http://localhost:3000.

Verify your server with a command

```
$ curl -X POST -H "Content-Type: application/json" \
  -d '{"identity":"my_identity"}' \
  http://localhost:3000/authenticate
```

The response should looks like

```
{"token":"my_identity-0cfc6f0f-0024-4ea1-b5ac-93bb586c113d"}
```

## Usage
To generate JWT, you need to use the `JwtGenerator` class from the SDK.

```js
const virgilCrypto = new VirgilCrypto();

const generator = new JwtGenerator({
  appId: process.env.APP_ID,
  apiKeyId: process.env.API_KEY_ID,
  apiKey: virgilCrypto.importPrivateKey(process.env.API_PRIVATE_KEY),
  accessTokenSigner: new VirgilAccessTokenSigner(virgilCrypto)
});

```
Then you need to provide an HTTP endpoint which will return the JWT with the user's identity as a JSON.

For more details take a look at the [server.js](server.js) file.



## License

This library is released under the [3-clause BSD License](LICENSE.md).

## Support
Our developer support team is here to help you. Find out more information on our [Help Center](https://help.virgilsecurity.com/).

You can find us on [Twitter](https://twitter.com/VirgilSecurity) or send us email support@VirgilSecurity.com.

Also, get extra help from our support team on [Slack](https://virgilsecurity.slack.com/join/shared_invite/enQtMjg4MDE4ODM3ODA4LTc2OWQwOTQ3YjNhNTQ0ZjJiZDc2NjkzYjYxNTI0YzhmNTY2ZDliMGJjYWQ5YmZiOGU5ZWEzNmJiMWZhYWVmYTM).
