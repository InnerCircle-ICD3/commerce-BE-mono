package com.fastcampus.commerce.common.id

class SnowflakeIdGenerator(
    private val machineId: Long,
    private val customEpoch: Long = 1672531200000L, // 2023-01-01 기준
) : IdGenerator {
    companion object {
        private const val EPOCH_BITS = 41
        private const val MACHINE_ID_BITS = 10
        private const val SEQUENCE_BITS = 12

        private const val MAX_MACHINE_ID = (1L shl MACHINE_ID_BITS) - 1
        private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS) - 1

        private const val MACHINE_ID_SHIFT = SEQUENCE_BITS
        private const val TIMESTAMP_SHIFT = MACHINE_ID_BITS + SEQUENCE_BITS
    }

    private var lastTimestamp = -1L
    private var sequence = 0L
    private val lock = Any()

    init {
        require(machineId in 0..MAX_MACHINE_ID) {
            "Machine ID must be between 0 and $MAX_MACHINE_ID"
        }
    }

    override fun generate(): Long {
        synchronized(lock) {
            var currentTimestamp = System.currentTimeMillis()
            if (currentTimestamp < lastTimestamp) {
                throw IllegalStateException("Clock moved backwards. Refusing to generate ID.")
            }

            if (currentTimestamp == lastTimestamp) {
                sequence = (sequence + 1) and MAX_SEQUENCE
                if (sequence == 0L) {
                    currentTimestamp = waitNextMillis(currentTimestamp)
                }
            } else {
                sequence = 0L
            }

            lastTimestamp = currentTimestamp

            return ((currentTimestamp - customEpoch) shl TIMESTAMP_SHIFT) or
                (machineId shl MACHINE_ID_SHIFT) or
                sequence
        }
    }

    private fun waitNextMillis(currentTimestamp: Long): Long {
        var ts = currentTimestamp
        while (ts <= lastTimestamp) {
            ts = System.currentTimeMillis()
        }
        return ts
    }
}
