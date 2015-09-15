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
by the module in a simple and accessible way.

The following subsections include the format of the responses sent by the
adapter in case of success. In case of failure almost all the REST endpoints
return a generic HTTP code (400 for bad request and 500 for internal error) and
a JSON body describing the error more precisely. An example of error response is
the following:

```json
{
    "result":[

    ],
    "reason":"Unauthorized",
    "code":401,
    "message":"Access Denied"
}
```

### GET endpoints

 * **/rest/user/{id}** expects the "vitalManToken" session cookie to be included
in the request and returns some info about the user identified by "id". Response
example:

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

 * **/user/{id}/groups** expects the "vitalManToken" session cookie to be
included in the request and returns the list of groups having the user
identified by "id" as member. Response example:

    ```json
    {
        "result":[
            "Advanced_Users"
        ],
        "resultCount":1,
        "remainingPagedResults":-1
    }
    ```

 * **/group/{id}** expects the "vitalManToken" session cookie to be included in
the request and returns some info about the group identified by "id". Response
example:

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

 * **/policy/{id}** expects the "vitalManToken" session cookie to be included in
the request and returns some info about the policy identified by "id". Response
example:

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

 * **/application/{id}** expects the "vitalManToken" session cookie to be
included in the request and returns some info about the application identified
by "id". Response example:

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

 * **/application/{id}/policies** expects the "vitalManToken" session cookie to
be included in the request and returns some info about the policies part of the
application identified by "id". Response example:

    ```json
    {
        "result":[
            {
                "name":"Resource B",
                "active":true,
                "description":"Resource B",
                "applicationName":"iPlanetAMWebAgentService",
                "actionValues":{
                    "GET":true
                },
                "resources":[
                    "http://vitalsp.cloud.reply.eu:80/resB",
                    "http://vitalsp.cloud.reply.eu:80/resB/*"
                ],
                "subject":{
                    "type":"Identity",
                    "subjectValues":[
                        "id=Advanced_Users,ou=group,dc=openam,dc=forgerock,dc=org"
                    ]
                },
                "lastModifiedBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
                "lastModifiedDate":"2015-08-31T08:14:18.282Z",
                "createdBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
                "creationDate":"2015-08-07T09:18:42.369Z"
            },
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
        ],
        "resultCount":2,
        "remainingPagedResults":0
    }
    ```

 * **/users** expects the "vitalManToken" session cookie to be included in the
request and returns the list of users. Response example:

    ```json
    {
        "result":[
            "amAdmin",
            "devtry",
            "jsmith",
            "mrossi",
            "jconnor",
            "user0"
        ],
        "resultCount":6,
        "remainingPagedResults":-1
    }
    ```

 * **/groups** expects the "vitalManToken" session cookie to be included in the
request and returns the list of groups. Response example:

    ```json
    {
        "result":[
            "Advanced_Users",
            "Base_Users",
            "Dev_Users"
        ],
        "resultCount":3,
        "remainingPagedResults":-1
    }
    ```

 * **/policies** expects the "vitalManToken" session cookie to be included in
the request and returns some info about the policies. Response example:

    ```json
    {
        "result":[
            {
                "name":"Resource B",
                "active":true,
                "description":"Resource B",
                "applicationName":"iPlanetAMWebAgentService",
                "actionValues":{
                    "GET":true
                },
                "resources":[
                    "http://vitalsp.cloud.reply.eu:80/resB",
                    "http://vitalsp.cloud.reply.eu:80/resB/*"
                ],
                "subject":{
                    "type":"Identity",
                    "subjectValues":[
                        "id=Advanced_Users,ou=group,dc=openam,dc=forgerock,dc=org"
                    ]
                },
                "lastModifiedBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
                "lastModifiedDate":"2015-08-31T08:14:18.282Z",
                "createdBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
                "creationDate":"2015-08-07T09:18:42.369Z"
            },
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
        ],
        "resultCount":2,
        "remainingPagedResults":0
    }
    ```

 * **/applications** expects the "vitalManToken" session cookie to be included
