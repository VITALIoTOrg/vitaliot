#!/usr/bin/env bash

# pass is vitaliot
mvn -Djavax.net.ssl.trustStore=vitalKeyStore clean package wildfly:deploy