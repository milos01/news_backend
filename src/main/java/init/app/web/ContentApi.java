package init.app.web;

import com.jcabi.aspects.Loggable;
import init.app.component.HttpCustomEntity;
import init.app.domain.model.Content;
import init.app.domain.model.EntityTag;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.ContentCreationType;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.Role;
import init.app.domain.model.enumeration.UserContentType;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.EntityTagRepository;
import init.app.domain.repository.UserRepository;
import init.app.service.ContentService;
import init.app.service.TagService;
import init.app.service.UserService;
import init.app.web.dto.request.ContentRequestDto;
import init.app.web.dto.request.RepostContentRequestDto;
import init.app.web.dto.request.TagListDto;
import init.app.web.dto.response.*;
import init.app.web.dto.shared.GenericResponseDto;
import init.app.web.dto.shared.SingleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import java.util.List;

import static init.app.exception.CustomExceptionHandler.handleException;
import static init.app.util.ConvertUtil.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Loggable(trim = false, prepend = true)
@RestController
public class ContentApi {

    @Inject
    private HttpCustomEntity entity;
    @Inject
    private ContentService contentService;
    @Inject
    private ContentRepository contentRepository;
    @Inject
    private EntityTagRepository entityTagRepository;
    @Inject
    private TagService tagService;
    @Inject
    private UserService userService;
    @Inject
    private UserRepository userRepository;

