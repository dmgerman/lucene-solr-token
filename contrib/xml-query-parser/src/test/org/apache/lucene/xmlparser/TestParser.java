begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment

begin_package
DECL|package|org.apache.lucene.xmlparser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|index
operator|.
name|IndexReader
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
name|queryParser
operator|.
name|QueryParser
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
name|Query
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
name|Directory
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

begin_comment
comment|/**  * @author maharwood  */
end_comment

begin_class
DECL|class|TestParser
specifier|public
class|class
name|TestParser
extends|extends
name|TestCase
block|{
DECL|field|builder
name|CoreParser
name|builder
decl_stmt|;
DECL|field|dir
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|analyzer
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
comment|//CHANGE THIS TO SEE OUTPUT
DECL|field|printResults
name|boolean
name|printResults
init|=
literal|false
decl_stmt|;
comment|/* 	 * @see TestCase#setUp() 	 */
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|//initialize the parser
name|builder
operator|=
operator|new
name|CorePlusExtensionsParser
argument_list|(
name|analyzer
argument_list|,
operator|new
name|QueryParser
argument_list|(
literal|"contents"
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
comment|//initialize the index (done once, then cached in static data for use with ALL tests)
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|BufferedReader
name|d
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|TestParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"reuters21578.txt"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|d
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|int
name|endOfDate
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'\t'
argument_list|)
decl_stmt|;
name|String
name|date
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|endOfDate
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|line
operator|.
name|substring
argument_list|(
name|endOfDate
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|doc
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
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
name|line
operator|=
name|d
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//		dir.close();
block|}
DECL|method|testSimpleXML
specifier|public
name|void
name|testSimpleXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"TermQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"TermQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleTermsQueryXML
specifier|public
name|void
name|testSimpleTermsQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"TermsQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"TermsQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanQueryXML
specifier|public
name|void
name|testBooleanQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BooleanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"BooleanQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeFilterQueryXML
specifier|public
name|void
name|testRangeFilterQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"RangeFilterQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"RangeFilter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testUserQueryXML
specifier|public
name|void
name|testUserQueryXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"UserInputQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"UserInput with Filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testLikeThisQueryXML
specifier|public
name|void
name|testLikeThisQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"LikeThisQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"like this"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostingQueryXML
specifier|public
name|void
name|testBoostingQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BoostingQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"boosting "
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testFuzzyLikeThisQueryXML
specifier|public
name|void
name|testFuzzyLikeThisQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"FuzzyLikeThisQuery.xml"
argument_list|)
decl_stmt|;
comment|//show rewritten fuzzyLikeThisQuery - see what is being matched on
if|if
condition|(
name|printResults
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|q
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dumpResults
argument_list|(
literal|"FuzzyLikeThis"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermsFilterXML
specifier|public
name|void
name|testTermsFilterXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"TermsFilterQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Terms Filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanTermXML
specifier|public
name|void
name|testSpanTermXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"SpanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Span Query"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstantScoreQueryXML
specifier|public
name|void
name|testConstantScoreQueryXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"ConstantScoreQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"ConstantScoreQuery"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testMatchAllDocsPlusFilterXML
specifier|public
name|void
name|testMatchAllDocsPlusFilterXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"MatchAllDocsQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"MatchAllDocsQuery with range filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanFilterXML
specifier|public
name|void
name|testBooleanFilterXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"BooleanFilter.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Boolean filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedBooleanQuery
specifier|public
name|void
name|testNestedBooleanQuery
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"NestedBooleanQuery.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Nested Boolean query"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|testCachedFilterXML
specifier|public
name|void
name|testCachedFilterXML
parameter_list|()
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|Query
name|q
init|=
name|parse
argument_list|(
literal|"CachedFilter.xml"
argument_list|)
decl_stmt|;
name|dumpResults
argument_list|(
literal|"Cached filter"
argument_list|,
name|q
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
comment|//================= Helper methods ===================================
DECL|method|parse
specifier|private
name|Query
name|parse
parameter_list|(
name|String
name|xmlFileName
parameter_list|)
throws|throws
name|ParserException
throws|,
name|IOException
block|{
name|InputStream
name|xmlStream
init|=
name|TestParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|xmlFileName
argument_list|)
decl_stmt|;
name|Query
name|result
init|=
name|builder
operator|.
name|parse
argument_list|(
name|xmlStream
argument_list|)
decl_stmt|;
name|xmlStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|dumpResults
specifier|private
name|void
name|dumpResults
parameter_list|(
name|String
name|qType
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Hits
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|qType
operator|+
literal|" should produce results "
argument_list|,
name|h
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|printResults
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"========="
operator|+
name|qType
operator|+
literal|"============"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|Math
operator|.
name|min
argument_list|(
name|numDocs
argument_list|,
name|h
operator|.
name|length
argument_list|()
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|ldoc
init|=
name|h
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|ldoc
operator|.
name|get
argument_list|(
literal|"date"
argument_list|)
operator|+
literal|"]"
operator|+
name|ldoc
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

