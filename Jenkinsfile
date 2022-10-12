#!groovy

pipeline {
  agent { node { label 'linux' } }
  options {
    buildDiscarder logRotator( numToKeepStr: '50' )
  }
  parameters {
    string( defaultValue: 'servlet-module-atleast', description: 'GIT branch name to build TCK (master/servlet-module-atleast)',
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
            choices: ['olamy','arquillian'] )

    choice(
            description: 'TCK Github org',
            name: 'GITHUB_ORG_TCK',
            choices: ['olamy','eclipse-ee4j']
    )
    string( defaultValue: 'jdk17', description: 'JDK to build Jetty', name: 'JDKBUILD' )
  }

  stages {

    stage('Build External') {
      parallel {
        stage("Checkout Build Arquillian Jetty") {
          steps {
          container('jetty-build') {
            ws('arquillian') {
              sh "rm -rf *"
              git url: 'https://github.com/${GITHUB_ORG_ARQUILLIAN}/arquillian-container-jetty', branch: '${ARQUILLIAN_JETTY_BRANCH}'
              timeout(time: 30, unit: 'MINUTES') {
                withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                         "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                         "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
                  configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                    sh "mvn --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -V -B -U clean install -DskipTests -T3 -e -Denforcer.skip=true"
                  }
                }
              }
            }
          }
          }
        }
//        stage("Checkout Build Maven Surefire") {
//          steps {
//            ws('surefire') {
//              sh "rm -rf *"
//              git url: 'https://github.com/apache/maven-surefire.git', branch: 'master'
//              timeout(time: 30, unit: 'MINUTES') {
//                withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
//                         "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
//                         "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
//                  configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
//                    sh "mvn --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -Drat.skip=true -V -B -U clean install -DskipTests -T2 -e -Denforcer.skip=true -Dcheckstyle.skip=true"
//                  }
//                }
//              }
//            }
//          }
//        }

        stage("Checkout Build Jetty 12.0.x") {
          steps {
          container('jetty-build') {
            ws('jetty') {
              sh "rm -rf *"
              git url: 'https://github.com/eclipse/jetty.project.git', branch: '${JETTY_BRANCH}'
              timeout(time: 30, unit: 'MINUTES') {
                withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                         "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                         "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
                  configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                    // no need to run jetty now just get last snapshots
                    //sh "mvn --no-transfer-progress -s $GLOBAL_MVN_SETTINGS -V -B -U clean install -T4 -e -Pfast"
                    script {
                      if (JETTY_VERSION == "SNAPSHOT") {
                        def model = readMavenPom file: 'pom.xml'
                        JETTY_VERSION = model.getVersion()
                        //jetty_version = model.getVersion()
                      }
                    }
                  }
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
      container('jetty-build') {
        ws('arquillian') {
          sh "rm -rf *"
          git url: 'https://github.com/${GITHUB_ORG_TCK}/jakartaee-tck/', branch: '${TCK_BRANCH}'
          timeout(time: 30, unit: 'MINUTES') {
            withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                     "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                     "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
              configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                sh "mvn -ntp install:install-file -Dfile=./lib/javatest.jar -DgroupId=javatest -DartifactId=javatest -Dversion=5.0 -Dpackaging=jar"
                sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B -U -pl :servlet,:junit5-extensions -am clean install -DskipTests -e"
              }
            }
          }
        }
      }
      }
    }

    stage("Run TCK") {
      steps {
      container('jetty-build') {
        ws('run-tck') {
          sh "rm -rf *"
          git url: 'https://github.com/jetty-project/servlet-tck-run.git', branch: 'main'
          timeout(time: 120, unit: 'MINUTES') {
            withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                     "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool "maven3"}/bin",
                     "MAVEN_OPTS=-Xms4g -Xmx8g -Djava.awt.headless=true"]) {
              configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                sh "mvn -nsu -ntp -s $GLOBAL_MVN_SETTINGS -Dmaven.test.failure.ignore=true -V -B -U clean verify -e -Djetty.version=$JETTY_VERSION"
                // -Dmaven.test.failure.ignore=true
                //junit testResults: '**/surefire-reports/TEST-**.xml'
              }
            }
          }
        }
      }
      }
      post {
        always {
          ws('run-tck') {
            junit testResults: '**/tck-tests-reports/TEST-**.xml'
          }
        }
      }
    }
  }
}
