# HOW TO USE

The module exposes a set of GET and POST HTTP endpoints which can be used to
interact with the security system of VITAL. This file describes how the
requests must be constructed and the format of the responses in case of
success. In case of failure a HTTP error code is returned with a JSON body
describing, where possible, the problem. An example of error response is the
following:

```json
{
    "reason":"Unauthorized",
    "code":401,
    "message":"Access Denied"
}
```

All services are exposed under the subpath **_/rest_**.

## GET endpoints

You may have to pass information in three different ways for GET methods:

1. As part of the path of the endpoint (e.g. https://vital.com/endpoint/parameter)
2. As cookies
3. As query parameters (included at the end of the URL, e.g. https://vital.com/endpoint?par1=val1&par2=val2)

* **_/user/{id}_** expects the **_vitalAccessToken_** session cookie to be included
in the request and returns some info about the user identified by **_id_**.
Response example:

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

* **_/user/{id}/groups_** expects the **_vitalAccessToken_** session cookie to be
included in the request and returns the list of groups having the user
identified by **_id_** as member. Response example:

    ```json
    {
        "result":[
            "Advanced_Users"
        ],
        "resultCount":1,
        "remainingPagedResults":-1
    }
    ```

* **_/group/{id}_** expects the **_vitalAccessToken_** session cookie to be
included in the request and returns some info about the group identified by
**_id_**. Response example:

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

* **_/policy/{id}_** expects the **_vitalAccessToken_** session cookie to be
included in the request and returns some info about the policy identified by
**_id_**. Response example:

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

* **_/application/{id}_** expects the **_vitalAccessToken_** session cookie to be
included in the request and returns some info about the application identified
by **_id_**. Response example:

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

* **_/apptype/{id}_** expects the **_vitalAccessToken_** session cookie to be
included in the request and returns some info about the application type identified
by **_id_**. Response example:

    ```json
    {
       "name":"iPlanetAMWebAgentService",
       "actions":{
          "POST":true,
          "PATCH":true,
          "GET":true,
          "DELETE":true,
          "OPTIONS":true,
          "PUT":true,
          "HEAD":true
       },
       "applicationClassName":"com.sun.identity.entitlement.Application",
       "resourceComparator":"com.sun.identity.entitlement.URLResourceName",
       "saveIndex":"org.forgerock.openam.entitlement.indextree.TreeSaveIndex",
       "searchIndex":"org.forgerock.openam.entitlement.indextree.TreeSearchIndex"
    }
    ```

* **_/application/{id}/policies_** expects the **_vitalAccessToken_** session cookie
to be included in the request and returns some info about the policies part of
the application identified by **_id_**. Response example:

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

* **_/users_** expects the **_vitalAccessToken_** session cookie to be included in
the request and returns the list of users. Response example:

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

* **_/groups_** expects the **_vitalAccessToken_** session cookie to be included in
the request and returns the list of groups. Response example:

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

* **_/policies_** expects the **_vitalAccessToken_** session cookie to be included in
the request and returns the list of policies with some info for each of them. Response example:

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

* **_/applications_** expects the **_vitalAccessToken_** session cookie to be
included in the request and returns the list of applications with some info about each of them. Response
example:

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

* **_/apptypes_** expects the **_vitalAccessToken_** session cookie to be
included in the request and returns the list of application types with some info about each of them. Response
example:

    ```json
    {
       "result":[
          {
             "name":"FineGrainedAccess",
             "actions":{
                "RETRIEVE":false,
                "STORE":false
             },
             "applicationClassName":"com.sun.identity.entitlement.Application",
             "resourceComparator":"com.sun.identity.entitlement.URLResourceName",
             "saveIndex":"com.sun.identity.entitlement.util.ResourceNameIndexGenerator",
             "searchIndex":"com.sun.identity.entitlement.util.ResourceNameSplitter"
          },
          {
             "name":"iPlanetAMWebAgentService",
             "actions":{
                "POST":true,
                "PATCH":true,
                "GET":true,
                "DELETE":true,
                "OPTIONS":true,
                "PUT":true,
                "HEAD":true
             },
             "applicationClassName":"com.sun.identity.entitlement.Application",
             "resourceComparator":"com.sun.identity.entitlement.URLResourceName",
             "saveIndex":"org.forgerock.openam.entitlement.indextree.TreeSaveIndex",
             "searchIndex":"org.forgerock.openam.entitlement.indextree.TreeSearchIndex"
          },
          ...
       ],

       "resultCount":9,

       "remainingPagedResults":0

    }
    ```

* **_/stats_** expects the **_vitalAccessToken_** session cookie to be included in
the request and returns some statistics about the status of the OpenAM server.
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

* **_/user_** expects either the **_vitalAccessToken_** session cookie or the
**_vitalTestToken_** one to be included in the request and returns some info
useful for session management (whether the user session is still valid or not
and some info about the user); if the query parameter **_testCookie_** is set to
true the info is related to the session of the **_vitalTestToken_** cookie,
otherwise of the **_vitalAccessToken_** cookie. Response example:

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

* **_/validate_** expects the **_vitalAccessToken_** and **_vitalTestToken_** (the latter
is optional) session cookies to be included in the request and returns a single
json boolean attribute telling if the session is active or not; if the query
parameter **_testCookie_** is set to true the info is related to the session of the
**_vitalTestToken_** cookie, otherwise of the **_vitalAccessToken_** cookie. While the
above endpoint resets the user idle time, this one does not, but because of an
OpenAM bug requires the user corresponding to the **_vitalAccessToken_** to be an
administrator. Response example:

    ```json
    {
        "active":true
    }
    ```

* **_/getresource_** expects either the **_vitalAccessToken_** session cookie or the
**_vitalTestToken_** one to be included in the request and returns the response of
a GET request to the URL specified in the query parameter **_resource_**; if the
query parameter **_testCookie_** is set to true the **_vitalTestToken_** cookie is
included in the request, otherwise of the **_vitalAccessToken_** cookie is
included.

* **_/permissions_** expects the **_vitalAccessToken_** and **_vitalTestToken_** session cookies to be included in the request and returns some info about the user permissions for data access control. This information is in the form of values that the specific attribute of the documents to retrieve are allowed or denied to have. If the query
parameter **_testCookie_** is set to true the info is related to the user of the
**_vitalTestToken_** cookie, otherwise of the **_vitalAccessToken_** cookie. Response example:

    ```json
    {
       "retrieve":{
          "allowed":[
             {
                "attribute":"id",
                "value":"*"
             },
             {
                "attribute":"id",
                "value":"http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/vital2-I_TrS_2"
             },
             {
                "attribute":"id",
                "value":"http://vital-integration.atosresearch.eu:8180/hireplyppi/sensor/vital2*"
             },
             {
                "attribute":"type",
                "value":"vital:VitalSensor"
             },
             {
                "attribute":"type",
                "value":"*"
             }
          ],
          "denied":[
          ]
       }
    }
    ```

## POST endpoints

You may have to pass information in three different ways for GET methods:

1. As part of the path of the endpoint (e.g. https://vital.com/endpoint/parameter)
2. As cookies
3. As form parameters (included in the body with content type **_application/x-www-form-urlencoded_**, e.g. par1=value1&par2=value2)

* **_/user/create_** expects the **_vitalAccessToken_** session cookie and the
following form parameters to be included in the request:

    * **_givenName_**, the optional user first name

    * **_surname_**, the optional user last name

    * **_name_**, the mandatory username

    * **_password_**, the mandatory (8 characters or more) user password

    * **_mail_**, the optional user e-mail address

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

* **_/user/delete_** expects the **_vitalAccessToken_** session cookie and the **_name_**
form parameter (the user to delete) to be included in the request. Deletes the user.

* **_/group/create_** expects the **_vitalAccessToken_** session cookie and the
**_name_** form parameter (the name of the group to create) to be included in the
request.

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

* **_/group/delete_** expects the **_vitalAccessToken_** session cookie and the
**_name_** form parameter (the group to delete) to be included in the request. Deletes the group.

* **_/group/{id}/addUser_** expects the **_vitalAccessToken_** session cookie and the
**_user_** form parameter (the user to add to the group **_id_**) to be included in the
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

* **_/group/{id}/delUser_** expects the **_vitalAccessToken_** session cookie and the
**_user_** form parameter (the user to remove from the group **_id_**) to be included
in the request.

    It returns some info about the group updated without the removed user (same
format as **_/addUser_**).

* **_/policy/create_** expects the **_vitalAccessToken_** session cookie and the
following form parameters to be included in the request:

    * **_name_**, the name of the policy to create

    * **_description_**, a textual description of the policy to create

    * **_appname_**, the application name

    * **_resources[]_**, the array of resources the policy will affect

    * **_groups[]_**, the array of user groups the policy will affect

    * **_actions[ACTION]_**, boolean values specifying whether the ACTION (GET,
      POST, PUT, etc.) is allowed or denied

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

* **_/policy/delete_** expects the **_vitalAccessToken_** session cookie and the
**_name_** form parameter (the policy to delete) to be included in the request. Deletes the policy.

* **_/application/create_** expects the **_vitalAccessToken_** session cookie and the
following form parameters to be included in the request:

    * **_name_**, the name of the application to create

    * **_type_**, the name of the application type to use

    * **_description_**, some free text describing the application

    * **_resources[]_**, the array of patterns for allowed resources in policies

    * **_actions[ACTION]_**, specifying the default boolean value of the ACTION (GET,
      POST, PUT, etc.)

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

* **_/application/delete_** expects the **_vitalAccessToken_** session cookie and the
**_name_** form parameter (the application to delete) to be included in the
request. Deletes the application.

* **_/user/{id}_** expects the **_vitalAccessToken_** session cookie and the
following form parameters to be included in the request:

    * "givenName", the updated user first name

    * "surname", the updated user last name

    * "mail", the updated user e-mail address

    * "status", "Active" or "Inactive"

    It returns some info about the user identified by **_id_** with the updated
fields (please refer to user info GET or creation for response format).

* **_/user/changePassword_** expects the **_vitalAccessToken_** session cookie and
the following form parameters to be included in the request:

    * **_userpass_**, the new password

    * **_currpass_**, the old password

    It sets the new password **_userpass_** for the user corresponding to the
session of the **_vitalAccessToken_** cookie.

* **_/policy/{id}_** expects the **_vitalAccessToken_** session cookie and the
following form parameters to be included in the request:

    * **_description_**, the updated policy description

    * **_active_**, new policy status (false/true)

    * **_groups[]_**, the updated list of groups to be affected by the policy

    * **_nogr_**, a boolean value which set to false allows to update without
      including the previous parameter (i.e. groups are not updated), while
      set to true means that if no group is specified then all groups are
      removed from the policy

    * **_resources[]_**, the updated list of resources to be affected by the policy

    * **_nores_**, a boolean value which set to false allows to update without
      including the previous parameter (i.e. resources are not updated), while
      set to true means that if no resource is specified then all resources are
      removed from the policy

    * **_actions[ACTION]_**, updated boolean values specifying if the ACTION (GET,
      POST, PUT, etc.) is allowed or denied

    * **_noact_**, a boolean value which set to false allows to update without
      including the previous parameter (i.e. actions are not updated), while
      set to true means that if no action is specified then all actions are
      removed from the policy (the policy will have no effect)

    It returns some info about the policy identified by **_id_** with the updated
fields (please refer to policy info GET or creation for response format).

* **_/application/{id}_** expects the **_vitalAccessToken_** session cookie and the
following form parameters to be included in the request:

    * **_description_**, the updated application description

    * **_type_**, the updated application type

    * **_resources[]_**, the updated list of patterns for resources allowed in
      policies

    * **_nores_**, a boolean value which set to false allows to update without
      including the previous parameter (i.e. patterns are not updated), while
      set to true means that if no pattern is specified then all patterns are
      removed from the application

    * **_actions[ACTION]_**, updated default boolean value for ACTION (GET,
      POST, PUT, etc.)

    * **_noact_**, a boolean value which set to false allows to update without
      including the previous parameter (i.e. actions are not updated), while
      set to true means that if no action is specified then all actions are
      removed from the application

    It returns some info about the application identified by **_id_** with the
updated fields (please refer to application info GET or creation for response
format).

* **_/authenticate_** expects the following form parameters to be included in the
request:

    * **_name_**, username

    * **_password_**, user password

    * **_testCookie_**, if false the SSO **_vitalAccessToken_** cookie is returned,
      otherwise the alternative **_vitalTestToken_** cookie is included in the
      response.

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

* **_/logout_ ** expects either the **_vitalAccessToken_** or the **_vitalTestToken_**
session cookie to be included in the request and performs a logout (destroys the
session identified by the cookie and resets the cookie in the response with an empty value); if the
form parameter **_testCookie_** is set to true the module will use the
**_vitalTestToken_** cookie, otherwise the **_vitalAccessToken_** cookie.

* **_/evaluate_** expects both the **_vitalAccessToken_** and the **_vitalTestToken_**
session cookies and the form parameter **_resources[]_** (resources for which user
permissions are requested) to be included in the request. If the additional
query parameter **_testCookie_** is set to true the "vitalAccessToken" cookie is
considered corresponding to a user session with the rights to request a policy
evaluation while the **_vitalTestToken_** cookie to the user for whom the policies are
to be evaluated, otherwise it is the opposite.

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

* **_/user/register_** expects the form parameter **_mail_**. It send an e-mail to the address specified in the parameter with a link to perform user self-registration.

* **_/user/signup_** expects the
following form parameters to be included in the request:

    * **_givenName_**, the optional user first name

    * **_surname_**, the optional user last name

    * **_name_**, the mandatory username

    * **_password_**, the mandatory (8 characters or more) user password

    * **_mail_**, the optional user e-mail address

    * **_tokenId_**, generated by the above endpoint and included in the e-mail

    * **_confirmationId_**, generated by the above endpoint and included in the e-mail

    It returns some info about the created user (please refer to user info GET or creation for response format).

