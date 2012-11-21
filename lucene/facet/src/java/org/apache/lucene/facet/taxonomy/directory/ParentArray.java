begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
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
name|directory
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|CorruptIndexException
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
name|DocsAndPositionsEnum
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
name|TermsEnum
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
name|DocIdSetIterator
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
name|ArrayUtil
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ParentArray
class|class
name|ParentArray
block|{
comment|// TODO: maybe use PackedInts?
DECL|field|parentOrdinals
specifier|private
specifier|final
name|int
index|[]
name|parentOrdinals
decl_stmt|;
comment|/** Used by {@link #add(int, int)} when the array needs to grow. */
DECL|method|ParentArray
name|ParentArray
parameter_list|(
name|int
index|[]
name|parentOrdinals
parameter_list|)
block|{
name|this
operator|.
name|parentOrdinals
operator|=
name|parentOrdinals
expr_stmt|;
block|}
DECL|method|ParentArray
specifier|public
name|ParentArray
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|parentOrdinals
operator|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
if|if
condition|(
name|parentOrdinals
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|initFromReader
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Starting Lucene 2.9, following the change LUCENE-1542, we can
comment|// no longer reliably read the parent "-1" (see comment in
comment|// LuceneTaxonomyWriter.SinglePositionTokenStream). We have no way
comment|// to fix this in indexing without breaking backward-compatibility
comment|// with existing indexes, so what we'll do instead is just
comment|// hard-code the parent of ordinal 0 to be -1, and assume (as is
comment|// indeed the case) that no other parent can be -1.
name|parentOrdinals
index|[
literal|0
index|]
operator|=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
expr_stmt|;
block|}
block|}
DECL|method|ParentArray
specifier|public
name|ParentArray
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|ParentArray
name|copyFrom
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|copyFrom
operator|!=
literal|null
assert|;
name|int
index|[]
name|copyParents
init|=
name|copyFrom
operator|.
name|getArray
argument_list|()
decl_stmt|;
assert|assert
name|copyParents
operator|.
name|length
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
operator|:
literal|"do not init a new ParentArray if the index hasn't changed"
assert|;
name|this
operator|.
name|parentOrdinals
operator|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|copyParents
argument_list|,
literal|0
argument_list|,
name|parentOrdinals
argument_list|,
literal|0
argument_list|,
name|copyParents
operator|.
name|length
argument_list|)
expr_stmt|;
name|initFromReader
argument_list|(
name|reader
argument_list|,
name|copyParents
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Read the parents of the new categories
DECL|method|initFromReader
specifier|private
name|void
name|initFromReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|first
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|==
name|first
condition|)
block|{
return|return;
block|}
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|DocsAndPositionsEnum
name|positions
init|=
literal|null
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
if|if
condition|(
name|context
operator|.
name|docBase
operator|<
name|first
condition|)
block|{
continue|continue;
block|}
comment|// in general we could call readerCtx.reader().termPositionsEnum(), but that
comment|// passes the liveDocs. Since we know there are no deletions, the code
comment|// below may save some CPU cycles.
name|termsEnum
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
name|Consts
operator|.
name|FIELD_PAYLOADS
argument_list|)
operator|.
name|iterator
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|Consts
operator|.
name|PAYLOAD_PARENT_BYTES_REF
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent stream data for segment "
operator|+
name|context
operator|.
name|reader
argument_list|()
argument_list|)
throw|;
block|}
name|positions
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
comment|/* no deletes in taxonomy */
argument_list|,
name|positions
argument_list|)
expr_stmt|;
if|if
condition|(
name|positions
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent stream data for segment "
operator|+
name|context
operator|.
name|reader
argument_list|()
argument_list|)
throw|;
block|}
name|idx
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|positions
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|doc
operator|+=
name|context
operator|.
name|docBase
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|idx
condition|)
block|{
if|if
condition|(
name|positions
operator|.
name|freq
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// shouldn't happen
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|idx
argument_list|)
throw|;
block|}
name|parentOrdinals
index|[
name|idx
operator|++
index|]
operator|=
name|positions
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// this shouldn't happen
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|idx
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|idx
operator|+
literal|1
operator|<
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
operator|(
name|idx
operator|+
literal|1
operator|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|idx
operator|!=
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Missing parent data for category "
operator|+
name|idx
argument_list|)
throw|;
block|}
block|}
DECL|method|getArray
specifier|public
name|int
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|parentOrdinals
return|;
block|}
comment|/**    * Adds the given ordinal/parent info and returns either a new instance if the    * underlying array had to grow, or this instance otherwise.    *<p>    *<b>NOTE:</b> you should call this method from a thread-safe code.    */
DECL|method|add
name|ParentArray
name|add
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
name|parentOrdinal
parameter_list|)
block|{
if|if
condition|(
name|ordinal
operator|>=
name|parentOrdinals
operator|.
name|length
condition|)
block|{
name|int
index|[]
name|newarray
init|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|parentOrdinals
argument_list|)
decl_stmt|;
name|newarray
index|[
name|ordinal
index|]
operator|=
name|parentOrdinal
expr_stmt|;
return|return
operator|new
name|ParentArray
argument_list|(
name|newarray
argument_list|)
return|;
block|}
name|parentOrdinals
index|[
name|ordinal
index|]
operator|=
name|parentOrdinal
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

