package init.app.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import init.app.configuration.ConfigProperties;
import init.app.domain.model.Content;
import init.app.domain.model.User;
import init.app.domain.model.csv.ArticlePollCsv;
import init.app.domain.model.csv.PostPollCsv;
import init.app.domain.model.csv.UserCsv;
import init.app.domain.model.enumeration.ContentType;
import init.app.domain.repository.ContentRepository;
import init.app.domain.repository.UserRepository;
import init.app.exception.CustomException;
import init.app.web.dto.response.SimpleTagResponseDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by bojan.stankovic@codetri.be on 4/4/18.
 */
@Service
public class CsvExportService {

    @Inject
    UserService userService;
    @Inject
    UserRepository userRepository;
    @Inject
    ContentRepository contentRepository;
    @Inject
    ConfigProperties configProperties;
    @Inject
    MailService mailService;

    private static final char SEMICOLON_SEPARATOR = ';';
    private static final String[] userHeaders = {"Username", "Email", "Social Id", "Bio", "Role/Status", "Profile Image Url", "Tags", "Account Creation Timestamp", "Account Activation Timestamp"};
    private static final String[] articlePollHeaders = {"Headline", "Question", "First Choice Text", "First Choice Votes", "Second Choice Text", "Second Choice Votes", "Third Choice Text", "Third Choice Votes", "Timestamp"};
    private static final String[] postPollHeaders = {"Post Title", "Poster", "Question", "First Choice Text", "First Choice Votes", "Second Choice Text", "Second Choice Votes", "Third Choice Text", "Third Choice Votes", "Timestamp"};
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm a");

