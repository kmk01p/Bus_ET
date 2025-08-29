module.exports = {
  apps: [{
    name: 'ethiopia-bus-monitor',
    script: 'java',
    args: '-jar target/egov-bus-0.0.1-SNAPSHOT.jar',
    cwd: '/home/user/webapp',
    interpreter: '',
    env: {
      JAVA_OPTS: '-Xmx512m -Xms256m',
      SERVER_PORT: 8080
    },
    max_memory_restart: '1G',
    error_file: './logs/err.log',
    out_file: './logs/out.log',
    log_file: './logs/combined.log',
    time: true
  }]
};