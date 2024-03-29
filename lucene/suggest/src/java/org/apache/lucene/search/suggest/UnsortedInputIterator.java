begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|BytesRefBuilder
import|;
end_import

begin_comment
comment|/**  * This wrapper buffers the incoming elements and makes sure they are in  * random order.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|UnsortedInputIterator
specifier|public
class|class
name|UnsortedInputIterator
extends|extends
name|BufferedInputIterator
block|{
comment|// TODO keep this for now
DECL|field|ords
specifier|private
specifier|final
name|int
index|[]
name|ords
decl_stmt|;
DECL|field|currentOrd
specifier|private
name|int
name|currentOrd
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|payloadSpare
specifier|private
specifier|final
name|BytesRefBuilder
name|payloadSpare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
comment|/**     * Creates a new iterator, wrapping the specified iterator and    * returning elements in a random order.    */
DECL|method|UnsortedInputIterator
specifier|public
name|UnsortedInputIterator
parameter_list|(
name|InputIterator
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|ords
operator|=
operator|new
name|int
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
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
name|ords
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ords
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
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
name|ords
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|randomPosition
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|ords
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|temp
init|=
name|ords
index|[
name|i
index|]
decl_stmt|;
name|ords
index|[
name|i
index|]
operator|=
name|ords
index|[
name|randomPosition
index|]
expr_stmt|;
name|ords
index|[
name|randomPosition
index|]
operator|=
name|temp
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
assert|assert
name|currentOrd
operator|==
name|ords
index|[
name|curPos
index|]
assert|;
return|return
name|freqs
index|[
name|currentOrd
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|++
name|curPos
operator|<
name|entries
operator|.
name|size
argument_list|()
condition|)
block|{
name|currentOrd
operator|=
name|ords
index|[
name|curPos
index|]
expr_stmt|;
return|return
name|entries
operator|.
name|get
argument_list|(
name|spare
argument_list|,
name|currentOrd
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
block|{
if|if
condition|(
name|hasPayloads
argument_list|()
operator|&&
name|curPos
operator|<
name|payloads
operator|.
name|size
argument_list|()
condition|)
block|{
assert|assert
name|currentOrd
operator|==
name|ords
index|[
name|curPos
index|]
assert|;
return|return
name|payloads
operator|.
name|get
argument_list|(
name|payloadSpare
argument_list|,
name|currentOrd
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|contexts
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|()
block|{
if|if
condition|(
name|hasContexts
argument_list|()
operator|&&
name|curPos
operator|<
name|contextSets
operator|.
name|size
argument_list|()
condition|)
block|{
assert|assert
name|currentOrd
operator|==
name|ords
index|[
name|curPos
index|]
assert|;
return|return
name|contextSets
operator|.
name|get
argument_list|(
name|currentOrd
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

