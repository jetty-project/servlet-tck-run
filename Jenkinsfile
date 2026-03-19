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
    string( defaultValue: '6.1.x', description: 'GIT branch name to build TCK (master/6.1.x)',
            name: 'TCK_BRANCH' )

    choice( description: 'TCK Github org',
            name: 'GITHUB_ORG_TCK',
            choices: ['jakartaee','olamy','jetty-project','markt-asf'])

    string( defaultValue: 'servlet', description: 'Repository name with tck and servlet API',
        name: 'TCK_REPO_NAME' )

    string( defaultValue: 'jetty-12.1.x', description: 'GIT branch name to build Jetty (jetty-12.1.x)',
            name: 'JETTY_BRANCH' )

    string( defaultValue: 'SNAPSHOT', description: 'Jetty Version',
            name: 'JETTY_VERSION' )

    string( defaultValue: '6.1.0', description: 'TCK Version (6.1.0/SNAPSHOT to build from sources)',
            name: 'TCK_VERSION' )

    string( defaultValue: 'SNAPSHOT', description: 'Servlet API Version (6.1.0/SNAPSHOT)',
            name: 'API_VERSION' )

    choice( description: 'Arquillian Github org',
            name: 'GITHUB_ORG_ARQUILLIAN',
            choices: ['arquillian','olamy','jetty-project'] )

    string( defaultValue: 'master', description: 'GIT branch name to build arquillian Jetty (master/tck-all-changes)',
        name: 'ARQUILLIAN_JETTY_BRANCH' )

    choice( description: 'Arquillian Github org',
            name: 'GITHUB_ORG_ARQUILLIAN_CORE',
            choices: ['arquillian','olamy','jetty-project'] )

    string( defaultValue: 'main', description: 'GIT branch name to build arquillian Core (master/tck-multithread-failures)',
            name: 'ARQUILLIAN_CORE_BRANCH' )

    string( defaultValue: 'jdk17', description: 'JDK to build Jetty', name: 'JDKBUILD' )

    string( defaultValue: '-Djunit.jupiter.execution.parallel.enabled=false', description: 'Extra Maven Args', name: 'MVN_ARGS' )

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
                    sh "mkdir ~/.mimir"
                    sh "cp jenkins-mimir-daemon.properties ~/.mimir/daemon.properties"
                    //sh "echo 'mimir.jgroups.enabled=false' >> ~/.mimir/daemon.properties"
                    sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B clean install -T5 -Pfast -e -DskipTests -Dmaven.build.cache.restoreGeneratedSources=false -Dmaven.build.cache.remote.url=http://nexus-service.nexus.svc.cluster.local:8081/repository/maven-build-cache -Dmaven.build.cache.remote.enabled=true -Dmaven.build.cache.remote.save.enabled=true -Dmaven.build.cache.remote.server.id=nexus-cred"
                    script {
                      if (JETTY_VERSION == "SNAPSHOT") {
                        JETTY_VERSION = sh(script: "mvn -N help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                      }
                      sh "echo Jetty Version is ${JETTY_VERSION}"
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

        stage("Checkout Build Arquillian Core") {
          steps {
            ws('arquillian') {
              deleteDir()
              checkout([$class: 'GitSCM',
                       branches: [[name: "*/$ARQUILLIAN_CORE_BRANCH"]],
                       extensions: [[$class: 'CloneOption', depth: 1, noTags: true, shallow: true]],
                       userRemoteConfigs: [[url: 'https://github.com/${GITHUB_ORG_ARQUILLIAN_CORE}/arquillian-core']]])
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
                    userRemoteConfigs: [[url: 'https://github.com/${GITHUB_ORG_TCK}/${TCK_REPO_NAME}']]])
          timeout(time: 30, unit: 'MINUTES') {
            withEnv(["JAVA_HOME=${tool "$JDKBUILD"}",
                     "PATH+MAVEN=${env.JAVA_HOME}/bin:${tool 'maven3'}/bin",
                     "MAVEN_OPTS=-Xms2g -Xmx4g -Djava.awt.headless=true"]) {
              configFileProvider([configFile(fileId: 'oss-settings.xml', variable: 'GLOBAL_MVN_SETTINGS')]) {
                sh "mvn -ntp -s $GLOBAL_MVN_SETTINGS -V -B clean install -e -Dmaven.build.cache.remote.url=http://nexus-service.nexus.svc.cluster.local:8081/repository/maven-build-cache -Dmaven.build.cache.remote.enabled=true -Dmaven.build.cache.remote.save.enabled=true -Dmaven.build.cache.remote.server.id=nexus-cred"
                script {
                  if (TCK_VERSION == "SNAPSHOT") {
                    TCK_VERSION = sh(script: "mvn -N help:evaluate -f tck/pom.xml -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                  }
                  sh "echo TCK_VERSION is ${TCK_VERSION}"
                  if (API_VERSION == "SNAPSHOT") {
                    API_VERSION = sh(script: "mvn -N help:evaluate -f api/pom.xml -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                  }
                  sh "echo API_VERSION is ${API_VERSION}"
                }
              }
            }
          }
        }
      }
    }

    stage("Install TCK") {
      when {
        expression { params.TCK_VERSION != 'SNAPSHOT' || params.API_VERSION != 'SNAPSHOT' }
      }
      steps {
          ws('tck-install') {
            sh 'wget -O jakarta-servlet-tck.zip https://download.eclipse.org/jakartaee/servlet/6.1/jakarta-servlet-tck-6.1.0.zip'
            sh 'unzip -j jakarta-servlet-tck.zip servlet-tck/artifacts/servlet-tck-runtime-6.1.0.jar servlet-tck/artifacts/servlet-tck-util-6.1.0.jar servlet-tck/artifacts/servlet-tck-6.1.0.pom '
            sh "mvn -ntp install:install-file -Dfile=./servlet-tck-runtime-6.1.0.jar -DgroupId=jakarta.tck -DartifactId=servlet-tck-runtime -Dversion=6.1.0 -Dpackaging=jar"
            sh "mvn -ntp install:install-file -Dfile=./servlet-tck-util-6.1.0.jar -DgroupId=jakarta.tck -DartifactId=servlet-tck-util -Dversion=6.1.0 -Dpackaging=jar"
            sh "mvn -ntp install:install-file -Dfile=./servlet-tck-6.1.0.pom -DgroupId=jakarta.tck -DartifactId=servlet-tck -Dversion=6.1.0 -Dpackaging=pom"
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
              sh "mvn -nsu -ntp -s $GLOBAL_MVN_SETTINGS -Dmaven.test.failure.ignore=true -V -B -U clean verify -e -Djakarta.tck.version=$TCK_VERSION -Dservlet.api.version=$API_VERSION -Djetty.version=$JETTY_VERSION $MVN_ARGS"
            }
          }
        }
      }
      post {
        always {
          script{
            currentBuild.description = "Build TCK Jetty branch ${JETTY_BRANCH}, Jetty Version ${JETTY_VERSION}, jdk ${JDKBUILD}"
          }
          junit testResults: '**/surefire-reports/TEST-**.xml'
        }
      }
    }
  }
}
