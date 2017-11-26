package nl.pocketquest.server.utils

import org.slf4j.LoggerFactory

fun Any.getLogger() = LoggerFactory.getLogger(this.javaClass)