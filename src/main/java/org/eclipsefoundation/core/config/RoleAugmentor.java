package org.eclipsefoundation.core.config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;

/**
 * Custom override for production that can be enabled to set user roles to
 * include the role set in the property, defaulting to admin access.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class RoleAugmentor implements SecurityIdentityAugmentor {

	// properties that allow this functionality to be configured
	@ConfigProperty(name = "eclipse.oauth.override", defaultValue = "false")
	boolean overrideRole;
	@ConfigProperty(name = "eclipse.oauth.override.role", defaultValue = "marketplace_admin_access")
	String overrideRoleName;

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public CompletionStage<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
		// create a future to contain the original/updated role
		CompletableFuture<SecurityIdentity> cs = new CompletableFuture<>();
		if (overrideRole) {
			// create a new builder and copy principal, attributes, credentials and roles
			// from the original
			QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder()
					.setPrincipal(identity.getPrincipal()).addAttributes(identity.getAttributes())
					.addCredentials(identity.getCredentials()).addRoles(identity.getRoles());

			// add custom role source here
			builder.addRole(overrideRoleName);
			// put the updated role in the future
			cs.complete(builder.build());
		} else {
			// put the unmodified identity in the future
			cs.complete(identity);
		}
		return cs;
	}
}