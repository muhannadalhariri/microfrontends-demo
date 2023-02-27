package com.aiondigital.mfe.finances;

import com.aiondigital.mfe.finances.FinanceServiceApp;
import com.aiondigital.mfe.finances.config.AsyncSyncConfiguration;
import com.aiondigital.mfe.finances.config.EmbeddedElasticsearch;
import com.aiondigital.mfe.finances.config.EmbeddedKafka;
import com.aiondigital.mfe.finances.config.EmbeddedMongo;
import com.aiondigital.mfe.finances.config.EmbeddedRedis;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { FinanceServiceApp.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedMongo
@EmbeddedElasticsearch
@EmbeddedKafka
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
