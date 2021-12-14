package com.saneef.keeper.di

import com.saneef.keeper.domain.NotesUseCase
import com.saneef.keeper.domain.NotesUseCaseImpl
import com.saneef.keeper.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
object UseCaseModule {

    @Provides
    fun provideNotesUseCase(notesRepository: NotesRepository): NotesUseCase {
        return NotesUseCaseImpl(notesRepository)
    }
}
