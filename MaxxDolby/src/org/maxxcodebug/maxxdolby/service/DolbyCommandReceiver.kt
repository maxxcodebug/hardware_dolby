/*
 *           (C) 2026 Anshuman_X (maxxcodebug)
 * Copyright (C) 2026 Lunaris AOSP
 * SPDX-License-Identifier: Apache-2.0
 */

package org.maxxcodebug.maxxdolby.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.maxxcodebug.maxxdolby.DolbyConstants
import org.maxxcodebug.maxxdolby.data.DolbyRepository
import org.maxxcodebug.maxxdolby.data.SceneRepository

/**
 * Command interface for automation apps (Tasker, MacroDroid) and scripts.
 * Send an explicit broadcast to `org.maxxcodebug.maxxdolby.service.DolbyCommandReceiver`
 * (or any matching implicit broadcast) with one of the actions below:
 *
 * - [ACTION_TOGGLE]: flips the Dolby master switch. No extras.
 * - [ACTION_SET_ENABLED]: takes boolean extra [EXTRA_ENABLED].
 * - [ACTION_SET_PROFILE]: takes int extra [EXTRA_PROFILE] (0-6, see
 *   R.array.dolby_profile_values: 0 Dynamic, 1 Movie, 2 Music, 3 Game,
 *   4 Work, 5 Casual, 6 Mood).
 * - [ACTION_APPLY_SCENE]: takes string extra [EXTRA_SCENE_ID] with a scene id
 *   ("builtin_movie_night", "builtin_bass_boost", "builtin_podcast",
 *   "builtin_gaming", or a "custom_<...>" id).
 *
 * Example (adb):
 *   adb shell am broadcast -n org.maxxcodebug.maxxdolby/.service.DolbyCommandReceiver \
 *     -a org.maxxcodebug.maxxdolby.action.SET_PROFILE --ei profile 1
 */
class DolbyCommandReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val appContext = context.applicationContext
        val repository = try {
            DolbyRepository(appContext)
        } catch (e: Exception) {
            DolbyConstants.dlog(TAG, "No audio effect, ignoring $action")
            return
        }
        try {
            when (action) {
                ACTION_TOGGLE -> repository.setDolbyEnabled(!repository.getDolbyEnabled())
                ACTION_SET_ENABLED -> {
                    if (intent.hasExtra(EXTRA_ENABLED)) {
                        repository.setDolbyEnabled(
                            intent.getBooleanExtra(EXTRA_ENABLED, false)
                        )
                    }
                }
                ACTION_SET_PROFILE -> {
                    val profile = intent.getIntExtra(EXTRA_PROFILE, -1)
                    if (profile in MIN_PROFILE..MAX_PROFILE) {
                        repository.setCurrentProfile(profile)
                    }
                }
                ACTION_APPLY_SCENE -> {
                    val sceneId = intent.getStringExtra(EXTRA_SCENE_ID)
                    val scene = sceneId?.let { SceneRepository(appContext).getScene(it) }
                    if (scene != null) {
                        SceneRepository(appContext).applyScene(scene, repository)
                    }
                }
            }
        } catch (e: Exception) {
            DolbyConstants.dlog(TAG, "Command $action failed: ${e.message}")
        } finally {
            try {
                repository.close()
            } catch (e: Exception) {
                DolbyConstants.dlog(TAG, "Error closing repository: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "DolbyCommandReceiver"
        private const val MIN_PROFILE = 0
        private const val MAX_PROFILE = 6

        const val ACTION_TOGGLE = "org.maxxcodebug.maxxdolby.action.TOGGLE"
        const val ACTION_SET_ENABLED = "org.maxxcodebug.maxxdolby.action.SET_ENABLED"
        const val ACTION_SET_PROFILE = "org.maxxcodebug.maxxdolby.action.SET_PROFILE"
        const val ACTION_APPLY_SCENE = "org.maxxcodebug.maxxdolby.action.APPLY_SCENE"

        const val EXTRA_ENABLED = "enabled"
        const val EXTRA_PROFILE = "profile"
        const val EXTRA_SCENE_ID = "scene_id"
    }
}
