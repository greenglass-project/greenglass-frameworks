package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
 */
@NonNullApi
public class LibrariesForLibsInPluginsBlock extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final CommonsLibraryAccessors laccForCommonsLibraryAccessors = new CommonsLibraryAccessors(owner);
    private final GoogleLibraryAccessors laccForGoogleLibraryAccessors = new GoogleLibraryAccessors(owner);
    private final JacksonLibraryAccessors laccForJacksonLibraryAccessors = new JacksonLibraryAccessors(owner);
    private final KotlinLibraryAccessors laccForKotlinLibraryAccessors = new KotlinLibraryAccessors(owner);
    private final Neo4jLibraryAccessors laccForNeo4jLibraryAccessors = new Neo4jLibraryAccessors(owner);
    private final OshiLibraryAccessors laccForOshiLibraryAccessors = new OshiLibraryAccessors(owner);
    private final Pi4jLibraryAccessors laccForPi4jLibraryAccessors = new Pi4jLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibsInPluginsBlock(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

        /**
         * Creates a dependency provider for hivemq (com.hivemq:hivemq-mqtt-client)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getHivemq() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("hivemq");
    }

        /**
         * Creates a dependency provider for influxdb (com.influxdb:influxdb-client-kotlin)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getInfluxdb() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("influxdb");
    }

        /**
         * Creates a dependency provider for javalin (io.javalin:javalin)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getJavalin() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("javalin");
    }

        /**
         * Creates a dependency provider for jpy (org.jpyconsortium:jpy)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getJpy() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("jpy");
    }

        /**
         * Creates a dependency provider for klogging (io.klogging:klogging-jvm)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getKlogging() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("klogging");
    }

        /**
         * Creates a dependency provider for ksp (com.google.devtools.ksp:symbol-processing-api)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getKsp() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("ksp");
    }

        /**
         * Creates a dependency provider for nats (io.nats:jnats)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getNats() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("nats");
    }

        /**
         * Creates a dependency provider for settings (com.russhwolf:multiplatform-settings-no-arg)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getSettings() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("settings");
    }

        /**
         * Creates a dependency provider for slf4j (org.slf4j:slf4j-nop)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getSlf4j() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("slf4j");
    }

        /**
         * Creates a dependency provider for tahu (org.eclipse.tahu:tahu-core)
         * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
     * @deprecated Will be removed in Gradle 9.0.
         */
    @Deprecated
        public Provider<MinimalExternalModuleDependency> getTahu() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return create("tahu");
    }

    /**
     * Returns the group of libraries at commons
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public CommonsLibraryAccessors getCommons() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForCommonsLibraryAccessors;
    }

    /**
     * Returns the group of libraries at google
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public GoogleLibraryAccessors getGoogle() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForGoogleLibraryAccessors;
    }

    /**
     * Returns the group of libraries at jackson
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public JacksonLibraryAccessors getJackson() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForJacksonLibraryAccessors;
    }

    /**
     * Returns the group of libraries at kotlin
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public KotlinLibraryAccessors getKotlin() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForKotlinLibraryAccessors;
    }

    /**
     * Returns the group of libraries at neo4j
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public Neo4jLibraryAccessors getNeo4j() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForNeo4jLibraryAccessors;
    }

    /**
     * Returns the group of libraries at oshi
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public OshiLibraryAccessors getOshi() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForOshiLibraryAccessors;
    }

    /**
     * Returns the group of libraries at pi4j
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public Pi4jLibraryAccessors getPi4j() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return laccForPi4jLibraryAccessors;
    }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Returns the group of bundles at bundles
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public BundleAccessors getBundles() {
        org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
        return baccForBundleAccessors;
    }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class CommonsLibraryAccessors extends SubDependencyFactory {

        public CommonsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for lang3 (org.apache.commons:commons-lang3)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getLang3() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("commons.lang3");
        }

            /**
             * Creates a dependency provider for math3 (org.apache.commons:commons-math3)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getMath3() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("commons.math3");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class GoogleLibraryAccessors extends SubDependencyFactory {

        public GoogleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for guava (com.google.guava:guava)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getGuava() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("google.guava");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class JacksonLibraryAccessors extends SubDependencyFactory {

        public JacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (com.fasterxml.jackson.core:jackson-databind)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getCore() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("jackson.core");
        }

            /**
             * Creates a dependency provider for jsr310 (com.fasterxml.jackson.datatype:jackson-datatype-jsr310)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getJsr310() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("jackson.jsr310");
        }

            /**
             * Creates a dependency provider for kotlin (com.fasterxml.jackson.module:jackson-module-kotlin)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getKotlin() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("jackson.kotlin");
        }

            /**
             * Creates a dependency provider for yaml (com.fasterxml.jackson.dataformat:jackson-dataformat-yaml)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getYaml() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("jackson.yaml");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class KotlinLibraryAccessors extends SubDependencyFactory {
        private final KotlinPoetLibraryAccessors laccForKotlinPoetLibraryAccessors = new KotlinPoetLibraryAccessors(owner);
        private final KotlinSerializationLibraryAccessors laccForKotlinSerializationLibraryAccessors = new KotlinSerializationLibraryAccessors(owner);

        public KotlinLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for coroutines (org.jetbrains.kotlinx:kotlinx-coroutines-core)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getCoroutines() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.coroutines");
        }

            /**
             * Creates a dependency provider for datetime (org.jetbrains.kotlinx:kotlinx-datetime-jvm)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getDatetime() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.datetime");
        }

            /**
             * Creates a dependency provider for logging (io.github.oshai:kotlin-logging-jvm)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getLogging() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.logging");
        }

            /**
             * Creates a dependency provider for reflect (org.jetbrains.kotlin:kotlin-reflect)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getReflect() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.reflect");
        }

            /**
             * Creates a dependency provider for stdlib (org.jetbrains.kotlin:kotlin-stdlib)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getStdlib() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.stdlib");
        }

        /**
         * Returns the group of libraries at kotlin.poet
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public KotlinPoetLibraryAccessors getPoet() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForKotlinPoetLibraryAccessors;
        }

        /**
         * Returns the group of libraries at kotlin.serialization
         * @deprecated Will be removed in Gradle 9.0.
         */
        @Deprecated
        public KotlinSerializationLibraryAccessors getSerialization() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
            return laccForKotlinSerializationLibraryAccessors;
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class KotlinPoetLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public KotlinPoetLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for poet (com.squareup:javapoet)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> asProvider() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.poet");
        }

            /**
             * Creates a dependency provider for ksp (com.squareup:kotlinpoet-ksp)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getKsp() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.poet.ksp");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class KotlinSerializationLibraryAccessors extends SubDependencyFactory {

        public KotlinSerializationLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for json (org.jetbrains.kotlinx:kotlinx-serialization-json)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getJson() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.serialization.json");
        }

            /**
             * Creates a dependency provider for yaml (com.charleskorn.kaml:kaml)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getYaml() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("kotlin.serialization.yaml");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class Neo4jLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public Neo4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for neo4j (org.neo4j:neo4j)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> asProvider() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("neo4j");
        }

            /**
             * Creates a dependency provider for bolt (org.neo4j:neo4j-bolt)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getBolt() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("neo4j.bolt");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class OshiLibraryAccessors extends SubDependencyFactory {

        public OshiLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (com.github.oshi:oshi-core)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getCore() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("oshi.core");
        }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class Pi4jLibraryAccessors extends SubDependencyFactory {

        public Pi4jLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (com.pi4j:pi4j-core)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getCore() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("pi4j.core");
        }

            /**
             * Creates a dependency provider for linuxfs (com.pi4j:pi4j-plugin-linuxfs)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getLinuxfs() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("pi4j.linuxfs");
        }

            /**
             * Creates a dependency provider for pi (com.pi4j:pi4j-plugin-raspberrypi)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getPi() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("pi4j.pi");
        }

            /**
             * Creates a dependency provider for pigpio (com.pi4j:pi4j-plugin-pigpio)
             * This dependency was declared in catalog io.greenglass:version-catalog:0.0.2
         * @deprecated Will be removed in Gradle 9.0.
             */
        @Deprecated
            public Provider<MinimalExternalModuleDependency> getPigpio() {
            org.gradle.internal.deprecation.DeprecationLogger.deprecateBehaviour("Accessing libraries or bundles from version catalogs in the plugins block.").withAdvice("Only use versions or plugins from catalogs in the plugins block.").willBeRemovedInGradle9().withUpgradeGuideSection(8, "kotlin_dsl_deprecated_catalogs_plugins_block").nagUser();
                return create("pi4j.pigpio");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final CommonsVersionAccessors vaccForCommonsVersionAccessors = new CommonsVersionAccessors(providers, config);
        private final DokkaVersionAccessors vaccForDokkaVersionAccessors = new DokkaVersionAccessors(providers, config);
        private final GuavaVersionAccessors vaccForGuavaVersionAccessors = new GuavaVersionAccessors(providers, config);
        private final HivemqVersionAccessors vaccForHivemqVersionAccessors = new HivemqVersionAccessors(providers, config);
        private final InfluxdbVersionAccessors vaccForInfluxdbVersionAccessors = new InfluxdbVersionAccessors(providers, config);
        private final JacksonVersionAccessors vaccForJacksonVersionAccessors = new JacksonVersionAccessors(providers, config);
        private final JavalinVersionAccessors vaccForJavalinVersionAccessors = new JavalinVersionAccessors(providers, config);
        private final JibVersionAccessors vaccForJibVersionAccessors = new JibVersionAccessors(providers, config);
        private final JpyVersionAccessors vaccForJpyVersionAccessors = new JpyVersionAccessors(providers, config);
        private final KloggingVersionAccessors vaccForKloggingVersionAccessors = new KloggingVersionAccessors(providers, config);
        private final KotlinVersionAccessors vaccForKotlinVersionAccessors = new KotlinVersionAccessors(providers, config);
        private final KspVersionAccessors vaccForKspVersionAccessors = new KspVersionAccessors(providers, config);
        private final NatsVersionAccessors vaccForNatsVersionAccessors = new NatsVersionAccessors(providers, config);
        private final Neo4jVersionAccessors vaccForNeo4jVersionAccessors = new Neo4jVersionAccessors(providers, config);
        private final OshiVersionAccessors vaccForOshiVersionAccessors = new OshiVersionAccessors(providers, config);
        private final Pi4jVersionAccessors vaccForPi4jVersionAccessors = new Pi4jVersionAccessors(providers, config);
        private final SettingsVersionAccessors vaccForSettingsVersionAccessors = new SettingsVersionAccessors(providers, config);
        private final Slf4jVersionAccessors vaccForSlf4jVersionAccessors = new Slf4jVersionAccessors(providers, config);
        private final TahuVersionAccessors vaccForTahuVersionAccessors = new TahuVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.commons
         */
        public CommonsVersionAccessors getCommons() {
            return vaccForCommonsVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.dokka
         */
        public DokkaVersionAccessors getDokka() {
            return vaccForDokkaVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.guava
         */
        public GuavaVersionAccessors getGuava() {
            return vaccForGuavaVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.hivemq
         */
        public HivemqVersionAccessors getHivemq() {
            return vaccForHivemqVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.influxdb
         */
        public InfluxdbVersionAccessors getInfluxdb() {
            return vaccForInfluxdbVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.jackson
         */
        public JacksonVersionAccessors getJackson() {
            return vaccForJacksonVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.javalin
         */
        public JavalinVersionAccessors getJavalin() {
            return vaccForJavalinVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.jib
         */
        public JibVersionAccessors getJib() {
            return vaccForJibVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.jpy
         */
        public JpyVersionAccessors getJpy() {
            return vaccForJpyVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.klogging
         */
        public KloggingVersionAccessors getKlogging() {
            return vaccForKloggingVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.kotlin
         */
        public KotlinVersionAccessors getKotlin() {
            return vaccForKotlinVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.ksp
         */
        public KspVersionAccessors getKsp() {
            return vaccForKspVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.nats
         */
        public NatsVersionAccessors getNats() {
            return vaccForNatsVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.neo4j
         */
        public Neo4jVersionAccessors getNeo4j() {
            return vaccForNeo4jVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.oshi
         */
        public OshiVersionAccessors getOshi() {
            return vaccForOshiVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.pi4j
         */
        public Pi4jVersionAccessors getPi4j() {
            return vaccForPi4jVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.settings
         */
        public SettingsVersionAccessors getSettings() {
            return vaccForSettingsVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.slf4j
         */
        public Slf4jVersionAccessors getSlf4j() {
            return vaccForSlf4jVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.tahu
         */
        public TahuVersionAccessors getTahu() {
            return vaccForTahuVersionAccessors;
        }

    }

    public static class CommonsVersionAccessors extends VersionFactory  {

        private final CommonsLang3VersionAccessors vaccForCommonsLang3VersionAccessors = new CommonsLang3VersionAccessors(providers, config);
        private final CommonsMath3VersionAccessors vaccForCommonsMath3VersionAccessors = new CommonsMath3VersionAccessors(providers, config);
        public CommonsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.commons.lang3
         */
        public CommonsLang3VersionAccessors getLang3() {
            return vaccForCommonsLang3VersionAccessors;
        }

        /**
         * Returns the group of versions at versions.commons.math3
         */
        public CommonsMath3VersionAccessors getMath3() {
            return vaccForCommonsMath3VersionAccessors;
        }

    }

    public static class CommonsLang3VersionAccessors extends VersionFactory  {

        public CommonsLang3VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: commons.lang3.version (3.14.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("commons.lang3.version"); }

    }

    public static class CommonsMath3VersionAccessors extends VersionFactory  {

        public CommonsMath3VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: commons.math3.version (3.6.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("commons.math3.version"); }

    }

    public static class DokkaVersionAccessors extends VersionFactory  {

        public DokkaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: dokka.version (1.9.10)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("dokka.version"); }

    }

    public static class GuavaVersionAccessors extends VersionFactory  {

        public GuavaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: guava.version (32.0.0-android)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("guava.version"); }

    }

    public static class HivemqVersionAccessors extends VersionFactory  {

        public HivemqVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: hivemq.version (1.3.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("hivemq.version"); }

    }

    public static class InfluxdbVersionAccessors extends VersionFactory  {

        public InfluxdbVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: influxdb.version (6.12.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("influxdb.version"); }

    }

    public static class JacksonVersionAccessors extends VersionFactory  {

        public JacksonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: jackson.version (2.14.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("jackson.version"); }

    }

    public static class JavalinVersionAccessors extends VersionFactory  {

        public JavalinVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: javalin.version (6.1.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("javalin.version"); }

    }

    public static class JibVersionAccessors extends VersionFactory  {

        public JibVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: jib.version (3.4.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("jib.version"); }

    }

    public static class JpyVersionAccessors extends VersionFactory  {

        public JpyVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: jpy.version (0.15.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("jpy.version"); }

    }

    public static class KloggingVersionAccessors extends VersionFactory  {

        public KloggingVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: klogging.version (0.5.11)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("klogging.version"); }

    }

    public static class KotlinVersionAccessors extends VersionFactory  {

        private final KotlinCoroutinesVersionAccessors vaccForKotlinCoroutinesVersionAccessors = new KotlinCoroutinesVersionAccessors(providers, config);
        private final KotlinDateVersionAccessors vaccForKotlinDateVersionAccessors = new KotlinDateVersionAccessors(providers, config);
        private final KotlinLoggingVersionAccessors vaccForKotlinLoggingVersionAccessors = new KotlinLoggingVersionAccessors(providers, config);
        private final KotlinPoetVersionAccessors vaccForKotlinPoetVersionAccessors = new KotlinPoetVersionAccessors(providers, config);
        private final KotlinSerializationVersionAccessors vaccForKotlinSerializationVersionAccessors = new KotlinSerializationVersionAccessors(providers, config);
        public KotlinVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: kotlin.version (1.9.23)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("kotlin.version"); }

        /**
         * Returns the group of versions at versions.kotlin.coroutines
         */
        public KotlinCoroutinesVersionAccessors getCoroutines() {
            return vaccForKotlinCoroutinesVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.kotlin.date
         */
        public KotlinDateVersionAccessors getDate() {
            return vaccForKotlinDateVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.kotlin.logging
         */
        public KotlinLoggingVersionAccessors getLogging() {
            return vaccForKotlinLoggingVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.kotlin.poet
         */
        public KotlinPoetVersionAccessors getPoet() {
            return vaccForKotlinPoetVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.kotlin.serialization
         */
        public KotlinSerializationVersionAccessors getSerialization() {
            return vaccForKotlinSerializationVersionAccessors;
        }

    }

    public static class KotlinCoroutinesVersionAccessors extends VersionFactory  {

        public KotlinCoroutinesVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: kotlin.coroutines.version (1.7.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("kotlin.coroutines.version"); }

    }

    public static class KotlinDateVersionAccessors extends VersionFactory  {

        private final KotlinDateTimeVersionAccessors vaccForKotlinDateTimeVersionAccessors = new KotlinDateTimeVersionAccessors(providers, config);
        public KotlinDateVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.kotlin.date.time
         */
        public KotlinDateTimeVersionAccessors getTime() {
            return vaccForKotlinDateTimeVersionAccessors;
        }

    }

    public static class KotlinDateTimeVersionAccessors extends VersionFactory  {

        public KotlinDateTimeVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: kotlin.date.time.version (0.5.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("kotlin.date.time.version"); }

    }

    public static class KotlinLoggingVersionAccessors extends VersionFactory  {

        public KotlinLoggingVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: kotlin.logging.version (6.0.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("kotlin.logging.version"); }

    }

    public static class KotlinPoetVersionAccessors extends VersionFactory  {

        public KotlinPoetVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: kotlin.poet.version (1.16.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("kotlin.poet.version"); }

    }

    public static class KotlinSerializationVersionAccessors extends VersionFactory  {

        private final KotlinSerializationJsonVersionAccessors vaccForKotlinSerializationJsonVersionAccessors = new KotlinSerializationJsonVersionAccessors(providers, config);
        private final KotlinSerializationYamlVersionAccessors vaccForKotlinSerializationYamlVersionAccessors = new KotlinSerializationYamlVersionAccessors(providers, config);
        public KotlinSerializationVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of versions at versions.kotlin.serialization.json
         */
        public KotlinSerializationJsonVersionAccessors getJson() {
            return vaccForKotlinSerializationJsonVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.kotlin.serialization.yaml
         */
        public KotlinSerializationYamlVersionAccessors getYaml() {
            return vaccForKotlinSerializationYamlVersionAccessors;
        }

    }

    public static class KotlinSerializationJsonVersionAccessors extends VersionFactory  {

        public KotlinSerializationJsonVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: kotlin.serialization.json.version (1.6.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("kotlin.serialization.json.version"); }

    }

    public static class KotlinSerializationYamlVersionAccessors extends VersionFactory  {

        public KotlinSerializationYamlVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: kotlin.serialization.yaml.version (0.58.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("kotlin.serialization.yaml.version"); }

    }

    public static class KspVersionAccessors extends VersionFactory  {

        public KspVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: ksp.version (1.9.23-1.0.20)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("ksp.version"); }

    }

    public static class NatsVersionAccessors extends VersionFactory  {

        public NatsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: nats.version (2.17.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("nats.version"); }

    }

    public static class Neo4jVersionAccessors extends VersionFactory  {

        public Neo4jVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: neo4j.version (5.19.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("neo4j.version"); }

    }

    public static class OshiVersionAccessors extends VersionFactory  {

        public OshiVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: oshi.version (6.5.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("oshi.version"); }

    }

    public static class Pi4jVersionAccessors extends VersionFactory  {

        public Pi4jVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: pi4j.version (2.3.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("pi4j.version"); }

    }

    public static class SettingsVersionAccessors extends VersionFactory  {

        public SettingsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: settings.version (1.1.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("settings.version"); }

    }

    public static class Slf4jVersionAccessors extends VersionFactory  {

        public Slf4jVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: slf4j.version (2.0.13)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("slf4j.version"); }

    }

    public static class TahuVersionAccessors extends VersionFactory  {

        public TahuVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: tahu.version (1.0.7)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<String> getVersion() { return getVersion("tahu.version"); }

    }

    /**
     * @deprecated Will be removed in Gradle 9.0.
     */
    @Deprecated
    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {
        private final KotlinPluginAccessors paccForKotlinPluginAccessors = new KotlinPluginAccessors(providers, config);

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for dokka to the plugin id 'org.jetbrains.dokka'
             * This plugin was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<PluginDependency> getDokka() { return createPlugin("dokka"); }

            /**
             * Creates a plugin provider for jib to the plugin id 'com.google.cloud.tools.jib'
             * This plugin was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<PluginDependency> getJib() { return createPlugin("jib"); }

            /**
             * Creates a plugin provider for jvm to the plugin id 'org.jetbrains.kotlin.jvm'
             * This plugin was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<PluginDependency> getJvm() { return createPlugin("jvm"); }

            /**
             * Creates a plugin provider for ksp to the plugin id 'com.google.devtools.ksp'
             * This plugin was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<PluginDependency> getKsp() { return createPlugin("ksp"); }

        /**
         * Returns the group of plugins at plugins.kotlin
         */
        public KotlinPluginAccessors getKotlin() {
            return paccForKotlinPluginAccessors;
        }

    }

    public static class KotlinPluginAccessors extends PluginFactory {

        public KotlinPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for kotlin.serialisation to the plugin id 'org.jetbrains.kotlin.plugin.serialization'
             * This plugin was declared in catalog io.greenglass:version-catalog:0.0.2
             */
            public Provider<PluginDependency> getSerialisation() { return createPlugin("kotlin.serialisation"); }

    }

}
