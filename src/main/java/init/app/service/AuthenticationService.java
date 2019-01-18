package init.app.service;

import com.jcabi.aspects.Loggable;
import init.app.configuration.ConfigProperties;
import init.app.configuration.security.JWTUtils;
import init.app.domain.model.AuthenticationLink;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.LinkType;
import init.app.domain.model.enumeration.Role;
import init.app.domain.model.enumeration.SocialType;
import init.app.domain.repository.AuthenticationLinkRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.util.Md5HashUtil;
import init.app.web.dto.response.SignInResponseDto;
import init.app.web.dto.response.TwitterRedirectDTO;
import init.app.web.dto.response.UserVerifyLinkResponseDto;
import init.app.web.dto.shared.GenericResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class AuthenticationService {

    @Inject
    private UserRepository userRepository;
    @Inject
    private AuthenticationLinkRepository authenticationLinkRepository;
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private MailService mailService;
    @Inject
    private UserService userService;
    @Inject
    private ConfigProperties configProperties;

    private static final int daysCodeValid = 30;

    public void signUp(String email, String password) throws CustomException {

        email = email.toLowerCase();

        User user = userRepository.findAllByEmail(email).orElse(null);

        if (user != null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("USER_EXISTS"));
        }

        user = userService.createUser(email, password);

        String url = createLink(user, LinkType.CREATE_PROFILE);
        String subject = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("SIGNUP_SUBJECT");
        String textPlain = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("SIGNUP_TEXT_PLAIN").replace("INSERT_LINK_HERE", url);
        String textHtml = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("SIGNUP_TEXT_HTML").replace("INSERT_LINK_HERE", url);
        mailService.send(email, subject, textPlain, textHtml);
    }

    //Email
    public GenericResponseDto signIn(String email, String password) throws CustomException {

        email = email.toLowerCase();

        final User user = checkSignInUser(email);

        if (user == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("USER_NOT_EXIST"), HttpStatus.NOT_FOUND);
        } else if (!passwordEncoder.matches(password, user.getPassword()) && !passwordEncoder.matches(Md5HashUtil.hashPassword(password), user.getPassword())) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CREDENTIALS_INVALID"), HttpStatus.UNAUTHORIZED);
        }

        return new GenericResponseDto(new SignInResponseDto(JWTUtils.generateToken(user), user.getUsername(), user.getRole(), user.getId(), user.getEmail(), user.getImageUrl(), userService.getAllUserTags(user)));
    }

    //Social (Facebook & Google)
    public GenericResponseDto signIn(SocialType type, String email, String socialId, String username, String imageUrl) throws CustomException {

        email = email.toLowerCase();

        User user = null;

        switch (type) {
            case GOOGLE:
                user = userRepository.findByGoogleId(socialId);
                break;
            case FACEBOOK:
                user = userRepository.findByFacebookId(socialId);
                break;
            case TWITTER:
                user = userRepository.findByTwitterId(socialId);
                break;
        }

        if (user != null) {
            checkUser(user, true);
        }

        if (user == null || user.getRole() != Role.ADMIN && user.getRole() != Role.ACTIVE) {
            user = userService.createUpdateUser(user, email, username, type, socialId, imageUrl);
        }

        return new GenericResponseDto(new SignInResponseDto(JWTUtils.generateToken(user), user.getUsername(), user.getRole(), user.getId(), user.getEmail(), user.getImageUrl(), userService.getAllUserTags(user)));
    }

    public TwitterRedirectDTO getTwitterAuthUrl(HttpServletResponse response) throws CustomException, TwitterException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(configProperties.getTwitter().getKey())
                .setOAuthConsumerSecret(configProperties.getTwitter().getSecret());

        TwitterRedirectDTO redirectDTO = new TwitterRedirectDTO();

        try {
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();

            RequestToken requestToken = twitter.getOAuthRequestToken(
                    configProperties.getBaseurl() + configProperties.getTwitter().getRedirecturl()
            );

            Cookie cookie = new Cookie(requestToken.getToken(), requestToken.getTokenSecret());
            cookie.setHttpOnly(false);
            cookie.setDomain(configProperties.getCookiedomain());
            cookie.setMaxAge(28800);
            cookie.setPath("/");

            response.addCookie(cookie);

            redirectDTO.setRedirectUrl(requestToken.getAuthorizationURL());

            return redirectDTO;

        } catch (TwitterException te) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("TWITTER_SIGN_UP_FAILED"));
        }
    }

    public String loginWithTwitter(Cookie[] cookies, String oauth_token, String oauth_verifier, HttpServletResponse response) throws CustomException, TwitterException {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(configProperties.getTwitter().getKey())
                .setOAuthConsumerSecret(configProperties.getTwitter().getSecret());

        twitter4j.User twitterUser = null;
        Cookie requestTokenCookie = null;

        try {
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();


            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase(oauth_token)) {
                        requestTokenCookie = cookie;
                    }
                }
            }

            if (requestTokenCookie == null) {
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("TWITTER_SIGN_UP_FAILED"));
            }

            RequestToken requestToken = new RequestToken(oauth_token, requestTokenCookie.getValue());

            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);

            twitterUser = twitter.showUser(accessToken.getUserId());

        } catch (TwitterException te) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("TWITTER_SIGN_UP_FAILED"));
        } catch (Exception e) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("TWITTER_SIGN_UP_FAILED"));
        }

        if (twitterUser == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("TWITTER_SIGN_UP_FAILED"));
        }

        User user = userRepository.findByTwitterId(Long.toString(twitterUser.getId()));

        Boolean isDeactivated = user != null && user.getRole().equals(Role.DEACTIVATED);
        Boolean isBanned = user != null && user.getRole().equals(Role.BANNED);

        Boolean isNewUser = user == null || (!user.getRole().equals(Role.ACTIVE) && !user.getRole().equals(Role.ADMIN));

        if (isNewUser) {
            user = userService.createUpdateUser(user, StringUtils.hasText(twitterUser.getEmail()) ? twitterUser.getEmail().toLowerCase() : null, twitterUser.getName(), SocialType.TWITTER, Long.toString(twitterUser.getId()), twitterUser.getOriginalProfileImageURL());
        }

        String cookieValue = "";

        if(!isBanned) {
            cookieValue = JWTUtils.generateToken(user) + "|" + user.getUsername() + "|" + user.getRole() + "|" + user.getId() + "|" + user.getEmail() + "|" + user.getImageUrl();
        } else {
            cookieValue = "banned user|" + user.getUsername() + "|" + user.getRole();
        }

        cookieValue = cookieValue.replace(" ", "_");

        Cookie cookie = new Cookie("7BILLION_TWITTER_USER_INFO", cookieValue);
        cookie.setHttpOnly(false);
        cookie.setDomain(configProperties.getCookiedomain());
        cookie.setMaxAge(28800);
        cookie.setPath("/");

        response.addCookie(cookie);

        requestTokenCookie.setMaxAge(-1);
        requestTokenCookie.setDomain(configProperties.getCookiedomain());
        requestTokenCookie.setPath("/");

        response.addCookie(requestTokenCookie);
        if (isDeactivated || isBanned) {
            return configProperties.getFrontendbaseurl() + configProperties.getFrontendsignin();
        } else {
            return isNewUser ? configProperties.getFrontendbaseurl() + configProperties.getFrontendcreateprofile() : configProperties.getFrontendbaseurl();
        }
    }

    public void resendAuthLink(String email) throws CustomException {

        email = email.toLowerCase();

        User user = userService.getByRepoMethod(userRepository.findAllByEmail(email).orElse(null));

        if (user.getRole() != Role.UNCONFIRMED && user.getRole() != Role.CONFIRMED) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ONLY_UNCONFIRMED_CONFIRMED_USERS_CAN_RESEND"), HttpStatus.FORBIDDEN);
        }

        String url = createLink(user, LinkType.CREATE_PROFILE);
        String subject = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("RESEND_AUTH_SUBJECT");
        String textPlain = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("RESEND_AUTH_TEXT_PLAIN").replace("INSERT_LINK_HERE", url);
        String textHtml = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("RESEND_AUTH_TEXT_HTML").replace("INSERT_LINK_HERE", url);
        mailService.send(email, subject, textPlain, textHtml);
    }

    public void forgotPassword(String email) throws CustomException {

        email = email.toLowerCase();

        User user = userService.getByRepoMethod(userRepository.findAllByEmail(email).orElse(null));

        String url = createLink(user, LinkType.RESET_PASS);
        String subject = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("FORGOT_PASSWORD_SUBJECT");
        String textPlain = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("FORGOT_PASSWORD_TEXT_PLAIN").replace("INSERT_LINK_HERE", url).replace("INSERT_USERNAME_HERE", StringUtils.hasText(user.getUsername()) ? user.getUsername() : "");
        String textHtml = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("FORGOT_PASSWORD_TEXT_HTML").replace("INSERT_LINK_HERE", url).replace("INSERT_USERNAME_HERE", StringUtils.hasText(user.getUsername()) ? user.getUsername() : "");
        mailService.send(email, subject, textPlain, textHtml);
    }

    public GenericResponseDto reactivateAccount(Long principalId) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(principalId));

        changeRole(user, Role.ACTIVE);

        return new GenericResponseDto(new SignInResponseDto(JWTUtils.generateToken(user), user.getUsername(), user.getRole(), user.getId(), user.getEmail(), user.getImageUrl(), userService.getAllUserTags(user)));
    }

    public void changeRole(User user, Role role) throws CustomException {

        if (role == Role.DEACTIVATED && user.getRole() != Role.ACTIVE && user.getRole() != Role.CONFIRMED) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("ONLY_ACTIVE_CONFIRMED_USERS_CAN_DEACTIVATE"), HttpStatus.UNAUTHORIZED);
        }

        user.setRole(role);
        user.setUpdateTime(ZonedDateTime.now());
        userRepository.save(user);
    }

    public void changePassword(String code, String password) throws CustomException {

        AuthenticationLink authenticationLink = checkAndExpireCode(code, LinkType.RESET_PASS);

        userService.updatePassword(authenticationLink.getUser(), password);
    }

    public void setNewPassword(Long userId, String oldPassword, String newPassword) throws CustomException {

        User user = userService.getByRepoMethod(userRepository.findById(userId));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("CREDENTIALS_INVALID"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(ZonedDateTime.now());
        userRepository.save(user);
    }

    public GenericResponseDto verifyEmail(String code) throws CustomException {

        AuthenticationLink authenticationLink = checkAndExpireCode(code, LinkType.CREATE_PROFILE);

        User userToVerify = authenticationLink.getUser();

        userToVerify.setEmailVerifiedTime(ZonedDateTime.now());

        changeRole(userToVerify, Role.CONFIRMED);

        UserVerifyLinkResponseDto userVerifyLinkResponseDto = new UserVerifyLinkResponseDto();

        userVerifyLinkResponseDto.setAccessToken(JWTUtils.generateToken(authenticationLink.getUser().getId(), Role.CONFIRMED));
        userVerifyLinkResponseDto.setRole(Role.CONFIRMED);
        userVerifyLinkResponseDto.setBio(authenticationLink.getUser().getBio());
        userVerifyLinkResponseDto.setEmail(authenticationLink.getUser().getEmail().toLowerCase());
        userVerifyLinkResponseDto.setImageUrl(authenticationLink.getUser().getImageUrl());
        userVerifyLinkResponseDto.setUsername(authenticationLink.getUser().getUsername());
        userVerifyLinkResponseDto.setId(authenticationLink.getUser().getId());

        return new GenericResponseDto(userVerifyLinkResponseDto);
    }

    private User checkSignInUser(String email) throws CustomException {

        email = email.toLowerCase();

        User user = userRepository.findAllByEmail(email).orElse(null);

        if (user == null) {
            return null;
        }

        checkUser(user, false);

        return user;
    }

    private void checkUser(User user, boolean isSocialLogin) throws CustomException {

        if (user.getIsDeleted()) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("USER_DELETED"));
        }

        switch (user.getRole()) {
            case UNCONFIRMED:
                if (!isSocialLogin) {
                    throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("USER_UNCONFIRMED"));
                }
            case BANNED:
                throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("USER_BANNED"), HttpStatus.FORBIDDEN);
        }
    }

    public String createLink(User user, LinkType type) throws CustomException {

        String code = passwordEncoder.encode(ZonedDateTime.now().toString() + user.getEmail());

        List<AuthenticationLink> oldLinks = authenticationLinkRepository.findAllByUserAndExpiryTimeAfter(user, ZonedDateTime.now());

        for (AuthenticationLink oldLink : oldLinks) {
            oldLink.setExpiryTime(ZonedDateTime.now());

            authenticationLinkRepository.save(oldLink);
        }

        AuthenticationLink authenticationLink = new AuthenticationLink();
        authenticationLink.setType(type);
        authenticationLink.setUrl(code);
        authenticationLink.setUser(user);
        authenticationLink.setExpiryTime(ZonedDateTime.now().plusDays(daysCodeValid));

        authenticationLinkRepository.save(authenticationLink);

        switch (type) {
            case CREATE_PROFILE:
                return configProperties.getFrontendbaseurl() + configProperties.getFrontendcreateprofilewithcode() + code;
            case RESET_PASS:
                return configProperties.getFrontendbaseurl() + configProperties.getFrontendchangepasswordwithcode() + code;
            case SET_PASS:
            default:
                return code;
        }

    }

    private AuthenticationLink checkAndExpireCode(String code, LinkType type) throws CustomException {
        AuthenticationLink authenticationLink = authenticationLinkRepository.findByUrlIsAndTypeIsAndExpiryTimeIsAfter(code, type, ZonedDateTime.now());

        if (authenticationLink == null) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("AUTH_CODE_INVALID"));
        }

        authenticationLink.setExpiryTime(ZonedDateTime.now());
        authenticationLinkRepository.save(authenticationLink);

        return authenticationLink;
    }

}