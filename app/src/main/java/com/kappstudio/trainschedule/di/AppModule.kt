package com.kappstudio.trainschedule.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.kappstudio.trainschedule.data.local.DataStoreManager
import com.kappstudio.trainschedule.data.local.TrainDatabase
import com.kappstudio.trainschedule.data.remote.TrainApi
import com.kappstudio.trainschedule.data.repository.TrainRepositoryImpl
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return DataStoreManager.createDataStore(context)
    }

    @Provides
    @Singleton
    fun provideRepoDatabase(@ApplicationContext context: Context): TrainDatabase {
        return Room.databaseBuilder(
            context,
            TrainDatabase::class.java,
            "train.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTrainApi(): TrainApi {
        return Retrofit.Builder()
            .baseUrl(TrainApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrainApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTrainRepository(
        api: TrainApi,
        dataStore: DataStore<Preferences>,
        trainDb: TrainDatabase
    ): TrainRepository {
        return TrainRepositoryImpl(api = api, dataStore = dataStore, trainDb = trainDb)
    }
}

