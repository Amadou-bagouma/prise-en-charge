package com.mycompany.myapp.config;

import java.security.Principal;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import tech.jhipster.config.JHipsterProperties;

/**
 * Le canal par lequel un avis atteint son destinataire pendant qu'il travaille.
 *
 * <p>Jusqu'ici un avis n'apparaissait qu'au prochain changement d'ecran : un dossier retourne
 * pour correction attendait que son auteur pense a regarder sa boite, et le delai de validite
 * de quatorze jours courait pendant ce temps.
 *
 * <p>Le canal ne sert qu'a prevenir. Il ne porte aucune decision et n'accepte rien de
 * l'exterieur : le client ne peut qu'ecouter ce qui lui est adresse. Un avis manque - connexion
 * coupee, onglet ferme - ne se perd pas pour autant : il reste en base et la cloche le montre
 * au prochain chargement.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    /** Le point de rendez-vous, sous {@code /api} comme le reste : une seule regle de securite. */
    public static final String POINT_ENTREE = "/api/avis";

    /** La file personnelle d'un destinataire. Prefixee par {@code /user} a l'abonnement. */
    public static final String FILE_AVIS = "/queue/avis";

    private final JHipsterProperties jHipsterProperties;

    public WebsocketConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Un courtier en memoire suffit : les avis sont de simples signaux, et leur perte n'a
        // pas de consequence - la base reste la source, le canal ne fait que prevenir plus tot.
        config.enableSimpleBroker("/queue", "/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] originesAutorisees =
            jHipsterProperties.getCors().getAllowedOrigins() == null
                ? new String[] { "*" }
                : jHipsterProperties
                      .getCors()
                      .getAllowedOrigins()
                      .toArray(new String[0]);
        // En direct : c'est ce qu'utilise le client.
        registry
            .addEndpoint(POINT_ENTREE)
            .setHandshakeHandler(new PoigneeDeMainAuthentifiee())
            .setAllowedOriginPatterns(originesAutorisees);
        // Et en repli, pour un reseau qui couperait les connexions persistantes.
        registry
            .addEndpoint(POINT_ENTREE)
            .setHandshakeHandler(new PoigneeDeMainAuthentifiee())
            .setAllowedOriginPatterns(originesAutorisees)
            .withSockJS();
    }

    /**
     * Rattache la session au compte deja authentifie par le filtre JWT.
     *
     * <p>Sans cela la session serait anonyme, et {@code convertAndSendToUser} n'aurait aucun
     * destinataire a qui remettre l'avis : il partirait dans le vide, sans erreur.
     */
    private static class PoigneeDeMainAuthentifiee extends DefaultHandshakeHandler {

        @Override
        protected Principal determineUser(ServerHttpRequest request, WebSocketHandler handler, Map<String, Object> attributs) {
            return request.getPrincipal();
        }
    }
}
