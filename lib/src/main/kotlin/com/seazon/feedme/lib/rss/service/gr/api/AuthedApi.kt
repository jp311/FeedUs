package com.seazon.feedme.lib.rss.service.gr.api

import com.seazon.feedme.lib.rss.service.Static
import com.seazon.feedme.lib.network.HttpException
import com.seazon.feedme.lib.network.HttpManager
import com.seazon.feedme.lib.network.HttpMethod
import com.seazon.feedme.lib.network.NameValuePair
import com.seazon.feedme.lib.rss.bo.RssToken
import com.seazon.feedme.lib.rss.service.RssApi
import com.seazon.feedme.lib.rss.service.gr.GrConfig
import com.seazon.feedme.lib.rss.service.gr.GrConstants
import io.ktor.client.statement.HttpResponse

open class AuthedApi(token: RssToken, config: GrConfig, val api: RssApi) : BaseApi(token, config) {

    protected fun setHeaderToken(headers: MutableMap<String, String>) {
        if (api.getAuthType() == RssApi.AUTH_TYPE_OAUTH2) {
            headers[GrConstants.HTTP_HEADER_AUTHORIZATION_KEY] =
                String.format(GrConstants.HTTP_HEADER_AUTHORIZATION_VALUE_OAUTH2, token.accessToken)
        } else if (api.getAuthType() == RssApi.AUTH_TYPE_BASE) {
            headers[GrConstants.HTTP_HEADER_AUTHORIZATION_KEY] =
                String.format(GrConstants.HTTP_HEADER_AUTHORIZATION_VALUE, token.accessToken)
        } else {
        }
    }

    protected suspend fun execute(
        httpMethod: HttpMethod,
        url: String,
        params: List<NameValuePair>? = null,
        headers: MutableMap<String, String>? = null,
        body: String? = null,
        json: Boolean = true,
    ): HttpResponse {
        var headers = headers
        if (headers == null) {
            headers = HashMap<String, String>()
        }
        setHeaderToken(headers)
        if (isTheseRssType(Static.ACCOUNT_TYPE_INOREADER_OAUTH2, Static.ACCOUNT_TYPE_INOREADER)) {
            setHeaderAppAuthentication(headers)
        }
        val response = HttpManager.request(httpMethod, getSchema() + url, params, headers, body, json)
        if (response.status.value == RssApi.HTTP_CODE_UNAUTHORIZED) {
            throw HttpException(HttpException.Type.EEXPIRED)
        }
        return response
    }
}
