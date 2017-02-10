cd ~/workspace/tools/apache-tomcat-8.5.9/bin/
./shutdown.sh
cd ~/workspace/tools/apache-tomcat-8.5.9/webapps/
rm -rf teamdivider
rm teamdivider.war
cd ~/workspace/source/SF-Sports-Club/
git pull origin page_refactor
mvn clean install -Dmaven.test.skip=true
mv target/teamdivider-1.war ~/workspace/tools/apache-tomcat-8.5.9/webapps/teamdivider.war
cd ~/workspace/tools/apache-tomcat-8.5.9/bin/
./startup.sh