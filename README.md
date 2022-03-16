### Servlet TCK using arquillian and test Jetty 11.0.x

Currently it;s real work in progress only which need to build locally few projects

#### TCK Servlet build

This will build only the servlet TCK module
```shell
git clone https://github.com/olamy/jakartaee-tck.git
cd jakartaee-tck
git checkout servlet-module-atleast
mvn install -pl :servlet -am
```

#### Arquillian Jetty 11.0.x support 

```shell
git clone https://github.com/arquillian/arquillian-container-jetty
cd arquillian-container-jetty
mvn install 
```

#### TCK Run with Jetty-11.0.x

This project
```shell
git clone https://github.com/jetty-project/servlet-tck-run
cd servlet-tck-run
mvn verify
```
