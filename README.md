<p align="center">
  <img src="https://www.pac4j.org/img/logo-lagom.png" width="300" />
</p>

The `lagom-pac4j` project is an **easy and powerful security library for Lagom framework** which supports authentication and authorization.
It's based on Lagom 1.5/1.6 (and Scala 2.11/2.12/2.13) and the **[pac4j security engine](https://github.com/pac4j/pac4j) v3**. 
It's available under the Apache 2 license.

Several versions of the library are available for the different versions of the Lagom framework:

| Lagom version     | pac4j version | lagom-pac4j version
|-------------------|---------------|--------------------
| 1.4+              | 3.6           | 1.x.y (Java & Scala)
| 1.[5|6]+          | 3.7           | 2.x.y (Java & Scala)

[**Main concepts and components:**](http://www.pac4j.org/docs/main-concepts-and-components.html)

1) A [**client**](http://www.pac4j.org/docs/clients.html) represents an authentication mechanism. It performs the login process and returns a user profile. An indirect client is for web applications authentication while a direct client is for web services authentication:

&#9656; OAuth - SAML - CAS - OpenID Connect - HTTP - OpenID - Google App Engine - Kerberos - LDAP - SQL - JWT - MongoDB - CouchDB - IP address - REST API

2) An [**authorizer**](http://www.pac4j.org/docs/authorizers.html) is meant to check authorizations on the authenticated user profile(s) or on the current web context:

&#9656; Roles / permissions - Anonymous / remember-me / (fully) authenticated - Profile type, attribute -  CORS - CSRF - Security headers - IP address, HTTP method

3) The `SecuredService` interface/trait protect methods in Lagom service by checking that the user is authenticated and that the authorizations are valid, according to the clients and authorizers configuration.
If the user is not authenticated, the method can be processed with an anonymous profile.

## Usage

### 1) [Add the required dependencies](https://github.com/pac4j/lagom-pac4j/wiki/Dependencies)

### 2) Define:

### - the [security configuration](https://github.com/pac4j/lagom-pac4j/wiki/Security-configuration)

### 3) [Apply security and get the authenticated user profiles](https://github.com/pac4j/lagom-pac4j/wiki/Apply-security)

## Demos

Two demo services demonstrate authenticate/authorize by JWT: 
([Scala/Sbt demo](https://github.com/pac4j/lagom-pac4j-scala-demo), [Java/Maven demo](https://github.com/pac4j/lagom-pac4j-java-demo))

## Versions

The latest released version is the [![Maven](https://img.shields.io/maven-central/v/org.pac4j/lagom-pac4j-parent.svg)](https://search.maven.org/search?q=a:lagom-pac4j-parent%20AND%20g:org.pac4j)
The [next version](https://github.com/pac4j/lagom-pac4j/wiki/Next-version) is under development.

See the [release notes](https://github.com/pac4j/lagom-pac4j/releases). Learn more by browsing the [pac4j documentation](http://www.pac4j.org/3.3.x/docs/index.html) and the [lagom-pac4j Javadoc](http://www.javadoc.io/doc/org.pac4j/lagom-pac4j/).

## Need help?

If you need commercial support (premium support or new/specific features), contact us at [info@pac4j.org](mailto:info@pac4j.org).

If you have any questions, want to contribute or be notified about the new releases and security fixes, please subscribe to the following [mailing lists](http://www.pac4j.org/mailing-lists.html):

- [pac4j-users](https://groups.google.com/forum/?hl=en#!forum/pac4j-users)
- [pac4j-developers](https://groups.google.com/forum/?hl=en#!forum/pac4j-dev)
- [pac4j-announce](https://groups.google.com/forum/?hl=en#!forum/pac4j-announce)
- [pac4j-security](https://groups.google.com/forum/#!forum/pac4j-security)
