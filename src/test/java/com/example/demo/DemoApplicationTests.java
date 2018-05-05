package com.example.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Autowired
	private MigrateService migrateService;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Autowired
	private MigrationStarter migrationStarter;

	@Test
	public void contextLoads() throws InterruptedException {

		Assert.assertNotNull(migrateService);

		Assert.assertNotNull(executor);

		migrationStarter.start();

		Thread.sleep(100000L);
	}



}
