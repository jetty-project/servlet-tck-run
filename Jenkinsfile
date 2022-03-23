#!groovy

pipeline {
  agent { node { label 'linux' } }
  options {
    buildDiscarder logRotator( numToKeepStr: '50' )
  }
  parameters {
    string( defaultValue: 'servlet-module-atleast', description: 'GIT branch name to build (master/servlet-module-atleast)',
            name: 'TCK_BRANCH' )
    choice(
            description: 'Github org',
            name: 'GITHUB_ORG',
            choices: ['olamy','eclipse-ee4j']
    )
    string( defaultValue: 'jdk11', description: 'JDK to build Jetty', name: 'JDKBUILD' )
  }

  stages {
    stage("cleanup"){
      steps {
            sh "rm -rf *"
      }
    }

    stage("Checkout Build Arquillian Jetty") {
      steps {
        git url: 'https://github.com/arquillian/arquillian-container-jetty', branch: 'master'
        timeout(time: 30, unit: 'MINUTES') {
          withEnv(["JAVA_HOME=${ tool "$JDKBUILD" }",
                   "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                    "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
            configFileProvider( [configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
              sh "mvn --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -V -B -U clean install -DskipTests -T3 -e -Denforcer.skip=true"
            }
          }

        }
      }
    }

//    stage("Checkout Build Maven Surefire") {
//      steps {
//        git url: 'https://github.com/apache/maven-surefire.git', branch: 'master'
//        timeout(time: 30, unit: 'MINUTES') {
//          withEnv(["JAVA_HOME=${ tool "$JDKBUILD" }",
//                   "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
//                   "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
//            configFileProvider( [configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
//              sh "mvn --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -Drat.skip=true -V -B -U clean install -DskipTests -T3 -e -Denforcer.skip=true -Dcheckstyle.skip=true"
//            }
//          }
//
//        }
//      }
//    }

    stage("Checkout Build TCK Sources") {
      steps {
        git url: 'https://github.com/${GITHUB_ORG}/jakartaee-tck/', branch: '${TCK_BRANCH}'
        timeout(time: 30, unit: 'MINUTES') {
          withEnv(["JAVA_HOME=${ tool "$JDKBUILD" }",
                   "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                    "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
            configFileProvider( [configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
              sh "mvn --no-transfer-progress install:install-file -Dfile=./lib/javatest.jar -DgroupId=javatest -DartifactId=javatest -Dversion=5.0 -Dpackaging=jar"
              sh "mvn --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -V -B -U -pl :servlet,:junit5-extensions -am clean install -DskipTests -e"
            }
          }

        }
      }
    }

    stage("cleanup again"){
      steps {
        sh "rm -rf *"
      }
    }

    stage("Checkout TCK Run") {
      steps {
        git url: 'https://github.com/jetty-project/servlet-tck-run.git', branch: 'main'
      }
    }

    stage("Run TCK") {
      steps {
        timeout(time: 30, unit: 'MINUTES') {
          withEnv(["JAVA_HOME=${ tool "$JDKBUILD" }",
                   "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                    "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
            configFileProvider( [configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
              sh "mvn -Dmaven.test.failure.ignore=true --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -V -B -U clean verify -e"
            }
          }

        }
      }
      post {
        always {
          junit testResults: '**/surefire-reports/*.xml'
        }
      }
    }
  }
}
