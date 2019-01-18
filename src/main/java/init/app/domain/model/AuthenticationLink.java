package init.app.domain.model;

import init.app.domain.model.enumeration.LinkType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "authentication_link")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticationLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LinkType type;

    @NotNull
    @Size(max = 256)
    @Column(name = "url")
    private String url;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "expiry_time")
    private ZonedDateTime expiryTime;

}
