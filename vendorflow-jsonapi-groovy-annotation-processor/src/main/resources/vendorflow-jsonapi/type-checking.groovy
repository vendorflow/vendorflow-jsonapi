import static java.lang.reflect.Modifier.*
import static org.codehaus.groovy.ast.tools.GeneralUtils.param

boolean isJsonApiResource(cn) {
    cn.isDerivedFrom(classNodeFor('co.vendorflow.oss.jsonapi.model.resource.JsonApiResource'))
        || cn.getAnnotations(classNodeFor('co.vendorflow.oss.jsonapi.model.resource.JsonApiType'))
}


unresolvedProperty { expr ->
    if (
            'TYPE' != expr.propertyAsString
            || (expr.objectExpression !instanceof org.codehaus.groovy.ast.expr.ClassExpression)
            || !(isJsonApiResource(expr.objectExpression.type))
    ) {
        return
    }

    expr.objectExpression.type.with { rcn ->  // work around spurious access errors
        if (! rcn.getField('TYPE')) {
            rcn.addField('TYPE', PUBLIC | STATIC | FINAL, classNodeFor(String), null)
        }
    }

    storeType(expr, classNodeFor(String))
    handled = true
}


methodNotFound { receiver, name, argList, argTypes, call ->
    if (! isClassClassNodeWrappingConcreteType(receiver)) {
        return emptyList()
    }

    def staticReceiver = receiver.genericsTypes[0].type

    // argTypes will be null for a method pointer
    if ('id' ==  name && (argTypes == null || argTypes.size() == 1) && isJsonApiResource(staticReceiver)) {
        handled = true
        return newMethod('id', classNodeFor('co.vendorflow.oss.jsonapi.model.resource.JsonApiResourceId')).tap {
            modifiers |= STATIC
            parameters = [param(classNodeFor(Object), 'id')]
        }
    }
}
