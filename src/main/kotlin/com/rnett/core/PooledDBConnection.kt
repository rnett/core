package com.rnett.core

import org.apache.commons.dbcp.BasicDataSource

//TODO JNDI from jetty?
object PooledDBConnection {
    fun connect(connectionURL: String) = {
        val ds = BasicDataSource()
        ds.url = connectionURL
        ds
    }
}