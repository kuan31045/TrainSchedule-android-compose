package com.kappstudio.trainschedule.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.kappstudio.trainschedule.BuildConfig
import com.kappstudio.trainschedule.data.local.DataStoreManager
import com.kappstudio.trainschedule.data.local.TrainDatabase
import com.kappstudio.trainschedule.data.remote.TrainApi
import com.kappstudio.trainschedule.data.repository.PreferenceRepositoryImpl
import com.kappstudio.trainschedule.data.repository.TrainRepositoryImpl
import com.kappstudio.trainschedule.domain.repository.PreferenceRepository
import com.kappstudio.trainschedule.domain.repository.TrainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideTrainApi(): TrainApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = when (BuildConfig.DEBUG) {
                        true -> HttpLoggingInterceptor.Level.BODY
                        false -> HttpLoggingInterceptor.Level.NONE
                    }
                }
            ).build()

        return Retrofit.Builder()
            .baseUrl(TrainApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TrainApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTrainRepository(
        api: TrainApi,
        dataStore: DataStore<Preferences>,
        trainDb: TrainDatabase,
        connectivityManager: ConnectivityManager,
    ): TrainRepository {
        return TrainRepositoryImpl(
            api = api,
            dataStore = dataStore,
            trainDb = trainDb,
            connectivityManager = connectivityManager
        )
    }

    @Provides
    @Singleton
    fun providePreferenceRepository(
        dataStore: DataStore<Preferences>,
    ): PreferenceRepository {
        return PreferenceRepositoryImpl(dataStore = dataStore)
    }
}