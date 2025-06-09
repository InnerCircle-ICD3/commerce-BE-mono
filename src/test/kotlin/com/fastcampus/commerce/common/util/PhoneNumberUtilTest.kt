package com.fastcampus.commerce.common.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PhoneNumberUtilTest : FunSpec({

    context("removeHyphens") {
        test("하이픈이 있는 전화번호에서 하이픈을 제거한다") {
            val phoneWithHyphens = "010-1234-5678"

            val result = PhoneNumberUtil.removeHyphens(phoneWithHyphens)

            result shouldBe "01012345678"
        }

        test("하이픈이 없는 전화번호는 그대로 반환한다") {
            val phoneWithoutHyphens = "01012345678"

            val result = PhoneNumberUtil.removeHyphens(phoneWithoutHyphens)

            result shouldBe "01012345678"
        }
    }

    context("addHyphens") {
        test("11자리 전화번호에 하이픈을 추가한다") {
            val phone11Digits = "01012345678"

            val result = PhoneNumberUtil.addHyphens(phone11Digits)

            result shouldBe "010-1234-5678"
        }

        test("10자리 전화번호에 하이픈을 추가한다") {
            val phone10Digits = "0212345678"

            val result = PhoneNumberUtil.addHyphens(phone10Digits)

            result shouldBe "021-234-5678"
        }

        test("10자리도 11자리도 아닌 전화번호는 그대로 반환한다") {
            val invalidPhone = "123456789"

            val result = PhoneNumberUtil.addHyphens(invalidPhone)

            result shouldBe "123456789"
        }

        test("빈 문자열은 그대로 반환한다") {
            val emptyPhone = ""

            val result = PhoneNumberUtil.addHyphens(emptyPhone)

            result shouldBe ""
        }
    }
})