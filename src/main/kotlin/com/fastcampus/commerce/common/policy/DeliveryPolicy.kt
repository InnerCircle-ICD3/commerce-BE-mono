package com.fastcampus.commerce.common.policy

import org.springframework.stereotype.Component

@Component
class DeliveryPolicy {

    fun calculateDeliveryFee(totalPrice : Int) : Int{
        return if(totalPrice >=30000) {
            0
        } else{
            3000
        }
    }
}
