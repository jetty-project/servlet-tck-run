#!groovy

pipeline {
  agent { node { label 'linux' } }
  tools {
    maven 'maven3'
  }
//  triggers {
//    upstream(upstreamProjects: 'tck/tck-olamy-github-tck-run-module-glassfish') //, threshold: hudson.model.Result.SUCCESS)
//  }
  options {
    buildDiscarder logRotator( numToKeepStr: '50' )
  }
  parameters {
    string( defaultValue: 'master', description: 'GIT branch name to build TCK (master/6.1.x)',
            name: 'TCK_BRANCH' )

    choice( description: 'TCK Github org',
            name: 'GITHUB_ORG_TCK',
            choices: ['jakartaee','olamy','jetty-project'])

    string( defaultValue: 'jetty-12.1.x', description: 'GIT branch name to build Jetty (jetty-12.1.x)',
            name: 'JETTY_BRANCH' )

    string( defaultValue: 'SNAPSHOT', description: 'Jetty Version',
            name: 'JETTY_VERSION' )

    string( defaultValue: 'SNAPSHOT', description: 'Jetty Version',
            name: 'TCK_VERSION' )

    choice( description: 'Arquillian Github org',
            name: 'GITHUB_ORG_ARQUILLIAN',
            choices: ['arquillian','olamy','jetty-project'] )

    string( defaultValue: 'jetty-12-ee11', description: 'GIT branch name to build arquillian Jetty (master/tck-all-changes)',
            name: 'ARQUILLIAN_JETTY_BRANCH' )

    string( defaultValue: 'jdk17', description: 'JDK to build Jetty', name: 'JDKBUILD' )

    string( defaultValue: '', description: 'Extra Maven Args', name: 'MVN_ARGS' )

  }

  stages {

    stage('Build External') {
      parallel {

        stage("Checkout Build Jetty 12.1.x") {
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
                    sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B -U clean install -T5 -e -DskipTests -Dmaven.build.cache.restoreGeneratedSources=false -Dmaven.build.cache.remote.url=http://nginx-cache-service.jenkins.svc.cluster.local:80 -Daether.connector.http.supportWebDav=true -Dmaven.build.cache.remote.enabled=true -Dmaven.build.cache.remote.save.enabled=true -Dmaven.build.cache.remote.server.id=remote-build-cache-server"
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

       stage("Checkout Build Arquillian Jetty") {
         steps {
           ws('arquillian') {
             deleteDir()
             checkout([$class: 'GitSCM',
                       branches: [[name: "*/$ARQUILLIAN_JETTY_BRANCH"]],
                       extensions: [[$class: 'CloneOption', depth: 1, noTags: true, shallow: true]],
                       userRemoteConfigs: [[url: 'https://github.com/${GITHUB_ORG_ARQUILLIAN}/arquillian-container-jetty']]])
             timeout(time: 30, unit: 'MINUTES') {
               withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                        "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool 'maven3'}/bin",
                        "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
                 configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                   sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B -U clean install -DskipTests -T3 -e -Denforcer.skip=true"
                 }
               }
             }
           }
         }
       }


      }
    }
    stage("Checkout Build TCK Sources") {
      steps {
        ws('tck') {
          deleteDir()
          checkout([$class: 'GitSCM',
                    branches: [[name: "*/$TCK_BRANCH"]],
                    extensions: [[$class: 'CloneOption', depth: 1, noTags: true, shallow: true]],
                    userRemoteConfigs: [[url: 'https://github.com/${GITHUB_ORG_TCK}/servlet']]])
          timeout(time: 30, unit: 'MINUTES') {
            withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                     "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool 'maven3'}/bin",
                     "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
              configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                //sh "mvn -ntp install:install-file -Dfile=./lib/javatest.jar -DgroupId=javatest -DartifactId=javatest -Dversion=5.0 -Dpackaging=jar"
                sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B clean install -e -Dmaven.build.cache.remote.url=http://nginx-cache-service.jenkins.svc.cluster.local:80 -Dmaven.build.cache.remote.enabled=true -Dmaven.build.cache.remote.save.enabled=true -Dmaven.build.cache.remote.server.id=remote-build-cache-server -Daether.connector.http.supportWebDav=true"
                script {
                  if (TCK_VERSION == "SNAPSHOT") {
                    def model = readMavenPom file: 'pom.xml'
                    TCK_VERSION = model.getVersion()
                  }
                }
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
              sh "mvn -nsu -ntp -s $GLOBAL_MVN_SETTINGS -Dmaven.test.failure.ignore=true -V -B -U clean verify -e -Djakarta.tck.version=$TCK_VERSION -Djetty.version=$JETTY_VERSION $MVN_ARGS"
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
