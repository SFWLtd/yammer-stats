package com.sfwltd.yammerstats.client

import java.time.LocalDateTime

interface YammerExportClient {

    /**
     * Exports data from the Yammer instance
     * @param from (Optional) The date to retrieve the data from
     * @param to (Optional) The date to retrieve the data until
     */
    fun export(from: LocalDateTime = LocalDateTime.MIN, to: LocalDateTime = LocalDateTime.now()): ByteArray
}