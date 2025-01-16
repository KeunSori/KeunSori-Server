package com.keunsori.keunsoriserver.domain.auth.login;

import com.keunsori.keunsoriserver.domain.auth.login.dto.LoginRequestDTO;
import com.keunsori.keunsoriserver.domain.auth.login.dto.LoginResponseDTO;
import com.keunsori.keunsoriserver.domain.auth.service.AuthService;
import com.keunsori.keunsoriserver.domain.auth.exception.InvalidPasswordException;
import com.keunsori.keunsoriserver.domain.auth.exception.MemberNotFoundException;
import com.keunsori.keunsoriserver.domain.member.Member;
import com.keunsori.keunsoriserver.domain.member.MemberRepository;
import com.keunsori.keunsoriserver.domain.member.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenCreater jwtTokenCreater;
    private final AuthService refreshTokenService;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws MemberNotFoundException, InvalidPasswordException {

        //학번으로 사용자 조회
        Member member= memberRepository.findByStudentId(loginRequestDTO.getStudentId())
                .orElseThrow(()->new MemberNotFoundException("존재하지 않는 학번입니다."));

        //비밀번호 일치하는지 검증
        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())){
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        //Access Token, Refresh Token 생성
        String accessToken=jwtTokenCreater.generateAccessToken(
                member.getStudentId(), member.getName(), MemberStatus.일반
        );

        String refreshToken=jwtTokenCreater.generateRefreshToken(
                member.getStudentId(), member.getName(), MemberStatus.일반
        );

        //Refresh Token Redis에 저장
        refreshTokenService.saveRefreshToken(member.getStudentId(), refreshToken, 7*24*60*60*1000L);

        return new LoginResponseDTO(member.getStudentId(),member.getName(),
                MemberStatus.일반,
                accessToken,
                refreshToken,
                String.valueOf(jwtTokenCreater.getExpirationTime(accessToken)));

    }

}
