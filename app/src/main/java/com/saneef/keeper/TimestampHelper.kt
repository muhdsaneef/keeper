package com.saneef.keeper

class TimestampHelperImpl : TimestampHelper {

    override val currentTimestamp: Long
        get() = System.currentTimeMillis()

}

interface TimestampHelper {
    val currentTimestamp: Long
}
