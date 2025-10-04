package rayyan.asia.config;

import com.mongodb.event.CommandFailedEvent;
import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import org.bson.BsonDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoCommandLoggingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("rayyan.asia.mongo");

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoCommandListenerCustomizer() {
        return builder -> builder.addCommandListener(new CommandListener() {
            @Override
            public void commandStarted(CommandStartedEvent event) {
                try {
                    BsonDocument cmd = event.getCommand();
                    LOGGER.debug("MongoDB command started: name={}, database={}, command={}",
                            event.getCommandName(), event.getDatabaseName(), cmd.toJson());
                } catch (Exception ex) {
                    LOGGER.debug("MongoDB command started (failed to stringify): name={} database={}",
                            event.getCommandName(), event.getDatabaseName(), ex);
                }
            }

            @Override
            public void commandSucceeded(CommandSucceededEvent event) {
                try {
                    LOGGER.debug("MongoDB command succeeded: name={} elapsedMs={}",
                            event.getCommandName(), event.getElapsedTime(TimeUnit.MILLISECONDS));
                } catch (Exception ex) {
                    LOGGER.debug("MongoDB command succeeded: name={} (failed to read elapsed)", event.getCommandName(), ex);
                }
            }

            @Override
            public void commandFailed(CommandFailedEvent event) {
                try {
                    LOGGER.warn("MongoDB command failed: name={} elapsedMs={} error={}",
                            event.getCommandName(), event.getElapsedTime(TimeUnit.MILLISECONDS), event.getThrowable().getMessage());
                } catch (Exception ex) {
                    LOGGER.warn("MongoDB command failed: name={} (failed to read details)", event.getCommandName(), ex);
                }
            }
        });
    }
}

