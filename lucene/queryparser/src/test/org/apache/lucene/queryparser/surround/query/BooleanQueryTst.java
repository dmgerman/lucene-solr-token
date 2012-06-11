begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|AtomicReaderContext
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
name|DirectoryReader
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
name|Collector
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
name|Scorer
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
name|queryparser
operator|.
name|surround
operator|.
name|parser
operator|.
name|QueryParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|BooleanQueryTst
specifier|public
class|class
name|BooleanQueryTst
block|{
DECL|field|queryText
name|String
name|queryText
decl_stmt|;
DECL|field|expectedDocNrs
specifier|final
name|int
index|[]
name|expectedDocNrs
decl_stmt|;
DECL|field|dBase
name|SingleFieldTestDb
name|dBase
decl_stmt|;
DECL|field|fieldName
name|String
name|fieldName
decl_stmt|;
DECL|field|testCase
name|Assert
name|testCase
decl_stmt|;
DECL|field|qf
name|BasicQueryFactory
name|qf
decl_stmt|;
DECL|field|verbose
name|boolean
name|verbose
init|=
literal|true
decl_stmt|;
DECL|method|BooleanQueryTst
specifier|public
name|BooleanQueryTst
parameter_list|(
name|String
name|queryText
parameter_list|,
name|int
index|[]
name|expectedDocNrs
parameter_list|,
name|SingleFieldTestDb
name|dBase
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Assert
name|testCase
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
name|this
operator|.
name|queryText
operator|=
name|queryText
expr_stmt|;
name|this
operator|.
name|expectedDocNrs
operator|=
name|expectedDocNrs
expr_stmt|;
name|this
operator|.
name|dBase
operator|=
name|dBase
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|testCase
operator|=
name|testCase
expr_stmt|;
name|this
operator|.
name|qf
operator|=
name|qf
expr_stmt|;
block|}
DECL|method|setVerbose
specifier|public
name|void
name|setVerbose
parameter_list|(
name|boolean
name|verbose
parameter_list|)
block|{
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
block|}
DECL|class|TestCollector
class|class
name|TestCollector
extends|extends
name|Collector
block|{
comment|// FIXME: use check hits from Lucene tests
DECL|field|totalMatched
name|int
name|totalMatched
decl_stmt|;
DECL|field|encountered
name|boolean
index|[]
name|encountered
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
init|=
literal|null
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
init|=
literal|0
decl_stmt|;
DECL|method|TestCollector
name|TestCollector
parameter_list|()
block|{
name|totalMatched
operator|=
literal|0
expr_stmt|;
name|encountered
operator|=
operator|new
name|boolean
index|[
name|expectedDocNrs
operator|.
name|length
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|docNr
parameter_list|)
throws|throws
name|IOException
block|{
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|docNr
operator|+=
name|docBase
expr_stmt|;
comment|/* System.out.println(docNr + " '" + dBase.getDocs()[docNr] + "': " + score); */
name|Assert
operator|.
name|assertTrue
argument_list|(
name|queryText
operator|+
literal|": positive score"
argument_list|,
name|score
operator|>
literal|0.0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|queryText
operator|+
literal|": too many hits"
argument_list|,
name|totalMatched
operator|<
name|expectedDocNrs
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|expectedDocNrs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
operator|!
name|encountered
index|[
name|i
index|]
operator|)
operator|&&
operator|(
name|expectedDocNrs
index|[
name|i
index|]
operator|==
name|docNr
operator|)
condition|)
block|{
name|encountered
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|i
operator|==
name|expectedDocNrs
operator|.
name|length
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|queryText
operator|+
literal|": doc nr for hit not expected: "
operator|+
name|docNr
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|totalMatched
operator|++
expr_stmt|;
block|}
DECL|method|checkNrHits
name|void
name|checkNrHits
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|queryText
operator|+
literal|": nr of hits"
argument_list|,
name|expectedDocNrs
operator|.
name|length
argument_list|,
name|totalMatched
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|queryText
argument_list|)
expr_stmt|;
block|}
name|SrndQuery
name|lq
init|=
name|QueryParser
operator|.
name|parse
argument_list|(
name|queryText
argument_list|)
decl_stmt|;
comment|/* if (verbose) System.out.println("Srnd: " + lq.toString()); */
name|Query
name|query
init|=
name|lq
operator|.
name|makeLuceneQueryField
argument_list|(
name|fieldName
argument_list|,
name|qf
argument_list|)
decl_stmt|;
comment|/* if (verbose) System.out.println("Lucene: " + query.toString()); */
name|TestCollector
name|tc
init|=
operator|new
name|TestCollector
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dBase
operator|.
name|getDb
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
try|try
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|tc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|tc
operator|.
name|checkNrHits
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

