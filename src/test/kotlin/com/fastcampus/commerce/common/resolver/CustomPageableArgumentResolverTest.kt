package com.fastcampus.commerce.common.resolver

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.core.MethodParameter
import org.springframework.data.domain.Pageable
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest

class CustomPageableArgumentResolverTest : DescribeSpec({

    describe("CustomPageableArgumentResolver") {

        context("oneIndexedParameters = true인 경우") {
            val properties = PageableProperties(
                oneIndexedParameters = true,
                defaultPageSize = 10,
                maxPageSize = 50,
            )
            val resolver = CustomPageableArgumentResolver(properties)

            it("page가 null이면 기본값 1을 사용하여 page 0으로 변환한다") {
                val request = MockHttpServletRequest()
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageNumber shouldBe 0
                pageable.pageSize shouldBe 10
            }

            it("page=1이면 page 0으로 변환한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("page", "1")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageNumber shouldBe 0
            }

            it("page=5이면 page 4로 변환한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("page", "5")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageNumber shouldBe 4
            }

            it("page=0이면 page 0으로 보정한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("page", "0")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageNumber shouldBe 0
            }

            it("page가 음수이면 page 0으로 보정한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("page", "-5")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageNumber shouldBe 0
            }
        }

        context("oneIndexedParameters = false인 경우") {
            val properties = PageableProperties(
                defaultPageSize = 20,
                maxPageSize = 50,
                oneIndexedParameters = false,
            )
            val resolver = CustomPageableArgumentResolver(properties)

            it("page가 null이면 기본값 0을 사용한다") {
                val request = MockHttpServletRequest()
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageNumber shouldBe 0
            }

            it("page=0이면 page 0을 그대로 사용한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("page", "0")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageNumber shouldBe 0
            }

            it("page=5이면 page 5를 그대로 사용한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("page", "5")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageNumber shouldBe 5
            }
        }

        context("페이지 사이즈 처리") {
            val properties = PageableProperties(
                defaultPageSize = 20,
                maxPageSize = 50,
                oneIndexedParameters = true,
            )
            val resolver = CustomPageableArgumentResolver(properties)

            it("size가 null이면 기본값을 사용한다") {
                val request = MockHttpServletRequest()
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageSize shouldBe 20
            }

            it("size가 정상 범위내면 그대로 사용한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("size", "30")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageSize shouldBe 30
            }

            it("size가 maxPageSize를 초과하면 maxPageSize로 제한한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("size", "100")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageSize shouldBe 50
            }

            it("size가 1보다 작으면 1로 보정한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("size", "0")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageSize shouldBe 1
            }

            it("size가 음수이면 1로 보정한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("size", "-10")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                pageable.pageSize shouldBe 1
            }
        }

        context("정렬 처리") {
            val properties = PageableProperties(
                defaultPageSize = 20,
                maxPageSize = 50,
                oneIndexedParameters = true,
            )
            val resolver = CustomPageableArgumentResolver(properties)

            it("sort가 없으면 기본값 createdAt,DESC를 사용한다") {
                val request = MockHttpServletRequest()
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                val sort = pageable.sort
                sort.getOrderFor("createdAt")?.direction?.name shouldBe "DESC"
            }

            it("단일 ASC 정렬을 처리한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("sort", "name")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                val sort = pageable.sort
                sort.getOrderFor("name")?.direction?.name shouldBe "ASC"
            }

            it("단일 DESC 정렬을 처리한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("sort", "-name")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                val sort = pageable.sort
                sort.getOrderFor("name")?.direction?.name shouldBe "DESC"
            }

            it("다중 정렬을 처리한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("sort", "name,-createdAt,id")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                val sort = pageable.sort
                sort.getOrderFor("name")?.direction?.name shouldBe "ASC"
                sort.getOrderFor("createdAt")?.direction?.name shouldBe "DESC"
                sort.getOrderFor("id")?.direction?.name shouldBe "ASC"
            }

            it("공백이 포함된 정렬을 처리한다") {
                val request = MockHttpServletRequest().apply {
                    setParameter("sort", " name , -createdAt , id ")
                }
                val webRequest = ServletWebRequest(request)

                val pageable = resolver.resolveArgument(
                    mockMethodParameter(),
                    null,
                    webRequest,
                    null,
                )

                val sort = pageable.sort
                sort.getOrderFor("name")?.direction?.name shouldBe "ASC"
                sort.getOrderFor("createdAt")?.direction?.name shouldBe "DESC"
                sort.getOrderFor("id")?.direction?.name shouldBe "ASC"
            }
        }

        context("supportsParameter") {
            val properties = PageableProperties()
            val resolver = CustomPageableArgumentResolver(properties)

            it("Pageable 타입이면 true를 반환한다") {
                val parameter = mockMethodParameter()
                resolver.supportsParameter(parameter) shouldBe true
            }
        }
    }
}) {
    companion object {
        private fun mockMethodParameter(): MethodParameter {
            val method = TestController::class.java
                .getDeclaredMethod("testMethod", Pageable::class.java)
            return MethodParameter(method, 0)
        }
    }

    class TestController {
        @Suppress("UNUSED_PARAMETER")
        fun testMethod(pageable: Pageable) {
            // 테스트용 더미 메서드
        }
    }
}