    @RequestMapping(value = "/content/", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity create(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody ContentRequestDto request) {
        try {
            return entity.response(contentService.create(principalId, request.getHeadline(), request.getText(), request.getCategoryId(), request.getPublished(), ContentCreationType.POST), CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/article", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity createArticle(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody ContentRequestDto request) {
        try {
            return entity.response(contentService.create(principalId, request.getHeadline(), request.getText(), request.getCategoryId(), request.getPublished(), ContentCreationType.ARTICLE), CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity read(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            SingleResponseDto singleResponseDto = convertQueryToSingleResponse(contentRepository.findByIdNative(id, principalId), ContentResponseDto.class);
            tagService.updateTagsTempActivity(contentService.getTagsForContent(id));
            return entity.response(singleResponseDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/single", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity get(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            return entity.response(convertQueryToSingleResponse(contentRepository.getSingleResponse(principalId, id), SingleContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}", method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity update(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @Valid @RequestBody ContentRequestDto request) {
        try {
            contentService.update(id, principalId, request.getHeadline(), request.getText(), request.getCategoryId(), request.getPublished());
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}", method = DELETE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity getById(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id) {
        try {
            contentService.delete(principalId, id);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "user/{id}/contents", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserAll(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @RequestParam(value = "type") UserContentType type, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        try {
            List<Object[]> response;

            switch (type) {
                case VOTED:
                    response = contentRepository.getUserContentVoted(id, principalId, limit, offset);
                    break;
                case SHARED:
                    response = contentRepository.getUserContentShared(principalId, id.toString(), limit, offset);
                    break;
                case ALL:
                default:
                    response = contentRepository.getUserContent(principalId, limit, offset);
            }
            return entity.response(convertToAllResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "user/contents", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity getUserAllByType(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "type") UserContentType type, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        try {
            List<Object[]> response;

            switch (type) {
                case VOTED:
                    response = contentRepository.getUserContentVoted(principalId, principalId, limit, offset);
                    break;
                case LIKED:
                    response = contentRepository.getUserContentLiked(principalId, limit, offset);
                    break;
                case SHARED:
                    response = contentRepository.getUserContentShared(principalId, principalId.toString(), limit, offset);
                    break;
                case STORED:
                    response = contentRepository.getUserContentStored(principalId, limit, offset);
                    break;
                case ALL:
                default:
                    response = contentRepository.getUserContent(principalId, limit, offset);
            }
            return entity.response(convertToAllResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/community", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity getUserChannel(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        try {
            List<Object[]> response = contentRepository.getCommunity(principalId, limit, offset);
            return entity.response(convertToAllResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/stream", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStream(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam("limit") int limit, @RequestParam("offset") int offset, @RequestParam(value = "type") ContentType type) {
        try {
            List<Object[]> response = contentRepository.getStream(principalId, type.name(), limit, offset);
            return entity.response(convertToAllResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/poll-of-the-day", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getPollOfTheDay(@ApiIgnore @AuthenticationPrincipal Long principalId) {
        try {
            Object[] response = contentRepository.getPollOfTheDay(principalId);
            return entity.response(convertQueryToSingleResponse(response, PollOfTheDayResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "type", required = false) ContentType type, @RequestParam(value = "keyword", required = false) @Size(min = 1, max = 600) String keyword, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "category", required = false) Long categoryId, @RequestParam(value = "hasVideo", required = false) Boolean hasVideo) {
        try {

            if (type == ContentType.ARTICLE_DRAFT) {
                if (principalId == null) {
                    return entity.response(HttpStatus.FORBIDDEN);
                } else {
                    User user = userRepository.findById(principalId);

                    if (user.getRole() != Role.ADMIN) {
                        return entity.response(HttpStatus.FORBIDDEN);
                    }
                }
            }

            Page<Object[]> response = contentRepository.getAll(type == null ? null : type.name(), keyword, false, principalId, categoryId, hasVideo, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/all/deleted", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAllDeleted(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "type") ContentType type, @RequestParam(value = "keyword", required = false) @Size(min = 1, max = 600) String keyword, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = contentRepository.getAll(type.name(), keyword, true, principalId, null, null, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/tag/{id}/contents", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAll(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @RequestParam(value = "type", required = false) ContentType type, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = contentRepository.getAllByTag(id, type == null ? null : type.name(), principalId, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/by-tag", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllByTag(@ApiIgnore @AuthenticationPrincipal Long principalId, @RequestParam(value = "type", required = false) ContentType type, @RequestParam("tag") String tagName, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {
            Page<Object[]> response = contentRepository.getAllByTag(tagName, type == null ? null : type.name(), principalId, new PageRequest(page, size));
            return entity.response(convertToPageResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/recommended", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getRecommended(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @RequestParam("page") int page, @RequestParam("size") int size) {
        try {

            Content content = contentService.getByRepoMethod(contentRepository.findById(id));

            List<EntityTag> contentTags = entityTagRepository.findAllByContentAndIsDeletedFalse(content);

            Page<Object[]> response = contentRepository.getRecommended(principalId, id, contentTags.size() > 0 ? contentTags.get(0).getTag().getId() : null, contentTags.size() > 1 ? contentTags.get(1).getTag().getId() : null, contentTags.size() > 2 ? contentTags.get(2).getTag().getId() : null, new PageRequest(page, size));

            return entity.response(convertToPageResponse(response, ContentResponseDto.class));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/all/count", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAllCount(@RequestParam("contentType") ContentType contentType) {
        try {
            CountResponseDto countResponseDto = new CountResponseDto();
            countResponseDto.setCount(contentRepository.countAll(contentType.name()));
            return entity.response(countResponseDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/type", method = PUT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity changeType(@PathVariable Long id, @RequestParam("type") ContentType type) {
        try {
            contentService.changeType(id, type);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content/{id}/tags", method = PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity updateTags(@ApiIgnore @AuthenticationPrincipal Long principalId, @PathVariable Long id, @Valid @RequestBody TagListDto request) {
        try {
            contentService.updateTags(request.getTagList(), id, principalId);
            return entity.responseNoContent();
        } catch (Exception e) {
            return handleException(e);
        }
    }


    @RequestMapping(value = "/content/{id}/repost", method = POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACTIVE') or hasAuthority('ADMIN')")
    public ResponseEntity repost(@ApiIgnore @AuthenticationPrincipal Long principalId, @Valid @RequestBody RepostContentRequestDto request, @PathVariable Long id) {
        try {
            GenericResponseDto genericResponseDto = contentService.repost(principalId, request.getText(), id);
            tagService.updateTagsTempActivity(contentService.getTagsForContent(id));
            return entity.response(genericResponseDto);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @RequestMapping(value = "/content-with-poll/all", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity getAll(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam(value = "keyword", required = false) @Size(min = 1, max = 600) String keyword) {
        try {
            Page<ContentPollResponseDto> response = contentRepository.fetchAllContentWithPoll(StringUtils.hasText(keyword) ? "%" + keyword + "%" : "%", new PageRequest(page, size));
            return entity.response(response);
        } catch (Exception e) {
            return handleException(e);
        }
    }
}