in the request and returns some info about the applications. Response example:

    ```json
    {
        "result":[
            {
                "name":"openProvisioning",
                "resources":[
                    "/*"
                ],
                "subjects":[

                ],
                "conditions":[

                ],
                "applicationType":"openProvisioning",
                "attributeNames":[

                ],
                "lastModifiedDate":1419267870652,
                "resourceComparator":null,
                "createdBy":"id=dsameuser,ou=user,dc=openam,dc=forgerock,dc=org",
                "saveIndex":null,
                "lastModifiedBy":"id=dsameuser,ou=user,dc=openam,dc=forgerock,dc=org",
                "searchIndex":null,
                "entitlementCombiner":"DenyOverride",
                "realm":"/",
                "creationDate":1419267870652,
                "actions":{
                    "UPDATE":true,
                    "CREATE":true,
                    "DELETE":true,
                    "READ":true
                }
            },
            {
                "name":"sunIdentityServerLibertyPPService",
                "resources":[
                    "*"
                ],
                "subjects":[
                    "JwtClaim",
                    "AuthenticatedUsers",
                    "Identity",
                    "NOT",
                    "AND",
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
                "applicationType":"sunIdentityServerLibertyPPService",
                "attributeNames":[

                ],
                "lastModifiedDate":1419267868733,
                "resourceComparator":null,
                "createdBy":"id=dsameuser,ou=user,dc=openam,dc=forgerock,dc=org",
                "saveIndex":null,
                "lastModifiedBy":"id=dsameuser,ou=user,dc=openam,dc=forgerock,dc=org",
                "searchIndex":null,
                "entitlementCombiner":"DenyOverride",
                "realm":"/",
                "creationDate":1419267868733,
                "actions":{
                    "QUERY_interactForConsent":false,
                    "QUERY_interactForValue":false,
                    "MODIFY_interactForValue":false,
                    "QUERY_deny":false,
                    "MODIFY_deny":false,
                    "MODIFY_interactForConsent":false,
                    "MODIFY_allow":true,
                    "QUERY_allow":true
                }
            },
            ...
        ],
        "resultCount":10,
        "remainingPagedResults":0
    }
    ```

 * **/stats** returns some statistics about the status of the OpenAM server.
Response example:

    ```json
    {
        "activeSessions":1,
        "currInternalSessions":9,
        "currRemoteSessions":0,
        "cumPolicyEval":238,
        "avgPolicyEval":0,
        "avgPolicyEvalTree":0
    }
    ```

 * **/user** expects the "vitalManToken" and "vitalAccessToken" (the latter is
optional) session cookies to be included in the request and returns some info
useful for session management (whether the user session is still valid or not
and some info about the user); if the query parameter "altCookie" is set to true
the info is related to the session of the "vitalManToken" cookie, otherwise of
the "vitalAccessToken" cookie. Response example:

    ```json
    {
        "valid":true,
        "uid":"jsmith",
        "realm":"/",
        "name":"John",
        "fullname":"John Connor",
        "mailhash":"6dd9fe44b007f7898e3ab1305cbcddca",
        "creation":{
            "year":"2015",
            "month":"July",
            "day":"09"
        }
    }
    ```

### POST endpoints

 * **/user/create** expects the "vitalManToken" session cookie and the following
form parameters to be included in the request:

    * "givenName", the optional user first name

    * "surname", the optional user last name

    * "name", the mandatory username

    * "password", the mandatory (8 characters or more) user password

    * "mail", the optional user e-mail address

   It returns some info about the created user. Response example:

    ```json
    {
        "username":"jbrown",
        "realm":"/",
        "uid":[
            "jbrown"
        ],
        "mail":[
            "jack.brown@example.com"
        ],
        "createTimestamp":[
            "20150915094620Z"
        ],
        "sn":[
            "Brown"
        ],
        "userPassword":[
            "{SSHA}UrcRwePAKyP/J8AEbVtkeDZh7STfuxaAI6HjGg=="
        ],
        "cn":[
            "jbrown"
        ],
        "givenName":[
            "Jack"
        ],
        "inetUserStatus":[
            "Active"
        ],
        "dn":[
            "uid=jbrown,ou=people,dc=reply,dc=vital,dc=eu"
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
            "id=jbrown,ou=user,dc=openam,dc=forgerock,dc=org"
        ]
    }
    ```

 * **/user/delete** expects the "vitalManToken" session cookie and the "name"
