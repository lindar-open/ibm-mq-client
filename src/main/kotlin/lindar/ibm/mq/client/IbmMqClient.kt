package lindar.ibm.mq.client

import lindar.ibm.mq.client.api.TopicResource
import lindar.ibm.mq.client.vo.MqAccessCredentials


class IbmMqClient(accessCredentials: MqAccessCredentials) {
    private val topicResource: TopicResource = TopicResource(accessCredentials)

    fun topics() = topicResource
}