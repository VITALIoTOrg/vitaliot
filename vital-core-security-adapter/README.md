# Vital Security Adapter

---

This project contains the code for the Vital Security Adapter. The module, made
to run on WildFly and Java 8, interacts with the OpenAM server and exposes a
REST interface to access Vital security features.

## Configuration

In order to change the configuration you need to edit file
"src/main/resources/config.properties" and change the values of the properties:

 * **IDP_HOST** is the address of the server running OpenAM

 * **IDP_PORT** is the port number where OpenAM is listening (REST interface)

 * **SNMP_PORT** is the port number where the OpenAM SNMP interface is listening
   (used for monitoring)

 * **AUTH_TOKEN** is the name of the token cookie used by OpenAM

 * **MAN_TOKEN** is the name of the alternative token cookie currently used by
   the Security Management application

## Build and run

In order to build the application you will need the following tool to be
installed on your machine:

 * **Maven** (https://maven.apache.org/)

Then you can build issuing the following command:

```
mvn package
```

You will then find a ".war" file in the "target" folder; you can use it to
deploy the application on WildFly (tested on WildFly 9.0.1 with OpenJDK 8) over
HTTPS.

You may also want to take a look at the Vital Deployer project, featuring a
script to automatically build and deploy this application plus the Security
Management UI, another component of the Vital security system.

## Usage

While reading the code may still be the best way to understand the adapter
working details, this section will try and describe the RESTful services exposed
by the module in a simple and more accessible way.

### GET endpoints

 * **/rest/user/{id}** expects the "vitalManToken" session cookie to be included in the request and returns some info about the user identified by "id". Response example:

    ```json
    {
        "username":"jconnor",
        "realm":"/",
        "uid":[
            "jconnor"
        ],
        "mail":[
            "john.connor@sky.net"
        ],
        "userPassword":[
            "{SSHA}adPfkc6+UG0gIsFXfxw4eH50SlWyWGqLqj8/Ng=="
        ],
        "sn":[
            "Connor"
        ],
        "createTimestamp":[
            "20150630124922Z"
        ],
        "cn":[
            "jconnor"
        ],
        "modifyTimestamp":[
            "20150826152345Z"
        ],
        "givenName":[
            "John"
        ],
        "givenname":[

        ],
        "inetUserStatus":[
            "Active"
        ],
        "dn":[
            "uid=jconnor,ou=people,dc=reply,dc=vital,dc=eu"
        ],
        "sun-fm-saml2-nameid-info":[

        ],
        "objectClass":[
            "devicePrintProfilesContainer",
            "person",
            "sunIdentityServerLibertyPPService",
            "inetorgperson",
            "sunFederationManagerDataStore",
            "iPlanetPreferences",
            "iplanet-am-auth-configuration-service",
            "organizationalperson",
            "sunFMSAML2NameIdentifier",
            "inetuser",
            "forgerock-am-dashboard-service",
            "iplanet-am-managed-person",
            "iplanet-am-user-service",
            "sunAMAuthAccountLockout",
            "top"
        ],
        "universalid":[
            "id=jconnor,ou=user,dc=openam,dc=forgerock,dc=org"
        ],
        "sun-fm-saml2-nameid-infokey":[

        ]
    }
    ```

 * **/user/{id}/groups** expects the "vitalManToken" session cookie to be included in the request and returns the list of groups having the user identified by "id" as member. Response example:

    ```json
    {
        "result":[
            "Advanced_Users"
        ],
        "resultCount":1,
        "remainingPagedResults":-1
    }
    ```

 * **/group/{id}** expects the "vitalManToken" session cookie to be included in the request and returns some info about the group identified by "id". Response example:

    ```json
    {
        "username":"Advanced_Users",
        "realm":"/",
        "uniqueMember":[
            "uid=devtry,ou=people,dc=reply,dc=vital,dc=eu",
            "uid=jconnor,ou=people,dc=reply,dc=vital,dc=eu",
            "uid=amAdmin,ou=people,dc=openam,dc=forgerock,dc=org"
        ],
        "cn":[
            "Advanced_Users"
        ],
        "dn":[
            "cn=Advanced_Users,ou=groups,dc=reply,dc=vital,dc=eu"
        ],
        "objectclass":[
            "groupofuniquenames",
            "top"
        ],
        "universalid":[
            "id=Advanced_Users,ou=group,dc=openam,dc=forgerock,dc=org"
        ]
    }
    ```

 * **/policy/{id}** expects the "vitalManToken" session cookie to be included in the request and returns some info about the policy identified by "id". Response example:

    ```json
    {
        "name":"Resource A",
        "active":true,
        "description":"Resource A",
        "applicationName":"iPlanetAMWebAgentService",
        "actionValues":{
            "GET":true
        },
        "resources":[
            "http://vitalsp.cloud.reply.eu:80/resA",
            "https://vitalsp.cloud.reply.eu:443/resA/*",
            "https://vitalsp.cloud.reply.eu:443/resA",
            "http://vitalsp.cloud.reply.eu:80/resA/*"
        ],
        "subject":{
            "type":"Identity",
            "subjectValues":[
                "id=Base_Users,ou=group,dc=openam,dc=forgerock,dc=org"
            ]
        },
        "lastModifiedBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
        "lastModifiedDate":"2015-09-01T12:55:23.181Z",
        "createdBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
        "creationDate":"2015-08-07T09:15:04.115Z"
    }
    ```

 * **/application/{id}** expects the "vitalManToken" session cookie to be included in the request and returns some info about the application identified by "id". Response example:

    ```json
    {
        "name":"iPlanetAMWebAgentService",
        "description":"The built-in Application used by OpenAM Policy Agents.",
        "resources":[
            "*://*:*/*?*",
            "*://*:*/*"
        ],
        "subjects":[
            "JwtClaim",
            "AuthenticatedUsers",
            "Identity",
            "NOT",
            "AND",
            "NONE",
            "OR"
        ],
        "conditions":[
            "AuthenticateToService",
            "AuthLevelLE",
            "AuthScheme",
            "IPv6",
            "SimpleTime",
            "OAuth2Scope",
            "IPv4",
            "AuthenticateToRealm",
            "OR",
            "AMIdentityMembership",
            "LDAPFilter",
            "AuthLevel",
            "SessionProperty",
            "Session",
            "NOT",
            "AND",
            "ResourceEnvIP"
        ],
        "applicationType":"iPlanetAMWebAgentService",
        "attributeNames":[

        ],
        "lastModifiedDate":1419267875053,
        "resourceComparator":null,
        "createdBy":"id=dsameuser,ou=user,dc=openam,dc=forgerock,dc=org",
        "saveIndex":null,
        "lastModifiedBy":"id=dsameuser,ou=user,dc=openam,dc=forgerock,dc=org",
        "searchIndex":null,
        "entitlementCombiner":"DenyOverride",
        "realm":"/",
        "creationDate":1419267875053,
        "actions":{
            "POST":true,
            "PATCH":true,
            "GET":true,
            "DELETE":true,
            "OPTIONS":true,
            "HEAD":true,
            "PUT":true
        }
    }
    ```

