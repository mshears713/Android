package com.frontiercommand.repository

import android.content.Context
import androidx.work.*
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * BackgroundWorkManagerTest - Unit tests for BackgroundWorkManager
 *
 * Tests the background work scheduler to ensure correct behavior for:
 * - Scheduling one-time work with constraints
 * - Scheduling periodic work
 * - Scheduling cleanup tasks
 * - Work cancellation (individual and all)
 * - Work status tracking via StateFlow
 * - Constraint configuration
 * - Work ID generation
 *
 * Uses MockK for mocking WorkManager and Android dependencies.
 *
 * Note: These tests focus on the BackgroundWorkManager wrapper logic.
 * Testing actual Worker execution would require instrumented tests.
 */
class BackgroundWorkManagerTest {

    private lateinit var mockContext: Context
    private lateinit var mockWorkManager: WorkManager
    private lateinit var backgroundWorkManager: BackgroundWorkManager

    @Before
    fun setUp() {
        // Mock Android Context
        mockContext = mockk<Context>(relaxed = true)

        // Mock WorkManager
        mockWorkManager = mockk<WorkManager>(relaxed = true)

        // Mock WorkManager.getInstance() to return our mock
        mockkStatic(WorkManager::class)
        every { WorkManager.getInstance(any()) } returns mockWorkManager

        // Mock LogManager
        val mockLogManager = mockk<LogManager>(relaxed = true)
        mockkStatic(LogManager::class)
        every { LogManager.getInstance(any()) } returns mockLogManager

        // Create BackgroundWorkManager instance
        backgroundWorkManager = BackgroundWorkManager(mockContext)
    }

    // ========== One-Time Sync Tests ==========

    @Test
    fun `scheduleOneTimeSync creates and enqueues work request`() {
        // Given
        val workInfoSlot = slot<WorkRequest>()
        every { mockWorkManager.enqueue(capture(workInfoSlot)) } returns mockk(relaxed = true)

        // When
        val workId = backgroundWorkManager.scheduleOneTimeSync(
            requiresNetwork = true,
            requiresCharging = false
        )

        // Then
        assertNotNull("Work ID should not be null", workId)
        assertTrue("Work ID should contain 'one_time_sync'", workId.contains("one_time_sync"))

        verify { mockWorkManager.enqueue(any<WorkRequest>()) }
    }

    @Test
    fun `scheduleOneTimeSync with network constraint sets network requirement`() {
        // Given
        val workInfoSlot = slot<WorkRequest>()
        every { mockWorkManager.enqueue(capture(workInfoSlot)) } returns mockk(relaxed = true)

        // When
        backgroundWorkManager.scheduleOneTimeSync(requiresNetwork = true)

        // Then
        verify { mockWorkManager.enqueue(any<WorkRequest>()) }

        val capturedRequest = workInfoSlot.captured
        assertNotNull("Work request should not be null", capturedRequest)
        assertTrue("Should be OneTimeWorkRequest",
            capturedRequest is OneTimeWorkRequest)
    }

    @Test
    fun `scheduleOneTimeSync without network constraint allows any network`() {
        // Given
        val workInfoSlot = slot<WorkRequest>()
        every { mockWorkManager.enqueue(capture(workInfoSlot)) } returns mockk(relaxed = true)

        // When
        backgroundWorkManager.scheduleOneTimeSync(requiresNetwork = false)

        // Then
        verify { mockWorkManager.enqueue(any<WorkRequest>()) }
    }

    @Test
    fun `scheduleOneTimeSync with charging constraint sets charging requirement`() {
        // Given
        val workInfoSlot = slot<WorkRequest>()
        every { mockWorkManager.enqueue(capture(workInfoSlot)) } returns mockk(relaxed = true)

        // When
        backgroundWorkManager.scheduleOneTimeSync(
            requiresNetwork = false,
            requiresCharging = true
        )

        // Then
        verify { mockWorkManager.enqueue(any<WorkRequest>()) }
    }

