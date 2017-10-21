package com.lqr.biliblili.mvp.model;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.lqr.biliblili.app.data.api.cache.RecommendCache;
import com.lqr.biliblili.app.data.api.service.RecommendService;
import com.lqr.biliblili.app.data.entity.recommend.IndexData;
import com.lqr.biliblili.mvp.contract.RecommendContract;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKeyGroup;

@FragmentScope
public class RecommendModel extends BaseModel implements RecommendContract.Model {

    private Gson mGson;
    private Application mApplication;

    @Inject
    public RecommendModel(IRepositoryManager repositoryManager, Gson gson, Application application) {
        super(repositoryManager);
        mGson = gson;
        mApplication = application;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<IndexData> getRecommendIndexData(int idx, boolean refresh) {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(RecommendService.class)
                .getRecommendIndexData(idx, refresh ? "true" : "false"))
                .flatMap(new Function<Observable<IndexData>, ObservableSource<IndexData>>() {
                    @Override
                    public ObservableSource<IndexData> apply(@NonNull Observable<IndexData> indexDataObservable) throws Exception {
                        return mRepositoryManager
                                .obtainCacheService(RecommendCache.class)
                                .getRecommendIndexData(indexDataObservable, new DynamicKeyGroup(idx, refresh ? "true" : "false"))
                                .map(indexDataReply -> indexDataReply.getData());
                    }
                });
    }
}
