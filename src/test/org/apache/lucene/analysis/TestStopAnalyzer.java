begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|PhraseQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Hits
import|;
end_import

begin_class
DECL|class|TestStopAnalyzer
specifier|public
class|class
name|TestStopAnalyzer
extends|extends
name|TestCase
block|{
DECL|field|stopAnalyzer
specifier|private
name|StopAnalyzer
name|stopAnalyzer
init|=
operator|new
name|StopAnalyzer
argument_list|()
decl_stmt|;
DECL|method|tokensFromAnalyzer
specifier|public
name|Token
index|[]
name|tokensFromAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"contents"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|ArrayList
name|tokenList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Token
name|token
init|=
name|stream
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
break|break;
name|tokenList
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|Token
index|[]
operator|)
name|tokenList
operator|.
name|toArray
argument_list|(
operator|new
name|Token
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|testNoHoles
specifier|public
name|void
name|testNoHoles
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
index|[]
name|tokens
init|=
name|tokensFromAnalyzer
argument_list|(
name|stopAnalyzer
argument_list|,
literal|"non-stop words"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// ensure all words are in successive positions
name|assertEquals
argument_list|(
literal|"non"
argument_list|,
literal|1
argument_list|,
name|tokens
index|[
literal|0
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"stop"
argument_list|,
literal|1
argument_list|,
name|tokens
index|[
literal|1
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"words"
argument_list|,
literal|1
argument_list|,
name|tokens
index|[
literal|2
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testHoles
specifier|public
name|void
name|testHoles
parameter_list|()
throws|throws
name|Exception
block|{
name|Token
index|[]
name|tokens
init|=
name|tokensFromAnalyzer
argument_list|(
name|stopAnalyzer
argument_list|,
literal|"the stop words are here"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// check for the holes noted by position gaps
name|assertEquals
argument_list|(
literal|"stop"
argument_list|,
literal|2
argument_list|,
name|tokens
index|[
literal|0
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"words"
argument_list|,
literal|1
argument_list|,
name|tokens
index|[
literal|1
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"here"
argument_list|,
literal|2
argument_list|,
name|tokens
index|[
literal|2
index|]
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPhraseQuery
specifier|public
name|void
name|testPhraseQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|stopAnalyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"field"
argument_list|,
literal|"the stop words are here"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// valid exact phrase query
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"stop"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"words"
argument_list|)
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// incorrect attempt at exact phrase query over stop word hole
name|query
operator|=
operator|new
name|PhraseQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"words"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"here"
argument_list|)
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// add some slop, and match over the hole
name|query
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

