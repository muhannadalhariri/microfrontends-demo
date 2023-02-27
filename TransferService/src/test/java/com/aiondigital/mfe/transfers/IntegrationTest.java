package com.aiondigital.mfe.transfers;

import com.aiondigital.mfe.transfers.TransferServiceApp;
import com.aiondigital.mfe.transfers.config.AsyncSyncConfiguration;
import com.aiondigital.mfe.transfers.config.EmbeddedElasticsearch;
import com.aiondigital.mfe.transfers.config.EmbeddedKafka;
import com.aiondigital.mfe.transfers.config.EmbeddedMongo;
import com.aiondigital.mfe.transfers.config.EmbeddedRedis;
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
@SpringBootTest(classes = { TransferServiceApp.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedMongo
@EmbeddedElasticsearch
@EmbeddedKafka
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
