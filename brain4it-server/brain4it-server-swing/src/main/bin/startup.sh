# Brain4it server startup

java -cp "../lib/*" -Djava.util.logging.manager=org.brain4it.server.standalone.ServerLogManager -Djava.util.logging.config.file="../conf/logging.properties" org.brain4it.server.swing.SwingRunner ../conf/server.properties