    private byte[] exportToCsv(List<Object> results, Class clazz, String[] headers) throws CustomException {

        Path path = Paths.get(configProperties.getStoragefolder() + "/OrdersHistory" + System.currentTimeMillis() + ".csv");

        Writer writer;
        try {
            writer = Files.newBufferedWriter(path);
        } catch (IOException e) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("EXPORT_TO_CSV_FAILED"));
        }


        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy() {
            @Override
            public String[] generateHeader() {
                return headers;
            }
        };

        strategy.setType(clazz);

        final StatefulBeanToCsvBuilder<Object> builder = new StatefulBeanToCsvBuilder<>(writer);
        StatefulBeanToCsv beanWriter = builder
                .withSeparator(SEMICOLON_SEPARATOR)
                .withEscapechar(CSVWriter.NO_ESCAPE_CHARACTER)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                .withMappingStrategy(strategy)
                .build();

        byte[] csvBytes;
        try {
            beanWriter.write(results);
            writer.close();
            csvBytes = Files.readAllBytes(path);
            Files.delete(path);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
            throw new CustomException(ResourceBundle.getBundle("i18n.exception", Locale.ENGLISH).getString("EXPORT_TO_CSV_FAILED"));
        }

        return csvBytes;
    }

    @Async
    public void exportUsersToCsv(Long principalId) throws CustomException {
        byte[] exportResult = exportToCsv(new ArrayList<>(getUserCSVItems()), UserCsv.class, userHeaders);

        User adminUser = userRepository.findById(principalId);

        String subject = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_SUBJECT");
        String textHtml = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_TEXT_HTML").replace("INSERT_USERNAME_HERE", adminUser.getUsername()).replace("ENTITY_TO_EXPORT", "users");
        String textPlain = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_TEXT_PLAIN").replace("INSERT_USERNAME_HERE", adminUser.getUsername());

        mailService.sendCSVAttachment(adminUser.getEmail(), subject, textPlain, textHtml, exportResult, "usersExport");
    }

    private List<UserCsv> getUserCSVItems() {

        List<UserCsv> result = new ArrayList<>();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            UserCsv userCsv = new UserCsv();
            userCsv.setBio(user.getBio());
            userCsv.setEmail(user.getEmail());
            userCsv.setSocialId(user.getFacebookId());
            userCsv.setProfileImageUrl(user.getImageUrl());
            userCsv.setRole(user.getRole().name());
            userCsv.setUsername(user.getUsername());
            userCsv.setTags(getAllUserTagsAndConvertToString(user));
            userCsv.setAccountCreationTimestamp(user.getCreateTime().format(formatter));
            userCsv.setAccountActivationTimestamp(user.getUpdateTime().format(formatter));


            result.add(userCsv);
        }

        return result;
    }

    private String getAllUserTagsAndConvertToString(User user) {
        List<SimpleTagResponseDto> userTags = userService.getAllUserTags(user);

        if (userTags.isEmpty()) {
            return "";
        }

        String tagsStringResponse = "";

        for (SimpleTagResponseDto userTag : userTags) {
            tagsStringResponse += userTag.getText() + "|";
        }

        return tagsStringResponse.substring(0, tagsStringResponse.length() - 1);
    }

    @Async
    public void exportArticlePollsToCsv(Long principalId) throws CustomException {
        byte[] exportResult = exportToCsv(new ArrayList<>(getArticlePollCsvItems()), ArticlePollCsv.class, articlePollHeaders);

        User adminUser = userRepository.findById(principalId);

        String subject = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_SUBJECT");
        String textHtml = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_TEXT_HTML").replace("INSERT_USERNAME_HERE", adminUser.getUsername()).replace("ENTITY_TO_EXPORT", "polls");
        String textPlain = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_TEXT_PLAIN").replace("INSERT_USERNAME_HERE", adminUser.getUsername());

        mailService.sendCSVAttachment(adminUser.getEmail(), subject, textPlain, textHtml, exportResult, "articlePollsExport");
    }

    @Async
    public void exportPostPollsToCsv(Long principalId) throws CustomException {
        byte[] exportResult = exportToCsv(new ArrayList<>(getPostPollCsvItems()), PostPollCsv.class, postPollHeaders);

        User adminUser = userRepository.findById(principalId);

        String subject = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_SUBJECT");
        String textHtml = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_TEXT_HTML").replace("INSERT_USERNAME_HERE", adminUser.getUsername()).replace("ENTITY_TO_EXPORT", "polls");
        String textPlain = ResourceBundle.getBundle("i18n.mail", Locale.ENGLISH).getString("EXPORT_TEXT_PLAIN").replace("INSERT_USERNAME_HERE", adminUser.getUsername());

        mailService.sendCSVAttachment(adminUser.getEmail(), subject, textPlain, textHtml, exportResult, "postPollsExport");
    }

    private List<ArticlePollCsv> getArticlePollCsvItems() {
        List<Content> contentList = contentRepository.findAllByPollIsNotNullAndTypeIs(ContentType.ARTICLE_REGULAR);

        List<ArticlePollCsv> result = new ArrayList<>();

        for (Content content : contentList) {
            ArticlePollCsv articlePollCsv = new ArticlePollCsv();
            articlePollCsv.setHeadline(content.getHeadline());
            articlePollCsv.setQuestion(content.getPoll().getQuestion());
            articlePollCsv.setFirstChoiceText(content.getPoll().getFirstChoice());
            articlePollCsv.setFirstChoiceVotes(content.getPoll().getRFirstAmount().toString());
            articlePollCsv.setSecondChoiceText(content.getPoll().getSecondChoice());
            articlePollCsv.setSecondChoiceVotes(content.getPoll().getRSecondAmount().toString());
            articlePollCsv.setThirdChoiceText(content.getPoll().getThirdChoice());
            articlePollCsv.setThirdChoiceVotes(content.getPoll().getRThirdAmount().toString());
            articlePollCsv.setTimestamp(content.getCreateTime().format(formatter));

            result.add(articlePollCsv);
        }

        return result;
    }

    private List<PostPollCsv> getPostPollCsvItems() {
        List<Content> contentList = contentRepository.findAllByPollIsNotNullAndTypeIs(ContentType.POST);

        List<PostPollCsv> result = new ArrayList<>();

        for (Content content : contentList) {
            PostPollCsv postPollCsv = new PostPollCsv();
            postPollCsv.setHeadline(content.getHeadline());
            postPollCsv.setPoster(content.getUser().getUsername());
            postPollCsv.setQuestion(content.getPoll().getQuestion());
            postPollCsv.setFirstChoiceText(content.getPoll().getFirstChoice());
            postPollCsv.setFirstChoiceVotes(content.getPoll().getRFirstAmount().toString());
            postPollCsv.setSecondChoiceText(content.getPoll().getSecondChoice());
            postPollCsv.setSecondChoiceVotes(content.getPoll().getRSecondAmount().toString());
            postPollCsv.setThirdChoiceText(content.getPoll().getThirdChoice());
            postPollCsv.setThirdChoiceVotes(content.getPoll().getRThirdAmount().toString());
            postPollCsv.setTimestamp(content.getCreateTime().format(formatter));

            result.add(postPollCsv);
        }

        return result;
    }

}