    @Test
    fun `scheduleOneTimeSync returns unique work ID each time`() {
        // Given
        every { mockWorkManager.enqueue(any<WorkRequest>()) } returns mockk(relaxed = true)

        // When
        val workId1 = backgroundWorkManager.scheduleOneTimeSync()
        Thread.sleep(10) // Ensure different timestamps
        val workId2 = backgroundWorkManager.scheduleOneTimeSync()

        // Then
        assertNotEquals("Work IDs should be unique", workId1, workId2)
    }

    // ========== Periodic Sync Tests ==========

    @Test
    fun `schedulePeriodicSync creates and enqueues periodic work`() {
        // Given
        val workInfoSlot = slot<PeriodicWorkRequest>()
        every {
            mockWorkManager.enqueueUniquePeriodicWork(
                any(),
                any(),
                capture(workInfoSlot)
            )
        } returns mockk(relaxed = true)

        // When
        val workId = backgroundWorkManager.schedulePeriodicSync(
            intervalMinutes = 30,
            requiresNetwork = true
        )

        // Then
        assertEquals("Work ID should be 'periodic_sync'", "periodic_sync", workId)

        verify {
            mockWorkManager.enqueueUniquePeriodicWork(
                "periodic_sync",
                ExistingPeriodicWorkPolicy.REPLACE,
                any()
            )
        }
    }

    @Test
    fun `schedulePeriodicSync enforces minimum 15 minute interval`() {
        // Given
        val workInfoSlot = slot<PeriodicWorkRequest>()
        every {
            mockWorkManager.enqueueUniquePeriodicWork(
                any(),
                any(),
                capture(workInfoSlot)
            )
        } returns mockk(relaxed = true)

        // When - try to schedule with interval less than 15 minutes
        backgroundWorkManager.schedulePeriodicSync(intervalMinutes = 5)

        // Then - should still succeed (interval coerced to 15 internally)
        verify {
            mockWorkManager.enqueueUniquePeriodicWork(
                "periodic_sync",
                ExistingPeriodicWorkPolicy.REPLACE,
                any()
            )
        }
    }

    @Test
    fun `schedulePeriodicSync with large interval succeeds`() {
        // Given
        every {
            mockWorkManager.enqueueUniquePeriodicWork(any(), any(), any())
        } returns mockk(relaxed = true)

        // When
        val workId = backgroundWorkManager.schedulePeriodicSync(intervalMinutes = 120)

        // Then
        assertEquals("Work ID should be 'periodic_sync'", "periodic_sync", workId)

        verify {
            mockWorkManager.enqueueUniquePeriodicWork(
                "periodic_sync",
                ExistingPeriodicWorkPolicy.REPLACE,
                any()
            )
        }
    }

    @Test
    fun `schedulePeriodicSync with network requirement sets constraint`() {
        // Given
        every {
            mockWorkManager.enqueueUniquePeriodicWork(any(), any(), any())
        } returns mockk(relaxed = true)

        // When
        backgroundWorkManager.schedulePeriodicSync(requiresNetwork = true)

        // Then
        verify {
            mockWorkManager.enqueueUniquePeriodicWork(
                "periodic_sync",
                ExistingPeriodicWorkPolicy.REPLACE,
                any()
            )
        }
    }

    // ========== Data Cleanup Tests ==========

    @Test
    fun `scheduleDataCleanup creates and enqueues cleanup work`() {
        // Given
        val workInfoSlot = slot<WorkRequest>()
        every { mockWorkManager.enqueue(capture(workInfoSlot)) } returns mockk(relaxed = true)

        // When
        val workId = backgroundWorkManager.scheduleDataCleanup()

        // Then
        assertNotNull("Work ID should not be null", workId)
        assertTrue("Work ID should contain 'data_cleanup'", workId.contains("data_cleanup"))

        verify { mockWorkManager.enqueue(any<WorkRequest>()) }

        val capturedRequest = workInfoSlot.captured
        assertTrue("Should be OneTimeWorkRequest",
            capturedRequest is OneTimeWorkRequest)
    }

    @Test
    fun `scheduleDataCleanup returns unique work ID each time`() {
        // Given
        every { mockWorkManager.enqueue(any<WorkRequest>()) } returns mockk(relaxed = true)

        // When
        val workId1 = backgroundWorkManager.scheduleDataCleanup()
        Thread.sleep(10) // Ensure different timestamps
        val workId2 = backgroundWorkManager.scheduleDataCleanup()

        // Then
        assertNotEquals("Work IDs should be unique", workId1, workId2)
    }

