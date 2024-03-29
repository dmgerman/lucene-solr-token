begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.taxonomy.writercache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|facet
operator|.
name|FacetTestCase
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
name|facet
operator|.
name|taxonomy
operator|.
name|FacetLabel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCompactLabelToOrdinal
specifier|public
class|class
name|TestCompactLabelToOrdinal
extends|extends
name|FacetTestCase
block|{
annotation|@
name|Test
DECL|method|testL2O
specifier|public
name|void
name|testL2O
parameter_list|()
throws|throws
name|Exception
block|{
name|LabelToOrdinal
name|map
init|=
operator|new
name|LabelToOrdinalMap
argument_list|()
decl_stmt|;
name|CompactLabelToOrdinal
name|compact
init|=
operator|new
name|CompactLabelToOrdinal
argument_list|(
literal|2000000
argument_list|,
literal|0.15f
argument_list|,
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|atLeast
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numUniqueValues
init|=
literal|50
operator|*
literal|1000
decl_stmt|;
name|String
index|[]
name|uniqueValues
init|=
operator|new
name|String
index|[
name|numUniqueValues
index|]
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|50
index|]
decl_stmt|;
name|Random
name|random
init|=
name|random
argument_list|()
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
name|numUniqueValues
condition|;
control|)
block|{
name|random
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|int
name|size
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// This test is turning random bytes into a string,
comment|// this is asking for trouble.
name|CharsetDecoder
name|decoder
init|=
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|newDecoder
argument_list|()
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPLACE
argument_list|)
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPLACE
argument_list|)
decl_stmt|;
name|uniqueValues
index|[
name|i
index|]
operator|=
name|decoder
operator|.
name|decode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// we cannot have empty path components, so eliminate all prefix as well
comment|// as middle consecutive delimiter chars.
name|uniqueValues
index|[
name|i
index|]
operator|=
name|uniqueValues
index|[
name|i
index|]
operator|.
name|replaceAll
argument_list|(
literal|"/+"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
if|if
condition|(
name|uniqueValues
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|uniqueValues
index|[
name|i
index|]
operator|=
name|uniqueValues
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uniqueValues
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
name|CompactLabelToOrdinal
operator|.
name|TERMINATOR_CHAR
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
block|}
name|Path
name|tmpDir
init|=
name|createTempDir
argument_list|(
literal|"testLableToOrdinal"
argument_list|)
decl_stmt|;
name|Path
name|f
init|=
name|tmpDir
operator|.
name|resolve
argument_list|(
literal|"CompactLabelToOrdinalTest.tmp"
argument_list|)
decl_stmt|;
name|int
name|flushInterval
init|=
literal|10
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
name|n
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
name|flushInterval
operator|==
literal|0
condition|)
block|{
name|compact
operator|.
name|flush
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|compact
operator|=
name|CompactLabelToOrdinal
operator|.
name|open
argument_list|(
name|f
argument_list|,
literal|0.15f
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|flushInterval
operator|<
operator|(
name|n
operator|/
literal|10
operator|)
condition|)
block|{
name|flushInterval
operator|*=
literal|10
expr_stmt|;
block|}
block|}
name|int
name|index
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numUniqueValues
argument_list|)
decl_stmt|;
name|FacetLabel
name|label
decl_stmt|;
name|String
name|s
init|=
name|uniqueValues
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|label
operator|=
operator|new
name|FacetLabel
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|label
operator|=
operator|new
name|FacetLabel
argument_list|(
name|s
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|ord1
init|=
name|map
operator|.
name|getOrdinal
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|int
name|ord2
init|=
name|compact
operator|.
name|getOrdinal
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ord1
argument_list|,
name|ord2
argument_list|)
expr_stmt|;
if|if
condition|(
name|ord1
operator|==
name|LabelToOrdinal
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|ord1
operator|=
name|compact
operator|.
name|getNextOrdinal
argument_list|()
expr_stmt|;
name|map
operator|.
name|addLabel
argument_list|(
name|label
argument_list|,
name|ord1
argument_list|)
expr_stmt|;
name|compact
operator|.
name|addLabel
argument_list|(
name|label
argument_list|,
name|ord1
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numUniqueValues
condition|;
name|i
operator|++
control|)
block|{
name|FacetLabel
name|label
decl_stmt|;
name|String
name|s
init|=
name|uniqueValues
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|label
operator|=
operator|new
name|FacetLabel
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|label
operator|=
operator|new
name|FacetLabel
argument_list|(
name|s
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|ord1
init|=
name|map
operator|.
name|getOrdinal
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|int
name|ord2
init|=
name|compact
operator|.
name|getOrdinal
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ord1
argument_list|,
name|ord2
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LabelToOrdinalMap
specifier|private
specifier|static
class|class
name|LabelToOrdinalMap
extends|extends
name|LabelToOrdinal
block|{
DECL|field|map
specifier|private
name|Map
argument_list|<
name|FacetLabel
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|LabelToOrdinalMap
name|LabelToOrdinalMap
parameter_list|()
block|{ }
annotation|@
name|Override
DECL|method|addLabel
specifier|public
name|void
name|addLabel
parameter_list|(
name|FacetLabel
name|label
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|ordinal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrdinal
specifier|public
name|int
name|getOrdinal
parameter_list|(
name|FacetLabel
name|label
parameter_list|)
block|{
name|Integer
name|value
init|=
name|map
operator|.
name|get
argument_list|(
name|label
argument_list|)
decl_stmt|;
return|return
operator|(
name|value
operator|!=
literal|null
operator|)
condition|?
name|value
operator|.
name|intValue
argument_list|()
else|:
name|LabelToOrdinal
operator|.
name|INVALID_ORDINAL
return|;
block|}
block|}
block|}
end_class

end_unit

