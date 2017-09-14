package lindar.ibm.mq.client.vo


data class MqAccessCredentials(val apiUrl: String, val apiKey: String)


data class ErrorResponse (val errorCode: String = "", val errorMessage: String = "")

data class Topic (val name: String, val partitions: Int, val configs: TopicConfigs)

data class TopicConfigs(val retentionMs: Long)
