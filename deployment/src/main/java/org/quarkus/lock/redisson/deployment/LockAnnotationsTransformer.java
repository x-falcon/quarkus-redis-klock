package org.quarkus.lock.redisson.deployment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;

import io.quarkus.arc.processor.AnnotationsTransformer;
import org.quarkus.lock.redisson.LockInfoProviderUtils;
import org.quarkus.lock.redisson.LockInterceptorBinding;

public class LockAnnotationsTransformer implements AnnotationsTransformer {

    @Override
    public boolean appliesTo(Kind kind) {
        return Kind.METHOD == kind;
    }

    @Override
    public void transform(TransformationContext context) {
        MethodInfo method = context.getTarget().asMethod();
        List<AnnotationInstance> interceptorBindings = new ArrayList<>();
        for (AnnotationInstance annotation : method.annotations()) {
            AnnotationTarget target = annotation.target();
            if (target.kind() == Kind.METHOD && DotNames.LOCK.equals(annotation.name())) {
                interceptorBindings.add(createLockBinding(method, annotation, target));
            }
        }
        context.transform().addAll(interceptorBindings).done();
    }

    private AnnotationInstance createLockBinding(MethodInfo method, AnnotationInstance annotation,
                                                 AnnotationTarget target) {
        List<AnnotationValue> parameters = new ArrayList<>();
        findLockKeyParameters(method).ifPresent(parameters::add);
        findLockName(annotation).ifPresent(parameters::add);
        findWaitTimeout(annotation).ifPresent(parameters::add);
        findLeaseTimeout(annotation).ifPresent(parameters::add);
        findLockType(annotation).ifPresent(parameters::add);
        return AnnotationInstance.create(DotName.createSimple(LockInterceptorBinding.class.getName()), target, toArray(parameters));
    }


    private Optional<AnnotationValue> findLockKeyParameters(MethodInfo method) {
        List<AnnotationValue> parameters = new ArrayList<>();
        for (AnnotationInstance annotation : method.annotations()) {
            if (annotation.target().kind() == Kind.METHOD_PARAMETER && DotNames.LOCK_KEY.equals(annotation.name())) {
                AnnotationValue fieldValue = annotation.value(DotNames.LOCK_KEY_VALUE_PARAM);
                String value;
                if (fieldValue != null) {
                    value = annotation.target().asMethodParameter().position() + LockInfoProviderUtils.LOCK_NAME_SEPARATOR + fieldValue.asString();
                } else {
                    value = annotation.target().asMethodParameter().position() + LockInfoProviderUtils.LOCK_NAME_SEPARATOR;
                }
                parameters.add(AnnotationValue.createStringValue("", value));
            }
        }
        if (parameters.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(AnnotationValue.createArrayValue(DotNames.LOCK_KEY_PARAMETER_POSITIONS_PARAM, toArray(parameters)));
    }

    private Optional<AnnotationValue> findLockName(AnnotationInstance annotation) {
        return Optional.ofNullable(annotation.value(DotNames.NAME_PARAM));
    }

    private Optional<AnnotationValue> findWaitTimeout(AnnotationInstance annotation) {
        return Optional.ofNullable(annotation.value(DotNames.WAIT_TIME_PARAM));
    }

    private Optional<AnnotationValue> findLeaseTimeout(AnnotationInstance annotation) {
        return Optional.ofNullable(annotation.value(DotNames.LEASE_TIME_PARAM));
    }

    private Optional<AnnotationValue> findLockType(AnnotationInstance annotation) {
        return Optional.ofNullable(annotation.value(DotNames.LOCK_TYPE_PARAM));
    }

    private AnnotationValue[] toArray(List<AnnotationValue> parameters) {
        return parameters.toArray(new AnnotationValue[0]);
    }
}
