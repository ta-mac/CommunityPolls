package com.example.communitypolls

import app.cash.turbine.test
import com.example.communitypolls.data.auth.AuthResult
import com.example.communitypolls.fakes.FakeAuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    @Test fun sign_up_and_in_and_out_flow() = runTest {
        val repo = FakeAuthRepository()

        repo.currentUser.test {
            assertNull(awaitItem())

            val s = repo.signUp("test@example.com", "pw", "Mac")
            assertTrue(s is AuthResult.Success)
            val u1 = awaitItem()
            assertNotNull(u1)
            assertEquals("Mac", u1!!.displayName)

            repo.signOut()
            assertNull(awaitItem())

            val s2 = repo.signIn("test@example.com", "pw")
            assertTrue(s2 is AuthResult.Success)
            val u2 = awaitItem()
            assertNotNull(u2)
            assertEquals("test@example.com", u2!!.email)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
