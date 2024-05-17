import io.greenglass.host.application.components.treewalker.TreeWalker
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class TreeWalkerTests {

    @Test
    fun simpleTests() {
        val uri1 = "/first/second/third/forth"
        val uri2 = "/first/second/forth/third"
        val uri3 = "/second/first/forth/third"
        val uri4 = "/first/third"
        val uri5 = "/a/quick/brown/fox"

        val tw = TreeWalker<String>()
        tw.add(uri1, "uri1")
        tw.add(uri2, "uri2")
        tw.add(uri3, "uri3")
        tw.add(uri4, "uri4")

        var result = tw.match(uri1)
        assertThat(result?.type).isEqualTo("uri1")

        result = tw.match(uri2)
        assertThat(result?.type).isEqualTo("uri2")

        result = tw.match(uri3)
        assertThat(result?.type).isEqualTo("uri3")

        result = tw.match(uri4)
        assertThat(result?.type).isEqualTo("uri4")

        result = tw.match(uri5)
        assertThat(result).isNull()
    }

    @Test
    fun parameterTests() {
        val uri1 = "/first/second/{param1}/forth"
        val uri1withParam = "/first/second/12345/forth"

        val tw = TreeWalker<String>()
        tw.add(uri1, "uri1")

        val result = tw.match(uri1withParam)
        assertThat(result?.type).isEqualTo("uri1")
        assertThat(result?.params?.size).isEqualTo(1)
        assertThat(result?.params?.get("param1")).isEqualTo("12345")
    }

    @Test
    fun pathTests() {
        val uri1 = "/first/second/{param1}/third"
        val uri2 = "/first/second/{param1}/fourth"

        val uri1withParam = "/first/second/12345/third"
        val uri2withParam = "/first/second/54321/fourth"

        val tw = TreeWalker<String>()
        tw.add(uri1, "uri1")
        tw.add(uri2, "uri2")

        var result = tw.match(uri1withParam)
        assertThat(result?.type).isEqualTo("uri1")
        assertThat(result?.params?.size).isEqualTo(1)
        assertThat(result?.params?.get("param1")).isEqualTo("12345")

        result = tw.match(uri2withParam)
        assertThat(result?.type).isEqualTo("uri2")
        assertThat(result?.params?.size).isEqualTo(1)
        assertThat(result?.params?.get("param1")).isEqualTo("54321")
    }

    @Test
    fun buildTests() {
        val uri1 = "/first/{param1}/second/{param2}/third"
        val uri2 = "/first/second/third/{param3}"

        val tw = TreeWalker<String>()

        var result = tw.build(uri1, mapOf("param1" to "12345", "param2" to "54321"))
        assertThat(result).isEqualTo("/first/12345/second/54321/third")

        result = tw.build(uri2, mapOf("param3" to "66666"))
        assertThat(result).isEqualTo("/first/second/third/66666")
    }
}