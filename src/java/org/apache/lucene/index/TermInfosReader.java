begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/** This stores a monotonically increasing set of<Term, TermInfo> pairs in a  * Directory.  Pairs are accessed either by Term or by ordinal position the  * set.  */
end_comment

begin_class
DECL|class|TermInfosReader
specifier|final
class|class
name|TermInfosReader
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
name|String
name|segment
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|enumerators
specifier|private
name|ThreadLocal
name|enumerators
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
DECL|field|origEnum
specifier|private
name|SegmentTermEnum
name|origEnum
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
decl_stmt|;
DECL|field|indexTerms
specifier|private
name|Term
index|[]
name|indexTerms
init|=
literal|null
decl_stmt|;
DECL|field|indexInfos
specifier|private
name|TermInfo
index|[]
name|indexInfos
decl_stmt|;
DECL|field|indexPointers
specifier|private
name|long
index|[]
name|indexPointers
decl_stmt|;
DECL|field|indexEnum
specifier|private
name|SegmentTermEnum
name|indexEnum
decl_stmt|;
DECL|method|TermInfosReader
name|TermInfosReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|seg
parameter_list|,
name|FieldInfos
name|fis
parameter_list|)
throws|throws
name|IOException
block|{
name|directory
operator|=
name|dir
expr_stmt|;
name|segment
operator|=
name|seg
expr_stmt|;
name|fieldInfos
operator|=
name|fis
expr_stmt|;
name|origEnum
operator|=
operator|new
name|SegmentTermEnum
argument_list|(
name|directory
operator|.
name|openInput
argument_list|(
name|segment
operator|+
literal|".tis"
argument_list|)
argument_list|,
name|fieldInfos
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|size
operator|=
name|origEnum
operator|.
name|size
expr_stmt|;
name|indexEnum
operator|=
operator|new
name|SegmentTermEnum
argument_list|(
name|directory
operator|.
name|openInput
argument_list|(
name|segment
operator|+
literal|".tii"
argument_list|)
argument_list|,
name|fieldInfos
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getSkipInterval
specifier|public
name|int
name|getSkipInterval
parameter_list|()
block|{
return|return
name|origEnum
operator|.
name|skipInterval
return|;
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|origEnum
operator|!=
literal|null
condition|)
name|origEnum
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexEnum
operator|!=
literal|null
condition|)
name|indexEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|enumerators
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the number of term/value pairs in the set. */
DECL|method|size
specifier|final
name|long
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|getEnum
specifier|private
name|SegmentTermEnum
name|getEnum
parameter_list|()
block|{
name|SegmentTermEnum
name|termEnum
init|=
operator|(
name|SegmentTermEnum
operator|)
name|enumerators
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|termEnum
operator|==
literal|null
condition|)
block|{
name|termEnum
operator|=
name|terms
argument_list|()
expr_stmt|;
name|enumerators
operator|.
name|set
argument_list|(
name|termEnum
argument_list|)
expr_stmt|;
block|}
return|return
name|termEnum
return|;
block|}
DECL|method|ensureIndexIsRead
specifier|private
specifier|synchronized
name|void
name|ensureIndexIsRead
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexTerms
operator|!=
literal|null
condition|)
comment|// index already read
return|return;
comment|// do nothing
try|try
block|{
name|int
name|indexSize
init|=
operator|(
name|int
operator|)
name|indexEnum
operator|.
name|size
decl_stmt|;
comment|// otherwise read index
name|indexTerms
operator|=
operator|new
name|Term
index|[
name|indexSize
index|]
expr_stmt|;
name|indexInfos
operator|=
operator|new
name|TermInfo
index|[
name|indexSize
index|]
expr_stmt|;
name|indexPointers
operator|=
operator|new
name|long
index|[
name|indexSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|indexEnum
operator|.
name|next
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|indexTerms
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|term
argument_list|()
expr_stmt|;
name|indexInfos
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|termInfo
argument_list|()
expr_stmt|;
name|indexPointers
index|[
name|i
index|]
operator|=
name|indexEnum
operator|.
name|indexPointer
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|indexEnum
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexEnum
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Returns the offset of the greatest index entry which is less than or equal to term.*/
DECL|method|getIndexOffset
specifier|private
specifier|final
name|int
name|getIndexOffset
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// binary search indexTerms[]
name|int
name|hi
init|=
name|indexTerms
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>
literal|1
decl_stmt|;
name|int
name|delta
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|indexTerms
index|[
name|mid
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|delta
operator|>
literal|0
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
return|return
name|mid
return|;
block|}
return|return
name|hi
return|;
block|}
DECL|method|seekEnum
specifier|private
specifier|final
name|void
name|seekEnum
parameter_list|(
name|int
name|indexOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|getEnum
argument_list|()
operator|.
name|seek
argument_list|(
name|indexPointers
index|[
name|indexOffset
index|]
argument_list|,
operator|(
name|indexOffset
operator|*
name|getEnum
argument_list|()
operator|.
name|indexInterval
operator|)
operator|-
literal|1
argument_list|,
name|indexTerms
index|[
name|indexOffset
index|]
argument_list|,
name|indexInfos
index|[
name|indexOffset
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the TermInfo for a Term in the set, or null. */
DECL|method|get
name|TermInfo
name|get
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|ensureIndexIsRead
argument_list|()
expr_stmt|;
comment|// optimize sequential access: first try scanning cached enum w/o seeking
name|SegmentTermEnum
name|enumerator
init|=
name|getEnum
argument_list|()
decl_stmt|;
if|if
condition|(
name|enumerator
operator|.
name|term
argument_list|()
operator|!=
literal|null
comment|// term is at or past current
operator|&&
operator|(
operator|(
name|enumerator
operator|.
name|prev
argument_list|()
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|enumerator
operator|.
name|prev
argument_list|()
argument_list|)
operator|>
literal|0
operator|)
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|>=
literal|0
operator|)
condition|)
block|{
name|int
name|enumOffset
init|=
call|(
name|int
call|)
argument_list|(
name|enumerator
operator|.
name|position
operator|/
name|enumerator
operator|.
name|indexInterval
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|indexTerms
operator|.
name|length
operator|==
name|enumOffset
comment|// but before end of block
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|indexTerms
index|[
name|enumOffset
index|]
argument_list|)
operator|<
literal|0
condition|)
return|return
name|scanEnum
argument_list|(
name|term
argument_list|)
return|;
comment|// no need to seek
block|}
comment|// random-access: must seek
name|seekEnum
argument_list|(
name|getIndexOffset
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|scanEnum
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|/** Scans within block for matching term. */
DECL|method|scanEnum
specifier|private
specifier|final
name|TermInfo
name|scanEnum
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentTermEnum
name|enumerator
init|=
name|getEnum
argument_list|()
decl_stmt|;
name|enumerator
operator|.
name|scanTo
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|enumerator
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
return|return
name|enumerator
operator|.
name|termInfo
argument_list|()
return|;
else|else
return|return
literal|null
return|;
block|}
comment|/** Returns the nth term in the set. */
DECL|method|get
specifier|final
name|Term
name|get
parameter_list|(
name|int
name|position
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|SegmentTermEnum
name|enumerator
init|=
name|getEnum
argument_list|()
decl_stmt|;
if|if
condition|(
name|enumerator
operator|!=
literal|null
operator|&&
name|enumerator
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|position
operator|>=
name|enumerator
operator|.
name|position
operator|&&
name|position
operator|<
operator|(
name|enumerator
operator|.
name|position
operator|+
name|enumerator
operator|.
name|indexInterval
operator|)
condition|)
return|return
name|scanEnum
argument_list|(
name|position
argument_list|)
return|;
comment|// can avoid seek
name|seekEnum
argument_list|(
name|position
operator|/
name|enumerator
operator|.
name|indexInterval
argument_list|)
expr_stmt|;
comment|// must seek
return|return
name|scanEnum
argument_list|(
name|position
argument_list|)
return|;
block|}
DECL|method|scanEnum
specifier|private
specifier|final
name|Term
name|scanEnum
parameter_list|(
name|int
name|position
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentTermEnum
name|enumerator
init|=
name|getEnum
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumerator
operator|.
name|position
operator|<
name|position
condition|)
if|if
condition|(
operator|!
name|enumerator
operator|.
name|next
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
name|enumerator
operator|.
name|term
argument_list|()
return|;
block|}
comment|/** Returns the position of a Term in the set or -1. */
DECL|method|getPosition
specifier|final
name|long
name|getPosition
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
return|return
operator|-
literal|1
return|;
name|ensureIndexIsRead
argument_list|()
expr_stmt|;
name|int
name|indexOffset
init|=
name|getIndexOffset
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|seekEnum
argument_list|(
name|indexOffset
argument_list|)
expr_stmt|;
name|SegmentTermEnum
name|enumerator
init|=
name|getEnum
argument_list|()
decl_stmt|;
while|while
condition|(
name|term
operator|.
name|compareTo
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|>
literal|0
operator|&&
name|enumerator
operator|.
name|next
argument_list|()
condition|)
block|{}
if|if
condition|(
name|term
operator|.
name|compareTo
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
return|return
name|enumerator
operator|.
name|position
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/** Returns an enumeration of all the Terms and TermInfos in the set. */
DECL|method|terms
specifier|public
name|SegmentTermEnum
name|terms
parameter_list|()
block|{
return|return
operator|(
name|SegmentTermEnum
operator|)
name|origEnum
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/** Returns an enumeration of terms starting at or after the named term. */
DECL|method|terms
specifier|public
name|SegmentTermEnum
name|terms
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|get
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
operator|(
name|SegmentTermEnum
operator|)
name|getEnum
argument_list|()
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class

end_unit

