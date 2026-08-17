package carmine.compiler.passes;

import carmine.compiler.helpers.AstVisualizer;
import carmine.compiler.helpers.CarmineLogger;
import carmine.compiler.helpers.LogLevel;
import carmine.compiler.helpers.VisualizationMode;
import carmine.compiler.structures.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import kotlin.system.exitProcess

val variableEnvironment = Environment() // used for the first parse where we evaluate all arithmetic expressions
val moduleEnvironment = Environment() // used to save modules
var hadError = false

private fun run(code : String) {
    variableEnvironment.put("print", {
        @Override
        fun arity() : Int {
            return 1
        }

        @Override
        fun call( arguments : List<Any>) : Any? {
            return null
        }
    })

    moduleEnvironment.put("and",  {
        @Override
        fun arity() : Int {
            return 2
        }

        @Override
        fun call( arguments : List<Any>) : Any? {
            return null
        }
    })

    moduleEnvironment.put("or", {
        @Override
        fun arity() : Int {
            return 2
        }

        @Override
        fun call( arguments : List<Any>) : Any? {
            return null
        }
    })

    moduleEnvironment.put("import", {
        @Override
        fun arity() : Int {
            return 1
        }

        @Override
        fun call(arguments : List<Any>) : Any?
        {
            return null
        }
    })

    moduleEnvironment.put("export",  {
        @Override
        fun arity() : Int {
            return 1
        }

        @Override
        fun call(arguments : List<Any>) : Any?
        {
            return null
        }
    })

    val scanner = Scanner(code)
    val tokens = scanner.scanTokens()

    for (token in tokens)
        println(token)

    val parser = Parser(tokens)

    val statements = parser.parse()
    /*
    for (Stmt statement : statements) {
        //statement.print();
        //statement.evaluate();
        //System.out.println();
    }
    //Debug.printEnvironments();
     */

    if (!hadError) {
        val visualizer = AstVisualizer()
        //Optimizer optimizer = new Optimizer(statements);

        //optimizer.constantFolding();
        //optimizer.constantPropagation(); // WIP
        //optimizer.constantFolding();
        //optimizer.constantPropagation(); // WIP
        //optimizer.constantFolding();

        //optimizer.loopUnrolling(); // WIP
        //optimizer.deadCodeElimination(); // WIP

        val dotContent = visualizer.visualizeAST(statements, VisualizationMode.PRETTY_PRINT)
        println(dotContent)
    }
    /*
    try {
        Files.writeString(
                Path.of("graph.dot"),
                dotContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
        System.out.println("Saved to graph.dot");
    } catch (IOException e) {
        e.printStackTrace();
    }
    */
}

private fun runFile(path : String) {
    try {
        val bytes = Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))
    } catch (_ : IOException) {
        CarmineLogger.log("Something went wrong when trying to read program.", LogLevel.ERROR)
    }
}

fun main(args : Array<String>) {
    if (args.count() != 1) {
        exitProcess(2)
    }
    else
    {
        runFile(args[0])
    }
}