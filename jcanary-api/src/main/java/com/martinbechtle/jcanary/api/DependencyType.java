package com.martinbechtle.jcanary.api;

/**
 * @author Martin Bechtle
 */
public enum DependencyType {

    API,
    DATABASE,
    CACHE,
    STORAGE,
    CONFIGURATION,
    WORKER,
    FTP,
    MESSAGE_QUEUE,
    MESSAGE_CHANNEL,
    STREAM,
    HTTP_RESOURCE,
    RESOURCE
}
