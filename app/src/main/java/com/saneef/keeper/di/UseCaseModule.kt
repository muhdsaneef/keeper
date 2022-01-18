package com.saneef.keeper.di

import com.saneef.keeper.domain.NotesUseCase
import com.saneef.keeper.domain.NotesUseCaseImpl
import com.saneef.keeper.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(ViewModelComponent::class)
@Module
object UseCaseModule {

    @Provides
    fun provideNotesUseCase(
        notesRepository: NotesRepository,
        @IODispatcher dispatcher: CoroutineDispatcher
    ): NotesUseCase {
        return NotesUseCaseImpl(notesRepository, dispatcher)
    }
}
