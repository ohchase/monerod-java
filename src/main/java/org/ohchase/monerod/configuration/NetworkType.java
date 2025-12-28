package org.ohchase.monerod.configuration;

/**
 * Enumeration of different network types.
 */
public enum NetworkType {
    /**
     * Test Network.
     * Used for testing and development purposes.
     */
    TEST_NET,
    /**
     * Staging Network. Used for pre-production testing.
     * This network simulates the main network environment.
     * This network is suitable for sandboxing external application development.
     */
    STAGE_NET,
    /**
     * Main Network.
     * The primary live network for real transactions.
     */
    MAIN_NET
}
