// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlinKapt) apply false
}

tasks.register("generateModuleGraph") {
    group = "reporting"
    description = "Generate module dependency graph in DOT format"

    doLast {
        val interestingConfigs = setOf(
            "api",
            "implementation",
            "compileOnly",
            "runtimeOnly",
            "kapt",
            "ksp",
            "debugImplementation",
            "releaseImplementation",
            "testImplementation",
            "androidTestImplementation"
        )

        val edges = mutableSetOf<Pair<String, String>>()
        val nodes = mutableSetOf<String>()

        rootProject.subprojects.forEach { project ->
            nodes += project.path

            project.configurations
                .filter { it.name in interestingConfigs }
                .forEach { config ->
                    config.dependencies
                        .withType(ProjectDependency::class.java)
                        .forEach { dep ->
                            val from = project.path
                            val to = dep.dependencyProject.path
                            nodes += to
                            edges += from to to
                        }
                }
        }

        val outDir = layout.buildDirectory.dir("reports/module-graph").get().asFile
        outDir.mkdirs()

        val dotFile = outDir.resolve("modules.dot")

        dotFile.writeText(buildString {
            appendLine("digraph Modules {")
            appendLine("""  graph [rankdir=BT, splines=true, overlap=false];""")
            appendLine("""  node [shape=circle, style="filled", fixedsize=true, width=1.2, fontsize=10];""")
            appendLine("""  edge [fontsize=9];""")
            appendLine()

            nodes.sorted().forEach { node ->
                val label = node.removePrefix(":").replace(":", "\n")
                appendLine("""  "$node" [label="$label"];""")
            }

            appendLine()

            edges.sortedBy { "${it.first}->${it.second}" }.forEach { (from, to) ->
                appendLine("""  "$from" -> "$to";""")
            }

            appendLine("}")
        })

        println("DOT file generated: ${dotFile.absolutePath}")
        println("Render PNG:")
        println("  dot -Tpng ${dotFile.absolutePath} -o ${outDir.resolve("modules.png").absolutePath}")
        println("Render SVG:")
        println("  dot -Tsvg ${dotFile.absolutePath} -o ${outDir.resolve("modules.svg").absolutePath}")
    }
}