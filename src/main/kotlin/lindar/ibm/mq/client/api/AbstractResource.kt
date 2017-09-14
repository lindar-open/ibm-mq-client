package lindar.ibm.mq.client.api

import com.google.gson.reflect.TypeToken
import com.lindar.wellrested.WellRestedRequest
import com.lindar.wellrested.WellRestedRequestBuilder
import com.lindar.wellrested.vo.Result
import com.lindar.wellrested.vo.ResultFactory
import com.lindar.wellrested.vo.WellRestedResponse
import lindar.acolyte.util.UrlAcolyte
import lindar.ibm.mq.client.vo.ErrorResponse
import lindar.ibm.mq.client.vo.MqAccessCredentials
import mu.KotlinLogging


abstract class AbstractResource {

    companion object {
        private val log = KotlinLogging.logger {}
        private const val X_AUTH_TOKEN_PARAM = "X-Auth-Token"
    }

    private val requestBuilder: WellRestedRequestBuilder
    private val apiUrl: String

    constructor(accessCredentials: MqAccessCredentials) {
        apiUrl = accessCredentials.apiUrl
        requestBuilder = WellRestedRequest.builder()
                .addGlobalHeader(X_AUTH_TOKEN_PARAM, accessCredentials.apiKey)
                .addContentTypeGlobalHeader("application/json")
    }

    private fun buildRequestFromResourcePath(resourcePath: String): WellRestedRequest {
        val url = UrlAcolyte.safeConcat(apiUrl, resourcePath)
        return requestBuilder.url(url).build()
    }

    protected fun <T : Any> sendAndGetRequest(resourcePath: String, clazz: Class<T>): Result<T> {
        val request = buildRequestFromResourcePath(resourcePath)
        val response = request.get().submit()
        return if (validResponse(response)) {
            ResultFactory.successful(response.fromJson().castTo(clazz))
        } else parseErrorResponse(response)
    }

    protected fun <T : Any> sendAndGetListRequest(resourcePath: String, typeToken: TypeToken<List<T>>): Result<List<T>> {
        val request = buildRequestFromResourcePath(resourcePath)
        val response = request.get().submit()
        return if (validResponse(response)) {
            ResultFactory.successful(response.fromJson().castToList(typeToken))
        } else parseErrorResponse(response)
    }

    protected fun <T : Any> postRequest(resourcePath: String, objectToPost: T): Result<Void> {
        val request = buildRequestFromResourcePath(resourcePath)
        val response = request.post().jsonContent(objectToPost).submit()
        return if (validResponse(response)) {
            ResultFactory.successfulMsg("Posted successfully")
        } else parseErrorResponse(response)
    }

    protected fun <T : Any> postAndGetRequest(resourcePath: String, objectToPost: T): Result<T> {
        val request = buildRequestFromResourcePath(resourcePath)
        val response = request.post().jsonContent(objectToPost).submit()
        return if (validResponse(response)) {
            ResultFactory.successful(response.fromJson().castTo(objectToPost::class.java))
        } else parseErrorResponse(response)
    }

    protected fun <T : Any> putAndGetRequest(resourcePath: String, objectToPost: T): Result<T> {
        val request = buildRequestFromResourcePath(resourcePath)
        val response = request.put().jsonContent(objectToPost).submit()
        return if (validResponse(response)) {
            ResultFactory.successful(response.fromJson().castTo(objectToPost.javaClass))
        } else parseErrorResponse(response)
    }

    protected fun deleteRequest(resourcePath: String): Result<Void> {
        val request = buildRequestFromResourcePath(resourcePath)
        val response = request.delete().submit()
        return if (validResponse(response)) {
            ResultFactory.successfulMsg("Deleted successfully")
        } else parseErrorResponse(response)
    }

    private fun validResponse(response: WellRestedResponse): Boolean {
        return response.statusCode < 300
    }

    private fun <T> parseErrorResponse(response: WellRestedResponse): Result<T> {
        val errorResponse = response.fromJson().castTo(ErrorResponse::class.java)
        if (errorResponse.errorMessage.isBlank()) {
            return ResultFactory.failed("Unknown Error", "UNKNOWN_ERROR")
        }
        return ResultFactory.failed(errorResponse.errorMessage, errorResponse.errorCode)
    }

}