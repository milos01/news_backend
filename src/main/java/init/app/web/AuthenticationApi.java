package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.configuration.ConfigProperties;
import init.app.domain.model.enumeration.Role;
import init.app.domain.model.enumeration.SocialType;
import init.app.domain.repository.UserRepository;
import init.app.service.AuthenticationService;
import init.app.service.UserService;
import init.app.web.dto.parent.EmailDto;
import init.app.web.dto.parent.PasswordDto;
import init.app.web.dto.parent.SetPasswordRequestDto;
import init.app.web.dto.request.AuthSocialRequestDto;
import init.app.web.dto.request.SignInRequestDto;
import init.app.web.dto.request.SignUpRequestDto;
import init.app.web.dto.response.TwitterRedirectDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.net.URI;

import static init.app.exception.CustomExceptionHandler.handleException;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
@RequestMapping("/authentication")
public class AuthenticationApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private AuthenticationService authenticationService;
    @Inject
    private ConfigProperties configProperties;
    @Inject
    private UserService userService;
    @Inject
    private UserRepository userRepository;


    @RequestMapping(value = "/sign-in", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signIn(@Valid @RequestBody SignInRequestDto request) {
        try {
            return entity.response(authenticationService.signIn(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/sign-in-social", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signInSocial(@RequestParam(value = "type") SocialType type, @Valid @RequestBody AuthSocialRequestDto request) {
        try {
            return entity.response(authenticationService.signIn(type, request.getEmail(), request.getSocialId(), request.getUserName(), request.getImageUrl()));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/sign-in-twitter", method = GET)
    public ResponseEntity signInTwitter(@RequestParam(value = "oauth_token", required = false) String oauthToken, @RequestParam(value = "oauth_verifier", required = false) String oauthVerifier, @RequestParam(value = "denied", required = false) String denied, HttpServletRequest request, HttpServletResponse response) {
        try {

            String redirectUrl;

            if(!StringUtils.hasText(oauthToken) || !StringUtils.hasText(oauthVerifier) || StringUtils.hasText(denied)) {
                redirectUrl = configProperties.getFrontendbaseurl();
            } else {
                redirectUrl = authenticationService.loginWithTwitter(request.getCookies(), oauthToken, oauthVerifier, response);
            }

            URI redirectUri = new URI(redirectUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (Exception e) {
            return handleException(e); 
        }
    }

    @RequestMapping(value = "/authorize-twitter", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    ResponseEntity redirectToTwitterAuthorize(HttpServletRequest request, HttpServletResponse response) {

        try {
            TwitterRedirectDTO twitterRedirectDTO = authenticationService.getTwitterAuthUrl(response);
            return ResponseEntity.ok(twitterRedirectDTO);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/sign-up", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signUp(@Valid @RequestBody SignUpRequestDto request) {
        try {
            authenticationService.signUp(request.getEmail(), request.getPassword());
        } catch (Exception e) {
            return handleException(e);
        }
        return entity.responseCreated();
    }

    @RequestMapping(value = "/verify-email", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity verifyEmail(@RequestParam("code") String code) {
        try {
            return entity.response(authenticationService.verifyEmail(code));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/forgot-password", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity forgotPassword(@Valid @RequestBody EmailDto request) {
        try {
            authenticationService.forgotPassword(request.getEmail());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/change-password", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity changePassword(@RequestParam("code") String code, @Valid @RequestBody PasswordDto request) {
        try {
            authenticationService.changePassword(code, request.getPassword());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/resend-auth-link", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity resendAuthlink(@Valid @RequestBody EmailDto request) {
        try {
            authenticationService.resendAuthLink(request.getEmail());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/set-new-password", method = POST)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ACTIVE')")
    public ResponseEntity setNewPassword(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody SetPasswordRequestDto setPasswordRequestDto) {
        try {
            authenticationService.setNewPassword(principalId, setPasswordRequestDto.getOldPassword(), setPasswordRequestDto.getNewPassword());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/deactivate-account", method = POST)
    @PreAuthorize("hasAuthority('CONFIRMED') or hasAuthority('ACTIVE')")
    public ResponseEntity deactivateAcc(@ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            authenticationService.changeRole(userService.getByRepoMethod(userRepository.findById(principalId)), Role.DEACTIVATED);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/reactivate-account", method = POST)
    @PreAuthorize("hasAuthority('DEACTIVATED')")
    public ResponseEntity reactivateAcc(@ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            return entity.response(authenticationService.reactivateAccount(principalId));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/user/{id}/role", method = PUT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity changeRole(@PathVariable Long id, @RequestParam("role") Role role) {
        try {
            authenticationService.changeRole(userService.getByRepoMethod(userRepository.findById(id)), role);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
