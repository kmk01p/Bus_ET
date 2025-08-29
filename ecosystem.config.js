module.exports = {
  apps: [{
    name: 'ethiopia-bus-monitoring',
    script: 'java',
    args: '-jar target/egov-bus-0.0.1-SNAPSHOT.jar',
    cwd: '/home/user/webapp',
    instances: 1,
    exec_mode: 'fork',
    autorestart: true,
    max_memory_restart: '1G',
    env: {
      NODE_ENV: 'production',
      JAVA_OPTS: '-Xmx512m -Xms256m'
    },
    error_file: './logs/err.log',
    out_file: './logs/out.log',
    log_file: './logs/combined.log',
    time: true
  }]
}