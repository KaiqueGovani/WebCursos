pipeline {
    agent any

    environment {
        MVN = "./mvnw"
        COVERAGE_SUCCESS = 'false'
    }

    stages {
        stage('Pre-Build') {
            steps {
                sh "${MVN} -q -DskipTests=true clean"
            }
        }

        stage('Build') {
            stages {
                stage('Pipeline-test-dev') {
                    steps {
                        sh "${MVN} verify"
                    }
                    post {
                        always {
                            junit 'target/surefire-reports/*.xml'
                            recordIssues tools: [pmdParser(pattern: 'target/pmd.xml')]
                            jacoco execPattern: 'target/jacoco.exec', classPattern: 'target/classes', sourcePattern: 'src/main/java'
                        }
                    }
                }

                stage('Quality Gate') {
                    steps {
                        script {
                            def reportPath = 'target/site/jacoco/jacoco.xml'
                            if (!fileExists(reportPath)) {
                                error("Relatório Jacoco não encontrado em ${reportPath}")
                            }

                            def jacocoXml = readFile(reportPath)
                            def xml = new XmlSlurper().parseText(jacocoXml)
                            def lineCounter = xml.counter.find { it.@type == 'LINE' }
                            def covered = lineCounter.@covered.toBigDecimal()
                            def missed = lineCounter.@missed.toBigDecimal()
                            def coverage = covered / (covered + missed) * 100

                            echo "Cobertura de linhas: ${coverage.setScale(2, java.math.RoundingMode.HALF_UP)}%"

                            if (coverage < 99) {
                                error("Quality gate falhou: cobertura ${coverage}% abaixo de 99%")
                            }

                            env.COVERAGE_SUCCESS = 'true'
                        }
                    }
                }

                stage('Image_Docker') {
                    when {
                        expression { env.COVERAGE_SUCCESS == 'true' }
                    }
                    steps {
                        sh 'docker build -t webcursos:dev .'
                    }
                }
            }
        }

        stage('Post-Build') {
            steps {
                archiveArtifacts artifacts: 'target/**/*.jar, target/site/jacoco/**', fingerprint: true
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
