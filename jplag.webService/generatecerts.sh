#!/bin/bash
#
# Creates a new self-signed certificate and imports it into a new truststore
# It also imports the www servers certificate to allow communication with it
#

cd ~/jplag_webservice/tomcat50-jwsdp
rm server.keystore
rm tomcat.cer
rm server.trust
keytool -genkey -dname "CN=www.ipd.uni-karlsruhe.de, OU=[IPD] JPlag Team, O=Uni Karlsruhe, L=Karlsruhe, S=Baden-Wuerttemberg, C=DE" -alias tomcat -keyalg RSA -keystore server.keystore -storepass gulpie! -keypass gulpie!
keytool -export -alias tomcat -keystore server.keystore -file tomcat.cer -storepass gulpie!
keytool -import -alias tomcat-server -keystore server.trust -trustcacerts -file tomcat.cer -storepass gulpie! -noprompt
keytool -import -alias apache-server -keystore server.trust -trustcacerts -file /etc/apache2/ssl.crt/www-06.crt -storepass gulpie! -noprompt