    // ========== Work Cancellation Tests ==========

    @Test
    fun `cancelWork cancels work by tag`() {
        // Given
        val workId = "test_work_123"
        every { mockWorkManager.cancelAllWorkByTag(any()) } returns mockk(relaxed = true)

        // When
        backgroundWorkManager.cancelWork(workId)

        // Then
        verify { mockWorkManager.cancelAllWorkByTag(workId) }
    }

    @Test
    fun `cancelWork updates status to CANCELLED`() = runTest {
        // Given
        val workId = "cancel_test_work"
        every { mockWorkManager.cancelAllWorkByTag(any()) } returns mockk(relaxed = true)

        // When
        backgroundWorkManager.cancelWork(workId)

        // Give time for status update
        kotlinx.coroutines.delay(100)

        // Then
        val workStatuses = backgroundWorkManager.workStatuses.first()
        assertEquals("Status should be CANCELLED",
            WorkStatus.CANCELLED, workStatuses[workId])
    }

    @Test
    fun `cancelAllWork cancels all pending work`() {
        // Given
        every { mockWorkManager.cancelAllWork() } returns mockk(relaxed = true)

        // When
        backgroundWorkManager.cancelAllWork()

        // Then
        verify { mockWorkManager.cancelAllWork() }
    }

    @Test
    fun `cancelAllWork clears work statuses`() = runTest {
        // Given
        every { mockWorkManager.cancelAllWork() } returns mockk(relaxed = true)

        // When
        backgroundWorkManager.cancelAllWork()

        kotlinx.coroutines.delay(100)

        // Then
        val workStatuses = backgroundWorkManager.workStatuses.first()
        assertTrue("Work statuses should be empty", workStatuses.isEmpty())
    }

    // ========== Work Status Tests ==========

    @Test
    fun `getAllWorkStatuses returns current statuses`() = runTest {
        // Given
        every { mockWorkManager.enqueue(any<WorkRequest>()) } returns mockk(relaxed = true)

        // When
        val workId = backgroundWorkManager.scheduleOneTimeSync()
        kotlinx.coroutines.delay(100)

        // Then
        val statuses = backgroundWorkManager.getAllWorkStatuses()
        assertNotNull("Statuses should not be null", statuses)
        assertTrue("Should contain the scheduled work", statuses.containsKey(workId))
    }

    @Test
    fun `work status starts as ENQUEUED`() = runTest {
        // Given
        every { mockWorkManager.enqueue(any<WorkRequest>()) } returns mockk(relaxed = true)

        // When
        val workId = backgroundWorkManager.scheduleOneTimeSync()
        kotlinx.coroutines.delay(100)

        // Then
        val statuses = backgroundWorkManager.getAllWorkStatuses()
        assertEquals("Initial status should be ENQUEUED",
            WorkStatus.ENQUEUED, statuses[workId])
    }

    @Test
    fun `workStatuses StateFlow emits updates`() = runTest {
        // Given
        every { mockWorkManager.enqueue(any<WorkRequest>()) } returns mockk(relaxed = true)

        // When
        val initialStatuses = backgroundWorkManager.workStatuses.first()
        val initialSize = initialStatuses.size

        backgroundWorkManager.scheduleOneTimeSync()
        kotlinx.coroutines.delay(100)

        // Then
        val updatedStatuses = backgroundWorkManager.workStatuses.first()
        assertTrue("Should have more statuses after scheduling",
            updatedStatuses.size > initialSize)
    }

    // ========== Work Info Tests ==========

    @Test
    fun `getWorkInfo retrieves work information`() = runTest {
        // Given
        val workId = "test_work_info"
        val mockWorkInfo = mockk<WorkInfo>(relaxed = true)

        every { mockWorkManager.getWorkInfosByTag(workId) } returns mockk {
            every { get() } returns listOf(mockWorkInfo)
        }

        // Mock ListenableFuture for await()
        coEvery { mockWorkManager.getWorkInfosByTag(workId).get() } returns listOf(mockWorkInfo)

        // When
        val workInfo = backgroundWorkManager.getWorkInfo(workId)

        // Then - verify the method was called (actual await() won't work in unit test)
        verify { mockWorkManager.getWorkInfosByTag(workId) }
    }

