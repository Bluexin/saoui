package com.saomc.saoui.util

import com.saomc.saoui.api.info.IPlayerStatsProvider

/**
 * Part of saoui

 * @author Bluexin
 */
class PlayerStats private constructor(val stats: IPlayerStatsProvider) {
    companion object {
        private var instance: PlayerStats? = null

        fun init(provider: IPlayerStatsProvider) {
            if (instance != null) throw IllegalStateException("PlayerStats already initialized!")
            instance = PlayerStats(provider)
        }

        fun instance(): PlayerStats {
            if (instance == null) throw IllegalStateException("PlayerStats not initialized!")
            return instance!!
        }
    }
}