form parameter (the user to delete) to be included in the request.

 * **/group/create** expects the "vitalManToken" session cookie and the "name"
form parameter (the name of the group to create) to be included in the request.

   It returns some info about the created group. Response example:

    ```json
    {
        "username":"test",
        "realm":"/",
        "cn":[
            "test"
        ],
        "dn":[
            "cn=test,ou=groups,dc=reply,dc=vital,dc=eu"
        ],
        "objectclass":[
            "groupofuniquenames",
            "top"
        ],
        "universalid":[
            "id=test,ou=group,dc=openam,dc=forgerock,dc=org"
        ]
    }
    ```

 * **/group/delete** expects the "vitalManToken" session cookie and the "name"
form parameter (the group to delete) to be included in the request.

 * **/group/{id}/addUser** expects the "vitalManToken" session cookie and the
"user" form parameter (the user to add to the group "id") to be included in the
request.

   It returns some info about the group updated with the added user. Response
example:

    ```json
    {
        "username":"test",
        "realm":"/",
        "uniqueMember":[
            "uid=devtry,ou=people,dc=reply,dc=vital,dc=eu"
        ],
        "cn":[
            "test"
        ],
        "dn":[
            "cn=test,ou=groups,dc=reply,dc=vital,dc=eu"
        ],
        "objectclass":[
            "groupofuniquenames",
            "top"
        ],
        "universalid":[
            "id=test,ou=group,dc=openam,dc=forgerock,dc=org"
        ]
    }
    ```

 * **/group/{id}/delUser** expects the "vitalManToken" session cookie and the
"user" form parameter (the user to remove from the group "id") to be included in
the request.

   It returns some info about the group updated without the removed user (see
above for the format).

 * **/policy/create** expects the "vitalManToken" session cookie and the
following form parameters to be included in the request:

    * "name", the name of the policy to create

    * "appname", the application name

    * "resources[]", the array of resources the policy will affect

    * "groups[]", the array of user groups the policy will affect

    * "actions[ACTION]", boolean values specifying if the ACTION (GET, POST,
      PUT, etc.) is allowed or denied

   It returns some info about the created policy. Response example:

    ```json
    {
        "name":"test",
        "active":true,
        "description":"test created from REST.",
        "applicationName":"iPlanetAMWebAgentService",
        "actionValues":{
            "POST":false,
            "GET":true,
            "PUT":false
        },
        "resources":[
            "http://what.com:80/hey"
        ],
        "subject":{
            "type":"Identity",
            "subjectValues":[
                "id=Dev_Users,ou=group,dc=openam,dc=forgerock,dc=org",
                "id=Advanced_Users,ou=group,dc=openam,dc=forgerock,dc=org"
            ]
        },
        "lastModifiedBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
        "lastModifiedDate":"2015-09-15T10:31:00.96Z",
        "createdBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
        "creationDate":"2015-09-15T10:31:00.96Z"
    }
    ```

 * **/policy/delete** expects the "vitalManToken" session cookie and the "name"
form parameter (the policy to delete) to be included in the request.

 * **/application/create** expects the "vitalManToken" session cookie and the
following form parameters to be included in the request:

    * "name", the name of the application to create

    * "description", some free text describing the application

    * "resources[]", the array of patterns for allowed resources in policies

   It returns some info about the created application. Response example:

    ```json
    {
        "creationDate":1442319637578,
        "lastModifiedDate":1442319637578,
        "conditions":[
            "AuthenticateToService",
            "AuthScheme",
            "IPv6",
            "SimpleTime",
            "OAuth2Scope",
            "IPv4",
            "AuthenticateToRealm",
            "OR",
            "AMIdentityMembership",
            "LDAPFilter",
            "SessionProperty",
            "AuthLevel",
            "LEAuthLevel",
            "Session",
            "NOT",
            "AND",
            "ResourceEnvIP"
        ],
        "lastModifiedBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
        "resourceComparator":null,
        "createdBy":"id=amAdmin,ou=user,dc=openam,dc=forgerock,dc=org",
        "applicationType":"iPlanetAMWebAgentService",
        "subjects":[
            "JwtClaim",
            "AuthenticatedUsers",
            "Identity",
            "NOT",
            "AND",
            "OR"
        ],
        "actions":{
            "POST":true,
            "PATCH":true,
            "GET":true,
            "DELETE":true,
            "OPTIONS":true,
            "HEAD":true,
            "PUT":true
        },
        "entitlementCombiner":"DenyOverride",
        "saveIndex":null,
        "searchIndex":null,
        "attributeNames":[

        ],
        "name":"TestApp",
        "resources":[
            "http//what.com/*"
        ],
        "description":"My awesome application",
        "realm":"/"
    }
    ```

 * **/application/delete** expects the "vitalManToken" session cookie and the
