package com.saneef.keeper.di

import com.saneef.keeper.TimestampHelper
import com.saneef.keeper.TimestampHelperImpl
import com.saneef.keeper.domain.NotesDbMapper
import com.saneef.keeper.domain.NotesDbMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTimestampHelper(): TimestampHelper = TimestampHelperImpl()

    @Provides
    @Singleton
    fun provideDatabaseModelMapper(timestampHelper: TimestampHelper): NotesDbMapper = NotesDbMapperImpl(timestampHelper)
}
