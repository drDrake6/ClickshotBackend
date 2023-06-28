package step.learning.ioc;

import com.google.inject.AbstractModule;
import step.learning.services.*;

public class ConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataService.class).to(MysqlDataService.class);
        bind(HashService.class).to(Sha1HashService.class);
        bind(EmailService.class).to(GmailService.class);
        bind(BodyParseService.class).to(BodyParseJSONService.class);
        bind(LoadConfigService.class).to(LoadJSONConfigService.class);
    }
}
