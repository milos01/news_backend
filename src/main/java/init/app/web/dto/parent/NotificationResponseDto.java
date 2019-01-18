package init.app.web.dto.parent;

import init.app.domain.model.enumeration.ContentType;
import init.app.domain.model.enumeration.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class NotificationResponseDto implements Serializable {

    private Long id;
    private NotificationType type;
    private String users;
    private String notificationHeadline;
    private Long contentId;
    private Long commentId;
    private Long pollId;
    private Boolean isRead;
    private ZonedDateTime updateTime;
    private String contentType;

    public NotificationResponseDto(Long id, NotificationType type, String users, String notificationHeadline, Long contentId, Long commentId, Long pollId, Boolean isRead, ZonedDateTime updateTime, ContentType contentType) {
        this.id = id;
        this.type = type;
        this.users = users;
        this.notificationHeadline = notificationHeadline;
        this.contentId = contentId;
        this.commentId = commentId;
        this.pollId = pollId;
        this.isRead = isRead;
        this.updateTime = updateTime;
        this.contentType = contentType.name();
    }

}
