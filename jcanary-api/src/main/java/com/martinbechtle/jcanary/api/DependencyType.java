package com.martinbechtle.jcanary.api;

/**
 * @author Martin Bechtle
 */
public enum DependencyType {

    /**
     * Any kind of remote api, eg SOAP, REST...
     */
    API,

    /**
     * Any kind of database (relational, key-value, document...)
     */
    DATABASE,

    /**
     * Any kind of caching system, remote or in memory
     */
    CACHE,

    /**
     * Any kind of storage: local or remote file system, S3, etc...
     */
    STORAGE,

    /**
     * Required configuration for running this service that cannot be validated at bootstrap, so needs to be checked at runtime
     */
    CONFIGURATION,

    /**
     * Typically scheduled batch jobs that run independently from this service, but on which the service relies for functioning
     */
    WORKER,

    /**
     * Both FTP or SFTP
     */
    FTP,

    /**
     * Messages queues like RabbitMQ or ActiveMQ
     */
    MESSAGE_QUEUE,

    /**
     * Any other message channel leading to some integration engine (eg: Camel, Mule)
     */
    MESSAGE_CHANNEL,

    /**
     * Any kind of streaming component (eg: Kafka, Spark)
     */
    STREAM,

    /**
     * External HTTP resources required by this service (eg: an HTML page or something served by a CDN)
     */
    HTTP_RESOURCE,

    /**
     * Other resource that does not fit any of the above
     */
    RESOURCE
}
