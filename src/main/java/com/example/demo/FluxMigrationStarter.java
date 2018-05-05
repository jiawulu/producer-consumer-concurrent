package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Component
public class FluxMigrationStarter {

    public static final int PAGE_SIZE = 15;
    public static final int MAX_SIZE = 20;
    public static final int CONSUMER_SIZE = 4;
    public static final long CACHE_SIZE = 10;

    private static Logger logger = LoggerFactory.getLogger(FluxMigrationStarter.class);

    @Autowired
    private MigrateService migrateService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    private BlockingDeque<ApiModel> apiModelBlockingDeque = new LinkedBlockingDeque<>();


    public void start() {

//        Flux.generate(new java.util.function.Consumer<SynchronousSink<? extends Object>>() {
//            @Override
//            public void accept(SynchronousSink<?> synchronousSink) {
//
//            }
//        });


        Producer producer = new Producer();

        threadPoolTaskExecutor.execute(producer);

        for (int i = 0; i < CONSUMER_SIZE; i++) {
            threadPoolTaskExecutor.execute(new Consumer(producer));
        }

    }


    private class Consumer implements Runnable {

        private Producer producer;

        public Consumer(Producer producer) {
            this.producer = producer;
        }

        @Override
        public void run() {

            while (true) {

                ApiModel pop = null;
                try {
                    pop = apiModelBlockingDeque.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }

                logger.info("pop task {}", pop);

                ArrayList<ApiModel> apiModels = new ArrayList<>(1);
                apiModels.add(pop);
                migrateService.migrate(apiModels);

                producer.trigger();

                if (producer.isFinish && apiModelBlockingDeque.size() == 0) {
                    return;
                }

            }

        }
    }


    private class Producer implements Runnable {

        private CompletableFuture completableFuture;
        //TODO is running
        private AtomicBoolean isFetching = new AtomicBoolean(false);
        private long offset = 0;
        private boolean isFinish;

        public void trigger() {
            if (null != completableFuture) {
                completableFuture.complete(true);
            }
        }


        public boolean isFinish() {
            return isFinish;
        }

        @Override
        public void run() {

            while (offset < MAX_SIZE) {

                fetchModels();

                if (null == completableFuture) {
                    completableFuture = new CompletableFuture();
                }

                try {
                    completableFuture.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                completableFuture = null;

            }

            isFinish = true;
        }


        private void fetchModels() {

            if (!isFetching.compareAndSet(false, true)) {
                logger.info("fetching models is already running！");
                return;
            }

            logger.info("fetching models with offset {}", offset);

            try {

                while (apiModelBlockingDeque.size() <= CACHE_SIZE) {


                    //TODO

                    List<DbModel> dbModels = migrateService.queryFromDb(offset, PAGE_SIZE);

                    offset += PAGE_SIZE;

                    if (dbModels.size() > 0) {
                        apiModelBlockingDeque.addAll(migrateService.transform(dbModels));
                    }


                }

                logger.info("apiModelBlockingDeque is full , SKIP");


            } finally {

                isFetching.compareAndSet(true, false);

            }


        }
    }


}
