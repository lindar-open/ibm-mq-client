package lindar.ibm.mq.client.api

import com.google.gson.reflect.TypeToken
import com.lindar.wellrested.vo.Result
import com.lindar.wellrested.vo.ResultBuilder
import lindar.acolyte.util.UrlAcolyte
import lindar.ibm.mq.client.util.TOPICS_PATH
import lindar.ibm.mq.client.vo.MqAccessCredentials
import lindar.ibm.mq.client.vo.Topic
import lindar.ibm.mq.client.vo.TopicConfigs


class TopicResource(accessCredentials: MqAccessCredentials) : AbstractResource(accessCredentials) {

    companion object {
        //used as topic retention period
        private const val _24H_IN_MILLISECONDS = 3600000L * 24
        private const val DEFAULT_NR_PARTITIONS = 1

        private const val RESOURCE_ALREADY_EXISTS_CODE = "42201"
    }

    fun listAll(): Result<List<Topic>> {
        return super.sendAndGetListRequest(TOPICS_PATH, object: TypeToken<List<Topic>>(){})
    }

    fun create(topicName: String): Result<Void> {
        val topic = Topic(topicName, DEFAULT_NR_PARTITIONS, TopicConfigs(_24H_IN_MILLISECONDS))
        val result = super.postRequest(TOPICS_PATH, topic)
        return if (result.code == RESOURCE_ALREADY_EXISTS_CODE) {
            ResultBuilder.of(result).success(true).build()
        } else result
    }

    fun delete(topicName: String): Result<Void> {
        return super.deleteRequest(UrlAcolyte.safeConcat(TOPICS_PATH, topicName))
    }
}