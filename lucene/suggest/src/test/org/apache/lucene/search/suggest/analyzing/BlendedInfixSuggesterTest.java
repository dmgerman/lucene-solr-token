begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|analyzing
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|analysis
operator|.
name|util
operator|.
name|CharArraySet
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
name|suggest
operator|.
name|Input
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
name|suggest
operator|.
name|InputArrayIterator
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
name|suggest
operator|.
name|Lookup
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
name|BytesRef
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|BlendedInfixSuggesterTest
specifier|public
class|class
name|BlendedInfixSuggesterTest
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Test the weight transformation depending on the position    * of the matching term.    */
DECL|method|testBlendedSort
specifier|public
name|void
name|testBlendedSort
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRef
name|payload
init|=
operator|new
name|BytesRef
argument_list|(
literal|"star"
argument_list|)
decl_stmt|;
name|Input
name|keys
index|[]
init|=
operator|new
name|Input
index|[]
block|{
operator|new
name|Input
argument_list|(
literal|"star wars: episode v - the empire strikes back"
argument_list|,
literal|8
argument_list|,
name|payload
argument_list|)
block|}
decl_stmt|;
name|File
name|tempDir
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"BlendedInfixSuggesterTest"
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
decl_stmt|;
name|BlendedInfixSuggester
name|suggester
init|=
operator|new
name|BlendedInfixSuggester
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|newFSDirectory
argument_list|(
name|tempDir
argument_list|)
argument_list|,
name|a
argument_list|,
name|a
argument_list|,
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_MIN_PREFIX_CHARS
argument_list|,
name|BlendedInfixSuggester
operator|.
name|BlenderType
operator|.
name|POSITION_LINEAR
argument_list|,
name|BlendedInfixSuggester
operator|.
name|DEFAULT_NUM_FACTOR
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
comment|// we query for star wars and check that the weight
comment|// is smaller when we search for tokens that are far from the beginning
name|long
name|w0
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"star "
argument_list|,
name|payload
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|w1
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"war"
argument_list|,
name|payload
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|w2
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"empire ba"
argument_list|,
name|payload
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|w3
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"back"
argument_list|,
name|payload
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|w4
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"bacc"
argument_list|,
name|payload
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|w0
operator|>
name|w1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|w1
operator|>
name|w2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|w2
operator|>
name|w3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|w4
operator|<
literal|0
argument_list|)
expr_stmt|;
name|suggester
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verify the different flavours of the blender types    */
DECL|method|testBlendingType
specifier|public
name|void
name|testBlendingType
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRef
name|pl
init|=
operator|new
name|BytesRef
argument_list|(
literal|"lake"
argument_list|)
decl_stmt|;
name|long
name|w
init|=
literal|20
decl_stmt|;
name|Input
name|keys
index|[]
init|=
operator|new
name|Input
index|[]
block|{
operator|new
name|Input
argument_list|(
literal|"top of the lake"
argument_list|,
name|w
argument_list|,
name|pl
argument_list|)
block|}
decl_stmt|;
name|File
name|tempDir
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"BlendedInfixSuggesterTest"
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
decl_stmt|;
comment|// BlenderType.LINEAR is used by default (remove position*10%)
name|BlendedInfixSuggester
name|suggester
init|=
operator|new
name|BlendedInfixSuggester
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|newFSDirectory
argument_list|(
name|tempDir
argument_list|)
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|w
argument_list|,
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"top"
argument_list|,
name|pl
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|int
call|)
argument_list|(
name|w
operator|*
operator|(
literal|1
operator|-
literal|0.10
operator|*
literal|2
operator|)
argument_list|)
argument_list|,
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"the"
argument_list|,
name|pl
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|int
call|)
argument_list|(
name|w
operator|*
operator|(
literal|1
operator|-
literal|0.10
operator|*
literal|3
operator|)
argument_list|)
argument_list|,
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"lake"
argument_list|,
name|pl
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|suggester
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// BlenderType.RECIPROCAL is using 1/(1+p) * w where w is weight and p the position of the word
name|suggester
operator|=
operator|new
name|BlendedInfixSuggester
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|newFSDirectory
argument_list|(
name|tempDir
argument_list|)
argument_list|,
name|a
argument_list|,
name|a
argument_list|,
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_MIN_PREFIX_CHARS
argument_list|,
name|BlendedInfixSuggester
operator|.
name|BlenderType
operator|.
name|POSITION_RECIPROCAL
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|w
argument_list|,
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"top"
argument_list|,
name|pl
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|int
call|)
argument_list|(
name|w
operator|*
literal|1
operator|/
operator|(
literal|1
operator|+
literal|2
operator|)
argument_list|)
argument_list|,
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"the"
argument_list|,
name|pl
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|int
call|)
argument_list|(
name|w
operator|*
literal|1
operator|/
operator|(
literal|1
operator|+
literal|3
operator|)
argument_list|)
argument_list|,
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"lake"
argument_list|,
name|pl
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|suggester
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Assert that the factor is important to get results that might be lower in term of weight but    * would be pushed up after the blending transformation    */
DECL|method|testRequiresMore
specifier|public
name|void
name|testRequiresMore
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRef
name|lake
init|=
operator|new
name|BytesRef
argument_list|(
literal|"lake"
argument_list|)
decl_stmt|;
name|BytesRef
name|star
init|=
operator|new
name|BytesRef
argument_list|(
literal|"star"
argument_list|)
decl_stmt|;
name|BytesRef
name|ret
init|=
operator|new
name|BytesRef
argument_list|(
literal|"ret"
argument_list|)
decl_stmt|;
name|Input
name|keys
index|[]
init|=
operator|new
name|Input
index|[]
block|{
operator|new
name|Input
argument_list|(
literal|"top of the lake"
argument_list|,
literal|18
argument_list|,
name|lake
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"star wars: episode v - the empire strikes back"
argument_list|,
literal|12
argument_list|,
name|star
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"the returned"
argument_list|,
literal|10
argument_list|,
name|ret
argument_list|)
block|,     }
decl_stmt|;
name|File
name|tempDir
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"BlendedInfixSuggesterTest"
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
decl_stmt|;
comment|// if factor is small, we don't get the expected element
name|BlendedInfixSuggester
name|suggester
init|=
operator|new
name|BlendedInfixSuggester
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|newFSDirectory
argument_list|(
name|tempDir
argument_list|)
argument_list|,
name|a
argument_list|,
name|a
argument_list|,
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_MIN_PREFIX_CHARS
argument_list|,
name|BlendedInfixSuggester
operator|.
name|BlenderType
operator|.
name|POSITION_RECIPROCAL
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
comment|// we don't find it for in the 2 first
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|suggester
operator|.
name|lookup
argument_list|(
literal|"the"
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|w0
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"the"
argument_list|,
name|ret
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|w0
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|// but it's there if we search for 3 elements
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|suggester
operator|.
name|lookup
argument_list|(
literal|"the"
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|w1
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"the"
argument_list|,
name|ret
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|w1
operator|>
literal|0
argument_list|)
expr_stmt|;
name|suggester
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// if we increase the factor we have it
name|suggester
operator|=
operator|new
name|BlendedInfixSuggester
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|newFSDirectory
argument_list|(
name|tempDir
argument_list|)
argument_list|,
name|a
argument_list|,
name|a
argument_list|,
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_MIN_PREFIX_CHARS
argument_list|,
name|BlendedInfixSuggester
operator|.
name|BlenderType
operator|.
name|POSITION_RECIPROCAL
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
comment|// we have it
name|long
name|w2
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"the"
argument_list|,
name|ret
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|w2
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// but we don't have the other
name|long
name|w3
init|=
name|getInResults
argument_list|(
name|suggester
argument_list|,
literal|"the"
argument_list|,
name|star
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|w3
operator|<
literal|0
argument_list|)
expr_stmt|;
name|suggester
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|rying
specifier|public
name|void
comment|/*testT*/
name|rying
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRef
name|lake
init|=
operator|new
name|BytesRef
argument_list|(
literal|"lake"
argument_list|)
decl_stmt|;
name|BytesRef
name|star
init|=
operator|new
name|BytesRef
argument_list|(
literal|"star"
argument_list|)
decl_stmt|;
name|BytesRef
name|ret
init|=
operator|new
name|BytesRef
argument_list|(
literal|"ret"
argument_list|)
decl_stmt|;
name|Input
name|keys
index|[]
init|=
operator|new
name|Input
index|[]
block|{
operator|new
name|Input
argument_list|(
literal|"top of the lake"
argument_list|,
literal|15
argument_list|,
name|lake
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"star wars: episode v - the empire strikes back"
argument_list|,
literal|12
argument_list|,
name|star
argument_list|)
block|,
operator|new
name|Input
argument_list|(
literal|"the returned"
argument_list|,
literal|10
argument_list|,
name|ret
argument_list|)
block|,     }
decl_stmt|;
name|File
name|tempDir
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"BlendedInfixSuggesterTest"
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
decl_stmt|;
comment|// if factor is small, we don't get the expected element
name|BlendedInfixSuggester
name|suggester
init|=
operator|new
name|BlendedInfixSuggester
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|newFSDirectory
argument_list|(
name|tempDir
argument_list|)
argument_list|,
name|a
argument_list|,
name|a
argument_list|,
name|AnalyzingInfixSuggester
operator|.
name|DEFAULT_MIN_PREFIX_CHARS
argument_list|,
name|BlendedInfixSuggester
operator|.
name|BlenderType
operator|.
name|POSITION_RECIPROCAL
argument_list|,
name|BlendedInfixSuggester
operator|.
name|DEFAULT_NUM_FACTOR
argument_list|)
decl_stmt|;
name|suggester
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|responses
init|=
name|suggester
operator|.
name|lookup
argument_list|(
literal|"the"
argument_list|,
literal|null
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Lookup
operator|.
name|LookupResult
name|response
range|:
name|responses
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
name|suggester
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getInResults
specifier|private
specifier|static
name|long
name|getInResults
parameter_list|(
name|BlendedInfixSuggester
name|suggester
parameter_list|,
name|String
name|prefix
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|responses
init|=
name|suggester
operator|.
name|lookup
argument_list|(
name|prefix
argument_list|,
literal|null
argument_list|,
name|num
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Lookup
operator|.
name|LookupResult
name|response
range|:
name|responses
control|)
block|{
if|if
condition|(
name|response
operator|.
name|payload
operator|.
name|equals
argument_list|(
name|payload
argument_list|)
condition|)
block|{
return|return
name|response
operator|.
name|value
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

