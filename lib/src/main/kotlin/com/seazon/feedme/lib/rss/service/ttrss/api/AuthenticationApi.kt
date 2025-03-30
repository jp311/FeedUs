package com.seazon.feedme.lib.rss.service.ttrss.api

import com.seazon.feedme.lib.network.HttpException
import com.seazon.feedme.lib.rss.bo.RssToken
import com.seazon.feedme.lib.rss.service.Static
import com.seazon.feedme.lib.rss.service.ttrss.TtrssConstants
import com.seazon.feedme.lib.rss.service.ttrss.bo.AuthResponse
import com.seazon.feedme.lib.utils.base64
import io.ktor.client.call.body

class AuthenticationApi(token: RssToken) : BaseApi(token) {
    suspend fun getAccessToken(username: String, password: String, httpUsername: String?, httpPassword: String?): String {
        val o = mapOf(
            "user" to username,
            "password" to password,
        )
        val token = String.format(
            TtrssConstants.HTTP_HEADER_AUTHORIZATION_VALUE,
            "${httpUsername ?: username}:${httpPassword ?: password}".base64()
        )
        val headers = mapOf(
            TtrssConstants.HTTP_HEADER_AUTHORIZATION_KEY to token,
        )
        val response = execute(TtrssConstants.METHOD_LOGIN, o, headers)
        if (response.status.value == 200) {
            return response.body()
        } else {
            throw HttpException(HttpException.Type.EAUTHFAILED)
        }
    }

    fun setUserWithAccessToken(token: RssToken, response: String) {
        val o = Static.defaultJson.decodeFromString<AuthResponse>(response)
        val content = o.content
        if (!content?.error.isNullOrEmpty()) {
            throw HttpException(HttpException.Type.EREMOTE, content.error)
        }
        token.auth = content?.sessionId
        val t = String.format(
            TtrssConstants.HTTP_HEADER_AUTHORIZATION_VALUE,
            "${token.httpUsername ?: token.username}:${token.httpPassword ?: token.password}".base64()
        )
        token.accessToken = t
        token.expiresTimestamp = System.currentTimeMillis() + getExpiredTimestamp() * 1000
    }
}
