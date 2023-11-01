package org.quarkus.lock.redisson.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import org.quarkus.lock.redisson.LockConfig;
import org.quarkus.lock.redisson.LockInterceptor;
import org.quarkus.lock.redisson.LockManagerDefinition;
import org.quarkus.lock.redisson.LockRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LockProcessor {

    private static final String FEATURE = "lock";

    private static final Logger logger = LoggerFactory.getLogger(LockProcessor.class);

    @BuildStep
    public void feature(BuildProducer<FeatureBuildItem> feature) {
        feature.produce(new FeatureBuildItem(FEATURE));
    }

    @BuildStep
    AnnotationsTransformerBuildItem annotationsTransformer() {
        return new AnnotationsTransformerBuildItem(new LockAnnotationsTransformer());
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    void load(BuildProducer<AdditionalBeanBuildItem> additionalBeans, LockConfig config, LockRecorder recorder) {
        if (config.enabled){
            logger.info("lock distributed lock is turned on");
            additionalBeans.produce(new AdditionalBeanBuildItem(LockManagerDefinition.class));
            AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder();
            builder.addBeanClass(LockInterceptor.class);
            additionalBeans.produce(builder.build());
        }else {
            logger.warn("lock distributed lock is not turned on, you can add [quarkus.lock=true] to enable it");
        }
    }
}
