package com.rnett.core

import org.apache.commons.dbcp.BasicDataSource

//TODO JNDI from jetty?
object PooledDBConnection {
    fun connect(connectionURL: String): BasicDataSource {
        val ds = BasicDataSource()
        ds.url = connectionURL
        return ds
    }
}