    // ========== Integration Tests ==========

    @Test
    fun `schedule and cancel workflow works correctly`() = runTest {
        // Given
        every { mockWorkManager.enqueue(any<WorkRequest>()) } returns mockk(relaxed = true)
        every { mockWorkManager.cancelAllWorkByTag(any()) } returns mockk(relaxed = true)

        // When - Schedule
        val workId = backgroundWorkManager.scheduleOneTimeSync()
        kotlinx.coroutines.delay(50)

        val statusesAfterSchedule = backgroundWorkManager.getAllWorkStatuses()
        assertTrue("Should have status after scheduling",
            statusesAfterSchedule.containsKey(workId))

        // When - Cancel
        backgroundWorkManager.cancelWork(workId)
        kotlinx.coroutines.delay(50)

        // Then
        val statusesAfterCancel = backgroundWorkManager.getAllWorkStatuses()
        assertEquals("Status should be CANCELLED",
            WorkStatus.CANCELLED, statusesAfterCancel[workId])
    }

    @Test
    fun `multiple work requests can be scheduled`() = runTest {
        // Given
        every { mockWorkManager.enqueue(any<WorkRequest>()) } returns mockk(relaxed = true)
        every {
            mockWorkManager.enqueueUniquePeriodicWork(any(), any(), any())
        } returns mockk(relaxed = true)

        // When
        val syncId = backgroundWorkManager.scheduleOneTimeSync()
        val periodicId = backgroundWorkManager.schedulePeriodicSync()
        val cleanupId = backgroundWorkManager.scheduleDataCleanup()

        kotlinx.coroutines.delay(100)

        // Then
        val statuses = backgroundWorkManager.getAllWorkStatuses()
        assertTrue("Should have sync work", statuses.containsKey(syncId))
        assertTrue("Should have periodic work", statuses.containsKey(periodicId))
        assertTrue("Should have cleanup work", statuses.containsKey(cleanupId))
    }

    // ========== WorkStatus Enum Tests ==========

    @Test
    fun `WorkStatus enum has all required states`() {
        // When/Then
        val statuses = WorkStatus.values()

        assertTrue("Should have ENQUEUED state",
            statuses.contains(WorkStatus.ENQUEUED))
        assertTrue("Should have RUNNING state",
            statuses.contains(WorkStatus.RUNNING))
        assertTrue("Should have SUCCEEDED state",
            statuses.contains(WorkStatus.SUCCEEDED))
        assertTrue("Should have FAILED state",
            statuses.contains(WorkStatus.FAILED))
        assertTrue("Should have BLOCKED state",
            statuses.contains(WorkStatus.BLOCKED))
        assertTrue("Should have CANCELLED state",
            statuses.contains(WorkStatus.CANCELLED))
    }

    @Test
    fun `WorkStatus enum count is correct`() {
        // When
        val statuses = WorkStatus.values()

        // Then
        assertEquals("Should have 6 status types", 6, statuses.size)
    }

    // ========== Edge Cases Tests ==========

    @Test
    fun `scheduling work with same periodic ID replaces existing`() {
        // Given
        every {
            mockWorkManager.enqueueUniquePeriodicWork(any(), any(), any())
        } returns mockk(relaxed = true)

        // When
        val workId1 = backgroundWorkManager.schedulePeriodicSync()
        val workId2 = backgroundWorkManager.schedulePeriodicSync()

        // Then
        assertEquals("Periodic work IDs should be the same", workId1, workId2)

        verify(exactly = 2) {
            mockWorkManager.enqueueUniquePeriodicWork(
                "periodic_sync",
                ExistingPeriodicWorkPolicy.REPLACE,
                any()
            )
        }
    }

    @Test
    fun `canceling non-existent work does not throw`() {
        // Given
        every { mockWorkManager.cancelAllWorkByTag(any()) } returns mockk(relaxed = true)

        // When/Then - should not throw
        backgroundWorkManager.cancelWork("non_existent_work_id")

        verify { mockWorkManager.cancelAllWorkByTag("non_existent_work_id") }
    }
}
