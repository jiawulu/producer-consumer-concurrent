package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.stream.Collectors;

@Component
public class MigrateService {

    long queryDbTime = 100;
    long transformTime = 5;
    long rpcTime = 10;


    public List<DbModel> queryFromDb(long offset, int size) {

        List<DbModel> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            long id = offset + i;

            if (id >= MigrationStarter.MAX_SIZE) {
                break;
            }

            DbModel dbModel = new DbModel();
            dbModel.setId(id);
            dbModel.setName("db:" + id);
            list.add(dbModel);
        }

        try {
            Thread.sleep(queryDbTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return list;

    }


    public List<ApiModel> transform(List<DbModel> list) {
        try {
            Thread.sleep(transformTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return list.stream().map(dbModel -> {
            ApiModel apiModel = new ApiModel();
            apiModel.setId(dbModel.getId());
            apiModel.setApiName(dbModel.getName());
            return apiModel;
        }).collect(Collectors.toList());
    }


    public void migrate(List<ApiModel> apiModels){

        for (ApiModel apiModel : apiModels){
            try {
                Thread.sleep(rpcTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }


}
