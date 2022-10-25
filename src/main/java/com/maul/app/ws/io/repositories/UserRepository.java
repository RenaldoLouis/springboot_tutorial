package com.maul.app.ws.io.repositories;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maul.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    @Query("select user from UserEntity user where user.userId =:userId")
    UserEntity findUserEntityByUserId(@Param(value = "userId") String userId);

    UserEntity findUserByEmailVerificationToken(String token);

    @Query(value = "select * from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'", countQuery = "select count(*) from Users u where u.EMAIL_VERIFICATION_STATUS = 'true'", nativeQuery = true)
    Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);

    @Query(value = "select * from Users u where LOWER(u.first_name) LIKE %:firstName%", nativeQuery = true)
    Page<UserEntity> findUserByFirstName(Pageable pageableRequest, @Param("firstName") String firstName);

//    @Query(value = "select * from Users u where LOWER(u.first_name)=?1", nativeQuery = true)
//    Page<UserEntity> findUserByFirstName(Pageable pageableRequest, String firstName);

    @Query(value = "select u.first_name,u.last_name from Users u where LOWER(u.first_name) LIKE %:keyword% or LOWER(u.last_name) LIKE %:keyword%", nativeQuery = true)
    Page<Object[]> findUserFirstNameLastNameByKeyword(Pageable pageableRequest, @Param("keyword") String keyword);

    @Transactional // to prevent error usually put in rest controller and the service class
    @Modifying // needed when change database with update
    @Query(value = "update Users set EMAIL_VERIFICATION_STATUS=:emailVerificationStatus where user_id=:userId", nativeQuery = true)
    void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,
            @Param("userId") String userId);

}
