package com.bloxico.userservice.config.oauth2;

import com.bloxico.userservice.entities.oauth.OauthClientDetails;
import com.bloxico.userservice.repository.oauth.ClientDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class CoinClientDetailsService implements ClientDetailsService {

    private ClientDetailsRepository clientDetailsRepository;

    @Autowired
    public CoinClientDetailsService(ClientDetailsRepository clientDetailsRepository) {
        this.clientDetailsRepository = clientDetailsRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        log.debug("Loading client by client id - start for client ID: {}", clientId);

        Optional<OauthClientDetails> op = clientDetailsRepository.findByClientId(clientId);

        OauthClientDetails clientDetailsEntity = op.orElseThrow(RuntimeException::new);

        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId(clientDetailsEntity.getClientId());
        clientDetails.setClientSecret(clientDetailsEntity.getClient_secret());

        //scopes
        String[] scopeArray = clientDetailsEntity.getScope().split(",");
        Set<String> scopes = new HashSet<>(Arrays.asList(scopeArray));

        clientDetails.setScope(scopes);

        //grant types
        String[] grantTypeArray = clientDetailsEntity.getAuthorized_grant_types().split(",");
        Set<String> grantTypes = new HashSet<>(Arrays.asList(grantTypeArray));

        clientDetails.setAuthorizedGrantTypes(grantTypes);

        //authorities
        String[] authoritiesArray = clientDetailsEntity.getAuthorities().split(",");

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        for( String authority : Arrays.asList(authoritiesArray)) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        clientDetails.setAuthorities(authorities);


        clientDetails.setAccessTokenValiditySeconds(clientDetailsEntity.getAccess_token_validity());

        log.debug("Loading client by client id - end for client ID: {}", clientId);
        return clientDetails;
    }
}
