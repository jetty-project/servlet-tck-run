### Servlet TCK using arquillian and test Jetty 11.0.x

Currently, it's really on work in progress only and need to build locally few projects

Current tests failures:
- com.sun.ts.tests.servlet.api.jakarta_servlet_http.sessioncookieconfig.URLClient.constructortest1() (need to update from `master` branch)

### Current status result 

https://jenkins.webtide.net/job/tck/job/tck-servlet-arquillian-experiment/job/jetty-12-ee10/
or 
gh action of this project 


### Install locally TCK jars

```shell
wget -O jakarta-servlet-tck.zip wget  https://download.eclipse.org/jakartaee/servlet/6.1/jakarta-servlet-tck-6.1.0.zip
unzip -j jakarta-servlet-tck.zip servlet-tck/artifacts/servlet-tck-runtime-6.1.0.jar servlet-tck/artifacts/servlet-tck-util-6.1.0.jar
mvn -ntp install:install-file -Dfile=./servlet-tck-runtime-6.1.0.jar -DgroupId=jakarta.tck -DartifactId=servlet-tck-runtime -Dversion=6.1.0 -Dpackaging=jar
mvn -ntp install:install-file -Dfile=./servlet-tck-util-6.1.0.jar -DgroupId=jakarta.tck -DartifactId=servlet-tck-util -Dversion=6.1.0 -Dpackaging=jar
mvn -ntp install:install-file -Dfile=./servlet-tck-6.1.0.pom -DgroupId=jakarta.tck -DartifactId=servlet-tck -Dversion=6.1.0 -Dpackaging=pom
```


#### TCK Servlet build

This will build only the servlet TCK module
```shell
git clone https://github.com/jakartaee/servlet.git
cd servlet
mvn clean install
```

#### Arquillian Jetty 11.0.x/12.0.x support 

NOT NEEDED ANYMORE

```shell
git clone https://github.com/arquillian/arquillian-container-jetty
cd arquillian-container-jetty
mvn install 
```

#### TCK Run with Jetty-12.0.x

This project
```shell
git clone https://github.com/jetty-project/servlet-tck-run
cd servlet-tck-run
git switch jetty-12-ee10
mvn verify
```

