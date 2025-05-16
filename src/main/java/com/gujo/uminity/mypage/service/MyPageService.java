package com.gujo.uminity.mypage.service;

import com.gujo.uminity.mypage.dto.MyPageResponseDto;
import com.gujo.uminity.mypage.dto.PasswordChangeRequestDto;
import com.gujo.uminity.mypage.dto.UpdateUserInfoRequestDto;

public interface MyPageService {

    MyPageResponseDto getMyPageInfo();

    void updateUserInfo(UpdateUserInfoRequestDto updateUserInfoRequestDto);

    void deleteUser(String userId);

    void changePassword(String userId, PasswordChangeRequestDto passwordChangeRequestDto);
}
