package com.elastic.mq.test.localsqsqueue.config;

import lombok.Data;

@Data
public class QueueBufferMappedProperties {
    /*
        The maximum amount of time, in milliseconds, that an outgoing call waits for other calls of the same type to batch with.
        The higher the setting, the fewer batches are required to perform the same amount of work.
        Of course, the higher the setting, the more the first call in a batch has to spend waiting.
        If this parameter is set to zero, submitted requests do not wait for other requests, effectively disabling batching.
        The default value of this setting is 200 milliseconds.
     */
    private Long maxBatchOpenMs;
    /*
        The maximum number of messages that will be batched together in a single batch request.
        The higher the setting, the fewer batches will be required to carry out the same number of requests.
        The default value of this setting is 10 requests per batch, which is also the maximum batch size currently allowed by Amazon SQS.
     */
    private Integer maxBatchSize;
    /*
        The maximum number of active outbound batches that can be processed at the same time.
        The higher the setting, the faster outbound batches can be sent (subject to other limits, such as CPU or bandwidth).
        The higher the setting, the more threads are consumed by the AmazonSQSBufferedAsyncClient. The default value is 5 batches.
     */
    private Integer maxInflightOutboundBatches;
}