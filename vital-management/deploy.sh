#!/usr/bin/env bash
mvn -Djavax.net.ssl.trustStore=vitalKeyStore clean package wildfly:deploy