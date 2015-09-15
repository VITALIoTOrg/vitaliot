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

 * **/rest/user/{id}**: returns info about the user identified by "id" (expects the
   "vitalManToken" session cookie); response example:

   ```
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

