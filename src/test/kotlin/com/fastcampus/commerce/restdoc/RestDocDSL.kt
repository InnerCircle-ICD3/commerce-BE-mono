package com.fastcampus.commerce.restdoc

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.fastcampus.commerce.common.error.CommonErrorCode
import com.fastcampus.commerce.common.error.CoreException
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.response.MockMvcResponse
import org.hamcrest.Matchers.equalTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.AbstractDescriptor
import org.springframework.restdocs.snippet.Attributes.key

fun documentation(
    identifier: String,
    tag: String,
    summary: String,
    description: String = "",
    privateResource: Boolean = false,
    deprecated: Boolean = false,
    block: RestDocDSL.() -> Unit,
) {
    val builder = RestDocDSL(
        identifier = identifier,
        tag = tag,
        summary = summary,
        description = description,
        privateResource = privateResource,
        deprecated = deprecated,
    )
    builder.block()
    builder.executeAndDocument()
}

class RestDocDSL(
    private val identifier: String,
    private val tag: String,
    private val summary: String,
    private val description: String,
    private val privateResource: Boolean,
    private val deprecated: Boolean,
) {
    private lateinit var method: HttpMethod
    private lateinit var uri: String

    private val pathVariables = mutableListOf<ParameterDescriptor>()
    private var requestHeaders = mutableListOf<HeaderDescriptor>()
    private var queryParams = mutableListOf<ParameterDescriptor>()
    private var requestBodyFields = mutableListOf<FieldDescriptor>()
    private var responseHeaders = mutableListOf<HeaderDescriptor>()
    private var responseBodyFields = mutableListOf<FieldDescriptor>()

    fun requestLine(method: HttpMethod, uri: String, block: PathVarBlock.() -> Unit = {}) {
        this.method = method
        this.uri = uri
        pathVariables.addAll(PathVarBlock().apply(block).descriptors)
    }

    fun requestHeaders(block: RequestHeaderBlock.() -> Unit) {
        requestHeaders.addAll(RequestHeaderBlock().apply(block).descriptors)
    }

    fun queryParameters(block: QueryParamBlock.() -> Unit) {
        queryParams.addAll(QueryParamBlock().apply(block).descriptors)
    }

    fun requestBody(block: RequestBodyBlock.() -> Unit) {
        requestBodyFields.addAll(RequestBodyBlock().apply(block).descriptors)
    }

    fun responseHeaders(block: ResponseHeaderBlock.() -> Unit) {
        responseHeaders.addAll(ResponseHeaderBlock().apply(block).descriptors)
    }

    fun responseBody(block: ResponseFieldBlock.() -> Unit) {
        responseBodyFields.addAll(ResponseFieldBlock().apply(block).descriptors)
    }

    internal fun executeAndDocument() {
        val response = performRequest()
        val handler = createDocumentationResultHandler()
        response
            .then()
            .apply(handler)
            .status(HttpStatus.OK)
        validateResponseBody(response, responseBodyFields)
    }

    private fun performRequest(): MockMvcResponse {
        val pathVarMap = extractExamplesToMap(pathVariables)
        val headerMap = extractExamplesToMap(requestHeaders).toMutableMap()
        headerMap.putIfAbsent(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

        val queryParamMap = extractExamplesToMap(queryParams)
        val requestBodyMap = extractExamplesToMap(requestBodyFields)

        val requestSpecification = RestAssuredMockMvc.given().headers(headerMap)

        val response = when (this.method) {
            HttpMethod.GET -> requestSpecification.params(queryParamMap).get(uri, pathVarMap)
            HttpMethod.POST -> requestSpecification.body(requestBodyMap).post(uri, pathVarMap)
            HttpMethod.PUT -> requestSpecification.body(requestBodyMap).put(uri, pathVarMap)
            HttpMethod.PATCH -> requestSpecification.body(requestBodyMap).patch(uri, pathVarMap)
            HttpMethod.DELETE -> requestSpecification.params(queryParamMap).delete(uri, pathVarMap)
            else -> throw CoreException(CommonErrorCode.SERVER_ERROR)
        }
        response.prettyPrint()
        return response
    }

    private fun <T : AbstractDescriptor<T>> extractExamplesToMap(descriptors: List<AbstractDescriptor<T>>): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        descriptors.forEach { descriptor ->
            descriptor.attributes.forEach { (key, value) ->
                if (value != null) {
                    map[key] = value
                }
            }
        }
        return map
    }

    private fun createDocumentationResultHandler(): RestDocumentationResultHandler {
        val commonSnippets = arrayOf(
            HeaderDocumentation.requestHeaders(*requestHeaders.toTypedArray()),
            RequestDocumentation.pathParameters(*pathVariables.toTypedArray()),
            HeaderDocumentation.responseHeaders(*responseHeaders.toTypedArray()),
            PayloadDocumentation.responseFields(*responseBodyFields.toTypedArray()),
        )
        val requestSnippet = when (this.method) {
            HttpMethod.GET, HttpMethod.DELETE -> RequestDocumentation.queryParameters(*queryParams.toTypedArray())
            else -> PayloadDocumentation.requestFields(*requestBodyFields.toTypedArray())
        }

        return MockMvcRestDocumentationWrapper.document(
            identifier = identifier,
            resourceDetails = ResourceSnippetParametersBuilder()
                .description(description)
                .summary(summary)
                .privateResource(privateResource)
                .deprecated(deprecated)
                .tag(tag),
            requestPreprocessor = Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
            responsePreprocessor = Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
            snippets = arrayOf(
                requestSnippet,
                *commonSnippets,
            ),
        )
    }
}

