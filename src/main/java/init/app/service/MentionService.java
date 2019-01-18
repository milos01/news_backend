package init.app.service;

import com.jcabi.aspects.Async;
import com.jcabi.aspects.Loggable;
import init.app.domain.model.Comment;
import init.app.domain.model.Content;
import init.app.domain.model.User;
import init.app.domain.model.enumeration.NotificationType;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bojan.stankovic@codetri.be on 5/18/18.
 */
@Service
@Loggable(trim = false, prepend = true)
@Transactional(rollbackFor = Exception.class)
public class MentionService {

    @Inject
    private UserService userService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private NotificationService notificationService;

    private static List<Long> getMentionedUserIds(final String html) {
        final List<Long> mentionedUserIds = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements users = doc.select("a[userid]");
        for (Element user : users) {
            mentionedUserIds.add(Long.valueOf(user.attr("userid")));
        }
        return mentionedUserIds;
    }

    @Async
    public void createMentionsForComment(Comment comment) throws CustomException {
        List<Long> listOfMentionedUserIds = getMentionedUserIds(comment.getText());

        createMentions(comment.getContent(), comment, listOfMentionedUserIds);
    }

    @Async
    public void createMentionsForContent(Content content) throws CustomException {
        List<Long> listOfMentionedUserIds = getMentionedUserIds(content.getText());

        createMentions(content, null, listOfMentionedUserIds);
    }

    private void createMentions(Content content, Comment comment, List<Long> listOfMentionedUserIds) throws CustomException {

        User author;

        if (comment != null) {
            author = comment.getUser();
        } else {
            author = content.getUser();
        }

        for (Long userId : listOfMentionedUserIds) {
            User user = userService.getByRepoMethod(userRepository.findById(userId));
            notificationService.createUpdateNotification(user, author, NotificationType.MENTIONED, content, comment, null);
        }
    }

}
