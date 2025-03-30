package com.seazon.feedme.lib.rss.service.feedbin.api

import com.seazon.feedme.lib.network.HttpException
import com.seazon.feedme.lib.network.HttpManager
import com.seazon.feedme.lib.network.HttpMethod
import com.seazon.feedme.lib.network.NameValuePair
import com.seazon.feedme.lib.rss.bo.RssToken
import com.seazon.feedme.lib.rss.service.RssApi
import com.seazon.feedme.lib.rss.service.feedbin.FeedbinConstants
import io.ktor.client.statement.HttpResponse

open class AuthedApi(token: RssToken) : BaseApi(token) {

    protected fun setHeaderToken(headers: MutableMap<String, String>) {
        headers.put(FeedbinConstants.HTTP_HEADER_AUTHORIZATION_KEY, token.auth.orEmpty())
    }

    protected suspend fun execute(
        httpMethod: HttpMethod,
        url: String,
        params: MutableList<NameValuePair>? = null,
        headers: MutableMap<String, String>? = null,
        body: String? = null,
        json: Boolean = true
    ): HttpResponse {
        var headers = headers
        if (headers == null) {
            headers = HashMap<String, String>()
        }
        setHeaderToken(headers)
        val response = HttpManager.request(httpMethod, getSchema() + url, params, headers, body, json)
        if (response.status.value == RssApi.HTTP_CODE_UNAUTHORIZED) {
            throw HttpException(HttpException.Type.EEXPIRED)
        }
        return response
    }
}