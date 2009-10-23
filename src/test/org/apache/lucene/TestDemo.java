begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|ScoreDoc
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|util
operator|.
name|Version
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
name|util
operator|.
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  * A very simple demo used in the API documentation (src/java/overview.html).  *  * Please try to keep src/java/overview.html up-to-date when making changes  * to this class.  */
end_comment

begin_class
DECL|class|TestDemo
specifier|public
class|class
name|TestDemo
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDemo
specifier|public
name|void
name|testDemo
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
comment|// Store the index in memory:
name|Directory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// To store an index on disk, use this instead:
comment|//Directory directory = FSDirectory.open("/tmp/testindex");
name|IndexWriter
name|iwriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|,
operator|new
name|IndexWriter
operator|.
name|MaxFieldLength
argument_list|(
literal|25000
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|text
init|=
literal|"This is the text to be indexed."
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"fieldname"
argument_list|,
name|text
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now search the index:
name|IndexSearcher
name|isearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// read-only=true
comment|// Parse a simple query that searches for "text":
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"fieldname"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|isearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Iterate through the results:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|hitDoc
init|=
name|isearcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"This is the text to be indexed."
argument_list|,
name|hitDoc
operator|.
name|get
argument_list|(
literal|"fieldname"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|isearcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

