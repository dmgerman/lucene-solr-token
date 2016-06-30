begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

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
name|MockAnalyzer
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
name|document
operator|.
name|TextField
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
name|CollectionStatistics
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
name|TermStatistics
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
name|similarities
operator|.
name|ClassicSimilarity
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
name|similarities
operator|.
name|PerFieldSimilarityWrapper
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
name|similarities
operator|.
name|Similarity
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
name|util
operator|.
name|LineFileDocs
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
name|TestUtil
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|TestCustomNorms
specifier|public
class|class
name|TestCustomNorms
extends|extends
name|LuceneTestCase
block|{
DECL|field|floatTestField
specifier|final
name|String
name|floatTestField
init|=
literal|"normsTestFloat"
decl_stmt|;
DECL|field|exceptionTestField
specifier|final
name|String
name|exceptionTestField
init|=
literal|"normsTestExcp"
decl_stmt|;
DECL|method|testFloatNorms
specifier|public
name|void
name|testFloatNorms
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|analyzer
operator|.
name|setMaxTokenLength
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
name|Similarity
name|provider
init|=
operator|new
name|MySimProvider
argument_list|()
decl_stmt|;
name|config
operator|.
name|setSimilarity
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
specifier|final
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|float
name|nextFloat
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
operator|new
name|TextField
argument_list|(
name|floatTestField
argument_list|,
literal|""
operator|+
name|nextFloat
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|f
operator|.
name|setBoost
argument_list|(
name|nextFloat
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|.
name|removeField
argument_list|(
name|floatTestField
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|open
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|NumericDocValues
name|norms
init|=
name|MultiDocValues
operator|.
name|getNormValues
argument_list|(
name|open
argument_list|,
name|floatTestField
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|norms
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
name|open
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
name|open
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|float
name|expected
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|document
operator|.
name|get
argument_list|(
name|floatTestField
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|norms
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|MySimProvider
specifier|public
class|class
name|MySimProvider
extends|extends
name|PerFieldSimilarityWrapper
block|{
DECL|field|delegate
name|Similarity
name|delegate
init|=
operator|new
name|ClassicSimilarity
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|queryNorm
argument_list|(
name|sumOfSquaredWeights
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|floatTestField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return
operator|new
name|FloatEncodingBoostSimilarity
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|delegate
return|;
block|}
block|}
block|}
DECL|class|FloatEncodingBoostSimilarity
specifier|public
specifier|static
class|class
name|FloatEncodingBoostSimilarity
extends|extends
name|Similarity
block|{
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|long
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|state
operator|.
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeWeight
specifier|public
name|SimWeight
name|computeWeight
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|simScorer
specifier|public
name|SimScorer
name|simScorer
parameter_list|(
name|SimWeight
name|weight
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

