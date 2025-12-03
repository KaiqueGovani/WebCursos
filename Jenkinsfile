pipeline {
    agent any

    environment {
        MVN = "./mvnw"
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
                            final String reportPath = 'target/site/jacoco/jacoco.xml'
                            if (!fileExists(reportPath)) {
                                error("Relatório Jacoco não encontrado em ${reportPath}")
                            }

                            String jacocoXmlText = readFile(reportPath)
                            jacocoXmlText = jacocoXmlText.replaceAll(/<!DOCTYPE[^>]*>/, '')

                            def xml = new groovy.util.XmlSlurper(false, false).parseText(jacocoXmlText)

                            // collect all <counter ...> and pick the LINE one without @-syntax
                            def counters = xml.depthFirst().findAll { it.name() == 'counter' }
                            def lineCounter = counters.find { (it.attributes().get('type') as String) == 'LINE' }

                            if (!lineCounter) {
                                error('Elemento <counter type="LINE"> não encontrado no jacoco.xml')
                            }

                            BigDecimal covered = (String.valueOf(lineCounter.attributes().get('covered')) ?: '0') as BigDecimal
                            BigDecimal missed  = (String.valueOf(lineCounter.attributes().get('missed'))  ?: '0') as BigDecimal
                            BigDecimal total   = covered + missed
                            BigDecimal coverage = total > 0 ? (covered / total * 100) : 0

                            echo "Cobertura de linhas: ${coverage.setScale(2, java.math.RoundingMode.HALF_UP)}%"

                            if (coverage < 99) {
                                error("Quality gate falhou: cobertura ${coverage.setScale(2, java.math.RoundingMode.HALF_UP)}% abaixo de 99%")
                            }

                            env.COVERAGE_SUCCESS = "true"
                        }
                    }
                }


                stage('Image_Docker') {
                    steps {
                        script {
                            if (env.COVERAGE_SUCCESS?.toBoolean()) {
                                echo "✅ Coverage gate passed → building Docker image..."
                                sh 'docker build -t kaiquemgovani/kaiquemg:latest .'
                            } else {
                                error("❌ Coverage gate failed — aborting Docker build (COVERAGE_SUCCESS=${env.COVERAGE_SUCCESS})")
                            }
                        }
                    }
                }

                stage('Push Docker Image') {
                    steps {
                        script {
                            if (env.COVERAGE_SUCCESS?.toBoolean()) {
                                echo "✅ Coverage gate passed → pushing Docker image..."
                                sh 'docker push kaiquemgovani/kaiquemg:latest'
                            } else {
                                error("❌ Coverage gate failed — aborting Docker push (COVERAGE_SUCCESS=${env.COVERAGE_SUCCESS})")
                            }
                        }
                    }
                }
            }
        }

        stage('Staging') {
            stages {
                stage('Start Container') {
                    steps {
                        echo 'Starting container from Docker Hub...'
                        sh 'docker compose -f docker-compose.staging.yml pull'
                        sh 'docker compose -f docker-compose.staging.yml down --remove-orphans'
                        sh 'docker compose -f docker-compose.staging.yml up -d --no-color --force-recreate'
                        sleep time: 60, unit: 'SECONDS'
                        sh 'docker compose -f docker-compose.staging.yml logs'
                        sh 'docker compose -f docker-compose.staging.yml ps'
                    }
                }

                stage('Run Tests Against Container') {
                    steps {
                        sh 'curl -f http://localhost:8686 || echo "Service not responding"'
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
