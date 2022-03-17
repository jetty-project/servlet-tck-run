#!groovy

pipeline {
  agent { node { label 'linux' } }
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

    stage("Checkout Arquillian Jetty") {
      steps {
        git url: 'https://github.com/arquillian/arquillian-container-jetty', branch: 'master'
      }
    }

    stage("Build Arquillian Jetty") {
      steps {
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

    stage("Checkout TCK Sources") {
      steps {
        git url: 'https://github.com/${GITHUB_ORG}/jakartaee-tck/', branch: '${TCK_BRANCH}'
      }
    }

    stage("Build TCK Sources") {
      steps {
        timeout(time: 30, unit: 'MINUTES') {
          withEnv(["JAVA_HOME=${ tool "$JDKBUILD" }",
                   "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                    "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
            configFileProvider( [configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
              sh "mvn --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -V -B -U -pl :servlet -am clean install -DskipTests -e"
            }
          }

        }
      }
    }

    stage("Checkout TCK Run") {
      steps {
        git url: 'https://github.com/jetty-project/servlet-tck-run.git', branch: 'master'
      }
    }

    stage("Run TCK") {
      steps {
        timeout(time: 30, unit: 'MINUTES') {
          withEnv(["JAVA_HOME=${ tool "$JDKBUILD" }",
                   "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                    "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
            configFileProvider( [configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
              sh "mvn --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -V -B -U -pl :servlet -am clean verify -e"
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
