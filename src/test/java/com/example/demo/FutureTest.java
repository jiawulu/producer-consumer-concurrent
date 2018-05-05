package com.example.demo;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class FutureTest {

    @Test
    public void test() throws InterruptedException {


        CompletableFuture completableFuture = new CompletableFuture();

//        Executors.newSingleThreadExecutor().submit()
        new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("start thread");

                try {
                    System.out.println(completableFuture.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                System.out.println("thread end");


            }
        }).start();


        completableFuture.complete(true);

        Thread.sleep(100);

        System.out.println("over");


    }

}
