package com.example.data

import kotlinx.coroutines.flow.Flow

class DialLogRepository(private val dialLogDao: DialLogDao) {
    val allLogs: Flow<List<DialLog>> = dialLogDao.getAllLogsFlow()

    suspend fun insert(log: DialLog): Long {
        return dialLogDao.insertLog(log)
    }

    suspend fun update(log: DialLog) {
        dialLogDao.updateLog(log)
    }

    suspend fun deleteById(id: Int) {
        dialLogDao.deleteById(id)
    }

    suspend fun deleteAll() {
        dialLogDao.deleteAllLogs()
    }
}
