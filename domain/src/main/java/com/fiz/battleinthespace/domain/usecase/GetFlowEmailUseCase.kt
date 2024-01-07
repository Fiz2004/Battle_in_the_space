package com.fiz.battleinthespace.domain.usecase

import com.fiz.battleinthespace.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFlowEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    operator fun invoke(): Flow<String?> {
        return authRepository.getFlowEmail()
    }
}