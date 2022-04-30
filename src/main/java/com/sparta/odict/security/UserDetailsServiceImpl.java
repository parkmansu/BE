package com.sparta.odict.security;

import com.sparta.odict.model.RefreshToken;
import com.sparta.odict.model.User;
import com.sparta.odict.repository.RefreshTokenRepository;
import com.sparta.odict.repository.UserRepository;
import com.sparta.odict.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("user를 찾을 수 없어요");
        User user = userRepository.findByUsername(username)
                //.orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER_ID));
                .orElseThrow(()-> new UsernameNotFoundException("Can't find " + username));
        return new UserDetailsImpl(user);
    }

    public String saveRefershToken(User user){
        User thisUser = userRepository.findById(user.getId())
                //.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER_ID));
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + user));
        String refreshToken = JwtTokenUtils.generateRefreshToken(thisUser);
        RefreshToken userRefreshToken = new RefreshToken();
        userRefreshToken.setUser(thisUser);
        userRefreshToken.setRefreshToken(refreshToken);
        refreshTokenRepository.save(userRefreshToken);
        return refreshToken;
    }

    public void findUser(String username) {
        userRepository.findByUsername(username);
    }
}
