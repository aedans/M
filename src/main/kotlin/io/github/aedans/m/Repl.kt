package io.github.aedans.m

import java.io.InputStream
import java.io.PrintStream

/**
 * Created by Aedan Smith.
 */

object Repl : Runnable {
    override fun run() {
        run(getDefaultRuntimeEnvironment().apply {
            setVar("exit", mFunction { _: Any -> System.exit(0) })
        })
    }

    private tailrec fun run(environment: RuntimeEnvironment,
                            evaluableIterator: Iterator<Evaluable> = ReplStream(System.`in`, System.out)
                                    .toEvaluable(environment)) {
        val success = try {
            evaluableIterator.next().eval(environment.memory)
                    .takeIf { it != Unit }
                    ?.also { println(it) }
            true
        } catch (t: Throwable) {
            t.printStackTrace(System.out)
            false
        }
        if (success) {
            run(environment, evaluableIterator)
        } else {
            run(environment)
        }
    }
}

class ReplStream(val inputStream: InputStream, val printStream: PrintStream) : Iterator<Char> {
    override fun hasNext() = true
    override fun next(): Char {
        if (inputStream.available() == 0)
            printStream.print(">")
        return inputStream.read().toChar()
    }
}
