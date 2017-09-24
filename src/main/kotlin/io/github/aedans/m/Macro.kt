package io.github.aedans.m

import io.github.aedans.cons.Nil
import io.github.aedans.cons.get

/**
 * Created by Aedan Smith.
 */

class Macro(function: (Any) -> Any) : (Expression) -> Expression by function {
    override fun toString() = "m.Macro"
}

fun Iterator<Expression>.expandMacros(env: RuntimeEnvironment) = asSequence()
        .map { it.expand(env) }
        .iterator()

fun Expression.expand(env: RuntimeEnvironment): Expression = takeIfInstance<SExpression>()
        ?.map { it.expand(env) }
        ?.toConsList()
        ?.takeIf { it !== Nil }
        ?.let { expr ->
            expr[0].takeIfInstance<IdentifierExpression>()
                    ?.let { env.getVar(it.name) }
                    ?.takeIfInstance<Macro>()
                    ?.let { macro -> macro(expr.cdr) }
                    ?.expand(env)
                    ?: expr
        }
        ?: this