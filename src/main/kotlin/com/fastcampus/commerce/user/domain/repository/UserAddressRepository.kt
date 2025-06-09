package com.fastcampus.commerce.user.domain.repository

import com.fastcampus.commerce.user.domain.entity.UserAddress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface UserAddressRepository : JpaRepository<UserAddress, Long> {
    @Query(
        """
        select ua 
        from UserAddress ua
        where ua.userId = :userId
        order by ua.isDefault desc, ua.createdAt desc
    """,
    )
    fun getAllByUserId(userId: Long): List<UserAddress>

    @Query(
        """
        select ua 
        from UserAddress ua
        where ua.userId = :userId
          and ua.isDefault = true
    """,
    )
    fun findDefaultByUserId(userId: Long): Optional<UserAddress>
}
