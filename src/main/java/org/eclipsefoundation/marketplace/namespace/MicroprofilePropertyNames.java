package org.eclipsefoundation.marketplace.namespace;

/**
 * Contains Microprofile property names used by this application.
 * 
 * @author Martin Lowe
 *
 */
public class MicroprofilePropertyNames {
	public static final String PROMO_WEIGHT_DEFAULT = "eclipse.promotion.weighting.default";
	public static final String PROMO_SERVE_COUNT = "eclipse.promotion.serve-count";
	public static final String CACHE_TTL_MAX_SECONDS = "cache.ttl.write.seconds";
	public static final String CACHE_SIZE_MAX = "cache.max.size";
	public static final String MONGODB_DB_NAME = "mongodb.database";
	public static final String MONGODB_RETURN_LIMIT = "mongodb.default.limit";
	public static final String MONGODB_RETURN_LIMIT_MAX = "mongodb.default.limit.max";
	public static final String MONGODB_MAINTENANCE_FLAG = "mongodb.maintenance";
	
	private MicroprofilePropertyNames() {
	}
}