internal fun validateResponseBody(response: MockMvcResponse, fields: List<FieldDescriptor>) {
    fields.mapNotNull { field ->
        val path = field.path
        val expected = field.attributes[path]

        if (expected == null) {
            if (field.isOptional || field.isIgnored) {
                return@mapNotNull null
            }
            error("[$path] 필드의 예제가 누락되었습니다.")
        }
        path to expected
    }.forEach { (path, expected) ->
        response.then().body(path, equalTo(expected))
    }
}

internal fun <T : AbstractDescriptor<T>> T.withExample(name: String, example: Any?): T {
    return if (example != null) this.attributes(key(name).value(example)) else this
}

abstract class DescriptorCollector<T> {
    val descriptors = mutableListOf<T>()
}

class PathVarBlock : DescriptorCollector<ParameterDescriptor>() {
    fun pathVariable(name: String, description: String, example: Any? = null) {
        descriptors += RequestDocumentation.parameterWithName(name).description(description)
            .withExample(name, example)
    }
}

class RequestHeaderBlock : DescriptorCollector<HeaderDescriptor>() {
    fun header(name: String, description: String, example: String? = null) {
        descriptors += HeaderDocumentation.headerWithName(name).description(description)
            .withExample(name, example)
    }
}

class QueryParamBlock : DescriptorCollector<ParameterDescriptor>() {
    fun field(name: String, description: String, example: Any? = null) {
        descriptors += RequestDocumentation.parameterWithName(name).description(description)
            .withExample(name, example)
    }

    fun optionalField(name: String, description: String, example: Any? = null) {
        descriptors += RequestDocumentation.parameterWithName(name).description(description).optional()
            .withExample(name, example)
    }
}

class RequestBodyBlock : DescriptorCollector<FieldDescriptor>() {
    fun field(name: String, description: String, example: Any? = null) {
        descriptors += PayloadDocumentation.fieldWithPath(name).description(description)
            .withExample(name, example)
    }

    fun optionalField(name: String, description: String, example: Any? = null) {
        descriptors += PayloadDocumentation.fieldWithPath(name).description(description).optional()
            .withExample(name, example)
    }

    fun ignoredField(name: String) {
        descriptors += PayloadDocumentation.fieldWithPath(name).ignored()
    }
}

class ResponseHeaderBlock : DescriptorCollector<HeaderDescriptor>() {
    fun header(name: String, description: String, example: String? = null) {
        descriptors += HeaderDocumentation.headerWithName(name).description(description)
            .withExample(name, example)
    }
}

class ResponseFieldBlock : DescriptorCollector<FieldDescriptor>() {
    fun field(name: String, description: String, example: Any? = null) {
        descriptors += PayloadDocumentation.fieldWithPath(name).description(description)
            .withExample(name, example)
    }

    fun optionalField(name: String, description: String, example: Any? = null) {
        descriptors += PayloadDocumentation.fieldWithPath(name).description(description).optional()
            .withExample(name, example)
    }

    fun ignoredField(path: String) {
        descriptors += PayloadDocumentation.subsectionWithPath(path).ignored()
    }
}
