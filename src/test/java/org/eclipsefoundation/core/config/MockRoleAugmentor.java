package org.eclipsefoundation.core.config;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;

/**
 * Custom override for test classes that ignores current login state and sets
 * all users as admin always. This should only ever be used in testing.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class MockRoleAugmentor implements SecurityIdentityAugmentor {

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
		// create a new builder and copy principal, attributes, credentials and roles
		// from the original
		QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder()
				.setPrincipal(identity.getPrincipal()).addAttributes(identity.getAttributes())
				.addCredentials(identity.getCredentials()).addRoles(identity.getRoles());

		// add custom role source here
		builder.addRole("marketplace_admin_access");
		return context.runBlocking(builder::build);
	}
}