"name" form parameter (the application to delete) to be included in the request.

 * **/user/{id}** expects the "vitalManToken" session cookie and the following
form parameters to be included in the request:

    * "givenName", the updated user first name

    * "surname", the updated user last name

    * "mail", the updated user e-mail address

    * "status", "Active" or "Inactive"

   It returns some info about the user identified by "id" with the updated
fields (please refer to user info GET or creation for response format).

 * **/user/changePassword** expects the "vitalManToken" session cookie and the
following form parameters to be included in the request:

    * "userpass", the new password

    * "currpass", the old password

   It sets the new password "currpass" for the user corresponding to the session
of the "vitalManToken" cookie.

 * **/policy/{id}** expects the "vitalManToken" session cookie and the following
form parameters to be included in the request:

    * "description", the updated policy description

    * "active", new policy status (false/true)

    * "groups[]", the updated list of groups to be affected by the policy

    * "nogr", a boolean value which set to false allows to update without
      including the previous parameter (a.k.a. groups are not updated), while
      set to true means that if no group is specified then all groups are
      removed from the policy

    * "resources[]", the updated list of resources to be affected by the policy

    * "nores", a boolean value which set to false allows to update without
      including the previous parameter (a.k.a. resources are not updated), while
      set to true means that if no resource is specified then all resources are
      removed from the policy

    * "actions[ACTION]", updated boolean values specifying if the ACTION (GET,
      POST, PUT, etc.) is allowed or denied

    * "noact", a boolean which set to false allows to update without including
      the previous parameter (a.k.a. actions are not updated), while set to true
      means that if no action is specified then all actions are removed from the
      policy (the policy then has not effect)

   It returns some info about the policy identified by "id" with the updated
fields (please refer to policy info GET or creation for response format).

 * **/application/{id}** expects the "vitalManToken" session cookie and the following form parameters to be included in the request:

    * "description", the updated application description

    * "resources[]", the updated list of patterns for resources allowed in
      policies

    * "nores", a boolean value which set to false allows to update without
      including the previous parameter (a.k.a. patterns are not updated), while
      set to true means that if no pattern is specified then all patterns are
      removed from the application

   It returns some info about the application identified by "id" with the
updated fields (please refer to application info GET or creation for response
format).

 * **/authenticate** expects the following form parameters to be included in the
request:

    * "name", username

    * "password", user password

    * "altCookie", if false the SSO "vitalAccessToken" cookie is returned,
    * otherwise the alternative "vitalManToken" cookie is included in the
    * response.

    It returns some info useful for session management. Response example:

    ```json
    {
        "uid":"jsmith",
        "name":"John",
        "fullname":"John Connor",
        "creation":{
            "year":"2015",
            "month":"July",
            "day":"09"
        },
        "mailhash":"6dd9fe44b007f7898e3ab1305cbcddca"
    }
    ```

 * **/logout** expects either the "vitalManToken" or the "vitalAccessToken"
session cookie to be included in the request and performs logout (destroys the
session identified by the cookie); if the form parameter "altCookie" is set to
true the adapter will use the "vitalManToken" cookie, otherwise the
"vitalAccessToken" cookie.

 * **/evaluate** expects both the "vitalManToken" (user session with the rights
to request a policy evaluation) and the "vitalAccessToken" (session
corresponding to the user for whom the policy is evaluated) session cookies and
the form parameter "resources[]" (resources for which user permissions are
requested) to be included in the request.

    It returns for each resource the list of permitted or denied actions.
Response example:

    ```json
    {
        "responses":[
            {
                "advices":{

                },
                "resource":"https://vitalsp.cloud.reply.eu/resA/",
                "actions":{
                    "GET":true
                },
                "attributes":{

                }
            }
        ]
    }
    ```

