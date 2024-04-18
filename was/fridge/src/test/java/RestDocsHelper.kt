import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.snippet.Snippet

import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint

object RestDocsHelper {
    fun customDocument(identifier: String, vararg snippets: Snippet?): RestDocumentationResultHandler {
        return document("{class-name}/$identifier",
                preprocessRequest(
                        prettyPrint()),
                preprocessResponse(
                        prettyPrint()),
                *snippets
        )
    }
}