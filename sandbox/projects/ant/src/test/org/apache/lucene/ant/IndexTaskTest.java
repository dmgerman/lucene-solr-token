begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.ant
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|StopAnalyzer
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
name|ParseException
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
name|search
operator|.
name|Searcher
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
name|ant
operator|.
name|IndexTask
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|FileSet
import|;
end_import

begin_comment
comment|/**  *  Test cases for index task  *  *@author     Erik Hatcher  */
end_comment

begin_class
DECL|class|IndexTaskTest
specifier|public
class|class
name|IndexTaskTest
extends|extends
name|TestCase
block|{
DECL|field|docHandler
specifier|private
specifier|final
specifier|static
name|String
name|docHandler
init|=
literal|"org.apache.lucene.ant.FileExtensionDocumentHandler"
decl_stmt|;
DECL|field|docsDir
specifier|private
name|String
name|docsDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"docs.dir"
argument_list|)
decl_stmt|;
DECL|field|indexDir
specifier|private
name|String
name|indexDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"index.dir"
argument_list|)
decl_stmt|;
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
comment|/**      *  Constructor for the IndexTaskTest object      *      *@param  name  Description of Parameter      */
DECL|method|IndexTaskTest
specifier|public
name|IndexTaskTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      *  The JUnit setup method      *      *@exception  IOException  Description of Exception      */
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|Project
name|project
init|=
operator|new
name|Project
argument_list|()
decl_stmt|;
name|IndexTask
name|task
init|=
operator|new
name|IndexTask
argument_list|()
decl_stmt|;
name|FileSet
name|fs
init|=
operator|new
name|FileSet
argument_list|()
decl_stmt|;
name|fs
operator|.
name|setDir
argument_list|(
operator|new
name|File
argument_list|(
name|docsDir
argument_list|)
argument_list|)
expr_stmt|;
name|task
operator|.
name|addFileset
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|task
operator|.
name|setOverwrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|task
operator|.
name|setDocumentHandler
argument_list|(
name|docHandler
argument_list|)
expr_stmt|;
name|task
operator|.
name|setIndex
argument_list|(
operator|new
name|File
argument_list|(
name|indexDir
argument_list|)
argument_list|)
expr_stmt|;
name|task
operator|.
name|setProject
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|task
operator|.
name|execute
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|analyzer
operator|=
operator|new
name|StopAnalyzer
argument_list|()
expr_stmt|;
block|}
comment|/**      *  A unit test for JUnit      */
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sysout"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"syserr"
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|QueryParser
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"contents"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
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
literal|"Find document(s)"
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *  The teardown method for JUnit      * @todo remove indexDir?      */
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

