package com.rnett.core

import org.postgresql.ds.PGPoolingDataSource
import org.postgresql.jdbc3.Jdbc3PoolingDataSource

object PooledDBConnection {
    fun makePooledConnection(server: String, user: String, password: String, database: String): PGPoolingDataSource {
        val ds = Jdbc3PoolingDataSource()
        ds.serverName = server
        ds.user = user
        ds.password = password
        ds.databaseName = database
        return ds
    }
}