#!groovy

pipeline {
  agent { node { label 'linux' } }
  tools { 
    maven 'maven3' 
  }  
  triggers {
    upstream(upstreamProjects: 'tck/tck-olamy-github-tck-run-module-glassfish') //, threshold: hudson.model.Result.SUCCESS)
  }
  options {
    buildDiscarder logRotator( numToKeepStr: '50' )
  }
  parameters {
    string( defaultValue: 'tckrefactor', description: 'GIT branch name to build TCK (master/servlet-module-atleast)',
            name: 'TCK_BRANCH' )

    string( defaultValue: 'jetty-12.0.x', description: 'GIT branch name to build Jetty (jetty-12.0.x)',
            name: 'JETTY_BRANCH' )

    string( defaultValue: 'master', description: 'GIT branch name to build arquillian Jetty (master/tck-all-changes)',
            name: 'ARQUILLIAN_JETTY_BRANCH' )

    string( defaultValue: 'SNAPSHOT', description: 'Jetty Version',
            name: 'JETTY_VERSION' )

    choice(
            description: 'Arquillian Github org',
            name: 'GITHUB_ORG_ARQUILLIAN',
            choices: ['arquillian','olamy'] )

    choice(
            description: 'TCK Github org',
            name: 'GITHUB_ORG_TCK',
            choices: ['jakartaee','olamy']
    )
    string( defaultValue: 'jdk17', description: 'JDK to build Jetty', name: 'JDKBUILD' )
  }

  stages {

    stage('Build External') {
      parallel {

        stage("Checkout Build Jetty 12.0.x") {
          steps {
            ws('jetty') {
              deleteDir()
              checkout([$class: 'GitSCM',
                        branches: [[name: "*/$JETTY_BRANCH"]],
                        extensions: [[$class: 'CloneOption', depth: 1, noTags: true, shallow: true, reference: "/home/jenkins/jetty.project.git"]],
                        userRemoteConfigs: [[url: 'https://github.com/eclipse/jetty.project.git']]])
              timeout(time: 45, unit: 'MINUTES') {
                withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                         "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool 'maven3'}/bin",
                         "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
                  configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                    sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B -U clean install -T5 -e -Pfast -Dmaven.build.cache.restoreGeneratedSources=false -Dmaven.build.cache.remote.url=dav:http://nginx-cache-service.jenkins.svc.cluster.local:80 -Dmaven.build.cache.remote.enabled=true -Dmaven.build.cache.remote.save.enabled=false -Dmaven.build.cache.remote.server.id=remote-build-cache-server"
                    script {
                      if (JETTY_VERSION == "SNAPSHOT") {
                        def model = readMavenPom file: 'pom.xml'
                        JETTY_VERSION = model.getVersion()
                      }
                    }
                  }
                }
              }
            }
          }
        }

//        stage("Checkout Build Arquillian Jetty") {
//          steps {
//            ws('arquillian') {
//              deleteDir()
//              checkout([$class: 'GitSCM',
//                        branches: [[name: "*/$ARQUILLIAN_JETTY_BRANCH"]],
//                        extensions: [[$class: 'CloneOption', depth: 1, noTags: true, shallow: true]],
//                        userRemoteConfigs: [[url: 'https://github.com/${GITHUB_ORG_ARQUILLIAN}/arquillian-container-jetty']]])
//              timeout(time: 30, unit: 'MINUTES') {
//                withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
//                         "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool 'maven3'}/bin",
//                         "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
//                  configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
//                    sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B -U clean install -DskipTests -T3 -e -Denforcer.skip=true"
//                  }
//                }
//              }
//            }
//          }
//        }


      }
    }
    stage("Checkout Build TCK Sources") {
      steps {
        ws('tck-sources') {
          deleteDir()
          checkout([$class: 'GitSCM',
                    branches: [[name: "*/$TCK_BRANCH"]],
                    extensions: [[$class: 'CloneOption', depth: 1, noTags: true, shallow: true]],
                    userRemoteConfigs: [[url: 'https://github.com/${GITHUB_ORG_TCK}/platform-tck']]])
          timeout(time: 30, unit: 'MINUTES') {
            withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                     "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool 'maven3'}/bin",
                     "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
              configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                sh "mvn -ntp install:install-file -Dfile=./lib/javatest.jar -DgroupId=javatest -DartifactId=javatest -Dversion=5.0 -Dpackaging=jar"
                sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B -U -pl :servlet -am clean install -DskipTests -e -Dmaven.build.cache.restoreGeneratedSources=false -Dmaven.build.cache.remote.url=dav:http://nginx-cache-service.jenkins.svc.cluster.local:80 -Dmaven.build.cache.remote.enabled=true -Dmaven.build.cache.remote.save.enabled=true -Dmaven.build.cache.remote.server.id=remote-build-cache-server"
              }
            }
          }
        }
      }
    }

    stage("Run TCK") {
      steps {
        timeout(time: 90, unit: 'MINUTES') {
          withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                   "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool 'maven3'}/bin",
                   "MAVEN_OPTS=-Xms4g -Xmx8g -Djava.awt.headless=true"]) {
            configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
              sh "mvn -nsu -ntp -s $GLOBAL_MVN_SETTINGS -Dmaven.test.failure.ignore=true -V -B -U clean verify -e -Djetty.version=$JETTY_VERSION"
            }
          }
        }
      }
      post {
        always {
          junit testResults: '**/surefire-reports/TEST-**.xml'
        }
      }
    }
  }
}
